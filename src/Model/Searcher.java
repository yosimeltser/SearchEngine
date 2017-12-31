package Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

public class Searcher {
    HashSet<String> stopword;
    Stemmer stem;
    ArrayList<String> ParsedQuery;
    boolean stemOrNot = true;

    public Searcher(HashSet<String> _stopword, Stemmer _stem) {
        this.stem = _stem;
        stopword = _stopword;
    }

    public void setParsedQuery(ArrayList<String> parsedQuery) {
        ParsedQuery = parsedQuery;
    }

    //Stemmer for a Query
    public ArrayList<String> stem() {
        ArrayList<String> StemmedQuery = new ArrayList<>();
        for (int k = 1; k < ParsedQuery.size(); k++) {
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
}
