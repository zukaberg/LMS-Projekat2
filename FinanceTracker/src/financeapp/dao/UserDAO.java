package financeapp.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import financeapp.models.User;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class UserDAO {

    private MongoCollection<Document> collection;

    public UserDAO() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        this.collection = db.getCollection("users");
    }

    public boolean register(String username, String email, String password) {

        Document existing = collection.find(
                or(eq("username", username), eq("email", email))
        ).first();

        if (existing != null) {
            System.out.println("User već postoji!");
            return false;
        }


        Document userDoc = new Document()
                .append("username", username)
                .append("email", email)
                .append("password", password);

        collection.insertOne(userDoc);
        System.out.println("User registrovan: " + username);
        return true;
    }

    public User login(String usernameOrEmail, String password) {

        Document userDoc = collection.find(
                or(eq("username", usernameOrEmail), eq("email", usernameOrEmail))
        ).first();

        if (userDoc == null) {
            System.out.println("User ne postoji!");
            return null;
        }

        String storedPassword = userDoc.getString("password");
        if (!password.equals(storedPassword)) {
            System.out.println("Pogrešna lozinka!");
            return null;
        }

            User user = new User();
        user.setId(userDoc.getObjectId("_id"));
        user.setUsername(userDoc.getString("username"));
        user.setEmail(userDoc.getString("email"));
        user.setPassword(storedPassword);
        user.setProfileImagePath(userDoc.getString("profileImagePath"));

        System.out.println("Login uspješan: " + user.getUsername());
        return user;
    }

    public void updateUser(User user) {
        collection.updateOne(
                eq("_id", user.getId()),
                combine(
                        set("username", user.getUsername()),
                        set("email", user.getEmail()),
                        set("password", user.getPassword()),
                        set("profileImagePath", user.getProfileImagePath())
                )
        );
        System.out.println("User updated: " + user.getUsername());
    }
}
