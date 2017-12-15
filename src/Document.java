import java.util.HashMap;
import java.util.Map;

public class Document {
    int docLength;
   // public HashMap <String,Integer> termFr;
    public HashMap<String,TermData> terms;
    public String docId;
    public int maxTermFr;
    public int unique;
    Map.Entry<String, TermData> maxEntry;
    public Document(String _docId) {
        this.docId=_docId;
        terms= new HashMap<>();
    }
    public void addDocLength (int _docLength){
        this.docLength=_docLength;
    }
    public boolean contains (String s) {
          return terms.containsKey(s);
    }
    //this function adds the new string as an object TermData and sets the first index. if the string already exist
    public void add (String s, int i){
        if(terms.containsKey(s)){
            TermData t=terms.get(s);
            t.increment();
        }
        else if (!s.equals(""))  {
            TermData td=new TermData (s,i);
            terms.put(s,td);
        }
    }
    public void setSize(int _size){
        this.docLength=_size;
    }

    public void setMaxTf(){
        maxEntry =null;
        for (Map.Entry<String, TermData> entry : terms.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }
        maxTermFr= maxEntry.getValue().tf;
        uniqueWords();
    }
    private void uniqueWords() {
        unique=terms.size();
    }
}
