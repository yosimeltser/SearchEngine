package Model;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;


//THIS CLASS JOB IS TO CONNECT BETWEEN ALL OF THE CLASSES IN THE WHOLE PROJECT
//IN ADDITION, THERE IS AN INTERACTION BETWEEN THE MODEL AND THE VIEW CLASSES THAT MAKES THE CODE MODULAR TO CHANGES.

public class Model {
    HashSet<String> stopword = null;
    boolean expand;
    public LinkedHashMap<Integer,LinkedHashMap<String, Double>> saved;
    public Model() {
        saved=new LinkedHashMap<>();
        expand=false;
    }

    //find all the docs that are relevant to the query
    public void findDocs(String st, boolean stemOrNot, int queryNumber) {
        stopword = DSstopwords("");
        Searcher s = new Searcher(stopword, st,stemOrNot);
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
        LinkedHashMap<String, Double> docToRamkSorted = sortByValue(r.getDocToRank());
        saved.put(queryNumber,docToRamkSorted);
        showResults(docToRamkSorted, queryNumber);
    }
    //run all the queries from the query text file
    public void queryChooser(String text, boolean stemOrNot) {
        try {
            if (text==null || text.equals("")) return;
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
    //write the results to a temp file
    //when the run finished the file opens
    private void showResults(LinkedHashMap<String, Double> docToRamkSorted, int queryNumber) {
        try {
            int trashHold=50;
            if (expand) {
                trashHold=70;
            }
            int line = 0;
            BufferedWriter rs = new BufferedWriter(new FileWriter("showFile.txt",true));
            rs.write("---" + String.valueOf(queryNumber) + "---");
            rs.newLine();
            rs.write("--- The Number Of Retrieved Documents : " +String.valueOf(trashHold)+"---");
            rs.newLine();
            for (Map.Entry<String, Double> entry : docToRamkSorted.entrySet()) {
                //write 50 line from each query to the file
                if (line >= trashHold) {
                    break;
                }
                rs.write(entry.getKey());
                rs.newLine();
                rs.flush();
                line++;
            }
            rs.close();
            expand=false;
        } catch (Exception e) {
        }
    }
    public void save(String path){
        if (saved==null) {return;}
        for (Map.Entry<Integer,LinkedHashMap<String, Double>> entry:saved.entrySet()) {
            writeTofile(entry.getValue(),entry.getKey(),path);
        }
        saved=new LinkedHashMap<>();

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

    //wikipedia expansion
    public void expand(String q, boolean StemOrNot){
        ExpandQuery ex = new ExpandQuery(q);
        ArrayList <String> arr= ex.expand();
        if (arr.size()==0) return;
        String query="";
        for (String s : arr) {
            query=query+" "+ s;
        }
        expand=true;
        findDocs(query,StemOrNot,0);
    }

    //clear from old files to show
    public void init (){
        try {
            Files.delete(Paths.get("showFile.txt"));
        } catch (IOException e) {
        }
    }
    //delete data stractures
    public void reset(String path) {
        saved=new LinkedHashMap<>();
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {

        }
        System.gc();
    }
    //writes 50 documents that retrieved from the query
    //by the format of track val
    private void writeTofile(LinkedHashMap<String, Double> docToRamkSorted, int queryNumber,String path) {
        try {

            int trashHold=50;
            if (expand) {
                trashHold=70;
            }
            int line = 0;
            BufferedWriter rs = new BufferedWriter(new FileWriter(path, true));
            for (Map.Entry<String, Double> entry : docToRamkSorted.entrySet()) {
                //write 50 line from each query to the file
                if (line >= trashHold) {
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
}

