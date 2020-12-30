package com.greetingcard.security;

import com.greetingcard.dao.UserDao;
import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import com.greetingcard.service.EmailService;
import com.greetingcard.service.impl.DefaultAmazonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import static com.greetingcard.entity.AccessHashType.VERIFY_EMAIL;

@Slf4j
@RequiredArgsConstructor
@Service
@PropertySource(value = "classpath:application.properties")
public class DefaultSecurityService implements SecurityService {
    private final UserDao userDao;
    private final DefaultAmazonService defaultAmazonService;

    @Value("${algorithm:SHA-256}")
    private String algorithm;
    @Value("${iteration:1}")
    private int iteration;
    @Value("${webapp.url}")
    private String siteUrl;
    @Value("${max.characters.name.and.last.name}")
    private int maxCharactersNameAndLastName;
    @Value("${max.characters.email.and.login}")
    private int maxCharactersEmailAndLogin;
    @Value("${max.characters.password}")
    private int maxCharactersPassword;

    @Autowired
    private EmailService emailService;

    @Override
    public User login(String login, String password) {

        log.info("login: {}", login);
        checkUserCredentials(login, maxCharactersEmailAndLogin, "login");

        try {
            User user = userDao.findByLogin(login);
            String salt = user.getSalt();
            String hashPassword = getHashPassword(salt.concat(password));

            if (user.getPassword().equals(hashPassword)) {
                log.info("Credentials is ok");
                return user;
            }

            log.info("Credentials not valid");
            throw new IllegalAccessError("Access denied. Please check your login and password");
        } catch (DataAccessException e) {
            log.info("Credentials not valid");
            throw new IllegalAccessError("Access denied. Please check your login and password");
        }
    }

    @Override
    public void register(User user) {
        checkUserCredentials(user.getFirstName(), maxCharactersNameAndLastName, "first name");
        checkUserCredentials(user.getLastName(), maxCharactersNameAndLastName, "last name");
        checkUserCredentials(user.getEmail(), maxCharactersEmailAndLogin, "email");
        checkUserCredentials(user.getLogin(), maxCharactersEmailAndLogin, "login");
        checkUserCredentials(user.getPassword(), maxCharactersPassword, "password");

        String salt = UUID.randomUUID().toString();
        String saltAndPassword = getHashPassword(salt.concat(user.getPassword()));

        user.setPassword(saltAndPassword);
        user.setSalt(salt);
        if (user.getLanguage() == null) {
            user.setLanguage(Language.ENGLISH);
        }

        userDao.save(user);
        sendVerificationEmail(user.getEmail());
    }

    @Override
    public void update(User user, MultipartFile file) {
        String profileFile = "profile";
        if (!file.isEmpty()) {
            String uuidFile = UUID.randomUUID().toString();
            String fileName = uuidFile + "." + file.getOriginalFilename();

            defaultAmazonService.uploadFile(file, profileFile + "/" + fileName);

            if (user.getPathToPhoto() != null) {
                defaultAmazonService.deleteFileFromS3Bucket(user.getPathToPhoto());
            }

            user.setPathToPhoto("/" + profileFile + "/" + fileName);
        }
        userDao.update(user);
    }

    @Override
    public void updatePassword(User user) {
        String newPasswordHash = getHashPassword(user.getSalt().concat(user.getPassword()));
        user.setPassword(newPasswordHash);
        userDao.updatePassword(user);
    }

    @Override
    public void updateLanguage(User user) {
        userDao.updateLanguage(user);
    }

    @Override
    public User findByLogin(String login) {
        try {
            return userDao.findByLogin(login);
        } catch (DataAccessException e) {
            log.info("Login does not exist: {}", login);
            throw new IllegalArgumentException("Login does not exist", e);
        }
    }

