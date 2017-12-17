
package Model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
//this class responsible to stem the chunk if the user choose the option
//and in addition sorts the data stricture that holds the temp posting list.
public class StemmerGenerator {
    private Stemmer stem;
    LinkedList<ArrayList<String>> ParsedDocs;
    //temp is the unsorted posting list
    public HashMap<String, LinkedList<Document>> temp;
    //termToDocs is after sorting the temp HashMap
    public LinkedHashMap<String, LinkedList<Document>> termToDocs;
    //DOCUMENT FREQUENCY OF THE TERMS
    private static HashMap<String, Integer> termDf = new HashMap<>();
    //holds all the words that were stem, for no Stemming them again
    public static HashMap<String, String> already_seen = new HashMap<>();
    public static HashSet<String> stopword;
    //If we Stem the words after parse => stemOrNot=true Else stem=false
    public static boolean stemOrNot = true;
    //holds all the occurrences of the term in the corpus
    public static HashMap<String, Integer> Sumtf = new HashMap<>();
    public static BufferedWriter docProperties;
    public StemmerGenerator(boolean _stemOrNot) {
        stemOrNot=_stemOrNot;
        try {
            docProperties  = new BufferedWriter(new FileWriter("docProperties.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public StemmerGenerator(){}
    public void setStopWords(HashSet<String> sw) {
        this.stopword = sw;
    }

    public HashMap<String, Integer> getDf() {
        return termDf;
    }

    public HashMap<String, Integer> getSumtf() {
        return Sumtf;
    }
    //stems the whole chunk if asked, and sort the temp posting list
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
                String wordStemmed = "";
                if (stemOrNot) {
                    //DON'T STEM TERMS THAT HAVE MULTIPLE WORDS
                    if (s.contains(" ")) {
                        wordStemmed = s;
                        already_seen.put(s, wordStemmed);
                    }
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
                        //ADDS THE NEW DOC TO THE LIST
                        if (temp.containsKey(wordStemmed)) {
                            temp.get(wordStemmed).addFirst(doc);
                        } else {
                            // IT'S NOT IN TEMP. CREATE NEW LIST AND ADD IT TO TEMP
                            LinkedList<Document> docs = new LinkedList<>();
                            docs.add(doc);
                            temp.put(wordStemmed, docs);
                        }

                        //First time that we see the term in THIS CHUNK
                        if (termDf.containsKey(wordStemmed)) {
                            termDf.put(wordStemmed, termDf.get(wordStemmed) + 1);
                        } else {
                            termDf.put(wordStemmed, 1);
                        }

                    }
                    //tf , the responsibility of the add function in document
                    doc.add(wordStemmed, k);
                    //Cache Memory
                    //sum of tf's in the whole corpus
                    if (Sumtf.containsKey(wordStemmed)) {
                        Sumtf.put(wordStemmed, Sumtf.get(wordStemmed) + 1);
                    } else {
                        Sumtf.put(wordStemmed, 1);
                    }
                }
            }
            doc.setMaxTf();
            try {
                docProperties.write("doc num= " +doc.docId + " unique= " + doc.unique + " Max TF= " + doc.maxTermFr + " doc length="+ doc.docLength);
                docProperties.newLine();
                docProperties.flush();
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
        //Sorts the Hash Map
        //Replace Tree Map, for better prefoemence
        ArrayList<String> arr = new ArrayList<>(temp.keySet());
        Collections.sort(arr);
        for (int i = 0; i < arr.size(); i++) {
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
