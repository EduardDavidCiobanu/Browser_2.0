package Browsing;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

/**
 * DataBase Browser<br>
 * Used to search words that use database stored indexes.
 */
public class DBBrowser extends Browser {

    /* Properties */

    private MongoDatabase DB;


    /* Methods */

    /*public DBBrowser(MongoDatabase DB) {
        this.DB = DB;
    }*/

    public DBBrowser() {
        super("");
        MongoClient mongoClient = MongoClients.create();
        this.DB = mongoClient.getDatabase("browser");
    }

    /**
     * Search the word in the inverseIndexes mongo collection and adds the
     * list of documents containing the current word, in the results map.
     */
    @Override
    void findWord(String word) {

        MongoCollection<Document> collection = this.DB.getCollection("inverseIndexes");

        final Document document = collection.find(eq("_word", word)).first();

        if (document != null){
            if (!this.results.containsKey(word)) {

                Map<String, Integer> valuesMap = new HashMap<>();

                document.forEach((key, value) -> {
                    if (!key.equals("_id") && !key.equals("_word"))
                        valuesMap.put(key, Integer.parseInt(value.toString()));
                });

                this.results.put(word, valuesMap);
            }
        }
    }
}
