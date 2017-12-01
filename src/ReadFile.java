import java.io.*;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Created by yosefmeltser on 16/11/2017.
 */

public class ReadFile {
    private final String path;
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
       int trashHold=100;
       int counter=0;
       for (int i = 0; i < listOfFiles.length; i++) {
           //counter+1
           //one more file added to the chunk of 100 files
           //transfer a chunk to the parser
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
                         String doc="";
                         String line = "";
                         while ((line = br.readLine()) != null ) {
                             if (line.startsWith("<DOCNO>")){
                                 line=Pattern.compile("<DOCNO>").matcher(line).replaceAll("");
                                 line=Pattern.compile("</DOCNO>").matcher(line).replaceAll("");
                                 line=Pattern.compile(" ").matcher(line).replaceAll("");
//                                 line.replaceAll("<DOCNO>","");
//                                 line.replaceAll("</DOCNO>","");
//                                 line.replaceAll(" ","");
                                 doc+=line+ " ";
                             }
                             if (line.startsWith("<TEXT>")) {
                                 while (((line = br.readLine()) != null ) && (!line.startsWith("</TEXT>") )) {
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
       }


// in case that the doc contains [text]-avoid from sentences that contains meta data about the article
private static String moveForwardLines(BufferedReader line) throws IOException {
    String s = line.readLine();
  //  System.out.println(s);
    //   if(s!= null && s.startsWith("Article")) {
    while (s != null && !(s.startsWith("  [Text]") || s.startsWith("  [Excerpts]") || s.startsWith("  [Excerpt]"))) {
      //  System.out.println(s);
        s = line.readLine();
    }

    if (s != null)
        return s.substring(8);
    else return "";

}
}
