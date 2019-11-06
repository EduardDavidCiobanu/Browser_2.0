package Browsing;

import Indexing.Indexer;
import Stemming.StringStemmer;

import java.util.*;

/**
 *     This class has the role of find functionality. It has methods
 * for receiving queries from user and to search in the files for
 * the provided words in queries. <br>
 *
 *     Every query is checked before passing to the search engine.
 * Only the queries in the correct form will be processed.<br>
 *
 *     It has an abstract method which will be implemented in subclasses
 * according to their functionality.
 */
public abstract class Browser {

    /* Properties */
    final String RESOURCES_FOLDER_NAME;
    Map<String, Map<String, Integer>> results;
    private Set<String> documents = null;
    private StringStemmer stemmer;


    /* Methods */
    /* Abstract methods to implement in subclass */

    abstract void findWord(String word);

    /* Implemented methods */
    Browser(String folderName){
        results = new HashMap<>();
        stemmer = new StringStemmer();
        RESOURCES_FOLDER_NAME = folderName;
    }

    private boolean isOperator(String string){

        return string.equals("+") || string.equals("/") || string.equals("-");
    }

    /**
     * The test method wich verify the query type by the
     * user.
     *
     * @param query typed by users
     * @return false if query format is incorrect, and true if
     *      query format is correct.
     */
    private boolean queryTest(String query){
        String[] splitquery = query.split(" ");

        if (isOperator(splitquery[0])) { /* case: OPR ... */
            System.out.println("[.queryTest()] : The first string must be a word!\n");
            return false;
        }
        if (splitquery.length % 2 == 0) { /* case: word OPR */
            System.out.println("[.queryTest()] : Wrong query format!\n");
            return false;
        }
        if (splitquery.length > 1) {

            String temp = splitquery[1];
            int i = 2;
            while (i < splitquery.length) {
                if (!isOperator(temp)) { /* case: word word */
                    System.out.println("[.queryTest()] : Put an operator BETWEEN two words!\n");
                    return false;
                }
                if (isOperator(splitquery[i])) { /* case word OPR OPR */
                    System.out.println("[.queryTest()] : query format - word OPERATOR word - " +
                            "unsatisfied!\n");
                }
                if (i+1 < splitquery.length) {
                    temp = splitquery[i+1];
                    i += 2;
                }
                else ++i;
            }
        }
        return true;
    }

    /* Operations */
    private void AND(String word){
        List<String> documentsToRemove = new LinkedList<>();
        for (String document : documents)
        {
            if(!results.get(word).containsKey(document))
                documentsToRemove.add(document);
        }
        for (String document : documentsToRemove)
            documents.remove(document);
    }

    private void OR(String word){
        for (Map.Entry<String, Integer> entry : results.get(word).entrySet())
            documents.add(entry.getKey());
    }

    private void NOT(String word){
        for (Map.Entry<String, Integer> entry : results.get(word).entrySet())
            documents.remove(entry.getKey());
    }

    /**
     * The search engine of the Browser. Search the query
     * in the indexes created by the {@link Indexer}
     *
     * @param query the string to be searched
     * @return the result of the search engine
     */
    private String search(String query) {

        /* Query separation into words and operators */
        String[] splitInput = query.split(" ");

        /* Query stemming */
        for (int i = 0; i < splitInput.length; i++){
            if (!isOperator(splitInput[i]))
                splitInput[i] = stemmer.stem(splitInput[i]);
        }

        for (String string : splitInput) {
            if (!isOperator(string))
                findWord(string);
        }

        /* Applied operations */
        String operator = "";

        for (String string : splitInput) {
            if (!isOperator(string)) {
                if (results.containsKey(string))
                {
                    if (documents == null) {
                        documents = new HashSet<>();
                        for (Map.Entry<String, Integer> entry : results.get(string).entrySet()) {
                            documents.add(entry.getKey());
                        }
                    } else {
                        switch (operator) {
                            case "+":
                                AND(string);
                                break;
                            case "/":
                                OR(string);
                                break;
                            case "-":
                                NOT(string);
                        }
                    }
                } else {
                    System.out.println("[.search()]: No matches for " + string);
                }
            } else
                operator = string;
        }

        String result;
        if (documents != null && documents.size() > 0) {
            result = documents.toString();
            documents.clear();
            documents = null;
        }
        else
            result = "No results.";

        results.clear();
        return result;
    }

    /**
     * Listens for queries and calls the search method.
     */
    public void run(){
        System.out.println("BooleanSearch program.\n\n" +
                "Here are the search operators you can use:" +
                "<  +   /   -  > \n" +
                "Ex: word + word - word / word\n Type EXIT to exit the search loop\n\n" +
                "Your query: ");

        Scanner in = new Scanner(System.in);
        String query;
        while (!(query = in.nextLine()).equals("EXIT")){
            if (queryTest(query)) {
                String result = search(query);
                System.out.println("Results: " + result + "\n\nYour query: ");
            }
            else {
                System.out.println("\nYour querry: ");
            }
        }
    }
}
