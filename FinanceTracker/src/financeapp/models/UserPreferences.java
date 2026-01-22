package financeapp.models;

import org.bson.types.ObjectId;

public class UserPreferences {
    private ObjectId id;
    private String userId;
    private boolean darkMode;

    public UserPreferences() {
        this.darkMode = false;
    }

    public UserPreferences(String userId, boolean darkMode) {
        this.userId = userId;
        this.darkMode = darkMode;
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public boolean isDarkMode() { return darkMode; }
    public void setDarkMode(boolean darkMode) { this.darkMode = darkMode; }
}
