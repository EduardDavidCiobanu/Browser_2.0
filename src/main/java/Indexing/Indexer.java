package Indexing;

import Stemming.StringStemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


/**
 *  Indexer is used to process the documents provided by the user and
 * create directIndexes and inverseIndexes. The last one helps the
 * browser to find the words much faster than normal document
 * search.<br>
 *
 *  This is the main class and contains general methods for creating
 * indexes. It has two abstract methods that will be implemented by
 * subclasses, each one according to their functionality.
 */
public abstract class Indexer {

    final String RESOURCES_FOLDER_NAME;
    List<String> stopwords;
    List<String> exceptions;

    Indexer(String folderName){
        this.stopwords = new LinkedList<>();
        this.exceptions = new LinkedList<>();
        this.RESOURCES_FOLDER_NAME = folderName;

        try (Scanner reader = new Scanner(new File(RESOURCES_FOLDER_NAME + "StopWords&Exceptions.txt"))){
            String type = "";

            while (reader.hasNext()){
                String line = reader.nextLine();
                if(!line.equals("")){
                    if(line.equals("STOPWORDS:") || line.equals("EXCEPTIONS:"))
                        type = line;
                    else {
                        switch (type){
                            case "STOPWORDS:": stopwords.add(line); break;
                            case "EXCEPTIONS:": exceptions.add(line);
                            default: break;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    abstract void createDirectIndex(final File folder);

    abstract void createInverseIndex();

    /**
     *  Here the word given is stemmed (brought to its root form) by the
     * stemmer.<br>
     *
     * Next, if the word is not in the stopwords list provided in
     * the "resources/StopWords&Exceptions.txt", it will pass the filter.
     *
     * @return the processed word if it passed the filter
     */
    String wordFilter(String word){

        StringStemmer stemmer = new StringStemmer();
        String result = stemmer.stem(word);

        if (stopwords.contains(result)) {
            if (exceptions.contains(result))
                return result;
            else
                return "";
        }
        else {
            return result;
        }
    }

    public void run(){
        createDirectIndex(new File(RESOURCES_FOLDER_NAME + "Documents"));
        createInverseIndex();
    }
}
