package services;

import org.json.JSONException;
import org.json.JSONObject;

import bdd.*;

public class UserServices {

    /**
     * Service that logs a user in
     *
     * @param login the unique login
     * @param pswd  the user's password
     * @return ServiceAccepted or error code
     */
    public static JSONObject login(String login, String pswd) {
        if (login == null || pswd == null) {
            return Tools.serviceRefused("Bad credentials", -1);
        }
        try {
            boolean user_exists = UserTools.userExists(login);
            if (!user_exists)
                return Tools.serviceRefused("Bad credentials", -1);
            boolean pswd_ok = UserTools.passwordCheck(login, pswd);
            if (!pswd_ok)
                return Tools.serviceRefused("Bad credentials", -1);
            int user_id = UserTools.getUserId(login);
            String key = ConnexionDB.insertConnexion(user_id);
            JSONObject json = new JSONObject();
            json.put("id", user_id);
            json.put("login", login);
            json.put("key", key);
            return json;
        } catch (JSONException j_err) {
            return Tools.serviceRefused(j_err.toString(), 100);
        }catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 1000);
        } catch (Exception e) {
            return Tools.serviceRefused(e.toString(), 10000);
        }
    }

    /**
     * Service that logs a user out
     * @param key key to delete from DB
     * @return ServiceAccepted or error code
     */
    public static JSONObject logout(String key) {
        if (key == null)
            return Tools.serviceRefused("No key", 1);
        try {
            if (!ConnexionDB.isConnexion(key))
                return Tools.serviceRefused("Unknown key", -1);
            ConnexionDB.removeConnexion(key);
            return Tools.serviceAccepted();
        } catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 1000);
        }
    }

    /**
     * Service for creating a new user if not exists
     * @param nom
     * @param prenom
     * @param login
     * @param pswd
     * @return ServiceAccepted or error code
     */
    public static JSONObject createUser(String nom, String prenom, String login, String pswd) {
        if (login == null || pswd == null || prenom == null || nom == null) {
            return Tools.serviceRefused("Manque une valeur", -1);
        }
        try {
            boolean user_exists = UserTools.userExists(login);
            if (user_exists)
                return Tools.serviceRefused("User already exists", -1);
            UserTools.createUser(nom, prenom, login, pswd);
            return Tools.serviceAccepted();
        }catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 10000);
        }
    }

    /**
     * Service that lists all users, this method is accesible to all
     *
     * @return JSONObject containing all users
     */
    public static JSONObject listUsers() {
        try {
            return UserTools.listUsers();
        } catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 10000);
        } catch (JSONException e) {
            return Tools.serviceRefused(e.toString(), 100);
        }
    }


    public static JSONObject getInfo(String key, String login) {
        if (login == null || key == null) {
            return Tools.serviceRefused("Manque une valeur", -1);
        }
        try {
            boolean connected = ConnexionDB.isConnexion(key);
            if (!connected)
                return Tools.serviceRefused("Bad connexion key", -1);
            return UserTools.getInfo(key, login);
        }catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 10000);
        }
    }
}
