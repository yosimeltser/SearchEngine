import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class StemmerGenerator {
    private Stemmer stem;
    LinkedList<ArrayList<String> > ParsedDocs;
    //represents  docs list of 100 files
    LinkedList<Document> Docs = new LinkedList<>() ;
    //DOCUMENT FREQUENCY OF THE TERMS
    private static HashMap <String, Integer>  termDf = new HashMap<>();
    int tHold;
    private static HashSet<String> hash=new HashSet<String>(0);
    public StemmerGenerator(Stemmer _stem,LinkedList<ArrayList<String> > _Docs) {
        this.stem=_stem;
        ParsedDocs=_Docs;
    }
    public void chunkStem() {
        for (ArrayList<String>  need_to_parse:ParsedDocs) {
            Document doc=  new  Document (need_to_parse.get(0));
            for (int k=1;k<need_to_parse.size(); k++){
                String s = need_to_parse.get(k);
                stem.add(s.toCharArray(),s.length());
                stem.stem();
                String wordStemmed= stem.toString().trim();
                //df
                if ( !wordStemmed.equals("") && !doc.contains(wordStemmed)){
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
            Docs.add(doc);
        }
        System.out.println();
    }
}
