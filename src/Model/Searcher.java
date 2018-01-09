package Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
//
public class Searcher {
    HashSet<String> stopword;
    Stemmer stem;
    //query after parse
    ArrayList<String> ParsedQuery;
    boolean stemOrNot = true;

    public Searcher(HashSet<String> _stopword, String st) {
        stopword=_stopword;
        stem=new Stemmer();
    }

    public void setParsedQuery(ArrayList<String> parsedQuery) {
        ParsedQuery = parsedQuery;
    }
    //Stemmer for a Query
    public ArrayList<String> stem() {
        ArrayList<String> StemmedQuery = new ArrayList<>();
        for (int k = 0; k < ParsedQuery.size(); k++) {
            String s = ParsedQuery.get(k);
            String wordStemmed = "";
            if (stemOrNot) {
                stem.add(s.toCharArray(), s.length());
                stem.stem();
                wordStemmed = stem.toString().trim();
            } else {
                wordStemmed = s;
            }
            StemmedQuery.add(wordStemmed);
        }
        return StemmedQuery;
    }

    public ArrayList<String> getParsedQuery() {
        return ParsedQuery;
    }

    //Parse Query
    public  void ParseQuery (String st){
        LinkedList <String> arr=new LinkedList<>();
        //not having doc number
        String q="* " + st;
        arr.addFirst(q);
        Parse queryParse = new Parse();
        queryParse.setStopword(stopword);
        LinkedList<ArrayList<String>> ParsedQuery=queryParse.ParseFile(arr);
        //remove the * array list
        ArrayList<String> PQ=ParsedQuery.getFirst();
        ArrayList<String> query=new ArrayList<>();
        for (String s: PQ) {
            if (!s.equals("") && !s.equals("*")) {
                query.add(s);
            }
        }
        setParsedQuery(query);
    }
}
