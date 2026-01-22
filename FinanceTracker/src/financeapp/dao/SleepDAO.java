package financeapp.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import financeapp.models.SleepEntry;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class SleepDAO {

    private MongoCollection<Document> collection;

    public SleepDAO() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        this.collection = db.getCollection("sleep_entries");
    }

    public void addSleepEntry(SleepEntry entry) {
        Document doc = new Document()
                .append("userId", entry.getUserId())
                .append("hours", entry.getHours())
                .append("quality", entry.getQuality())
                .append("date", entry.getDate())
                .append("timestamp", entry.getTimestamp());

        collection.insertOne(doc);
        System.out.println("Sleep entry added: " + entry.getHours() + "h");
    }

    public void updateSleepEntry(String id, double hours, String quality) {
        collection.updateOne(
                eq("_id", new ObjectId(id)),
                combine(
                        set("hours", hours),
                        set("quality", quality)
                )
        );
        System.out.println("Sleep entry updated: " + id);
    }

    public void deleteSleepEntry(String id) {
        collection.deleteOne(eq("_id", new ObjectId(id)));
        System.out.println("Sleep entry deleted: " + id);
    }

    public ArrayList<SleepEntry> getSleepEntriesByUserId(String userId) {
        ArrayList<SleepEntry> list = new ArrayList<>();

        for (Document doc : collection.find(eq("userId", userId))) {
            SleepEntry entry = new SleepEntry();
            entry.setId(doc.getObjectId("_id"));
            entry.setUserId(doc.getString("userId"));
            entry.setHours(doc.getDouble("hours"));
            entry.setQuality(doc.getString("quality"));
            entry.setDate(doc.getString("date"));
            entry.setTimestamp(doc.getLong("timestamp"));

            list.add(entry);
        }

        return list;
    }

    public double getTodaySleepByUser(String userId) {
        String today = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        Document doc = collection.find(and(eq("userId", userId), eq("date", today))).first();

        if (doc != null) {
            return doc.getDouble("hours");
        }

        return 0.0;
    }

    public double getAverageSleepByUser(String userId, int days) {
        ArrayList<SleepEntry> entries = getSleepEntriesByUserId(userId);

        if (entries.isEmpty()) {
            return 0.0;
        }

        int count = Math.min(entries.size(), days);
        double total = 0.0;

        for (int i = entries.size() - count; i < entries.size(); i++) {
            total += entries.get(i).getHours();
        }

        return total / count;
    }

    public boolean hasTodayEntry(String userId) {
        String today = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        long count = collection.countDocuments(and(eq("userId", userId), eq("date", today)));
        return count > 0;
    }

    public int getTotalDaysTracked(String userId) {
        ArrayList<SleepEntry> entries = getSleepEntriesByUserId(userId);

        java.util.Set<String> uniqueDates = new java.util.HashSet<>();

        for (SleepEntry entry : entries) {
            uniqueDates.add(entry.getDate());
        }

        return uniqueDates.size();
    }
}
