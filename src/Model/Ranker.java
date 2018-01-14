package Model;

import java.util.*;

public class Ranker {
    int querySize;
    boolean stemOrNot;
    int queryNumber;
    public Ranker(boolean _stemOrNot, int _queryNumber) {
        stemOrNot = _stemOrNot;
        queryNumber=_queryNumber;
        Load l= new Load();
        dictionary=l.getDictionary();
        HashMap<String, String> dictionary=l.getDictionary();
        docToRank = new HashMap<String, Double>();
        DocWeightDic = l.getDocWeightDic();
    }

    public void setQuerySize(int querySize) {
        this.querySize = querySize;
    }

    public HashMap<String, Double> getDocToRank() {
        return docToRank;
    }

    //dictionary loaded from the Load Class
    HashMap<String, String> DocWeightDic;
    //maps from doc name to query rank...
    HashMap<String, Double> docToRank;
    //all the words returned from the query after parse and stem
    ArrayList<String> query;
    HashMap<String, String> dictionary;

    //get term df from the dictionary
    private long getTermDF(String term) {
        String s = dictionary.get(term);
        int j = 0;
        String df = "";
        while (s != null && !s.equals("") && s.charAt(j) != 'C' && s.charAt(j) != 'D') {
            df += s.charAt(j);
            j++;
        }
        if (df.equals("")) {
            return -1;
        }
        return Long.parseLong(df);
    }

    public void setDictionary(HashMap<String, String> dictionary) {
        this.dictionary = dictionary;
    }

    public void setQuery(ArrayList<String> query) {
        this.query = query;
    }

    //rank one word that exists in the query and in the document
    public void rankWord(String [] postLine,String term) {
        //starts from 1 hence the term in the 0 place
        int i=1;
        String termPost=postLine[0];
        while (!term.equals(termPost)){
            termPost+=" "+postLine[i];
            i++;
        }
        for (; i < postLine.length; i++) {
            if (!postLine[i].equals(" ") && !postLine[i].equals("")) {
                String docNumber = postLine[i];
                long tf = Long.parseLong(postLine[++i]);
                long index = Long.parseLong(postLine[++i]);
                weight(getTermDF(term), docNumber, tf, index);
            }
        }
    }

    //wight of the word in the document
    private void weight(long df, String docNumber, long tf, long index) {
        double docRank;
        double maxDocTf;
        double docSize;
        double oneWordQuery;
        double place = 0;
        int counter = 0;
        String docWeight;
        //get the document weight
        //for cos similarity denominator
        if (!stemOrNot) {
            docWeight = "docs_weights_NoStem.txt";
        } else {
            docWeight = "docs_weightsStem.txt";
        }
        String[] docProp = DocWeightDic.get(docNumber).split("\\*");
        docRank = Double.parseDouble(docProp[0]);
        maxDocTf = Double.parseDouble(docProp[1]);
        docSize = Double.parseDouble(docProp[2]);
        //new weight parameter for cos similarity
        //as closer to the start of the document as higher the rank of the word
        place = (docSize - index) / docSize;
        //467767 -> the number of documents in the corpus
        oneWordQuery =   (((tf / maxDocTf)  * (Math.log(467767 / df) / Math.log(2)))*place);
        double cossin = (oneWordQuery / ((Math.sqrt(querySize)) * docRank));
        double BM25=(bm25( df,tf, docSize));
        //half from cossin and half from BM25
        double OurFormula= 0.5*cossin + 0.5*BM25;
        if (docToRank.containsKey(docNumber)) {
            double addedValue = docToRank.get(docNumber) + OurFormula;
            docToRank.put(docNumber, addedValue);
        } else {
            docToRank.put(docNumber, OurFormula);
        }
    }
    //BM25
    private double bm25 (long df, long tf,double len){
        //in our computation of BM25 k=1.4 , b=0.75
        double idf= (Math.log((467767-df+0.5)/(df+0.5)))/(Math.log(2));
        //tf*(k+1)
        double mone=tf*(2.4);
        // tf  + k*(1-b +b* document_length/avg document length)
        double mechane=tf+1.4*(1-0.75+0.75*(len/469.3722708));
        return idf*(mone/mechane);

    }
}
