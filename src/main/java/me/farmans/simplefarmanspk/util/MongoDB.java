package me.farmans.simplefarmanspk.util;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import me.farmans.simplefarmanspk.SimpleFarmansPK;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class MongoDB {
    static String connectionString;
    public static void main(SimpleFarmansPK plugin) {
        Configurator.setLevel("org.mongodb.driver", Level.ERROR);
        connectionString = plugin.getSettingsConfig().getString("mongodb");
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                MongoDatabase database = mongoClient.getDatabase("admin");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insert(Player player, String col, double time) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {

            MongoDatabase rilyeventspk = mongoClient.getDatabase("rilyeventspk");
            MongoCollection<Document> collection = rilyeventspk.getCollection(col);

            List<Document> documents = read(player, col);
            if (!documents.isEmpty()) {
                update(player, col, time);
                return;
            }

            Document document = new Document("_id", player.getUniqueId().toString());
            document.append("time", time);
            document.append("username", player.getName());

            collection.insertOne(document);
        }
    }

    public static void update(Player player, String col, double time) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {

            MongoDatabase rilyeventspk = mongoClient.getDatabase("rilyeventspk");
            MongoCollection<Document> collection = rilyeventspk.getCollection(col);

            // update one document
            Bson filter = eq("_id", player.getUniqueId().toString());
            UpdateResult timeResult = collection.updateOne(filter, set("time", time));
            UpdateResult nameResult = collection.updateOne(filter, set("username", player.getName()));

            if (!timeResult.wasAcknowledged() || !nameResult.wasAcknowledged()) {
                Bukkit.getLogger().log(java.util.logging.Level.SEVERE, String.format("Chyba při updatování MongoDB - %s (%s: %s)", col, player.getUniqueId(), time));
                System.out.println(timeResult);
                System.out.println(nameResult);
            }
        }
    }

    public static List<Document> read(Player player, String col) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase rilyeventspk = mongoClient.getDatabase("rilyeventspk");
            MongoCollection<Document> collection = rilyeventspk.getCollection(col);

            List<Document> documents = collection.find(new Document("_id", player.getUniqueId().toString())).into(new ArrayList<>());
            return documents;
        }
    }
}
