package Model;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

//THIS CLASS JOB IS TO CONNECT BETWEEN ALL OF THE CLASSES IN THE WHOLE PROJECT
//IN ADDITION, THERE IS AN INTERACTION BETWEEN THE MODEL AND THE VIEW CLASSES THAT MAKES THE CODE MODULAR TO CHANGES.

public class Model {
    HashSet<String> stopword = null;
    public LinkedHashMap<Integer,LinkedHashMap<String, Double>> saved;
    public Model() {
        saved=new LinkedHashMap<>();
    }

    // path_corpus tells u where the corpus directory & the stopword file sits
    //path_to save tells you where to save the final posting list & the dictionary
    public long start(String path_corpus, String path_tosave, boolean stemOrNot) {
        try {
            long start = System.currentTimeMillis();
            stopword = DSstopwords(path_corpus);
            ReadFile Fr = new ReadFile(path_corpus + "\\corpus");
            Parse parser = new Parse();
            parser.setStopword(stopword);
            StemmerGenerator StG = new StemmerGenerator(stemOrNot);
            StG.setStopWords(stopword);
            //  Indexer index = new Indexer(path_tosave, stemOrNot);
            for (int file = 0; file <= 72; file++) {
                LinkedList<String> Documents = Fr.fileReader();
                LinkedList<ArrayList<String>> ParsedDocs = parser.ParseFile(Documents);
                LinkedHashMap<String, LinkedList<Document>> StemmedDocs = StG.chunkStem(ParsedDocs);
                //      index.setDocs(StemmedDocs);
            }

            //    index.mergeFiles();
            long end = System.currentTimeMillis();
            return ((end - start) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //never suppose to get here
        return 1;
    }


    //find all the docs that are relevant to the query
    public void findDocs(String st, boolean stemOrNot, int queryNumber) {
        stopword = DSstopwords("");
        Searcher s = new Searcher(stopword, st);
        s.ParseQuery(st);
        ArrayList<String> Query;
        if (stemOrNot) {
            Query = s.stem();
        } else {
            //without stem
            Query = s.getParsedQuery();
        }
        Ranker r = new Ranker(stemOrNot, queryNumber);
        r.setQuery(Query);
        r.setQuerySize(Query.size());
        String[] postLine;
        for (String term : Query) {
            postLine = s.searchPostingList(term);
            if (postLine != null) {
                r.rankWord(postLine, term);
            }
        }
        //sorted docToRank
        LinkedHashMap<String, Double> docToRamkSorted = sortByValue(r.getDocToRank());
        saved.put(queryNumber,docToRamkSorted);
        showResults(docToRamkSorted, queryNumber);
    }

    private void showResults(LinkedHashMap<String, Double> docToRamkSorted, int queryNumber) {
        try {
            int line = 0;
            BufferedWriter rs = new BufferedWriter(new FileWriter("C:\\trec\\showFile.txt",true));
            rs.write("---" + String.valueOf(queryNumber) + "---");
            rs.newLine();
            for (Map.Entry<String, Double> entry : docToRamkSorted.entrySet()) {
                //write 50 line from each query to the file
                if (line >= 50) {
                    break;
                }
                rs.write(entry.getKey());
                rs.newLine();
                rs.flush();
                line++;
            }
            rs.close();
        } catch (Exception e) {
        }
    }
    public void save(String path){
        if (saved==null) {return;}
        for (Map.Entry<Integer,LinkedHashMap<String, Double>> entry:saved.entrySet()) {
            writeTofile(entry.getValue(),entry.getKey(),path);
        }
        saved=null;
    }
    //writes 50 documents that retrieved from the query
    //by the format of track val
    private void writeTofile(LinkedHashMap<String, Double> docToRamkSorted, int queryNumber,String path) {
        try {

            int line = 0;
            BufferedWriter rs = new BufferedWriter(new FileWriter(path, true));
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

    // inserts all the stop words into a hash
    private static HashSet<String> DSstopwords(String path) {
        HashSet<String> stopword = new HashSet<>();
        String line;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(path + "stopword.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        br = new BufferedReader(fr);
        try {
            while ((line = br.readLine()) != null) {
                stopword.add(line);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopword;
    }


    //run all the queries from the query text file
    public void queryChooser(String text, boolean stemOrNot) {
        try {
            init();
            FileReader f = new FileReader(text);
            BufferedReader br = new BufferedReader(f);
            String line = "";
            int queryNum;
            String query;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("<num>")) {
                    String[] number = line.split(" ");
                    queryNum = Integer.parseInt(number[2]);
                    line = br.readLine();
                    query = line.replaceAll("<title> ", "");
                    //run the query
                    findDocs(query, stemOrNot, queryNum);
                }
            }
            //free resources
            br.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //clear from old files to show
    public void init (){
        try {
            Files.delete(Paths.get("C:\\trec\\showFile.txt"));
        } catch (IOException e) {
        }
    }
    //returns array of string
    //1 element ->  Index Size
    //2 element -> Cache
    public String[] sizes(String path, boolean StemOrNot) {
        String Path;
        String[] size = new String[2];
        if (path.equals("") || path.equals("No Directory selected")) {
            Path = "";
        } else {
            Path = path + "\\";
        }
        long postSize;
        if (StemOrNot) {
            File postStem = new File(Path + "PostingListStem.txt");
            postSize = postStem.length();
        } else {
            File postNotStem = new File(Path + "PostingListNoStem.txt");
            postSize = postNotStem.length();
        }
        File Dictionary = new File(Path + "Dictionary.txt");
        File Cache = new File(Path + "Cache.txt");
        Long index = (Dictionary.length() + postSize);
        Long cache = Cache.length();
        size[0] = index.toString();
        size[1] = cache.toString();
        return size;
    }
    //delete data stractures
    public void reset(String path) {
        saved=null;
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {

        }
        System.gc();
    }
}

