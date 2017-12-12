

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
    public HashMap<String,LinkedList<Document>> temp;
    public LinkedHashMap<String,LinkedList<Document>> termToDocs;
    //DOCUMENT FREQUENCY OF THE TERMS
    private static HashMap <String, Integer>  termDf = new HashMap<>();
    public static HashMap<String,String> already_seen=new HashMap<>();
    private Indexer index;
    //Dictionary
    int tHold;
    public StemmerGenerator(Stemmer _stem,LinkedList<ArrayList<String> > _Docs) {
        this.stem=_stem;
        ParsedDocs=_Docs;
        termToDocs=new LinkedHashMap<>();
        temp=new HashMap<>();
        index= new Indexer();
    }
    public StemmerGenerator(){

    }
    public HashMap <String, Integer> getDf(){
        return termDf;
    }
    public void chunkStem() {
        i++;
        for (ArrayList<String>  need_to_parse:ParsedDocs) {
            Document doc=  new  Document (need_to_parse.get(0));
            doc.setSize(need_to_parse.size()-1);
            for (int k=1;k<need_to_parse.size(); k++){
                String s = need_to_parse.get(k);
                //DON'T STEM TERMS THAT HAVE MULTIPLE WORDS
                String wordStemmed;
                if (s.contains(" ")){
                    wordStemmed=s;
                    already_seen.put(s,wordStemmed);
                }
                //Exactly one word
                else if(!already_seen.containsKey(s)){
                    stem.add(s.toCharArray(),s.length());
                    stem.stem();
                    wordStemmed= stem.toString().trim();
                    already_seen.put(s,wordStemmed);
                }
                wordStemmed=already_seen.get(s);
                //df
                if ( !wordStemmed.equals("") && !doc.contains(wordStemmed) ){
                    if (temp.containsKey(wordStemmed)) {
                        temp.get(wordStemmed).addFirst(doc);
                    }
                    else {
                        LinkedList<Document> docs= new LinkedList<>();
                        docs.add(doc);
                        temp.put(wordStemmed,docs);
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
                doc.add(wordStemmed,i);
            }
            doc.setMaxTf();

        }
        //Sorts by tf, temp posting list
        for(Map.Entry<String, LinkedList<Document>> entry : termToDocs.entrySet()){
            entry.getValue().sort( new Comparator<Document>(){
                @Override
                public int compare(Document d1,Document d2){
                    String key=entry.getKey();
                    if ( d1.terms.get(key).getTf() > d2.terms.get(key).getTf()){
                        return -1;
                    }
                    else if (  d1.terms.get(key).getTf()  < d2.terms.get(key).getTf()){
                        return 1;
                    }
                    else {
                        return 0;
                    }

                }
            });
        }
        //Sort Hash Map
        //Replace Tree Map
        ArrayList <String> arr= new ArrayList<>(temp.keySet());
        Collections.sort(arr);
        for (int i=0 ; i<arr.size(); i++) {
            String s=arr.get(i);
            LinkedList<Document> val= temp.get(s);
            termToDocs.put(s,val);
        }
        index.setDocs(termToDocs);

    }
}
