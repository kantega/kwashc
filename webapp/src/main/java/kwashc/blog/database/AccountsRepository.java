package kwashc.blog.database;

import kwashc.blog.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository responsible for loading {@code User}s, checking login
 * or loading a users secret from storage.
 */
public class AccountsRepository {

    private static final DatabaseHolder databaseHolder = new DatabaseHolder();

    public static User loadUser(final String username) {
        try {
            ResultSet rs = databaseHolder.get().createStatement().executeQuery("SELECT username,secret FROM users WHERE username = '" + username + "';");
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("secret"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkLogin(final String username, final String secret) {
        try {
            ResultSet rs = databaseHolder.get().createStatement().executeQuery("SELECT 1 FROM users WHERE username = '" + username + "' and secret = '" + secret + "';");
            if (rs.next()) {
                return 1 == rs.getInt(1) ;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getSecret(final String username) {
        try {
            ResultSet rs = databaseHolder.get().createStatement().executeQuery("SELECT secret FROM users WHERE username = '" + username + "';");
            if (rs.next()) {
                return rs.getString("secret");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
