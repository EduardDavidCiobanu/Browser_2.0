package Browsing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Used to search words that use file stored indexes.
 */
public class FileBrowser extends Browser {

    public FileBrowser(String resourcesFolderName) {
        super(resourcesFolderName);
    }

    /**
     * Search the word in the inverse indexes files and adds it to
     * results HashMap if found.
     * @param word the word to find
     */
    void findWord(String word){

        final File folder = new File(RESOURCES_FOLDER_NAME + "Indexes/Inverse");
        String data;
        String[] splitData;
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())){
            if (!fileEntry.getName().equals("fileMapperII.txt")) {
                try (Scanner scanner = new Scanner(fileEntry)) {
                    while (scanner.hasNext()) {

                        data = scanner.nextLine();
                        splitData = data.split("\"");

                        if (splitData[1].equals(word)) {
                            JSONObject jsonObject = new JSONObject(data);
                            results.put(word, new Gson().fromJson(
                                    jsonObject.get(word).toString(),
                                    new TypeToken<HashMap<String,Integer>>(){}.getType()));
                            return;
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
