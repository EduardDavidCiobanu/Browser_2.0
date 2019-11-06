package Indexing;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * The indexer class where the documents are processed and the indexes are
 * stored in files. <br><br>
 * The run() method calls the createDirectIndex method, then the createInverseIndex method.
 */
public class FileIndexer extends Indexer {

    private final int MAX_DOCS_PER_FILE;
    private final int MAX_WORDS_PER_FILE;

    /* Properties */

    private Map<String, Map<String, Integer>> documentWordsMap;
    private Map<String, Map<String, Integer>> inverseIndexerMap;

    /* Methods */

    /**
     * @param MAX_DOCS_PER_FILE how many documents to put in a single  directIndex file.
     * @param MAX_WORDS_PER_FILE how many words to put in a single inverseIndex file.
     */
    public FileIndexer(String resourcesFolderName, int MAX_DOCS_PER_FILE, int MAX_WORDS_PER_FILE) {
        super(resourcesFolderName);
        this.MAX_DOCS_PER_FILE = MAX_DOCS_PER_FILE;
        this.MAX_WORDS_PER_FILE = MAX_WORDS_PER_FILE;
        this.documentWordsMap = new HashMap<>();
        this.inverseIndexerMap = new HashMap<>();
    }

    /**
     * Creates direct indexes into files.
     *
     * @param folder contains the files from wich the indexes will be created
     */
    @Override
    void createDirectIndex(final File folder) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())){
            if (fileEntry.isDirectory())
                createDirectIndex(fileEntry);
            else
            {
                documentWordsMap.put(fileEntry.getName(), new LinkedHashMap<>());
                try (Scanner scanner = new Scanner(fileEntry)) {
                    while (scanner.hasNext())
                        addToMap(scanner.next(), documentWordsMap.get(fileEntry.getName()));
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }
        fileIndexerCreator(documentWordsMap, "Direct/", "ID", MAX_DOCS_PER_FILE);
    }

    /**
     * Creates inverse indexes into files.
     */
    @Override
    void createInverseIndex() {
        for (Map.Entry<String, Map<String, Integer>> entry : documentWordsMap.entrySet()){
            for (Map.Entry<String, Integer> word : entry.getValue().entrySet()){
                if (!inverseIndexerMap.containsKey(word.getKey())) {
                    inverseIndexerMap.put(word.getKey(), new HashMap<>());
                    inverseIndexerMap.get(word.getKey()).put(entry.getKey(), word.getValue());
                }
                else {
                    inverseIndexerMap.get(word.getKey()).put(entry.getKey(), word.getValue());
                }
            }
        }
        TreeMap<String, Map<String, Integer>> sorted = new TreeMap<>(inverseIndexerMap);
        inverseIndexerMap.clear();

        fileIndexerCreator(sorted, "Inverse/","II", MAX_WORDS_PER_FILE);

        System.out.println("\nFiles were successfully indexed in index files.\n ");
    }

    /**
     * Filters the words and adds them to a given map.
     *
     * @param word to add in map
     * @param map with the words and the count of words in the current document.
     */
    private void addToMap(String word, Map<String, Integer> map) {

        word = this.wordFilter(word);
        if (word.equals(""))
            return;

        if (map.containsKey(word))
        {
            map.replace(word,map.get(word)+1);
        }
        else
        {
            map.put(word,1);
        }
    }

    /**
     * Creates the index files from the early created map.
     *
     * @param map temporal store for indexes;
     * @param folderName where to store indexes;
     * @param fileName of an index file;
     * @param MAX parameter for document [or word] count per file.
     */
    private void fileIndexerCreator(Map<String, Map<String, Integer>> map, String folderName, String fileName, int MAX){
        int idFile = 1;
        int idDocument = 0;
        String message = "";
        String Mapper = "";

        for(Map.Entry<String, Map<String, Integer>> entry : map.entrySet())
        {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put(entry.getKey(), entry.getValue());
            message += jsonObject.toString() + "\n";
            jsonObject.remove(entry.getKey());
            idDocument++;

            jsonObject.put(entry.getKey(), fileName + idFile);
            Mapper += jsonObject.toString() + "\n";

            if (idDocument == MAX)
            {
                try(PrintWriter out = new PrintWriter(RESOURCES_FOLDER_NAME + "Indexes/" +
                                            folderName + fileName + idFile + ".txt")){
                    out.println(message);
                } catch (FileNotFoundException e){System.out.println("Cannot open the file :" +
                        RESOURCES_FOLDER_NAME + "Indexes/" + fileName + idFile + ".txt");
                }
                idFile++;
                idDocument = 0;
                message = "";
            }
        }
        if(!message.equals(""))
        {
            try(PrintWriter out = new PrintWriter(RESOURCES_FOLDER_NAME + "Indexes/" +
                                        folderName + fileName + idFile + ".txt")){
                out.println(message);
            } catch (FileNotFoundException e){System.out.println("Cannot open the file:" +
                    RESOURCES_FOLDER_NAME + "Indexes/" + fileName + idFile + ".txt");
            }
        }

        try (PrintWriter out = new PrintWriter(RESOURCES_FOLDER_NAME + "Indexes/" +
                                folderName + "fileMapper"+ fileName + ".txt")) {
            out.println(Mapper);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
