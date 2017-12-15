package Model;
import java.io.*;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Created by yosefmeltser on 16/11/2017.
 */

public class ReadFile {
    private final String path;
    //constructor accepts the path to the corpus
    File[] listOfFiles;
    ReadFile(String path ) {
       this.path=path;
        File folder = new File(path);
        listOfFiles = folder.listFiles();
   }
    public int i=0;
    //method responsible for reading files and transfer them into data stractures
   public  LinkedList <String> fileReader (){
       BufferedReader br = null;
       FileReader fr = null;
       LinkedList <String> documents= new LinkedList<>();
       int counter=0;
       for (; i < listOfFiles.length; i++) {
           System.out.println(i);
           // each time we read 25 files
           //one more file added to the chunk of 100 files
           //transfer a chunk to the parser when reaches to threshold
           if (counter==25){
               counter=0;
               return documents;
           }
           else {
               counter++;
           }
           if (listOfFiles[i].isDirectory()) {
                 try {
                     String [] s=listOfFiles[i].list();
                     fr = new FileReader(listOfFiles[i]+"\\"+s[0]);
                      br = new BufferedReader(fr);

                     try {
                         StringBuilder doc=new StringBuilder();
                         String line = "";
                         while ((line = br.readLine()) != null ) {
                             // takes the doc ID and puts it in each beginning of the document
                             if (line.startsWith("<DOCNO>")){
                                 line=Pattern.compile("<DOCNO>").matcher(line).replaceAll("");
                                 line=Pattern.compile("</DOCNO>").matcher(line).replaceAll("");
                                 line=Pattern.compile(" ").matcher(line).replaceAll("");
                                 doc.append(line);
                                  doc.append(" ");
                             }
                             if (line.startsWith("<TEXT>")) {
                                 while (((line = br.readLine()) != null ) && (!line.startsWith("</TEXT>") )) {
                                     if(line.startsWith("Language:")){
                                         line=moveForwardLines(br);
                                     }
                                     line+="\n";
                                     //concatenate all the document as one string
                                     doc.append(line);
                                 }
                                 //adds the container another doc
                                 documents.add(doc.toString());
                                 // reset the document
                                 doc=new StringBuilder();
                             }
                         }
                         //free resources
                         br.close();
                         fr.close();
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
    String s = line.readLine();
    while (s != null && !(s.startsWith("  [Text]") || s.startsWith("  [Excerpts]") || s.startsWith("  [Excerpt]") || s.startsWith("  [Abstract]"))) {
        s = line.readLine();
    }

    if (s != null)
        return s.substring(8);
    else return "";

}
}
