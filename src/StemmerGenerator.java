import java.util.ArrayList;
import java.util.HashSet;

public class StemmerGenerator {
    private Stemmer stem;
    private ArrayList<String> need_to_parse;
    private static HashSet<String> hash=new HashSet<String>(0);
    public StemmerGenerator(Stemmer _stem, ArrayList<String> _need_to_parse) {
        this.stem=_stem;
        need_to_parse=_need_to_parse;
    }
    public void chunkStem() {
        for (int k=1;k<need_to_parse.size(); k++){
            String s = need_to_parse.get(k);
            stem.add(s.toCharArray(),s.length());
            stem.stem();
            String wordStemmed= stem.toString().trim();
            if (!hash.contains(wordStemmed)) {
                hash.add(wordStemmed);
            }
        }
        int z= 1+2;
    }

}
