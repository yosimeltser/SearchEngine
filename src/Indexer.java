import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Indexer {
    TreeMap <String,LinkedList<Document>> Docs;
    public static int i=0;
    public Indexer() {
    }
    public void setDocs (TreeMap<String, LinkedList<Document>> _docs) {
        this.Docs=_docs;
        tempPosting();
    }

    private void tempPosting() {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter( new FileWriter( "postingList" +i+".txt"));
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

        }

}
