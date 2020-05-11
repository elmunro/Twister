package bdd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import services.Tools;

import java.sql.*;

public class UserTools {

    /**
     * DB tool that checks if provided password is correct
     *
     * @param login login
     * @param pswd  password to check
     * @return true if it matches, false otherwise
     * @throws DBException
     */
    public static boolean passwordCheck(String login, String pswd) throws DBException {
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT user_id FROM users WHERE login=? AND PASSWORD(?) = password";
            PreparedStatement preparedStatement = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pswd);
            ResultSet res = preparedStatement.executeQuery();
            if (!res.next())
                return false;
            res.close();
            preparedStatement.close();
            connexion.close();
        } catch (SQLException e) {
            throw new DBException("Impossible de vérifier le mot de passe : " + e.getMessage());
        }
        return true;
    }

    /**
     * DB tool that gets UserID with the login
     *
     * @param login
     * @return UID that matches the login
     * @throws DBException
     */
    public static int getUserId(String login) throws DBException {
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT * FROM users WHERE login=?";
            PreparedStatement preparedStatement = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, login);
            ResultSet res = preparedStatement.executeQuery();
            res.next();
            //System.out.println(res.toString());
            int user_id = res.getInt("user_id");
            res.close();
            preparedStatement.close();
            connexion.close();
            return user_id;
        } catch (SQLException e) {
            throw new DBException("UserTools.getUserId : Impossible de récupérer l'ID : " + e.getMessage().toString());
        }
    }

    /**
     * DB tool that add a user in the DB
     *
     * @param nom
     * @param prenom
     * @param login
     * @param pswd   that will be hashed
     * @throws DBException
     */
    public static void createUser(String nom, String prenom, String login, String pswd) throws DBException {
        try {
            Connection connexion = Database.getMySQLConnection();
            String query = "INSERT into users values(NULL, ?, ?, ?, PASSWORD(?), NULL)";
            PreparedStatement preparedStatement = connexion.prepareStatement(query, ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, prenom);
            preparedStatement.setString(3, login);
            preparedStatement.setString(4, pswd);
            int created = preparedStatement.executeUpdate();
            preparedStatement.close();
            connexion.close();
        } catch (SQLException e) {
            throw new DBException("Impossible de creer l'utilisateurt : " + e.getMessage().toString());
        }
    }

    /**
     * DB tool that checks if user exists in the database
     *
     * @param ID the ID as an integer
     * @return true if user exists
     * @throws DBException
     */
    public static boolean userExists(int ID) throws DBException {
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT user_id FROM users WHERE user_id = ?";
            PreparedStatement preparedStatement = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, ID);
            ResultSet res = preparedStatement.executeQuery();
            boolean exists = res.next();
            res.close();
            preparedStatement.close();
            connexion.close();
            return exists;
        } catch (SQLException e) {
            throw new DBException("Impossible de vérifier l'ID : " + e.getMessage() + ", cause : " + e.getCause());
        }
    }

    /**
     * DB tool that check if user exists in the database
     *
     * @param login the login string
     * @return true if user exists
     * @throws DBException
     */
    public static boolean userExists(String login) throws DBException {
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT * FROM users WHERE login = ?";
            PreparedStatement preparedStatement = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, login.trim());
            ResultSet res = preparedStatement.executeQuery();
            boolean exists = res.next();
            res.close();
            preparedStatement.close();
            connexion.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("Impossible de vérifier le LOGIN : " + e.getMessage() + ", cause : " + e.getCause());
        }
    }

    /**
     * DB tool that gets the login from a UID
     *
     * @param user_id
     * @return the login string
     * @throws DBException
     */
    public static String getLogin(int user_id) throws DBException {
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT login FROM users WHERE user_id = ?";
            PreparedStatement preparedStatement = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, user_id);
            ResultSet res = preparedStatement.executeQuery();
            res.next();
            String login = res.getString("login");
            res.close();
            preparedStatement.close();
            connexion.close();
            return login;
        } catch (SQLException e) {
            throw new DBException("getLogin : Impossible de vérifier l'identifiant : " + e.getMessage() + ", cause : " + e.getCause());
        }
    }




    /**
     * DB tool that lists all Users in the DB
     *
     * @return JSONObject containing all users
     * @throws JSONException
     * @throws DBException
     */
    public static JSONObject listUsers() throws JSONException, DBException {
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT * FROM users";
            PreparedStatement ps = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = ps.executeQuery();
            JSONObject users = new JSONObject();
            JSONArray tab = new JSONArray();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("nom", rs.getString("nom"));
                obj.put("prenom", rs.getString("prenom"));
                obj.put("login", rs.getString("login"));
                tab.put(obj);
            }
            users.put("users", tab);
            rs.close();
            ps.close();
            connexion.close();
            return users;
        } catch (SQLException e) {
            throw new DBException("Impossible d'obtenir l'ID : " + e.getMessage().toString());
        } catch (JSONException e) {
            throw new DBException("JSON : Impossible d'obtenir l'ID : " + e.getMessage().toString());
        }
    }

    public static JSONObject getInfo(String key, String login) throws DBException {
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT * FROM users WHERE login = ?";
            PreparedStatement preparedStatement = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, login);
            ResultSet res = preparedStatement.executeQuery();
            res.next();
            JSONObject u_info = new JSONObject();
            JSONObject obj = new JSONObject();
            u_info.put("nom", res.getString("nom"));
            u_info.put("prenom", res.getString("prenom"));
            u_info.put("login", res.getString("login"));
            u_info.put("_id", res.getString("user_id"));
            try {
                u_info.put("imgReference", res.getString("imgReference"));

            } catch (SQLException e) {
                u_info.put("imgReference", "");
            }

            obj.put("u_info", u_info);
            res.close();
            preparedStatement.close();
            connexion.close();
            return obj;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("UserTools.getInfo : " + e.getMessage() + ", cause : " + e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}
