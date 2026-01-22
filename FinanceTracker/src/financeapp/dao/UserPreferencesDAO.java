package financeapp.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import financeapp.models.UserPreferences;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class UserPreferencesDAO {

    private MongoCollection<Document> collection;

    public UserPreferencesDAO() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        this.collection = db.getCollection("user_preferences");
    }

    public UserPreferences getPreferences(String userId) {
        Document doc = collection.find(eq("userId", userId)).first();

        if (doc != null) {
            UserPreferences prefs = new UserPreferences();
            prefs.setId(doc.getObjectId("_id"));
            prefs.setUserId(doc.getString("userId"));
            prefs.setDarkMode(doc.getBoolean("darkMode", false));
            return prefs;
        }

        UserPreferences prefs = new UserPreferences(userId, false);
        savePreferences(prefs);
        return prefs;
    }

    public void savePreferences(UserPreferences prefs) {
        Document existing = collection.find(eq("userId", prefs.getUserId())).first();

        if (existing != null) {
            Document update = new Document("$set", new Document("darkMode", prefs.isDarkMode()));
            collection.updateOne(eq("userId", prefs.getUserId()), update);
            System.out.println("Preferences updated for user: " + prefs.getUserId());
        } else {
            Document doc = new Document()
                    .append("userId", prefs.getUserId())
                    .append("darkMode", prefs.isDarkMode());
            collection.insertOne(doc);
            System.out.println("Preferences created for user: " + prefs.getUserId());
        }
    }

    public void toggleDarkMode(String userId) {
        UserPreferences prefs = getPreferences(userId);
        prefs.setDarkMode(!prefs.isDarkMode());
        savePreferences(prefs);
    }
}
