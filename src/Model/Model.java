package Model;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Model {
    public static void main (String [] args) {

    }
    //No Directory selected
    public Model() {
        //initial stage
    }
// path_corpus tells u where the corpus directory & the stopword file sits
    //path_to save tells you where to save the final posting list & the dictionary
    public void start(String path_corpus,String path_tosave, boolean stemOrNot) {
        try {
            init();
            HashSet<String> stopword = null;
            stopword = DSstopwords(path_corpus);
            ReadFile Fr = new ReadFile(path_corpus+"\\corpus");
            Parse parser = new Parse();
            parser.setStopword(stopword);
            StemmerGenerator StG = new StemmerGenerator(stemOrNot);
            StG.setStopWords(stopword);
            Indexer index = new Indexer(path_tosave,stemOrNot);
            long start = System.currentTimeMillis();
            for (int file = 0; file <= 72; file++) {
                LinkedList<String> Documents = Fr.fileReader();
                LinkedList<ArrayList<String>> ParsedDocs = parser.ParseFile(Documents);
                LinkedHashMap<String, LinkedList<Document>> StemmedDocs = StG.chunkStem(ParsedDocs);
                index.setDocs(StemmedDocs);
            }
            //change
            index.mergeFiles();
            long end = System.currentTimeMillis();
            System.out.println(end - start);
            //delete all the temp posting list
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //deletes all files from the previous run
    private void init() {
        File tempPost = new File("PostingList");
        delete(tempPost);
        File docProp= new File("docProperties.txt");
        docProp.delete();
    }

    // inserts all the stopwords into a hash
    private static HashSet<String> DSstopwords(String path) {
        HashSet<String> stopword = new HashSet<>();
        String line;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(path+"\\stopword.txt");
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
        if (path==null || path.equals("No Directory selected")  ){
            Path="";
        }
        else {
            Path=path+"//";
        }
        File postStem=new File(Path+"PostingListStem.txt");
        File postNotStem=new File(Path+"PostingListStem.txt");
        File Dictionary=new File(Path+"Dictionary.txt");
        postStem.delete();
        postNotStem.delete();
        Dictionary.delete();
    }
}

