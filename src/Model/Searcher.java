package Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

//
public class Searcher {
    HashSet<String> stopword;
    Stemmer stem;
    //query after parse
    ArrayList<String> ParsedQuery;
    boolean stemOrNot = true;
    //dictionary loaded from the Load class
    //cache loaded from the Load Class
    ArrayList<String> cache;
    HashMap<String, String> dictionary;
    public Searcher(HashSet<String> _stopword, String st) {
        stopword=_stopword;
        stem=new Stemmer();
        //just for getting fields from the load class
        Load l = new Load();
        dictionary = l.getDictionary();
        //For future use
        //right now not in use
        cache = l.getLoadedCache();
    }

    //set the query after parse
    public void setParsedQuery(ArrayList<String> parsedQuery) {
        ParsedQuery = parsedQuery;
    }

    //Stemmer for the Query
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
    //rank one word that exists in the query and in the document
    public String[] searchPostingList(String term) {
        String s = dictionary.get(term);
        //if the word not exist in the dictionary, Stop!
        if (s == null) {
            return null;
        }
        //Variable postLine will contain the whole information from the line that relevant to the term in the posting list
        String[] postLine = {};
        //read from disc
        //cache is not in use right now
        int j = s.indexOf('D');
        int line = Integer.parseInt(s.substring(j + 1, s.indexOf('S')));
        if (stemOrNot) {
            return readFromFile(line, "Stemmer\\PostingListStem.txt");
        } else {
            return postLine = readFromFile(line, "noStemmer\\PostingListNoStem.txt");
        }
    }
    //func from java 8, seeks the line in the file given a line number
    public String[] readFromFile(int line, String path) {
        String[] s = {};
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            return lines.skip(line).findFirst().get().split("[<>, ]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
