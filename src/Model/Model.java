package Model;

import java.io.*;
import java.util.*;

//THIS CLASS JOB IS TO CONNECT BETWEEN ALL OF THE CLASSES IN THE WHOLE PROJECT
//IN ADDITION, THERE IS AN INTERACTION BETWEEN THE MODEL AND THE VIEW CLASSES THAT MAKES THE CODE MODULAR TO CHANGES.

public class Model {
    HashSet<String> stopword = null;
    public Model() {
        //empty constractor
    }
    // path_corpus tells u where the corpus directory & the stopword file sits
    //path_to save tells you where to save the final posting list & the dictionary
    public long start(String path_corpus, String path_tosave, boolean stemOrNot) {
        try {
            long start = System.currentTimeMillis();

            stopword = DSstopwords(path_corpus);
            ReadFile Fr = new ReadFile(path_corpus + "\\corpus");
            Parse parser = new Parse();
            parser.setStopword(stopword);
            StemmerGenerator StG = new StemmerGenerator(stemOrNot);
            StG.setStopWords(stopword);
            Indexer index = new Indexer(path_tosave, stemOrNot);
            for (int file = 0; file <= 72; file++) {
                LinkedList<String> Documents = Fr.fileReader();
                LinkedList<ArrayList<String>> ParsedDocs = parser.ParseFile(Documents);
                LinkedHashMap<String, LinkedList<Document>> StemmedDocs = StG.chunkStem(ParsedDocs);
                index.setDocs(StemmedDocs);
            }
            index.mergeFiles();
            long end = System.currentTimeMillis();
            return ((end-start )/ 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //never suppose to get here
        return 1;
    }
    //part2 engine engine!!!!
    //parse query
    private  ArrayList<String> ParseQuery (String st){
        LinkedList <String> arr=new LinkedList<>();
        //not having doc number
        String q="* " + st;
        arr.addFirst(q);
        Parse queryParse = new Parse();
        queryParse.setStopword(stopword);
        LinkedList<ArrayList<String>> ParsedQuery=queryParse.ParseFile(arr);
        Stemmer s= new Stemmer();
        Model.Searcher StQ = new Model.Searcher(stopword,s);
        StQ.setParsedQuery(ParsedQuery.getFirst());
        ArrayList<String> StemmedQuery=StQ.stem();
        return StemmedQuery;
    }
    // inserts all the stop words into a hash
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
    //delete the Dictionary,Cache and the Posting List, when event reset accursed
    public void reset(String path) {
        String Path;
        if (path.equals("")|| path.equals("No Directory selected")) {
            Path = "";
        } else {
            Path = path + "\\";
        }
        File cache = new File(Path + "Cache.txt");
        File postStem = new File(Path + "PostingListStem.txt");
        File postNotStem = new File(Path + "PostingListNoStem.txt");
        File Dictionary = new File(Path + "Dictionary.txt");
        if (cache.exists()){
            cache.delete();
        }
        if (postStem.exists()){
            postStem.delete();
        }
        if (postNotStem.exists()){
            postNotStem.delete();
        }
        if (Dictionary.exists()){
            Dictionary.delete();
        }
        System.gc();
    }
    //returns array of string
    //1 element ->  Index Size
    //2 element -> Cache
    public String[] sizes(String path, boolean StemOrNot) {
        String Path;
        String[] size = new String[2];
        if (path.equals("") || path.equals("No Directory selected")) {
            Path = "";
        } else {
            Path = path + "\\";
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

