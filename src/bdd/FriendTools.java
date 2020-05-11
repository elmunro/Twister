package bdd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class FriendTools {

    /**
     * DB tool that adds a friend listing in DB
     *
     * @param from_id User that makes the request
     * @param to_id   User that is beeing friended
     * @throws DBException
     */
    public static void follow(int from_id, int to_id) throws DBException {
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "INSERT into friends values(?, ?, ?)";
            PreparedStatement ps = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, from_id);
            ps.setInt(2, to_id);
            Timestamp tmp = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(3, tmp);
            int created = ps.executeUpdate();
            ps.close();
            connexion.close();
        } catch (SQLException e) {
            throw new DBException("Impossible d'obtenir l'ID : " + e.getMessage().toString());
        }
    }

    /**
     * DB tool that deletes a friend listing in DB
     * @param from_id User that makes the request
     * @param to_id User that is beeing unfriended
     * @throws DBException
     */
    public static void unfollow(int from_id, int to_id) throws DBException {
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "DELETE FROM friends WHERE from_id=? and to_id=?";
            PreparedStatement ps = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, from_id);
            ps.setInt(2, to_id);
            int deleted = ps.executeUpdate();
            ps.close();
            connexion.close();
        } catch (SQLException e) {
            throw new DBException("Impossible d'unfollow : " + e.getMessage().toString());
        }
    }

    /**
     * DB tool that lists a user's friends
     * @param uid User ID
     * @return a JSONObject containing all the user's friends, or an error message
     * @throws DBException
     * @throws JSONException
     */
    public static JSONObject listFriends(int uid) throws DBException, JSONException {
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT * FROM users u, friends f WHERE (f.from_id=? AND f.to_id=u.user_id) " +
                    "OR (f.to_id=? AND f.from_id=u.user_id)";
            PreparedStatement ps = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, uid);
            ps.setInt(2, uid);
            ResultSet rs = ps.executeQuery();
            JSONObject users = new JSONObject();
            JSONArray tab = new JSONArray();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("nom", rs.getString("nom"));
                obj.put("prenom", rs.getString("prenom"));
                obj.put("login", rs.getString("login"));
                obj.put("_id", rs.getString("user_id"));
                try {
                    obj.put("imgReference", rs.getString("imgReference"));

                } catch (SQLException e) {
                    obj.put("imgReference", "");
                }
                tab.put(obj);
            }
            users.put("users", tab);
            rs.close();
            ps.close();
            connexion.close();
            return users;
        } catch (SQLException e) {
            throw new DBException("Impossible d'obtenir l'ID : " + e.getMessage().toString());
        }
    }

    /**
     * DB tool that lists a user's friends
     * @param uid User ID
     * @return a JSONObject containing all the user's friends, or an error message
     * @throws DBException
     * @throws JSONException
     */
    public static JSONArray listFriendIds(int uid) throws DBException, JSONException {
        try {
            Connection connexion = Database.getMySQLConnection();
            String sql = "SELECT * FROM users u, friends f WHERE (f.from_id=? AND f.to_id=u.user_id) " +
                    "OR (f.to_id=? AND f.from_id=u.user_id)";
            PreparedStatement ps = connexion.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, uid);
            ps.setInt(2, uid);
            ResultSet rs = ps.executeQuery();
            JSONArray tab = new JSONArray();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("user_id", rs.getString("user_id"));
                tab.put(obj);
            }
            rs.close();
            ps.close();
            connexion.close();
            return tab;
        } catch (SQLException e) {
            throw new DBException("Impossible d'obtenir les ID des friends : " + e.getMessage().toString());
        }
    }
}
