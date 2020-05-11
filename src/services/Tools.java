package services;

import bdd.DBException;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.GregorianCalendar;

public class Tools {

    private static int min = 100000;
    private static int max = 999999;

    public static JSONObject serviceRefused(String msg, int i) {
        // version argument manqg
        String error_type;
        switch (i) {
            case -1:
                error_type = "Client error";
                break;
            case 100:
                error_type = "JSON error";
                break;
            case 1000:
                error_type = "SQL error";
                break;
            case 10000:
                error_type = "Java error";
                break;
            default:
                error_type = "Unknown error code : " + i;
                break;
        }
        JSONObject Jerror = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            json.put("error_message", msg);
            json.put("error_type", error_type);
            Jerror.put("error", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Jerror;
    }

    public static JSONObject serviceAccepted() {
        JSONObject json = new JSONObject();
        try {
            json.put("output", "OK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    /**
     * Checks if key exists in Connection table and generates a key
     * @return new key
     */
    public static String genKey() throws DBException {
        String key = String.valueOf(min + (int) (Math.random() * ((max - min) + 1)));
        while (bdd.ConnexionDB.isConnexion(key))
            key = String.valueOf(min + (int) (Math.random() * ((max - min) + 1)));
        return key;
    }

    /**
     * Generates a new postID
     *
     * @return a new postID
     */
    public static int genPostId() throws UnknownHostException {
        int key = min + (int) (Math.random() * ((max - min) + 1));
        while (bdd.MessageTools.isPost(key))
            key = min + (int) (Math.random() * ((max - min) + 1));
        return key;
    }

    /**
     * Builds a Date object with current time in GregorianCalendar format
     *
     * @return a date
     */
    public static Date getDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        return calendar.getTime();
    }
}
