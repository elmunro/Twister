package bdd;

import services.Tools;

import java.sql.*;

public class ConnexionDB {

    /**
     * IMPORTANT : must be called every time the connexion table is queryed.
     * Checks for old connections and deletes them if older than 30min
     *
     * @throws DBException
     */
    public static void connexionUpdate() throws DBException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() - 1800000);
        //30 * 60 * 1000 = 1800000ms  -> 30min
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "DELETE FROM connection where timestamp < ?";
            PreparedStatement ps = connexion.prepareStatement(sql, ResultSet.CONCUR_UPDATABLE);
            ps.setTimestamp(1, timestamp);
            ps.executeUpdate();
            ps.close();
            connexion.close();
        } catch (SQLException e) {
            throw new DBException("Problème à la MaJ des connections " + e.getMessage().toString());
        }
    }

    /**
     * Inserts a new connexion into the DB
     * @param user_id
     * @return a freshly created connexion key
     * @throws DBException
     */
    public static String insertConnexion(int user_id) throws DBException {
        String key;
        boolean root = false;
        connexionUpdate();
        //if a key already exists, we return it
        key = getConnectionKeyIfExists(user_id);
        if (key != null)
            return key;
        else
            key = Tools.genKey();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "INSERT into connection values(?, ?, ?, ?)";
            PreparedStatement ps = connexion.prepareStatement(sql, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, key);
            ps.setInt(2, user_id);
            ps.setTimestamp(3, timestamp);
            ps.setBoolean(4, root);
            int res = ps.executeUpdate();
            ps.close();
            connexion.close();
        } catch (SQLException e) {
            throw new DBException("Impossible d'inserer la connection : " + e.getMessage().toString());
        }
        return key;
    }

    /**
     * Queries the DB to check if key exists and is valid
     * @param key
     * @return true if connection exists
     * @throws DBException
     */
    public static boolean isConnexion(String key) throws DBException {
        connexionUpdate();
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT * FROM connection WHERE connectionKey=?";
            PreparedStatement ps = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, key);
            ResultSet res = ps.executeQuery();
            if (res.next())
                return true;
            res.close();
            ps.close();
            connexion.close();
        } catch (SQLException e) {
            throw new DBException("Impossible de verifier la connection : " + e.getMessage().toString());
        }
        return false;
    }


    /**
     * Gets the connection key if it exists, none if not
     * @param user_id
     * @return the connection key, or an empty string if none is found
     * @throws DBException
     */
    public static String getConnectionKeyIfExists(int user_id) throws DBException {
        connexionUpdate();
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT connectionKey FROM connection WHERE id=?";
            PreparedStatement ps = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, user_id);
            ResultSet res = ps.executeQuery();
            String key = null;
            if (res.next())
                key = res.getString("connectionKey");
            res.close();
            ps.close();
            connexion.close();
            return key;
        } catch (SQLException e) {
            return "";
        }
    }

    /**
     * Remove connection from DB
     * @param key connectionKey id
     * @throws DBException
     */
    public static void removeConnexion(String key) throws DBException {
        connexionUpdate();
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "DELETE FROM connection WHERE connectionKey=?";
            PreparedStatement ps = connexion.prepareStatement(sql, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, key);
            int deleted = ps.executeUpdate();
            ps.close();
            connexion.close();
        } catch (SQLException e) {
            throw new DBException("Impossible de supprimer la connection : " + e.getMessage().toString());
        }
    }

    /**
     * DB tool that gets the ID of a User with it's connection key
     *
     * @param key
     * @return
     * @throws DBException
     */
    public static int getIdWithKey(String key) throws DBException {
        int user_id = -1;
        connexionUpdate();
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT * FROM connection WHERE connectionKey=?";
            PreparedStatement ps = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, key);
            ResultSet res = ps.executeQuery();
            res.next();
            user_id = res.getInt("id");
            res.close();
            ps.close();
            connexion.close();
        } catch (SQLException e) {
            throw new DBException("getIDWithKey : Impossible d'obtenir l'ID : " + e.getMessage().toString());
        }
        return user_id;
    }

    public static int getIdWithLogin(String login) throws DBException {
        int user_id = -1;
        connexionUpdate();
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT * FROM users WHERE login=?";
            PreparedStatement ps = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, login);
            ResultSet res = ps.executeQuery();
            res.next();
            user_id = res.getInt("user_id");
            res.close();
            ps.close();
            connexion.close();
        } catch (SQLException e) {
            throw new DBException("getIDwithLogin Impossible d'obtenir l'ID : " + e.getMessage().toString());
        }
        return user_id;
    }
}
