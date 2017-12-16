package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Model {
    public static void main(String[] args) {

    }

    //No Directory selected
    public Model() {
        //initial stage
    }

    // path_corpus tells u where the corpus directory & the stopword file sits
    //path_to save tells you where to save the final posting list & the dictionary
    public long start(String path_corpus, String path_tosave, boolean stemOrNot) {
        try {
            init();
            HashSet<String> stopword = null;
            stopword = DSstopwords(path_corpus);
            ReadFile Fr = new ReadFile(path_corpus + "\\corpus");
            Parse parser = new Parse();
            parser.setStopword(stopword);
            StemmerGenerator StG = new StemmerGenerator(stemOrNot);
            StG.setStopWords(stopword);
            Indexer index = new Indexer(path_tosave, stemOrNot);
            long start = System.currentTimeMillis();
            for (int file = 0; file <= 72; file++) {
                LinkedList<String> Documents = Fr.fileReader();
                LinkedList<ArrayList<String>> ParsedDocs = parser.ParseFile(Documents);
                LinkedHashMap<String, LinkedList<Document>> StemmedDocs = StG.chunkStem(ParsedDocs);
                index.setDocs(StemmedDocs);
            }
            //change
            index.SortCache();
            long end = System.currentTimeMillis();
            //delete all the temp posting list
            return (end / 10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //never suppose to get here
        return 1;
    }

    //deletes all files from the previous run
    private void init() {
        File tempPost = new File("PostingList");
        if (!tempPost.exists()) {
            new File("PostingList").mkdirs();
        } else {
            delete(tempPost);
            new File("PostingList").mkdirs();
        }
        File docProp = new File("docProperties.txt");
        docProp.delete();
    }

    // inserts all the stopwords into a hash
    private static HashSet<String> DSstopwords(String path) {
        HashSet<String> stopword = new HashSet<>();
        String line;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(path + "\\stopword.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        br = new BufferedReader(fr);
        try {
            while ((line = br.readLine()) != null) {
                stopword.add(line);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopword;
    }

    private static void delete(File file) {
        for (File childFile : file.listFiles()) {

            if (childFile.isDirectory()) {
                delete(childFile);
            } else {
                childFile.delete();
            }
        }
        file.delete();
    }


    //delete the dictionary and the posting list, when event reset accursed
    public void reset(String path) {
        String Path;
        if (path == null || path.equals("No Directory selected")) {
            Path = "";
        } else {
            Path = path + "//";
        }
        File cache = new File(Path + "Cache.txt");
        File postStem = new File(Path + "PostingListStem.txt");
        File postNotStem = new File(Path + "PostingListNoStem.txt");
        File Dictionary = new File(Path + "Dictionary.txt");
        postStem.delete();
        postNotStem.delete();
        Dictionary.delete();
        cache.delete();
    }

    //returns array of string
    //1 element ->  Index Size
    //2 element -> Cache
    public String[] sizes(String path, boolean StemOrNot) {
        String Path;
        String[] size = new String[2];
        if (path == null || path.equals("No Directory selected")) {
            Path = "";
        } else {
            Path = path + "//";
        }
        long postSize;
        if (StemOrNot) {
            File postStem = new File(Path + "PostingListStem.txt");
            postSize = postStem.length();
        } else {
            File postNotStem = new File(Path + "PostingListNoStem.txt");
            postSize = postNotStem.length();
        }
        File Dictionary = new File(Path + "Dictionary.txt");
        File Cache = new File(Path + "Cache.txt");
        Long index = (Dictionary.length() + postSize);
        Long cache = Cache.length();
        size[0] = index.toString();
        size[1] = cache.toString();
        return size;
    }
}

