package services;

import bdd.*;
import org.json.JSONObject;

public class FriendServices {

    /**
     * Adds someone to the user's friend list
     *
     * @param key           user connection key
     * @param valueFriendID friend login
     * @return ServiceAccepted if all succeeded, error code otherwise
     */
    public static JSONObject addFriend(String key, String valueFriendID) {
        try {
            boolean connected = ConnexionDB.isConnexion(key);
            if (!connected)
                return Tools.serviceRefused("Bad connexion key", -1);
            if (UserTools.userExists(valueFriendID))
                return Tools.serviceRefused("Invalid friend ID", -1);
            int from_id = ConnexionDB.getIdWithKey(key);
            int to_id = Integer.parseInt(valueFriendID);
            if (from_id == to_id)
                return Tools.serviceRefused("Can't friend yourself..", -1);

            //DB call
            FriendTools.follow(from_id, to_id);
            return Tools.serviceAccepted();
        } catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 1000);
        } catch (Exception e) {
            return Tools.serviceRefused(e.toString(), 10000);
        }
    }

    /**
     * Remove a friend from the user's friend list
     * @param key user's connection key
     * @param valueFriendID friend Login
     * @return ServiceAccepted if all succeeded, error code otherwise
     */
    public static JSONObject removeFriend(String key, String valueFriendID) {
        try {
            boolean connected = ConnexionDB.isConnexion(key);
            if (!connected)
                return Tools.serviceRefused("Bad connexion key", -1);
            if (UserTools.userExists(valueFriendID))
                return Tools.serviceRefused("Invalid friend ID", -1);
            int from_id = ConnexionDB.getIdWithKey(key);
            int to_id = Integer.parseInt(valueFriendID);
            System.out.println("Unfriending "+from_id+" and "+to_id);
            if (from_id == to_id)
                return Tools.serviceRefused("Can't unfriend yorself..", -1);
            FriendTools.unfollow(from_id, to_id);
            FriendTools.unfollow(to_id, from_id);
            return Tools.serviceAccepted();
        } catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 1000);
        } catch (Exception e) {
            return Tools.serviceRefused(e.toString(), 10000);
        }
    }

    /**
     * Lists the users' friends
     * @param key the users' connection key
     * @return A JSONObject with the list of users
     */
    public static JSONObject listFriends(String key, String login) {
        try {
            boolean connected = ConnexionDB.isConnexion(key);
            if (!connected)
                return Tools.serviceRefused("Bad connexion key", -1);
            int uid = ConnexionDB.getIdWithLogin(login);
            //DB call
            return FriendTools.listFriends(uid);
        } catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 1000);
        } catch (Exception e) {
            return Tools.serviceRefused(e.toString(), 10000);
        }
    }
}
