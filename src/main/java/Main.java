import Browsing.*;
import Indexing.*;

import java.util.Scanner;

/*
        This is a model of browser which search word from given documents.
    The files must be stored in /resources/Documents. This folder will be
    processed by the browser.

        First the Indexer is called so that the words to be reorganised, and
    stored in indexes for a better search time. Indexes can be stored on files
    (/resources/Indexes/) or on mongoDB.

        Finally the browser engine is called and the user can type word to search
    in the provided documents. The browser search implements the Boolean Search
    method.
 */

public class Main {

    /**
     * The main function where the interface method is called.
     */
    public static void main(String[] args) {
       run();
    }

    /**
     * Interface method for selecting the type of indexer to use.<br>
     * File stored indexer or DataBase stored indexer.
     */
    private static void run(){
        String resourcesFolderName = "Resources/";

        System.out.println("Welcome!\nThis is the Browser_2.0 program.\n" +
                            "This browser uses indexes for a better search time.\n" +
                            "Indexes can be stored on files or on a DataBase.\n\n" +
                            "Type: \n\t1 for -- file stored index\n" +
                            "\t\tor\n\t2 for -- DataBase stored index\n" +
                            "Option: ");

        boolean ok = false;
        Scanner in = new Scanner(System.in);
        while (!ok){

            String opt = in.next();
            switch (opt){
                // File stored index case
                case "1":{
                    System.out.println("File indexer chosen.\n");
                    Indexer indexer = new FileIndexer(resourcesFolderName,3, 40);
                    indexer.run();
                    Browser browser = new FileBrowser(resourcesFolderName);
                    browser.run();
                    ok = true;
                }
                // mongoDB stored index case
                case "2":{
                    System.out.println("DataBase indexer chosen.\n");
                    Indexer mongoDBIndexer = new MongoDBIndexer(resourcesFolderName);
                    mongoDBIndexer.run();
                    Browser browser = new DBBrowser();
                    browser.run();
                    ok = true;
                }
                // wrong option inserted case
                default: {
                    System.out.println("Wrong option! \n" +
                            "Type: \n\t1 for -- file stored index\n" +
                            "\t\tor\n\t2 for -- DataBase stored index\n" +
                            "Option: ");
                }
            }
        }
    }
}
