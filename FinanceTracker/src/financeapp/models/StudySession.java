package financeapp.models;

import org.bson.types.ObjectId;

public class StudySession {
    private ObjectId id;
    private String userId;
    private String subject;
    private double hours;
    private String notes;
    private String date;
    private long timestamp;

    public StudySession() {
        this.timestamp = System.currentTimeMillis();
    }

    public StudySession(String subject, double hours, String notes) {
        this();
        this.subject = subject;
        this.hours = hours;
        this.notes = notes;
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
