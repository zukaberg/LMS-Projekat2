package financeapp.models;

import org.bson.types.ObjectId;

public class SleepEntry {
    private ObjectId id;
    private String userId;
    private double hours;
    private String quality;
    private long timestamp;
    private String date;

    public SleepEntry() {
        this.timestamp = System.currentTimeMillis();
    }

    public SleepEntry(double hours, String quality) {
        this();
        this.hours = hours;
        this.quality = quality;
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }

    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
