package Model;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
//THE CLASS REPRESENTS EACH DOCUMENT IN THE CORPUS
public class Document {

    double DocWeight;
    int docLength;
    // public HashMap <String,Integer> termFr;
    public HashMap<String, TermData> terms;
    public String docId;
    public int maxTermFr;
    public int unique;
    Map.Entry<String, TermData> maxEntry;

    public Document(String _docId) {
        this.docId = _docId;
        terms = new HashMap<>();
    }

    public boolean contains(String s) {

        return terms.containsKey(s);
    }

    //this function adds the new string as an object TermData and sets the first index. if the string already exist
    public void add(String s, int i) {
        if (terms.containsKey(s)) {
            TermData t = terms.get(s);
            t.increment();
        } else if (!s.equals("")) {
            TermData td = new TermData(s, i);
            terms.put(s, td);
        }
    }

    //sets the size of the document
    public void setSize(int _size) {
        this.docLength = _size;
    }

    //sets the term that has the highest TF
    public void setMaxTf() {
        maxEntry = null;
        for (Map.Entry<String, TermData> entry : terms.entrySet()) {
            if (maxEntry == null || entry.getValue().tf > (maxEntry.getValue().tf)) {
                maxEntry = entry;
            }
        }
        //if there are non relevant words in the Document
        if (maxEntry == null) {
            maxTermFr=0;
        }
        //At least one relevant word
        else {
            maxTermFr = maxEntry.getValue().tf;
            uniqueWords();
        }

    }
    //sets the number of word per document
    //with out repeats
    private void uniqueWords() {
        unique = terms.size();
    }
    private void DocWeight() {
        Load l = new Load();
        HashMap<String, String> dic= l.getDictionary();
        for (Map.Entry<String, TermData> entry : terms.entrySet()) {
            int df=Integer.parseInt(dic.get(entry.getKey()));
            int N= this.docLength;
            int maxTf=this.maxTermFr;
            int tf= entry.getValue().tf;
            int index = entry.getValue().getFirst_index();
            DocWeight=+func(df,N,maxTf,tf,index);
        }
        DocWeight=Math.sqrt(DocWeight);

        try {
            BufferedWriter bufferedWriter= new BufferedWriter(new FileWriter("docs_weights.txt",true));
            bufferedWriter.write(docId + " "+DocWeight);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public double func(int df,int N,int tmaxTf,int tf,int index ){
        return Math.pow((tf/tmaxTf)*((N-index)/N)*(Math.log10((N/df))),2);
    }
}
