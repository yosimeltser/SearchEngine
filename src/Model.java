import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Model {
    public static void main(String[] args) {
       Model m= new Model();
       m.start();
    }
    public Model() {
        //initial stage
    }
    public void start() {
        try {
            HashSet<String> stopword=null;
            stopword=DSstopwords();
            ReadFile Fr = new ReadFile("C:\\Users\\yosefmel\\Downloads\\corpus\\corpus");
            Parse parser = new Parse();
            parser.setStopword(stopword);
            StemmerGenerator  StG= new StemmerGenerator();
            StG.setStopWords(stopword);
            Indexer index= new Indexer();
            long start= System.currentTimeMillis();
            for (int file=0 ; file <=72 ;file++){
                LinkedList<String> Documents = Fr.fileReader();
                LinkedList<ArrayList<String>> ParsedDocs = parser.ParseFile(Documents);
                LinkedHashMap<String, LinkedList<Document>> StemmedDocs=StG.chunkStem(ParsedDocs);
                index.setDocs(StemmedDocs);
            }
            index.mergeFiles();
            long end= System.currentTimeMillis();
            System.out.println(end-start);
        } catch (Exception IO) {
        }
    }
    // inserts all the stopwords into a hash
    private static HashSet<String> DSstopwords() {
        HashSet<String> stopword= new HashSet<>();
        String line;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader("C:\\Users\\yosefmel\\IdeaProjects\\SearchEngine\\stopword.txt");
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
}
