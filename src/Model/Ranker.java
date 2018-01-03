package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Ranker {
    int querySize;

    public void setQuerySize(int querySize) {
        this.querySize = querySize;
    }
    //dictionary loaded from the Load Class
    HashMap<String,String> dictionary;
    //maps from doc name to query rank...
    HashMap<String,Double> docToRank;
    //all the words returned from the query after parse and stem
    ArrayList <String> query;
    //cache loaded from the Load Class
    ArrayList<String> cache;
    public void Cosin(){
        //just for getting fields from the load class
        Load l =new Load();
        dictionary=l.getDictionary();
        cache=l.getLoadedCache();
        for (String term:query) {
            //get rank for each term
            //put the value to the dictionary
            rankWord(term);
        }
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
        int j=s.indexOf('C');
        String [] postLine={};
        //If the world is in the disc but not in the cache
        if (j==-1) {
            j=s.indexOf('D');
            postLine = readFromFile(Integer.parseInt(s.substring(j+1,s.indexOf('s'))),"Dictionary");
        }
        //if the word is in the cache
        else {
            int line= Integer.parseInt(s.substring(j+1,s.indexOf('D')));
            postLine=cache.get(line).split("[<>, ]");
        }
        //starts from 1 hence the term in the 0 place
        for (int i=1;i<postLine.length;i++){
            String docNumber=postLine[i]; 
            long tf= Long.parseLong(postLine[++i]);
            long index = Long.parseLong(postLine[++i]);
            weight (getTermDF(term),docNumber,tf,index);
        }
    }

    private void weight(long df,String docNumber, long tf, long index) {
        long docRank;
        long maxDocTf;
        long docSize;
        double oneWordQuery;
        try (BufferedReader br = new BufferedReader(new FileReader("docs_weights_not_stemmed.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                int space=line.indexOf("*");
                String docN = line.substring(0,space);
                //if the document found break
                if (docN.equals(docNumber)) {
                    String [] docProp= line.split("\\*");
                    docRank = Long.parseLong(docProp[1]);
                    maxDocTf = Long.parseLong(docProp[2]);
                    docSize = Long.parseLong(docProp[3]);
                    oneWordQuery=(tf/maxDocTf)*((docSize-index)/docSize)*(Math.log10((467767/df)));
                    double cossin=oneWordQuery/((Math.sqrt(querySize))* docRank);
                    if (docToRank.containsKey(docNumber)) {
                        double addedValue=docToRank.get(docNumber)+cossin;
                        docToRank.put(docNumber,addedValue);
                    }
                    else {
                        docToRank.put(docNumber,cossin);
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
}
