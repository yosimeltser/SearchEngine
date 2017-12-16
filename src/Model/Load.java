package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Load {
    ArrayList<String> loadedCache;
    HashMap <String,String> dictionary;
    public Load(String path ) {
        if (path.equals("") || path.equals("No Directory selected")) {
            path = "";
        } else {
            path = path + "//";
        }
        dictionary=new HashMap<>();
        loadedCache= new ArrayList<>();
        loadCache(path);
        loadDictionary(path);
    }
    //Loads the dictionary into an hash map
    // key -> term
    // value -> df , pointer to cache if exists, pointer to disc
    public void loadDictionary(String path ) {
        try{
            BufferedReader br = new BufferedReader(new FileReader(path + "Dictionary.txt"));
            String line="";
            while ((line = br.readLine()) != null) {
                String []arr= line.split(" ");
                System.out.println(line);
                if (arr[4]=="C=X"){
                    dictionary.put( arr[1],arr[3]+"D"+arr[6]);
                }
                else {
                    dictionary.put(arr[1],arr[3]+"C"+arr[5]+"D"+arr[7]);
                }
            }
        }
        catch (Exception e){

        }
    }

    public void loadCache(String path ) {
        try{
            BufferedReader br = new BufferedReader(new FileReader(path + "Cache.txt"));
            String line="";
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                loadedCache.add(line);
            }
            br.close();
        }
        catch (Exception e){}
    }
}
