import java.io.*;
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
       File folder = new File(path);
       File[] listOfFiles = folder.listFiles();
       LinkedList <String> documents= new LinkedList<>();
       for (int i = 0; i < listOfFiles.length; i++) {
             if (listOfFiles[i].isDirectory()) {
                 try {
                     String [] s=listOfFiles[i].list();
                     //check
                     fr = new FileReader(listOfFiles[i]+"\\"+s[0]);
                     br = new BufferedReader(fr);

                     try {
                         String doc="";
                         String line = "";
                         while ((line = br.readLine()) != null ) {
                             if (line.startsWith("<TEXT>")) {
                                 while ((!line.startsWith("</TEXT>") && ((line = br.readLine()) != null ))) {
                                     if(line.startsWith("Language:")){
                                         line=moveForwardLines(br);
                                     }
                                     doc +=line;
                                 }
                                 documents.add(doc);
                                 doc="";
                             }
                         }
                         br.close();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }

                 } catch (FileNotFoundException e) {
                     e.printStackTrace();
                 }
             }
           }
           return documents;
       }


// in case that the doc contains [text]-avoid from sentences that contains meta data about the article
   private static String moveForwardLines(BufferedReader line) throws IOException {
       String s=line.readLine();
       if(s!= null && s.startsWith("Article")) {
           while (s != null && !s.startsWith("  [Text]")) {
               s = line.readLine();
           }

       }
       if(s!=null)
        return s.substring(8);
       else return "";

   }
}
