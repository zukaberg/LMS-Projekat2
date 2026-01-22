package financeapp.models;

import org.bson.types.ObjectId;

public class Transaction {
    private ObjectId id;
    private String userId;
    private String type;
    private double amount;
    private String description;
    private long timestamp;

    public Transaction() {
        this.timestamp = System.currentTimeMillis();
    }

    public Transaction(String type, double amount, String description) {
        this();
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
