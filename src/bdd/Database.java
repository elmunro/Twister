package bdd;

import java.sql.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Database {
    public enum DBType {MYSQLDB, SQLSERVER}

    private static boolean mysql_pooling;
    private static final String mysql_username = "root";
    private static final String mysql_password = "root";
    private static final String mysql_host = "localhost:3306";
    private static final String mysql_db = "twista";

    private static final String sqlserver_CS = "jdbc:sqlserver://localhost\\EL-GRINGO:1433;database=Twister";


    //TODO implémenter un choix de DB, MYSQL ou SQLSERVER
    private DataSource dataSource;
    private static Database database;

    public Database(String jndiname) throws SQLException {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/" + jndiname);
        } catch (NamingException e) {
            // Handle error that it’s not configured in JNDI.
            throw new SQLException(jndiname + " is missing in JNDI! : " + e.getMessage());
        }
    }

    public Connection getConnection(DBType type) throws SQLException {
        switch (type) {
            case MYSQLDB:
                return dataSource.getConnection();
            case SQLSERVER:
                return DriverManager.getConnection(sqlserver_CS);
            default:
                return null;
        }

    }


    public static Connection getMySQLConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (mysql_pooling == false) {
            return (DriverManager.getConnection("jdbc:mysql://" + mysql_host + "/" + mysql_db,
                    mysql_username, mysql_password));
        } else {
            if (database == null) {
                database = new Database("jdbc/db");
            }
            return (database.getConnection(DBType.MYSQLDB));
        }
    }
}