    @Override
    @Transactional
    public void restorePassword(String email) {
        User user = userDao.findByEmail(email);

        log.info("Generating new password for user with email {}", email);
        String newPassword = UUID.randomUUID().toString().substring(0, 15);
        user.setPassword(newPassword);
        updatePassword(user);

        log.info("Sending letter with new password to user with email address: {}", email);
        String emailMessageBody = "We received your request to restore access to your account with forgotten password. \n" +
                "Here is a new generated password for you: " + newPassword +
                "\n You can now access your account with that password. Later you can change it in your profile. ";
        emailService.sendMail(email, "Greeting Card: Restore access", emailMessageBody);
    }

    @Override
    public void verifyEmailAccessHash(String hash) {
        userDao.verifyEmailAccessHash(hash);
    }

    @Override
    @Transactional
    public void verifyForgotPasswordAccessHash(String hash, String password) {
        log.info("Getting user by forgot password access hash");
        User user = userDao.findByForgotPasswordAccessHash(hash);
        String newPasswordHash = getHashPassword(user.getSalt().concat(password));
        user.setPassword(newPasswordHash);

        log.info("Updating user's password");
        userDao.verifyForgotPasswordAccessHash(hash, user);
    }

    @Override
    public String generateAccessHash(String email, AccessHashType hashType) {
        String salt = UUID.randomUUID().toString();
        String emailAndSalt = salt.concat(email);
        String newAccessHash = getHashPassword(emailAndSalt).replaceAll("/", "");

        userDao.saveAccessHash(email, newAccessHash, hashType);
        return newAccessHash;
    }

    @Override
    public User loginWithFacebook(Map<String, String> facebookCredentials) {
        String name = facebookCredentials.get("name");
        String email = facebookCredentials.get("email");
        String facebookId = facebookCredentials.get("userID");
        User user;
        try {
            return userDao.findByEmail(email);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        String[] names = name.split(" ", 2);
        user = User.builder().facebook(facebookId).firstName(names[0]).lastName(names[1]).login(email)
                .email(email).salt("").password(getHashPassword(facebookId)).language(Language.ENGLISH).build();
        long id = userDao.saveUserFromFacebook(user);
        user.setId(id);
        return user;
    }

    @Override
    public User loginWithGoogle(Map<String, String> googleCredentials) {
        String googleId = googleCredentials.get("googleId");
        String pathToPhoto = googleCredentials.get("imageUrl");
        String email = googleCredentials.get("email");
        String firstName = googleCredentials.get("givenName");
        String lastName = googleCredentials.get("familyName");
        User user;
        try {
            return userDao.findByEmail(email);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        user = User.builder().google(googleId).firstName(firstName).lastName(lastName).login(email)
                .email(email).pathToPhoto(pathToPhoto).salt("").password(getHashPassword(googleId)).
                        language(Language.ENGLISH).build();
        long id = userDao.saveUserFromGoogle(user);
        user.setId(id);
        return user;
    }

    String getHashPassword(String saltAndPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] bytes = saltAndPassword.getBytes();
            for (int i = 0; i < iteration; i++) {
                digest.update(bytes);
                bytes = digest.digest();
            }
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("Cannot find algorithm -", e);
            throw new RuntimeException("Cannot find algorithm -", e);
        }
    }

    void sendVerificationEmail(String email) {
        log.debug("Sending letter to verify user's email address after registration: {}", email);
        String accessHash = generateAccessHash(email, VERIFY_EMAIL);

        String emailMessageBody = "Welcome to the Greeting Card!" +
                "To finish the registration process, we need to verify your email." +
                "Please confirm your address by opening this link:\n " +
                siteUrl + "user/verification/" + accessHash;
        emailService.sendMail(email, "Greeting Card: Verify email", emailMessageBody);

        log.debug("Sent letter for email verification to: {}", email);
    }

    private void checkUserCredentials(String fieldValue, int maxCharacters, String fieldName) {
        if (fieldValue.length() > maxCharacters) {
            throw new IllegalArgumentException("Sorry, " + fieldName + " is too long. " +
                    "Please put " + fieldName + " up to " + maxCharacters + " characters.");
        }
    }
}
