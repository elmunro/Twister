package bdd;

/**
 * Our special exception fro handling errors in DB or Mongo
 */
public class DBException extends Exception{
    DBException(String string) {
        super(string);
    }
}
