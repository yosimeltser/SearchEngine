import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class StemmerGenerator {
    private Stemmer stem;
    LinkedList<ArrayList<String> > ParsedDocs;
    //represents  docs list of 100 files
    //LinkedList<Document> Docs = new LinkedList<>() ;
    HashMap <String,LinkedList<Document>> termToDocs;

    //DOCUMENT FREQUENCY OF THE TERMS
    private static HashMap <String, Integer>  termDf = new HashMap<>();
    //Dictionary
    int tHold;
    public StemmerGenerator(Stemmer _stem,LinkedList<ArrayList<String> > _Docs) {
        this.stem=_stem;
        ParsedDocs=_Docs;
        termToDocs=new HashMap<String,LinkedList<Document>>();
    }
    public void chunkStem() {

        Indexer index= new Indexer();
        for (ArrayList<String>  need_to_parse:ParsedDocs) {
            Document doc=  new  Document (need_to_parse.get(0));
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
        index.setDocs(termToDocs);
        System.out.println();
    }
}
