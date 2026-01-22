package financeapp.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import financeapp.models.Meal;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class MealDAO {

    private MongoCollection<Document> collection;

    public MealDAO() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        this.collection = db.getCollection("meals");
    }

    public void addMeal(Meal meal) {
        Document doc = new Document()
                .append("userId", meal.getUserId())
                .append("mealType", meal.getMealType())
                .append("name", meal.getName())
                .append("calories", meal.getCalories())
                .append("protein", meal.getProtein())
                .append("carbs", meal.getCarbs())
                .append("fats", meal.getFats())
                .append("date", meal.getDate())
                .append("timestamp", meal.getTimestamp());

        collection.insertOne(doc);
        System.out.println("Meal added: " + meal.getName());
    }

    public void updateMeal(String id, String mealType, String name, int calories, double protein, double carbs, double fats) {
        collection.updateOne(
                eq("_id", new ObjectId(id)),
                combine(
                        set("mealType", mealType),
                        set("name", name),
                        set("calories", calories),
                        set("protein", protein),
                        set("carbs", carbs),
                        set("fats", fats)
                )
        );
        System.out.println("Meal updated: " + id);
    }

    public void deleteMeal(String id) {
        collection.deleteOne(eq("_id", new ObjectId(id)));
        System.out.println("Meal deleted: " + id);
    }

    public ArrayList<Meal> getMealsByUserId(String userId) {
        ArrayList<Meal> list = new ArrayList<>();

        for (Document doc : collection.find(eq("userId", userId))) {
            Meal meal = new Meal();
            meal.setId(doc.getObjectId("_id"));
            meal.setUserId(doc.getString("userId"));
            meal.setMealType(doc.getString("mealType") != null ? doc.getString("mealType") : "Obrok");
            meal.setName(doc.getString("name"));
            meal.setCalories(doc.getInteger("calories") != null ? doc.getInteger("calories") : 0);
            meal.setProtein(doc.getDouble("protein") != null ? doc.getDouble("protein") : 0.0);
            meal.setCarbs(doc.getDouble("carbs") != null ? doc.getDouble("carbs") : 0.0);
            meal.setFats(doc.getDouble("fats") != null ? doc.getDouble("fats") : 0.0);
            meal.setDate(doc.getString("date"));
            meal.setTimestamp(doc.getLong("timestamp") != null ? doc.getLong("timestamp") : 0L);

            list.add(meal);
        }

        return list;
    }

    public int getTodayCalories(String userId) {
        String today = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        int total = 0;

        for (Document doc : collection.find(and(eq("userId", userId), eq("date", today)))) {
            Integer calories = doc.getInteger("calories");
            if (calories != null) {
                total += calories;
            }
        }

        return total;
    }

    public double getTodayProtein(String userId) {
        String today = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        double total = 0.0;

        for (Document doc : collection.find(and(eq("userId", userId), eq("date", today)))) {
            Double protein = doc.getDouble("protein");
            if (protein != null) {
                total += protein;
            }
        }

        return total;
    }

    public double getTodayCarbs(String userId) {
        String today = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        double total = 0.0;

        for (Document doc : collection.find(and(eq("userId", userId), eq("date", today)))) {
            Double carbs = doc.getDouble("carbs");
            if (carbs != null) {
                total += carbs;
            }
        }

        return total;
    }

    public double getTodayFats(String userId) {
        String today = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        double total = 0.0;

        for (Document doc : collection.find(and(eq("userId", userId), eq("date", today)))) {
            Double fats = doc.getDouble("fats");
            if (fats != null) {
                total += fats;
            }
        }

        return total;
    }
}
