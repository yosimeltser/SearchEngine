public class termLine {
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
}
