package financeapp.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import financeapp.models.StudySession;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class StudySessionDAO {

    private MongoCollection<Document> collection;

    public StudySessionDAO() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        this.collection = db.getCollection("study_sessions");
    }

    public void addStudySession(StudySession session) {
        Document doc = new Document()
                .append("userId", session.getUserId())
                .append("subject", session.getSubject())
                .append("hours", session.getHours())
                .append("notes", session.getNotes())
                .append("date", session.getDate())
                .append("timestamp", session.getTimestamp());

        collection.insertOne(doc);
        System.out.println("Study session added: " + session.getSubject());
    }

    public void updateStudySession(String id, String subject, double hours, String notes) {
        collection.updateOne(
                eq("_id", new ObjectId(id)),
                combine(
                        set("subject", subject),
                        set("hours", hours),
                        set("notes", notes)
                )
        );
        System.out.println("Study session updated: " + id);
    }

    public void deleteStudySession(String id) {
        collection.deleteOne(eq("_id", new ObjectId(id)));
        System.out.println("Study session deleted: " + id);
    }

    public ArrayList<StudySession> getStudyEntriesByUserId(String userId) {
        ArrayList<StudySession> list = new ArrayList<>();

        for (Document doc : collection.find(eq("userId", userId))) {
            StudySession session = new StudySession();
            session.setId(doc.getObjectId("_id"));
            session.setUserId(doc.getString("userId"));
            session.setSubject(doc.getString("subject"));
            session.setHours(doc.getDouble("hours"));
            session.setNotes(doc.getString("notes"));
            session.setDate(doc.getString("date"));
            session.setTimestamp(doc.getLong("timestamp"));

            list.add(session);
        }

        return list;
    }

    public double getTodayStudyHours(String userId) {
        String today = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        Document doc = collection.find(and(eq("userId", userId), eq("date", today))).first();
        return doc != null ? doc.getDouble("hours") : 0.0;
    }

    public double getAverageStudyHours(String userId, int days) {
        ArrayList<StudySession> sessions = getStudyEntriesByUserId(userId);
        if (sessions.isEmpty()) return 0.0;

        int count = Math.min(sessions.size(), days);
        double total = 0.0;

        for (int i = sessions.size() - count; i < sessions.size(); i++) {
            total += sessions.get(i).getHours();
        }

        return total / count;
    }

    public int getTotalDaysStudied(String userId) {
        ArrayList<StudySession> sessions = getStudyEntriesByUserId(userId);

        // Uzmi samo JEDINSTVENE datume
        java.util.Set<String> uniqueDates = new java.util.HashSet<>();

        for (StudySession session : sessions) {
            uniqueDates.add(session.getDate());
        }

        return uniqueDates.size();  // Broj različitih dana
    }
}
