


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class StemmerGenerator {
    private Stemmer stem;
    LinkedList<ArrayList<String>> ParsedDocs;
    //represents  docs list of 100 files
    //LinkedList<Document> Docs = new LinkedList<>() ;
    public HashMap<String, LinkedList<Document>> temp;
    public LinkedHashMap<String, LinkedList<Document>> termToDocs;
    //DOCUMENT FREQUENCY OF THE TERMS
    private static HashMap<String, Integer> termDf = new HashMap<>();
    public static HashMap<String, String> already_seen = new HashMap<>();
    private Indexer index;
    //Dictionary
    int tHold;
    public static HashSet<String> stopword;
    //If we Stem the words after parse => stemOrNot=true Else stem=false
    public static boolean stemOrNot = true;
    public static HashMap<String, Integer> cache = new HashMap<>();
    public static BufferedWriter docProperties;
    public StemmerGenerator() {
        try {
            docProperties  = new BufferedWriter(new FileWriter("docProperties.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStopWords(HashSet<String> sw) {
        this.stopword = sw;
    }

    public HashMap<String, Integer> getDf() {
        return termDf;
    }

    public HashMap<String, Integer> getCache() {
        return cache;
    }

    public LinkedHashMap<String, LinkedList<Document>> chunkStem(LinkedList<ArrayList<String>> parsedDocs) {
        stem = new Stemmer();
        ParsedDocs = parsedDocs;
        temp = new HashMap<>();
        termToDocs= new LinkedHashMap<>();
        for (ArrayList<String> need_to_parse : ParsedDocs) {
            Document doc = new Document(need_to_parse.get(0));
            doc.setSize(need_to_parse.size() - 1);
            for (int k = 1; k < need_to_parse.size(); k++) {
                String s = need_to_parse.get(k);
                //DON'T STEM TERMS THAT HAVE MULTIPLE WORDS
                String wordStemmed = "";
                if (s.contains(" ")) {
                    wordStemmed = s;
                    already_seen.put(s, wordStemmed);
                }
                if (stemOrNot) {
                    //Exactly one word
                    //First Time
                    if (!already_seen.containsKey(s)) {
                        stem.add(s.toCharArray(), s.length());
                        stem.stem();
                        wordStemmed = stem.toString().trim();
                        already_seen.put(s, wordStemmed);
                    }
                    //Already Seen, At lest one time
                    else {
                        wordStemmed = already_seen.get(s);
                    }
                }
                //Not getting stemmed
                else {
                    wordStemmed = s;
                }
                //Already seen the world, get from the dictionary

                //Clean Stop Words
                if (stopword.contains(wordStemmed)) {
                    wordStemmed = "";
                }

                if (!wordStemmed.equals("")) {
                    //df
                    if (!doc.contains(wordStemmed)) {
                        if (temp.containsKey(wordStemmed)) {
                            temp.get(wordStemmed).addFirst(doc);
                        } else {
                            LinkedList<Document> docs = new LinkedList<>();
                            docs.add(doc);
                            temp.put(wordStemmed, docs);
                        }
                        //First time that we see the term in doc
                        if (termDf.containsKey(wordStemmed)) {
                            termDf.put(wordStemmed, termDf.get(wordStemmed) + 1);
                        } else {
                            termDf.put(wordStemmed, 1);
                        }

                    }
                    //tf
                    doc.add(wordStemmed, k);
                    //Cache Memory
                    //sum of tf's in the whole corpus
                    if (cache.containsKey(wordStemmed)) {
                        cache.put(wordStemmed, cache.get(wordStemmed) + 1);
                    } else {
                        cache.put(wordStemmed, 1);
                    }
                }
            }
            doc.setMaxTf();
            try {
                docProperties.write("doc num= " +doc.docId + " unique= " + doc.unique + " Max TF= " + doc.maxTermFr + " doc length="+ doc.docLength);
                docProperties.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Sorts by tf, temp posting list
        for (Map.Entry<String, LinkedList<Document>> entry : temp.entrySet()) {
            entry.getValue().sort(new Comparator<Document>() {
                @Override
                public int compare(Document d1, Document d2) {
                    String key = entry.getKey();
                    if (d1.terms.get(key).getTf() > d2.terms.get(key).getTf()) {
                        return -1;
                    } else if (d1.terms.get(key).getTf() < d2.terms.get(key).getTf()) {
                        return 1;
                    } else {
                        return 0;
                    }

                }
            });
        }
        //Sort Hash Map
        //Replace Tree Map
        ArrayList<String> arr = new ArrayList<>(temp.keySet());
        Collections.sort(arr);
        for (int i = 0; i < arr.size(); i++) {
        //    System.out.println("end");
            String s = arr.get(i);
            LinkedList<Document> val = temp.get(s);
            termToDocs.put(s, val);

        }

        stem = null;
        ParsedDocs = null;
        temp = null;

        return termToDocs;
    }
}
