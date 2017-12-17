package Model;
import java.util.Comparator;
// saves all the data below about a term
public class TermData implements Comparator ,Comparable {
    String name;
    int tf;
    private int first_index;

    public TermData(String s, int index) {
        this.name = s;
        this.first_index = index;
        this.tf = 1;

    }
// increment the tf of a word in a document in 1.
    public void increment() {
        this.tf++;
    }

    public int getTf() {
        return tf;
    }

    @Override
    public int compare(Object left, Object right) {
        TermData t1 = (TermData) left;
        TermData t2 = (TermData) right;
        return t1.name.compareTo(t2.name);
    }

    @Override
    public int compareTo(Object o) {
        TermData t1 = (TermData) o;
        return this.name.compareTo(t1.name);
    }
// returns the index where the word appeared in the document for the first time
    public int getFirst_index() {
        return first_index;
    }

    public String toString() {
        return name +" " + tf;
    }
}