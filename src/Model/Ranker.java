package Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Ranker {
    int querySize;
    boolean stemOrNot;
    public Ranker(boolean _stemOrNot) {
        stemOrNot=_stemOrNot;
    }

    public void setQuerySize(int querySize) {
        this.querySize = querySize;
    }
    //dictionary loaded from the Load Class
    HashMap<String,String> dictionary;
    //maps from doc name to query rank...
    HashMap <String,Double> docToRank;
    //maps from doc -> how much of the query words found in to doc
    HashMap <String,Integer> docToWordsFound;
    //sorted docToRank
    LinkedHashMap<String,Double> docToRamkSorted;
    //all the words returned from the query after parse and stem
    ArrayList <String> query;
    //cache loaded from the Load Class
    ArrayList<String> cache;

    public void Cosin(){
        //just for getting fields from the load class
        Load l =new Load();
        dictionary=l.getDictionary();
        cache=l.getLoadedCache();
        docToRank=  new HashMap<String,Double>();
        setQuerySize(query.size());
        docToWordsFound=new HashMap<>();
        for (String term:query) {
            //get rank for each term
            //put the value to the dictionary
            rankWord(term);
        }
        docToRamkSorted=sortByValue(docToRank);;
        writeTofile(docToRamkSorted);
    }
    private long getTermDF(String term) {
        String s=dictionary.get(term);
        int j=0;
        String df="";
        while (s!=null && !s.equals("")&& s.charAt(j)!='C' && s.charAt(j)!='D' ){
            df+=s.charAt(j);
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

    public void rankWord(String term) {
        String s= dictionary.get(term);
        if (s==null) {
            return;
        }
        int j=0;
        j=s.indexOf('C');
        String [] postLine={};
        //If the world is in the disc but not in the cache
        if (j==-1) {
            j=s.indexOf('D');
            int line=Integer.parseInt(s.substring(j+1,s.indexOf('S')));
            if (stemOrNot) {
                postLine = readFromFile(line,"Stemmer\\PostingListStem.txt");
            }
            else {
                postLine = readFromFile(line,"noStemmer\\PostingListNoStem.txt");
            }

        }
        //if the word is in the cache
        else {
            int line= Integer.parseInt(s.substring(j+1,s.indexOf('D')));
            String d=cache.get(line);
            postLine=cache.get(line).split("[<>,\\s+]");
        }
        //starts from 1 hence the term in the 0 place
        for (int i=1;i<postLine.length;i++){
            if (!postLine[i].equals(" ") && !postLine[i].equals("")) {
                String docNumber=postLine[i];
                long tf= Long.parseLong(postLine[++i]);
                long index = Long.parseLong(postLine[++i]);
                weight (getTermDF(term),docNumber,tf,index);
            }
        }

    }

    private void writeTofile(LinkedHashMap<String, Double> docToRamkSorted) {
        try{
            int line=0;
            BufferedWriter rs = new BufferedWriter(new FileWriter("C:\\trec\\results.txt"));
            if (querySize>1) {
                for (Map.Entry<String, Integer> entry:docToWordsFound.entrySet()) {
                    if (entry.getValue()==querySize) {
                        docToRamkSorted.remove(entry.getKey());
                        line++;
                        rs.write("380");
                        rs.write(" 0");
                        rs.write(" " + entry.getKey());
                        rs.write(" 100");
                        rs.write(" 0.1");
                        rs.write(" mt");
                        rs.newLine();
                        rs.flush();
                        line++;
                    }
                }
            }


            for(Map.Entry<String, Double> entry : docToRamkSorted.entrySet()) {
                if(line>= 50 ) {
                    break;
                }
                rs.write("380");
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
        }
        catch (Exception e ) {
        }
    }


    private void weight(long df,String docNumber, long tf, long index) {
        double docRank;
        double maxDocTf;
        double docSize;
        double oneWordQuery;
        double place=0;
        int counter=0;
        String docWeight;
        if (!stemOrNot) {
            docWeight="docs_weights_NoStem.txt";
        }
        else {
            docWeight="docs_weightsStem.txt";
        }
        try (BufferedReader br = new BufferedReader(new FileReader(docWeight))) {
            String line;
            while ((line = br.readLine()) != null) {

                int space=line.indexOf("*");
                String docN = line.substring(0,space);
                //if the document found break
                if (docN.equals(docNumber)) {
                    String [] docProp= line.split("\\*");
                    docRank = Double.parseDouble(docProp[1]);
                    maxDocTf = Double.parseDouble(docProp[2]);
                    docSize = Double.parseDouble(docProp[3]);
                    place= (docSize-index)/docSize;
//0.3*((tf/maxDocTf)*(place)*(Math.log(467767/df)/Math.log(2)))+
                    oneWordQuery=0.5*(computeTfIdfWeighted(df,tf,docSize))+0.5*((tf/maxDocTf)*(place)*(Math.log(467767/df)/Math.log(2)));

                    double cossin=oneWordQuery/((Math.sqrt(querySize))* docRank);
                    if (docToRank.containsKey(docNumber)) {
                        double addedValue=docToRank.get(docNumber)+cossin;
                        docToRank.put(docNumber,addedValue);
                        docToWordsFound.put(docNumber,docToWordsFound.get(docNumber)+1);
                    }
                    else {
                        docToRank.put(docNumber,cossin);
                        docToWordsFound.put(docNumber,1);
                    }
                    return;
                }
            }
        }
        catch (Exception e) {
        }
    }
    public String [] readFromFile (int line,String path){
        String [] s={};
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
           return   lines.skip(line).findFirst().get().split("[<>, ]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
    //code from
    private   <K, V> LinkedHashMap<K, V> sortByValue(HashMap<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2) {
                return ((Comparable<V>) ((Map.Entry<K, V>) (o2)).getValue()).compareTo(((Map.Entry<K, V>) (o1)).getValue());
            }
        });

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext();) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    private double computeTfIdfWeighted (long df, long tf,double len){
        double idf= Math.log(467767/df)/Math.log(2);
        //k=10
        double weight=(10*len)/(469.3722708);
        return (tf/(tf+weight))*idf;

    }
}
