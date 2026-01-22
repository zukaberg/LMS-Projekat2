package financeapp.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import financeapp.models.Transaction;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class TransactionDAO {

    private MongoCollection<Document> collection;

    public TransactionDAO() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        this.collection = db.getCollection("transactions");
    }

    public void addTransaction(Transaction transaction) {
        Document doc = new Document()
                .append("userId", transaction.getUserId())
                .append("type", transaction.getType())
                .append("amount", transaction.getAmount())
                .append("description", transaction.getDescription())
                .append("timestamp", transaction.getTimestamp());

        collection.insertOne(doc);
        System.out.println("Transaction added: " + transaction.getType() + " - " + transaction.getAmount());
    }

    public void updateTransaction(String id, String type, String category, double amount, String description) {
        collection.updateOne(
                eq("_id", new ObjectId(id)),
                combine(
                        set("type", type),
                        set("amount", amount),
                        set("description", description)
                )
        );
        System.out.println("Transaction updated: " + id);
    }

    public void deleteTransaction(String id) {
        collection.deleteOne(eq("_id", new ObjectId(id)));
        System.out.println("Transaction deleted: " + id);
    }

    public ArrayList<Transaction> getTransactionsByUserId(String userId) {
        ArrayList<Transaction> list = new ArrayList<>();

        for (Document doc : collection.find(eq("userId", userId))) {
            Transaction transaction = new Transaction();
            transaction.setId(doc.getObjectId("_id"));
            transaction.setUserId(doc.getString("userId"));
            transaction.setType(doc.getString("type"));
            transaction.setAmount(doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0);
            transaction.setDescription(doc.getString("description"));
            transaction.setTimestamp(doc.getLong("timestamp") != null ? doc.getLong("timestamp") : 0L);

            list.add(transaction);
        }

        return list;
    }

    public double getTotalIncomeByUser(String userId) {
        double total = 0.0;
        for (Document doc : collection.find(eq("userId", userId))) {
            String type = doc.getString("type");
            if (type != null && type.equals("Income")) {
                Double amount = doc.getDouble("amount");
                if (amount != null) {
                    total += amount;
                }
            }
        }
        return total;
    }

    public double getTotalExpenseByUser(String userId) {
        double total = 0.0;
        for (Document doc : collection.find(eq("userId", userId))) {
            String type = doc.getString("type");
            if (type != null && type.equals("Expense")) {
                Double amount = doc.getDouble("amount");
                if (amount != null) {
                    total += amount;
                }
            }
        }
        return total;
    }

    public double getBalance(String userId) {
        return getTotalIncomeByUser(userId) - getTotalExpenseByUser(userId);
    }
}
