package Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Ranker {
    int querySize;
    boolean stemOrNot;
    int queryNumber;
    public Ranker(boolean _stemOrNot, int _queryNumber) {
        stemOrNot = _stemOrNot;
        queryNumber=_queryNumber;
    }

    public void setQuerySize(int querySize) {
        this.querySize = querySize;
    }

    //dictionary loaded from the Load class
    HashMap<String, String> dictionary;
    //dictionary loaded from the Load Class
    HashMap<String, String> DocWeightDic;
    //maps from doc name to query rank...
    HashMap<String, Double> docToRank;
    //sorted docToRank
    LinkedHashMap<String, Double> docToRamkSorted;
    //all the words returned from the query after parse and stem
    ArrayList<String> query;
    //cache loaded from the Load Class
    ArrayList<String> cache;

    public void Cosin() {
        //just for getting fields from the load class
        Load l = new Load();
        dictionary = l.getDictionary();
        //For future use
        //right now not in use
        cache = l.getLoadedCache();
        docToRank = new HashMap<String, Double>();
        DocWeightDic = l.getDocWeightDic();
        setQuerySize(query.size());
        for (String term : query) {
            //get rank for each term
            //put the value to the dictionary
            rankWord(term);
        }
        docToRamkSorted = sortByValue(docToRank);
        writeTofile(docToRamkSorted);
    }
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
    public void rankWord(String term) {
        String s = dictionary.get(term);
        //if the word not exist in the dictionary, Stop!
        if (s == null) {
            return;
        }
        //Variable postLine will contain the whole information from the line that relevant to the term in the posting list
        String[] postLine = {};
        //read from disc
        //cache is not in use right now
        int j = s.indexOf('D');
        int line = Integer.parseInt(s.substring(j + 1, s.indexOf('S')));
        if (stemOrNot) {
            postLine = readFromFile(line, "Stemmer\\PostingListStem.txt");
        } else {
            postLine = readFromFile(line, "noStemmer\\PostingListNoStem.txt");
        }

        //starts from 1 hence the term in the 0 place
        for (int i = 1; i < postLine.length; i++) {
            if (!postLine[i].equals(" ") && !postLine[i].equals("")) {
                String docNumber = postLine[i];
                long tf = Long.parseLong(postLine[++i]);
                long index = Long.parseLong(postLine[++i]);
                weight(getTermDF(term), docNumber, tf, index);
            }
        }
    }

    private void writeTofile(LinkedHashMap<String, Double> docToRamkSorted) {
        try {
            int line = 0;

            BufferedWriter rs = new BufferedWriter(new FileWriter("C:\\trec\\results.txt",true));
            for (Map.Entry<String, Double> entry : docToRamkSorted.entrySet()) {
                //write 50 line from each query to the file
                if (line >= 50) {
                    break;
                }
            //FORMAT OF TRECEVAL
                rs.write(String.valueOf(queryNumber));
                rs.write(" 0");
                rs.write(" " + entry.getKey());
                rs.write(" 100");
                rs.write(" 0.1");
                rs.write(" mt");
                rs.newLine();
                rs.flush();
                line++;
            }
            rs.close();
        } catch (Exception e) {
        }
    }

    //wight each word by the doc
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
        double MB25=(computeTfIdfWeighted( df,tf, docSize));
        //half from cossin and half from MB25
        double OurFormula= 0.5*cossin + 0.5*MB25;
        if (docToRank.containsKey(docNumber)) {
            double addedValue = docToRank.get(docNumber) + OurFormula;
            docToRank.put(docNumber, addedValue);
        } else {
            docToRank.put(docNumber, OurFormula);
        }
        return;
    }


    public String[] readFromFile(int line, String path) {
        String[] s = {};
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            return lines.skip(line).findFirst().get().split("[<>, ]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    //Sort hash map
    private <K, V> LinkedHashMap<K, V> sortByValue(HashMap<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2) {
                return ((Comparable<V>) ((Map.Entry<K, V>) (o2)).getValue()).compareTo(((Map.Entry<K, V>) (o1)).getValue());
            }
        });

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    //BM25
    private double computeTfIdfWeighted (long df, long tf,double len){
        double idf= (Math.log((467767-df+0.5)/(df+0.5)))/(Math.log(2));
        double mone=tf*(2.4);
        double mechane=tf+1.4*(1-0.75+0.75*(len/469.3722708));
        return idf*(mone/mechane);

    }
}
