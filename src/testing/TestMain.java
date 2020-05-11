package testing;

import bdd.ConnexionDB;
import bdd.FriendTools;
import org.json.JSONException;
import org.json.JSONObject;
import services.MsgServices;

import java.sql.Connection;
import java.sql.SQLException;

public class TestMain {

    public static void main(String[] args) throws SQLException {
        Connection db = null;

        try {
            //System.out.println("Populating DB :");
            //populate(10, 1, 543394);
            JSONObject loginTest = services.UserServices.login("toto", "toto");
            String key = loginTest.getString("key");
            int uid = ConnexionDB.getIdWithKey(key);
            System.out.println(FriendTools.listFriendIds(uid));
            System.out.println(MsgServices.listLatestMessages(key, "0", "12"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
    }

    private static void populate(int N, int id_user, int id_post) throws JSONException {
        for (int i = 0; i < N; i++) {
            int unique_id = i;
            JSONObject user = services.UserServices.createUser("Hack", "MePlz", "hackme@please" + unique_id, "mdp");
            System.out.println("User create res : " + user);
            JSONObject loginTest = services.UserServices.login("hackme@please" + unique_id, "mdp");
 
            System.out.println("Login res : " + loginTest);
            String key = loginTest.getString("key");

            JSONObject friendAdd = services.FriendServices.addFriend(key, String.valueOf(id_user)); //id d'un user déjà pésent

            System.out.println("Adding friend :" + friendAdd);

            System.out.println("\nDebuts tests MongoDB");

            System.out.println("New Post : " + MsgServices.newPost(key, "This is a message"));


            System.out.println("Ajout de commentaire : " + MsgServices.addComment(key,
                    String.valueOf(id_post), "Commentaire : et moi je suis " + i + "ème !"));


            System.out.println("\nLogout test :  (2 fois)");

            JSONObject logoutTest = services.UserServices.logout(key);
            System.out.println(logoutTest);

            logoutTest = services.UserServices.logout(key);
            System.out.println("Erreur attendue :" + logoutTest);
        }
    }
}
