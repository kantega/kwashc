package kwashc.blog.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides a {@code Connection} to a database.
 *
 * <p>{@code DatabaseHolder} creates an in-memory database, populates
 * it with some default data and provides a {@code Connection} that can
 * be used to query the database.</p>
 */
public class DatabaseHolder {

    private static Connection conn;

    static {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
            conn.createStatement().execute("CREATE TABLE users AS SELECT * FROM CSVREAD('src/main/resources/users.csv');");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection get() throws RuntimeException {
        return conn;
    }

}
