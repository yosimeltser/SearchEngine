package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
//THIS FILES REPRESENTS THE LOAD OF THE FILES FROM THE DISC TO THE DATA STRACTURS
public class Load {
    public static ArrayList<String> loadedCache;
    public static HashMap <String,String> dictionary;
    public static HashMap <String,String> docWeightDic;
    public Load (){}
    public Load(boolean stemOrNot ) {
        String path="";
        if (stemOrNot) {
            path+="Stemmer\\";
        }
        else {
            path+="noStemmer\\";
        }
        dictionary=new HashMap<>();
        loadedCache= new ArrayList<>();
        docWeightDic=new HashMap<>();
        loadDocWeight(stemOrNot);
        loadCache(path);
        loadDictionary(path);
    }
    //Loads the dictionary into an hash map
    // key -> term
    // value -> df , pointer to cache if exists, pointer to disc

    public static ArrayList<String> getLoadedCache() {
        return loadedCache;
    }

    public void loadDictionary(String path ) {
        try{
            BufferedReader br = new BufferedReader(new FileReader(path + "Dictionary.txt"));
            String line="";
            while ((line = br.readLine()) != null) {
                String []arr= line.split("\\*");
                if (arr[4].equals("C=X")){
                    dictionary.put( arr[1],arr[3]+"D"+arr[6]+"STF"+arr[8]);
                }
                else {
                    dictionary.put(arr[1],arr[3]+"C"+arr[5]+"D"+arr[7]+"STF"+arr[9]);
                }
            }
            br.close();
        }
        catch (Exception e){

        }
    }
    //BRUTE FORCE LOAD, STRING BY STRING TO THE ARRAY LIST
    public void loadCache(String path ) {
        try{
            BufferedReader br = new BufferedReader(new FileReader(path + "Cache.txt"));
            String line="";
            while ((line = br.readLine()) != null) {
                loadedCache.add(line);
            }
            br.close();
        }
        catch (Exception e){}
    }

    public HashMap<String, String> getDictionary() {
        return dictionary;
    }
    public void loadDocWeight(boolean stemOrNot){
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
                int space = line.indexOf("*");
                String docN = line.substring(0,space);
                String details=line.substring(space+1,line.length());
                docWeightDic.put(docN,details);
            }
        }
    catch(Exception e) {
            }
        }

    public static HashMap<String, String> getDocWeightDic() {
        return docWeightDic;
    }
}
