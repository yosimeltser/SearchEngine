package Model;

import java.util.*;

public class Ranker {
    ArrayList<String> loadedCache;
    HashMap<String,String> dictionary;
    ArrayList <String> query;
    public void Cosin(){
        ArrayList <Integer> Dfs=getVectorDf();

    }

    private ArrayList<Integer> getVectorDf() {
        ArrayList <Integer> Dfs=new ArrayList<>();
        for (String term: query) {
            Dfs.add(getTermDF(term));
        }
        return Dfs;
    }
    private int getTermDF(String term) {
        String s=dictionary.get(term);
        int j=0;
        String df="";
        while (s.charAt(j)!='C' ||s.charAt(j)!='D' ){
            df+=s.charAt(j);
            j++;
        }
        return Integer.parseInt(df);
    }

    public void setLoadedCache(ArrayList<String> loadedCache) {
        this.loadedCache = loadedCache;
    }

    public void setDictionary(HashMap<String, String> dictionary) {
        this.dictionary = dictionary;
    }

    public void setQuery(ArrayList<String> query) {
        this.query = query;
    }
}
