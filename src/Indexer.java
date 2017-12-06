import java.util.HashMap;
import java.util.LinkedList;

public class Indexer {
    HashMap <String,LinkedList<Document>> Docs;
    public Indexer() {
    }
    public void setDocs (HashMap<String, LinkedList<Document>> _docs) {
        this.Docs=_docs;
        tempPosting();
    }

    private void tempPosting() {
//        for (Document d:Docs) {
//
//        }
    }

}
