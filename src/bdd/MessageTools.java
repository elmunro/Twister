package bdd;

import com.mongodb.*;
import services.Tools;

import java.net.UnknownHostException;

public class MessageTools {

    /**
     * Gets and returns MongoDB Messages connection
     *
     * @return MongoDB Messages connection
     * @throws UnknownHostException
     */
    public static DBCollection getMessagesCollection() throws UnknownHostException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("TwistaMongoDB");
        return db.getCollection("Messages");
    }

    /**
     * Checks if a post exists
     *
     * @param ID post ID to check
     * @return true if exists
     * @throws UnknownHostException
     */
    public static boolean isPost(int ID) throws UnknownHostException {
        DBCollection msg = getMessagesCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("_id", ID);
        int nbRes = msg.find(query).count();
        if (nbRes > 1)
            return true;
        return false;
    }

    /**
     * Builds a BasicDBObject for a user message
     *
     * @param login
     * @param user_id
     * @param text
     * @return the BasicDBobject
     * @throws UnknownHostException
     */
    public static BasicDBObject buildPost(String login, int user_id, String text, int source_post_id) throws UnknownHostException {
        //build the author object
        BasicDBObject author = new BasicDBObject();
        author.put("user_id", user_id);
        author.put("login", login);
        int _id = Tools.genPostId();
        //System.out.println("Gen : " + _id);
        //build the post to be added
        BasicDBObject post = new BasicDBObject();
        post.put("_id", _id);
        post.put("author", author);
        post.put("date", Tools.getDate());
        post.put("text", text);
        post.put("comments", new BasicDBList());
        if (source_post_id != -1)
            post.put("source_post_id", source_post_id);
        return post;
    }
}
