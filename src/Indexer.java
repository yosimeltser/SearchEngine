import java.io.*;
import java.util.*;

public class Indexer {
    TreeMap <String,LinkedList<Document>> Docs;
    public static int i=0;
    public Indexer() {
        new File("PostingList").mkdir();
    }


    public void setDocs (TreeMap<String, LinkedList<Document>> _docs) {
        this.Docs=_docs;
        tempPosting();
    }

    private void tempPosting() {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter( new FileWriter( "PostingList//postingList" +i+".txt"));
            i++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, LinkedList<Document>> entry : Docs.entrySet()) {
            String key = entry.getKey();
            LinkedList<Document> value = entry.getValue();
            try {
                bw.write(key+" ");
                for (Document document:value) {
                    bw.write("<"+document.docId +"," +document.termFr.get(key) + ">"+ " ");
                    bw.flush();
                }
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (i==2){
            mergeFiles();
        }
        }

    private void mergeFiles() {
        int i=0;

        try {
            BufferedWriter bw=new BufferedWriter( new FileWriter( "PostingList//postingList" +"merge"+".txt"));

            BufferedReader s0 = new BufferedReader( new FileReader("PostingList//postingList" +i+".txt" ));
            s0.readLine();
            i++;
            BufferedReader s1 = new BufferedReader( new FileReader("PostingList//postingList" +i+".txt" ));
            String line0=s0.readLine();
            String line1=s1.readLine();
            //Keys
            String term1=line0.substring(0,line0.indexOf("<")-1);
            String term2=line1.substring(0,line1.indexOf("<")-1);
            if (term1.compareTo(term2)==1){
                bw.write(line0);
            }
            else
            {
                bw.write(line1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
