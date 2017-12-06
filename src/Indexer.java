import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class Indexer {
    Map <String,LinkedList<Document>> Docs;
    public Indexer() {
    }
    public void setDocs (HashMap<String, LinkedList<Document>> _docs) {
        this.Docs=_docs;
        tempPosting();
    }

    private void tempPosting() {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter( new FileWriter( "postingList.txt",true ));
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
