import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by yosefmeltser on 16/11/2017.
 */

public class ReadFile {
    private final String path;

    ReadFile(String path ) {
       this.path=path;
   }
    //method responsible for reading files and transfer them into data stractures
   public  LinkedList <String> fileReader (){
       BufferedReader br = null;
       FileReader fr = null;
       try {
           fr = new FileReader(path);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }
       br = new BufferedReader(fr);
       LinkedList <String> documents= new LinkedList<>();
       try {
           String doc="";
           String line = "";
           String firstHandler = "";
           while ((line = br.readLine()) != null ) {
               if (line.startsWith("<TEXT>")) {
                   while ((!line.startsWith("</TEXT>") && ((line = br.readLine()) != null ))) {
                       doc +=line;
                   }
                   documents.add(doc);
                   doc="";
               }
           }
           System.out.println("");
       } catch (IOException e) {
           e.printStackTrace();
       }

       return documents;
   }
}
