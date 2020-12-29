package com.greetingcard.dao.jdbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueriesContext {
    /**
     * JdbcCardDao queries
     */
    @Bean
    public String getCardsByUserIdAndRoleId() {
        return "SELECT cards.card_id, name, background_image, background_congratulations, card_link, status_id, users.user_id, firstName, lastName, login, " +
                "email FROM cards LEFT JOIN users_cards ON (cards.card_id=users_cards.card_id) LEFT JOIN users " +
                "ON (users_cards.user_id=users.user_id) WHERE (users.user_id = :userId AND role_id = :roleId) ORDER BY cards.card_id";
    }

    @Bean
    public String cardAndCongratulation() {
        return "SELECT c.card_id, c.user_id as card_user, name, background_image, background_congratulations, card_link, c.status_id, cg.congratulation_id, " +
                "cg.status_id as con_status, message, cg.user_id, firstName, lastName, pathToPhoto, login, link_id, link,type_id FROM users_cards uc " +
                "JOIN cards c ON (uc.card_id = c.card_id) LEFT JOIN congratulations cg ON (c.card_id=cg.card_id) LEFT JOIN users u " +
                "ON (cg.user_id=u.user_id) LEFT JOIN links l ON (cg.congratulation_id=l.congratulation_id) WHERE uc.card_id = :cardId " +
                "AND uc.user_id = :userId";
    }

    @Bean
    public String getCardStatus() {
        return "SELECT status_id FROM cards WHERE card_id = :card_id";
    }

    @Bean
    public String saveNewCard() {
        return "INSERT INTO cards (user_id, name, status_id) VALUES (?,?,?)";
    }

    @Bean
    public String addToUsersCards() {
        return "INSERT INTO users_cards (card_id, user_id, role_id) VALUES (?,?,?)";
    }

    @Bean
    public String deleteByCardId() {
        return "DELETE FROM cards WHERE card_id=? and user_id=?";
    }

    @Bean
    public String changeStatusOfCardAndSetCardLinkById() {
        return "UPDATE cards SET status_id = ?, card_link = ? where card_id = ?";
    }

    @Bean
    public String getAllCardsByUserId() {
        return "SELECT c.card_id, c.name, c.background_image, c.background_congratulations, c.card_link, c.status_id, u.user_id, u.firstName, u.lastName, u.login, " +
                "u.email FROM users_cards uc JOIN cards c ON (uc.card_id = c.card_id) JOIN users u ON (c.user_id = u.user_id) " +
                "WHERE uc.user_id = :id ORDER BY c.card_id";
    }

    @Bean
    public String changeName() {
        return "UPDATE cards SET name = ? where card_id = ? and user_id = ?";
    }

    @Bean
    public String saveBackground() {
        return "UPDATE cards SET background_image = :background_image where card_id = :card_id and user_id = :user_id";
    }

    @Bean
    public String saveBackgroundOfCongratulations() {
        return "UPDATE cards SET background_congratulations = :background_congratulations where card_id = :card_id and user_id = :user_id";
    }

    @Bean
    public String deleteBackground() {
        return "UPDATE cards SET background_image=null where card_id = :card_id and user_id = :user_id";
    }

    /**
     * JdbcCardUserDao queries
     */
    @Bean
    public String insertMemberUser() {
        return "INSERT INTO users_cards (user_id, card_id, role_id) VALUES (:user_id, :card_id, 2)";
    }

    @Bean
    public String getUserRole() {
        return "SELECT role_id FROM users_cards WHERE user_id = :user_id AND card_id = :card_id";
    }

    @Bean
    public String getUsersByCardId() {
        return "SELECT u.user_id, u.firstName, u.lastName, u.login, u.email, u.pathToPhoto, count(cg.card_id) AS countCongratulations " +
                "FROM users_cards uc JOIN users u ON (u.user_id = uc.user_id) LEFT JOIN congratulations cg " +
                "ON (uc.card_id = cg.card_id AND uc.user_id = cg.user_id) WHERE uc.card_id = :card_id AND uc.role_id !=1 " +
                "GROUP BY u.user_id, u.firstName, u.lastName, u.login, u.email, u.pathToPhoto ORDER BY u.login";
    }

    @Bean
    public String getUsersByCardIdForWebSocketNotification() {
        return "SELECT u.user_id, u.firstName, u.lastName, u.login, u.email, u.pathToPhoto, count(cg.card_id) AS countCongratulations " +
                "FROM users_cards uc JOIN users u ON (u.user_id = uc.user_id) LEFT JOIN congratulations cg " +
                "ON (uc.card_id = cg.card_id AND uc.user_id = cg.user_id) WHERE uc.card_id = :card_id " +
                "GROUP BY u.user_id, u.firstName, u.lastName, u.login, u.email, u.pathToPhoto ORDER BY u.user_id";
    }

    @Bean
    public String deleteUser() {
        return "DELETE from users_cards WHERE user_id = :user_id AND card_id = :card_id";
    }

    @Bean
    public String deleteListUsers() {
        return "DELETE from users_cards WHERE card_id = :card_id AND user_id IN";
    }

    @Bean
    public String finishedCardAndCongratulation() {
        return "SELECT cards.card_id, cards.user_id as card_user, name, background_image, background_congratulations, card_link, cards.status_id, " +
                "cg.congratulation_id, cg.status_id as con_status, message, cg.user_id, firstName, lastName, " +
                "pathToPhoto, login, link_id, link,type_id FROM cards LEFT JOIN congratulations cg" +
                " ON (cards.card_id=cg.card_id) LEFT JOIN users u ON (cg.user_id=u.user_id) " +
                "LEFT JOIN links l ON (cg.congratulation_id=l.congratulation_id) " +
                "WHERE cards.card_id = :cardId";
    }

    /**
     * JdbcCongratulationDao queries
     */
    @Bean
    public String getCongratulation() {
        return "SELECT congratulations.congratulation_id, user_id, card_id, status_id, message, link_id, link, type_id " +
                "FROM congratulations LEFT JOIN links ON (congratulations.congratulation_id = links.congratulation_id) " +
                "WHERE congratulations.congratulation_id=?";
    }

    @Bean
    public String getLinks() {
        return "SELECT link_id, link, type_id, congratulation_id FROM links WHERE link_id IN";
    }

    @Bean
    public String saveCongratulation() {
        return "INSERT INTO congratulations (message, card_id, user_id, status_id) VALUES (?,?,?,?)";
    }

    @Bean
    public String updateCongratulation() {
        return "UPDATE congratulations SET message = ? where (congratulation_id = ? and user_id = ?)";
    }

    @Bean
    public String saveLink() {
        return "INSERT INTO links (link, type_id, congratulation_id) VALUES(?,?,?)";
    }

    @Bean
    public String leaveByCardId() {
        return "DELETE FROM congratulations WHERE card_id= :card_id and user_id= :user_id";
    }

    @Bean
    public String findImageAndAudioLinksByCardId() {
        return "SELECT link FROM links l JOIN congratulations cg ON (cg.congratulation_id = l.congratulation_id) " +
                "WHERE card_id=? and (type_id = 2 OR type_id = 3)";
    }

    @Bean
    public String findCongratulationsByCardId() {
        return "SELECT cg.congratulation_id, user_id, card_id, status_id, message, link_id, link, type_id " +
                "FROM congratulations cg LEFT JOIN links ON (cg.congratulation_id = links.congratulation_id) WHERE card_id=?";
    }

    @Bean
    public String changeCongratulationStatusByCardId() {
        return "UPDATE congratulations SET status_id = ? WHERE card_id = ?";
    }

    @Bean
    public String changeCongratulationStatusByCongratulationId() {
        return "UPDATE congratulations SET status_id = ? WHERE congratulation_id = ?";
    }

    @Bean
    public String deleteCongratulationById() {
        return "DELETE FROM congratulations WHERE congratulation_id= :congratulation_id";
    }

    @Bean
    public String findImageAndAudioLinksByCongratulationId() {
        return "SELECT link FROM links l LEFT JOIN congratulations cg ON (cg.congratulation_id = l.congratulation_id) " +
                "WHERE cg.congratulation_id=? and (type_id = 2 OR type_id = 3)";
    }

    @Bean
    public String deleteLinkById() {
        return "DELETE FROM links WHERE link_id IN";
    }


    /**
     * JdbcUserDao queries
     */
    @Bean
    public String saveUser() {
        return "INSERT INTO users (firstName, lastName, login, email, password, salt, language_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Bean
    public String updateUser() {
        return "UPDATE users SET firstName=?, lastName=?, login=?, pathToPhoto=COALESCE(?, pathToPhoto) WHERE user_id=?;";
    }

    @Bean
    public String updateUserPassword() {
        return "UPDATE users SET password=? WHERE user_id=?;";
    }

    @Bean
    public String updateUserLanguage() {
        return "UPDATE users SET language_id=? WHERE user_id=?;";
    }

    @Bean
    public String findUserById() {
        return "SELECT user_id, firstName, lastName, login, email, language_id, facebook, google, pathToPhoto FROM users WHERE user_id = ?";
    }

    @Bean
    public String findUserByLogin() {
        return "SELECT user_id, firstName, lastName, login, email, password, salt, language_id, facebook, google, pathToPhoto FROM users WHERE login = ?";
    }

    @Bean
    public String findUserByEmail() {
        return "SELECT user_id, firstName, lastName, login, email, password, salt, language_id, facebook, google, pathToPhoto FROM users WHERE email = ?";
    }

    @Bean
    public String saveForgotPassAccessHash() {
        return "INSERT INTO forgot_password_hashes (user_id, hash) VALUES (?, ?)";
    }

    @Bean
    public String saveVerifyEmailAccessHash() {
        return "INSERT INTO verify_email_hashes (user_id, hash) VALUES (?, ?)";
    }

    @Bean
    public String findUserByForgotPassAccessHash() {
        return "SELECT users.user_id, firstName, lastName, login, email, password, salt, language_id, facebook, google, pathToPhoto FROM users " +
                "LEFT JOIN forgot_password_hashes ON (users.user_id = forgot_password_hashes.user_id) WHERE forgot_password_hashes.hash = ?";
    }

    @Bean
    public String findVerifyEmailAccessHash() {
        return "SELECT user_id FROM verify_email_hashes WHERE hash = ?";
    }

    @Bean
    public String deleteForgotPassAccessHash() {
        return "DELETE FROM forgot_password_hashes WHERE hash = ?";
    }

    @Bean
    public String deleteVerifyEmailAccessHash() {
        return "DELETE FROM verify_email_hashes WHERE hash = ?";
    }

    @Bean
    public String updateUserVerifyEmail() {
        return "UPDATE users SET email_verified='1' WHERE user_id=?;";
    }
}
