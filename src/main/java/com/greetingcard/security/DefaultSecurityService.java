package com.greetingcard.security;

import com.greetingcard.dao.UserDao;
import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import com.greetingcard.service.EmailService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import static com.greetingcard.entity.AccessHashType.FORGOT_PASSWORD;
import static com.greetingcard.entity.AccessHashType.VERIFY_EMAIL;

@Slf4j
@Setter
public class DefaultSecurityService implements SecurityService {
    private UserDao userDao;

    private String algorithm;

    private int iteration;

    private String pathToFile;

    @Autowired
    private EmailService emailService;
    @Autowired
    private String siteUrl;

    @Override
    public User login(String login, String password) {
        log.info("login: {}", login);
        checkUserCredentials(login, 50, "login");
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
        checkUserCredentials(user.getFirstName(), 40, "first name");
        checkUserCredentials(user.getLastName(), 40, "last name");
        checkUserCredentials(user.getEmail(), 50, "email");
        checkUserCredentials(user.getLogin(), 50, "login");
        checkUserCredentials(user.getPassword(), 200, "password");

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
            try {
                Files.createDirectories(Path.of(pathToFile, profileFile));
                file.transferTo(Path.of(pathToFile, profileFile, fileName));
            } catch (IOException e) {
                log.error("Can not save new photo: {}", fileName);
                throw new RuntimeException("Can not save new photo", e);
            }

            try {
                Files.deleteIfExists(Path.of(user.getPathToPhoto()));
            } catch (IOException e) {
                log.error("Can not delete old photo: {}", user.getPathToPhoto());
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
        String newAccessHash = getHashPassword(emailAndSalt);

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
            throw new RuntimeException(e);
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
