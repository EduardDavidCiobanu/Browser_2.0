package Indexing;

import com.mongodb.client.*;
import org.bson.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

/**
 *  The indexer class where the documents are processed and the indexes are
 *  stored in mongoDataBase. <br><br>
 *  The run() method calls the createDirectIndex method, then the createInverseIndex method.
 */
public class MongoDBIndexer extends Indexer {

    private MongoDatabase db;

    public MongoDBIndexer(String resourcesFolderName) {
        super(resourcesFolderName);
        MongoClient mongoClient = MongoClients.create();
        this.db = mongoClient.getDatabase("browser");
    }

    /**
     * Creates direct indexes into mongoDataBase.
     *
     * @param folder contains the files from wich the indexes will be created
     */
    @Override
    void createDirectIndex(File folder) {
        MongoCollection<Document> directIndexes = db.getCollection("directIndexes");

        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())){
            if (fileEntry.isDirectory()) {
                createDirectIndex(fileEntry);
            }
            else {
                try (Scanner in = new Scanner(fileEntry)){
                    Document doc = new Document();

                    if(in.hasNext())
                        doc.put("_name", fileEntry.getName());

                    while (in.hasNext()){   // Put words into mongoDB document
                        String[] words = in.next().split(" ");
                        for (String word : words){

                            word = wordFilter(word);
                            if (!word.equals("")) {
                                if (doc.containsKey(word)) {
                                    int count = doc.getInteger(word, 1);
                                    doc.remove(word);
                                    doc.put(word, count + 1);
                                } else {
                                    doc.put(word, 1);
                                }
                            }
                        }
                    }
                    if (!doc.isEmpty()){
                        if (directIndexes.find(eq("_name", doc.get("_name"))).first() != null)
                            directIndexes.findOneAndDelete(eq("_name", doc.get("_name")));
                        directIndexes.insertOne(doc); // Insert into mongoDB
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates inverse indexes into mongoDataBase.
     */
    @Override
    void createInverseIndex() {
        MongoCollection<Document> directIndexes = db.getCollection("directIndexes");
        MongoCollection<Document> inverseIndexes = db.getCollection("inverseIndexes");
        Map<String, Map<String, Integer>> indexes = new TreeMap<>();

        if (inverseIndexes.countDocuments() > 0)
            inverseIndexes.deleteMany(new Document());

        try (MongoCursor<Document> cursor = directIndexes.find().iterator()){
            while (cursor.hasNext()) {
                Document doc = cursor.next();


                String docName = doc.getString("_name");
                doc.forEach((key, value) -> {
                    if (!key.equals("_name") && !key.equals("_id")) {
                        //System.out.println(key + value);

                        if (!indexes.containsKey(key)) {
                            indexes.put(key, new HashMap<>());
                            indexes.get(key).put(docName, Integer.parseInt(value.toString()));
                        } else {
                            indexes.get(key).put(docName, Integer.parseInt(value.toString()));
                        }
                    }
                });
            }

            for (Map.Entry<String, Map<String, Integer>> documentE : indexes.entrySet()){

                Document result = new Document().append("_word", documentE.getKey());

                for (Map.Entry<String, Integer> entryValue : documentE.getValue().entrySet())
                    result.append(entryValue.getKey(), entryValue.getValue());

                inverseIndexes.insertOne(result);
            }
        }
        System.out.println("\nFiles were successfully indexed in DataBase.\n ");
    }
}
