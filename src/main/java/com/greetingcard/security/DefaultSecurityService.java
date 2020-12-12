package com.greetingcard.security;

import com.greetingcard.dao.UserDao;
import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import com.greetingcard.service.EmailService;
import com.greetingcard.service.impl.DefaultAmazonService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import static com.greetingcard.entity.AccessHashType.VERIFY_EMAIL;

@Slf4j
@Setter//TODO:костыль - сеттер нужен только для тестов
@Service
@PropertySource(value = "classpath:application.properties")
public class DefaultSecurityService implements SecurityService {
    private UserDao userDao;
    private DefaultAmazonService defaultAmazonService;

    @Value("${algorithm:SHA-256}")
    private String algorithm;
    @Value("${iteration:1}")
    private int iteration;
    @Value("${webapp.url:https://greeting-team.herokuapp.com/}")
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
        User user = userDao.findByLogin(login);

        if (user != null) {
            String salt = user.getSalt();
            String hashPassword = getHashPassword(salt.concat(password));

            if (user.getPassword().equals(hashPassword)) {
                log.info("Credentials is ok");
                return user;
            }
        }
        return null;
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
    public User findById(long id) {
        return userDao.findById(id);
    }

    @Override
    public User findByLogin(String login) {
        return userDao.findByLogin(login);
    }

    @Override
    @Transactional
    public void restorePassword(String email) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Cannot find a user with email: " + email);
        }

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
            throw new RuntimeException();
        }
    }

    void sendVerificationEmail(String email) {
        log.debug("Sending letter to verify user's email address after registration: {}", email);
        String accessHash = generateAccessHash(email, VERIFY_EMAIL);

        String emailMessageBody = "Welcome to the Greeting Card!" +
                "To finish the registration process, we need to verify your email." +
                "Please confirm your address by opening this link:\n " +
                siteUrl + "api/v1/user/verification/" + accessHash;
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
