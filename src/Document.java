import java.util.HashMap;

public class Document {
    int docLength;
    public HashMap <String,Integer> termFr;
    public String docId;
    public Document(String _docId) {
        this.docId=_docId;
        termFr= new HashMap<String,Integer>();
    }
    public void addTerm (HashMap<String,Integer> _termFr) {
        this.termFr=_termFr;
    }
    public void addDocLength (int _docLength){
        this.docLength=_docLength;
    }
    public boolean contains (String s) {
        return termFr.containsKey(s);
    }
    public void add (String s){
        if (termFr.containsKey(s)) {
            int fr=termFr.get(s)+1;
            termFr.put(s,fr);
        }
        else if (!s.equals(""))  {
            termFr.put(s,1);
        }
    }
    public void setSize(int _size){
        this.docLength=_size;
    }
}
