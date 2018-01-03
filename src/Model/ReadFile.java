package Model;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Created by yosefmeltser on 16/11/2017.
 */

public class ReadFile {
    private final String path;
    public static HashMap<String,String> docs_path= new HashMap<>();
    //constructor accepts the path to the corpus
    File[] listOfFiles;
    public int i=0;
    ReadFile(String path ) {
       this.path=path;
        File folder = new File(path);
        listOfFiles = folder.listFiles();
        i=0;
   }

    //this method responsible for reading files and transfer them into data stracturs
   public  LinkedList <String> fileReader (){
       BufferedReader br = null;
       FileReader fr = null;
       LinkedList <String> documents= new LinkedList<>();
       int counter=0;
       for (; i < listOfFiles.length; i++) {
           System.out.println(i);
           // each time we read 25 files
           //transfer a chunk to the parser when the counter reaches to threshold
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
                                 insert(line,s[0]);
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
    public void insert(String doc,String path){
       docs_path.put(doc,path);
    }
    public void print(){
        try {
            FileWriter fileWriter = new FileWriter("docs_path.txt");
            BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
            for(HashMap.Entry<String,String> entry : docs_path.entrySet()){
                bufferedWriter.write(entry.getKey()+" "+entry.getValue());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
