

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class StemmerGenerator {
    private static int  i=0;
    private Stemmer stem;
    LinkedList<ArrayList<String> > ParsedDocs;
    //represents  docs list of 100 files
    //LinkedList<Document> Docs = new LinkedList<>() ;
    public TreeMap<String,LinkedList<Document>> termToDocs;

    //DOCUMENT FREQUENCY OF THE TERMS
    private static HashMap <String, Integer>  termDf = new HashMap<>();
    private Indexer index;
    //Dictionary
    int tHold;
    public StemmerGenerator(Stemmer _stem,LinkedList<ArrayList<String> > _Docs) {
        this.stem=_stem;
        ParsedDocs=_Docs;
        termToDocs=new TreeMap<String,LinkedList<Document>>();
        index= new Indexer();
    }
    public void chunkStem() {
        i++;
        for (ArrayList<String>  need_to_parse:ParsedDocs) {
            Document doc=  new  Document (need_to_parse.get(0));
            doc.setSize(need_to_parse.size()-1);
            for (int k=1;k<need_to_parse.size(); k++){
                String s = need_to_parse.get(k);
                stem.add(s.toCharArray(),s.length());
                 stem.stem();
                String wordStemmed= stem.toString().trim();
                //df
                if ( !wordStemmed.equals("") && !doc.contains(wordStemmed) ){
                    if (termToDocs.containsKey(wordStemmed)) {
                            termToDocs.get(wordStemmed).addFirst(doc);
                    }
                    else {
                        LinkedList<Document> docs= new LinkedList<Document>();
                        docs.add(doc);
                        termToDocs.put(wordStemmed,docs);
                    }
                    //First time that we see the term in doc
                    if (termDf.containsKey(wordStemmed)){
                        termDf.put(wordStemmed,termDf.get(wordStemmed)+1);
                    }
                    else {
                        termDf.put(wordStemmed,1);
                    }

                }
                //tf
                doc.add(wordStemmed);
            }
        }
        for(Map.Entry<String, LinkedList<Document>> entry : termToDocs.entrySet()){
            entry.getValue().sort( new Comparator<Document>(){
                @Override
                public int compare(Document d1,Document d2){
                    String key=entry.getKey();
                    if ( d1.termFr.get(key) > d2.termFr.get(key)){
                          return -1;
                    }
                    else if ( d1.termFr.get(key) < d2.termFr.get(key)){
                        return 1;
                    }
                    else {
                        return 0;
                    }

                }
            });
        }
//        if (i == 3) {
//            //Empty the ram from the df Dictionary
//            //Cut him to the disc
//            try {
//                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Df.txt"));
//                for (Map.Entry<String, Integer> entry : termDf.entrySet()) {
//                    String key=entry.getKey();
//                    bufferedWriter.write("Key = " + key + ", Value = " + entry.getValue());
//                    termDf.remove(key);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        };

        index.setDocs(termToDocs);

    }
}
