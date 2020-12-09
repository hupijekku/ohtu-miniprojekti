package miniprojekti.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.net.URI; 
import java.sql.Statement;
import org.sqlite.SQLiteConfig;

public class Database {
    
    private String databaseAddress;
    private boolean isLocal;
    
    public Database(String databaseAddress) {
        this.databaseAddress = databaseAddress;
        try (Connection connection = this.getConnection()) {
            Statement s = connection.createStatement();
            // PSQL needs SERIAL for autoincrementing but SQLITE doesn't recognize it
            if (isLocal) {
                s.execute("CREATE TABLE IF NOT EXISTS readingtip " +
                    "(id INTEGER PRIMARY KEY, " +
                    "author VARCHAR(255), " +
                    "title VARCHAR(255), " +
                    "url VARCHAR(255))");
                s.execute("CREATE TABLE IF NOT EXISTS tip ("
                        + "	id INTEGER NOT NULL,"
                        + "     account_id INTEGER NOT NULL,"
                        + "	title VARCHAR(144),"
                        + "	type VARCHAR(144),"
                        + "	note VARCHAR(144),"
                        + "	PRIMARY KEY (id),"
                        + "	FOREIGN KEY(account_id) REFERENCES account (id) ON DELETE CASCADE"
                        + ")");
                s.execute("CREATE TABLE IF NOT EXISTS book ("
                        + "	id INTEGER NOT NULL,"
                        + "	tip_id INTEGER NOT NULL,"
                        + "	author VARCHAR(144),"
                        + "	isbn VARCHAR(144),"
                        + "	url VARCHAR(144),"
                        + "	PRIMARY KEY (id),"
                        + "	FOREIGN KEY(tip_id) REFERENCES tip (id) ON DELETE CASCADE"
                        + ")");
                s.execute("CREATE TABLE IF NOT EXISTS video ("
                        + "	id INTEGER NOT NULL,"
                        + "	tip_id INTEGER NOT NULL,"
                        + "	url VARCHAR(144),"
                        + "	PRIMARY KEY (id),"
                        + "	FOREIGN KEY(tip_id) REFERENCES tip (id) ON DELETE CASCADE"
                        + ")");
                s.execute("CREATE TABLE IF NOT EXISTS blogpost ("
                        + "	id INTEGER NOT NULL,"
                        + "	tip_id INTEGER NOT NULL,"
                        + "	url VARCHAR(144),"
                        + "	PRIMARY KEY (id),"
                        + "	FOREIGN KEY(tip_id) REFERENCES tip (id) ON DELETE CASCADE"
                        + ")");
                s.execute("CREATE TABLE IF NOT EXISTS podcast ("
                        + "	id INTEGER NOT NULL,"
                        + "	tip_id INTEGER NOT NULL,"
                        + "	author VARCHAR(144),"
                        + "	description VARCHAR(144),"
                        + "	url VARCHAR(144),"
                        + "	PRIMARY KEY (id),"
                        + "	FOREIGN KEY(tip_id) REFERENCES tip (id) ON DELETE CASCADE"
                        + ")");
                s.execute("CREATE TABLE IF NOT EXISTS account ("
                        + "     id INTEGER NOT NULL,"
                        + "     name VARCHAR(144),"
                        + "     password_hash VARCHAR(144),"
                        + "     salt VARCHAR(144),"
                        + "	PRIMARY KEY (id)"
                        + ")");
            } else {
                s.execute("CREATE TABLE IF NOT EXISTS readingtip "
                        + "(id SERIAL PRIMARY KEY, "
                        + "author VARCHAR(255), "
                        + "title VARCHAR(255), "
                        + "url VARCHAR(255))");
                s.execute("CREATE TABLE IF NOT EXISTS tip ("
                        + "	id SERIAL PRIMARY KEY,"
                        + "     account_id INT NOT NULL,"
                        + "	title VARCHAR(255),"
                        + "	type VARCHAR(255),"
                        + "	note VARCHAR(255),"
                        + "	FOREIGN KEY(account_id) REFERENCES account (id) ON DELETE CASCADE"
                        + ")");
                s.execute("CREATE TABLE IF NOT EXISTS book ("
                        + "	id SERIAL PRIMARY KEY,"
                        + "	tip_id INT NOT NULL,"
                        + "	author VARCHAR(255),"
                        + "	isbn VARCHAR(255),"
                        + "	url VARCHAR(255),"
                        + "	FOREIGN KEY(tip_id) REFERENCES tip (id) ON DELETE CASCADE"
                        + ")");
                s.execute("CREATE TABLE IF NOT EXISTS video ("
                        + "	id SERIAL PRIMARY KEY,"
                        + "	tip_id INT NOT NULL,"
                        + "	url VARCHAR(255),"
                        + "	FOREIGN KEY(tip_id) REFERENCES tip (id) ON DELETE CASCADE"
                        + ")");
                s.execute("CREATE TABLE IF NOT EXISTS blogpost ("
                        + "	id SERIAL PRIMARY KEY,"
                        + "	tip_id INT NOT NULL,"
                        + "	url VARCHAR(255),"
                        + "	FOREIGN KEY(tip_id) REFERENCES tip (id) ON DELETE CASCADE"
                        + ")");
                s.execute("CREATE TABLE IF NOT EXISTS podcast ("
                        + "	id SERIAL PRIMARY KEY,"
                        + "	tip_id INT NOT NULL,"
                        + "	author VARCHAR(255),"
                        + "	description VARCHAR(255),"
                        + "	url VARCHAR(255),"
                        + "	FOREIGN KEY(tip_id) REFERENCES tip (id) ON DELETE CASCADE"
                        + ")");
                s.execute("CREATE TABLE IF NOT EXISTS account ("
                        + "     id SERIAL PRIMARY KEY,"
                        + "     name VARCHAR(255),"
                        + "     password_hash VARCHAR(255),"
                        + "     salt VARCHAR(255)"
                        + ")");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public Connection getConnection() throws Exception {

        // If we are in Heroku, use the environment variable for database URI
        // Otherwise use the set local address
        if (System.getenv("DATABASE_URL") != null) {
            Class.forName("org.postgresql.Driver");
            // Parse URI for credentials
            URI dbUri = new URI(System.getenv("DATABASE_URL"));
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
            
            System.out.println(dbUrl + " : " + username + " : " + password);
            this.isLocal = false;
            return DriverManager.getConnection(dbUrl, username, password);
        }
        this.isLocal = true;
        Connection connection = null;
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(databaseAddress, config.toProperties());
        } catch (Exception e) {
            System.out.println(e);
        }
        return connection;
    }
}
