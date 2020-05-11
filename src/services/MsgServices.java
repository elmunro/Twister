package services;

import bdd.*;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

public class MsgServices {

    /**
     * Service that adds a comment to an existing post
     *
     * @param key                connection key
     * @param source_post_id_str the id of the existing post
     * @param text               the comment text
     * @return ServiceAccepted if all succeeded, error code otherwise
     */
    public static JSONObject addComment(String key, String source_post_id_str, String text) {
        try {
            boolean connected = ConnexionDB.isConnexion(key);
            if (!connected)
                return Tools.serviceRefused("Bad connexion key", -1);
            int user_id = ConnexionDB.getIdWithKey(key);
            String login = UserTools.getLogin(user_id);
            int source_post_id = Integer.parseInt(source_post_id_str);

            DBCollection messages = bdd.MessageTools.getMessagesCollection();
            BasicDBObject comment = bdd.MessageTools.buildPost(login, user_id, text, source_post_id);

            //create the post
            WriteResult res = messages.insert(comment);

            //add new comment ID to post
            int commentID = (int) comment.get("_id");

            BasicDBObject commID = new BasicDBObject();
            commID.put("comments", commentID);

            //get the source post, add new comment id to comments array, and update
            BasicDBObject source_post = new BasicDBObject();
            source_post.put("_id", source_post_id);
            BasicDBObject add = new BasicDBObject();
            add.put("$addToSet", commID);

            WriteResult res2 = messages.update(source_post, add);
            return Tools.serviceAccepted();
        } catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 1000);
        } catch (Exception e) {
            return Tools.serviceRefused(e.toString(), 10000);
        }
    }

    /**
     * Service that creates a new post for user corresponding to key
     *
     * @param key  user's connection key
     * @param text post content
     * @return ServiceAccepted if all succeeded, error code otherwise
     */
    public static JSONObject newPost(String key, String text) {
        try {
            boolean connected = ConnexionDB.isConnexion(key);
            if (!connected)
                return Tools.serviceRefused("Bad connexion key", -1);
            int user_id = ConnexionDB.getIdWithKey(key);
            String login = UserTools.getLogin(user_id);

            DBCollection messages = MessageTools.getMessagesCollection();

            BasicDBObject post = bdd.MessageTools.buildPost(login, user_id, text, -1);
            WriteResult res = messages.insert(post);
            return Tools.serviceAccepted();
        } catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 1000);
        } catch (Exception e) {
            return Tools.serviceRefused(e.toString(), 10000);
        }
    }

    /**
     * Page principale
     * Le service renvoie les messages de tout le monde par ordre
     * chronologique inverse (du plus recent au plus vieux)
     *
     * Page profil
     * Le service renvoie les messages de l'utilisateur par ordre
     * chronologique inverse (du plus recent au plus vieux)
     *
     * Si une requ^ete est spécifiée
     * L'ordre de retour des messages n'est plus l'ordre chronologique,
     * mais l'ordre de pertinence : un score calculée entre la requete et
     * chaque message
     * @param key
     * @param query
     * @param profileID
     * @return
     */
    public static JSONObject search(String key, String query, String profileID) {
        try {
            if(key == null || query == null || profileID==null){
                System.out.println("Manque un arg "+key+" "+query+" "+profileID);
                return Tools.serviceRefused("Manque un arg "+key+" "+query+" "+profileID, -1);

            }
            boolean connected = ConnexionDB.isConnexion(key);
            if (!connected)
                return Tools.serviceRefused("Bad connexion key", -1);
            if(query.trim() == "" && profileID.equals("-11111")){
                return listLatestMessages(key, "0", "250");
            }else if(query.trim() == "" && !profileID.equals("-11111")){
                System.out.println("Search : returning FriendMessage with FID "+profileID);
                return FriendMessages(key, UserTools.getLogin(Integer.parseInt(profileID)));
            }
            //TODO finir la partie Map Reduce

            int user_id = ConnexionDB.getIdWithKey(key);

            DBCollection messages = bdd.MessageTools.getMessagesCollection();
            String map = "function wordMap() {\n" +
                    "    //to lower\n" +
                    "    var lower = this.text.toLowerCase();\n" +
                    "    //get rid of symbols\n" +
                    "    lower = lower.replace(/[^A-Z0-9 ]/ig, \"\");\n" +
                    "    var words = lower.match(/\\w+/g);\n" +
                    "    if (words === null) {\n" +
                    "        return;\n" +
                    "    }\n" +
                    "    for (var i = 0; i < words.length; i++) {\n" +
                    "        //emit with count of one, and source id\n" +
                    "        emit(words[i], { count : 1 , source : this._id});\n" +
                    "    }\n" +
                    "}";

            String red = "function wordReduce(key, values) {\n" +
                    "    var total = 0;\n" +
                    "    var sources = {};\n" +
                    "    for (var i = 0; i < values.length; i++) {\n" +
                    "        total += values[i].count;\n" +
                    "        if(sources[values[i].source] === undefined){\n" +
                    "            sources[values[i].source] = 1;\n" +
                    "        }else{\n" +
                    "            sources[values[i].source]+=1;\n" +
                    "        }\n" +
                    "    }\n" +
                    "    return { count : total, 'sources' : sources };\n" +
                    "}";

            MapReduceOutput out = messages.mapReduce(map, red, "MRoutput", MapReduceCommand.OutputType.REPLACE, null);
            BasicDBList list = new BasicDBList();
            for (DBObject obj : out.results()) {
                System.out.println(obj);
                list.add(obj);
            }

            JSONObject searchResult = new JSONObject();
            searchResult.put("searchResult" , list);

            //build the query with sorting by date
            return searchResult;

        } catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 1000);
        } catch (Exception e) {
            return Tools.serviceRefused(e.toString(), 10000);
        }
    }


    /**
     * Service that lists all messages for active user
     *
     * @param key connKey
     * @return list of messages
     */
    public static JSONObject listUserMessages(String key) {
        try {
            boolean connected = ConnexionDB.isConnexion(key);
            if (!connected)
                return Tools.serviceRefused("Bad connexion key", -1);
            int user_id = ConnexionDB.getIdWithKey(key);

            DBCollection messages = bdd.MessageTools.getMessagesCollection();

            //build the query
            BasicDBObject query = new BasicDBObject();
            query.put("author.user_id", user_id);

            //build the query with sorting by date
            BasicDBObject sortby = new BasicDBObject();
            sortby.put("date", -1);

            DBCursor cursor = messages.find(query).sort(sortby);

            JSONObject msgList = new JSONObject();
            JSONArray arr = new JSONArray();
            while (cursor.hasNext()) {
                JSONObject obj = new JSONObject(JSON.serialize(cursor.next()));
                arr.put(obj);
            }
            msgList.put("messages", arr);
            return msgList;
        } catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 1000);
        } catch (Exception e) {
            return Tools.serviceRefused(e.toString(), 10000);
        }
    }

    /**
     * Service that lists latest messages from user's friends
     *
     * @param key connKey
     * @return list of messages
     */
    public static JSONObject listLatestMessages(String key, String startStr, String stopStr) {
        try {
            boolean connected = ConnexionDB.isConnexion(key);
            if (!connected)
                return Tools.serviceRefused("Bad connexion key", -1);

            int uid = ConnexionDB.getIdWithKey(key);
            int start = 0;
            int stop = 15;
            try {
                start = Integer.parseInt(startStr);
                stop = Integer.parseInt(stopStr);
            } catch (Exception nb) {
                start = 0;
                stop = 15;
            }
            DBCollection messages = bdd.MessageTools.getMessagesCollection();
            //build the query with only friends
            JSONArray friends = FriendTools.listFriendIds(uid);
            BasicDBList loginList = new BasicDBList();
            for (int i = 0; i < friends.length(); i++) {
                JSONObject friend = (JSONObject) friends.get(i);
                loginList.add(friend.getInt("user_id"));
            }
            loginList.add(uid);

            BasicDBObject in = new BasicDBObject();
            in.put("$in", loginList);
            BasicDBObject query = new BasicDBObject();
            query.put("author.user_id", in);

            //build the query with sorting by date
            BasicDBObject sortby = new BasicDBObject();
            sortby.put("date", -1);

            DBCursor cursor = messages.find(query).sort(sortby).skip(start).limit(stop);
            JSONObject msgList = new JSONObject();
            JSONArray arr = new JSONArray();
            while (cursor.hasNext()) {
                JSONObject obj = new JSONObject(JSON.serialize(cursor.next()));
                arr.put(obj);
            }
            msgList.put("messages", arr);
            return msgList;
        } catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 1000);
        } catch (Exception e) {
            return Tools.serviceRefused(e.toString(), 10000);
        }
    }

    /**
     * Service that lists latest messages for a specied users
     *
     * @param key connKey
     * @param login specified user login
     * @return list of messages
     */
    public static JSONObject FriendMessages(String key, String login) {
        try {
            boolean connected = ConnexionDB.isConnexion(key);
            if (!connected)
                return Tools.serviceRefused("Bad connexion key", -1);
            int user_id = ConnexionDB.getIdWithKey(key);
            int FID = UserTools.getUserId(login);
            DBCollection messages = bdd.MessageTools.getMessagesCollection();

            //build the query
            BasicDBObject query = new BasicDBObject();
            query.put("author.user_id", FID);

            //build the query with sorting by date
            BasicDBObject sortby = new BasicDBObject();
            sortby.put("date", -1);

            DBCursor cursor = messages.find(query).sort(sortby);

            JSONObject msgList = new JSONObject();
            JSONArray arr = new JSONArray();
            while (cursor.hasNext()) {
                JSONObject obj = new JSONObject(JSON.serialize(cursor.next()));
                arr.put(obj);
            }
            msgList.put("messages", arr);
            return msgList;
        } catch (DBException e) {
            return Tools.serviceRefused(e.toString(), 1000);
        } catch (Exception e) {
            return Tools.serviceRefused(e.toString(), 10000);
        }
    }
}
