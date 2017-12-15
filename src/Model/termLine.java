package Model;
import java.util.Comparator;

public class termLine implements Comparator, Comparable {
    String term;
    String Link;
    public termLine(String _term, String _Link ) {
    this.term=_term;
    this.Link=_Link;
    }

    public String getTerm() {
        return term;
    }

    public String getLink() {
        return Link;
    }

    @Override
    public boolean equals(Object o) {
        termLine termLine= (termLine) o;
        return termLine.term.equals(this.term);
    }
    @Override
    public int hashCode() {
        return this.term.hashCode();
    }
    @Override
    public int compare(Object left, Object right) {
         termLine t1=(termLine) left;
         termLine t2=(termLine) right;
         return t1.term.compareTo(t2.term);
    }

    @Override
    public int compareTo(Object o) {
        termLine t= (termLine)o;
           return this.term.compareTo(t.term);
    }
}
