import java.io.*;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Created by yosefmeltser on 16/11/2017.
 */

public class ReadFile {
    private final String path;
    //constructor accepts the path to the corpus
    ReadFile(String path ) {
       this.path=path;
   }

    //method responsible for reading files and transfer them into data stractures
   public  void fileReader (){
       BufferedReader br = null;
       FileReader fr = null;
       Parse p= new Parse();
       File folder = new File(path);
       File[] listOfFiles = folder.listFiles();
       LinkedList <String> documents= new LinkedList<>();
       int trashHold=25;
       int counter=0;
       for (int i = 0; i < listOfFiles.length; i++) {
           // each time we read 100 files
           //one more file added to the chunk of 100 files
           //transfer a chunk to the parser when reaches to threshold
           counter+=1;
           System.out.println(i);
           if (trashHold==counter) {
               counter=0;
               p.ParseFile(documents);
               //check if an object was deleted
               documents=null;
               documents=new LinkedList<>();
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
       }


// in case that the doc contains [text]-avoid from sentences that contains meta data about the article
private static String moveForwardLines(BufferedReader line) throws IOException {
    String s = line.readLine();
    while (s != null && !(s.startsWith("  [Text]") || s.startsWith("  [Excerpts]") || s.startsWith("  [Excerpt]"))) {
        s = line.readLine();
    }

    if (s != null)
        return s.substring(8);
    else return "";

}
}
