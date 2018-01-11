package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class ExpandQuery {
    String query;
    ArrayList<String> terms;

    public ExpandQuery(String s) {
        this.query = s;
        terms = new ArrayList<>();
    }

    public ArrayList<String> expand() {
        try {
            if (!query.equals("")) {
                String wikipediaApiJSON = "https://en.wikipedia.org/wiki/" + query;
                //connect to wikipedia with the one word query
                org.jsoup.nodes.Document doc = Jsoup.connect(wikipediaApiJSON).ignoreContentType(true).get();
                // selects all the body content
                Elements data = doc.select("#bodyContent");
                //selects all the paragraphs
                Elements paragraphs = data.select("p");
                //checks if we have the query Can be interpreted to more than one term
                if (check_if_multipleValues(data)) {
                    multiple_values(data);
                } else {
                    //iterates the paragraphs and takes words that are in bold or anchor
                    for (Element p : paragraphs) {
                        if (terms.size() < 6) {
                            Elements bolds = p.getElementsByTag("b");
                            //check if we got any
                            if (bolds.size() > 0)
                                insert_bolds(bolds);
                            insert_anchors(p.getElementsByTag("a"));

                        } else break;
                    }
                }

            }
            } catch(Exception e){
            e.printStackTrace();
        }
        return terms;

    }

    private void insert_bolds(Elements bs) {
        for (Element b : bs) {
            if (terms.size() < 6) {
                String word = b.text();
                word = word.toLowerCase();
                //checks that the word is not equal to the query
                if (!word.equals(query)) {
                    terms.add(word);
                }
            } else break;
        }
    }

    private void insert_anchors(Elements as) {
        int i = 1;
        for (Element e : as) {
            if (terms.size() < 6) {
                Element divGuarantee = e.parent();
                if (divGuarantee.is("sup"))
                    continue;
                else {
                    //REMEMBER TO CHANGE
                    terms.add(e.text());
                    i++;
                }
            } else break;
        }
    }

    private boolean check_if_multipleValues(Elements elements) {
        Elements f = elements.select("td.mbox-text");
        if (f.text().contains("disambiguation"))
            return true;
        else return false;
    }

    private void multiple_values(Elements data) {
        int i = 1;
        Elements li = data.select("ul").not("h2");
        Elements as = li.select("a");
        for (Element e : as) {
            if (terms.size() < 6) {
                Elements f = e.select("span");
                if (f.size() > 0)
                    continue;
                terms.add(e.attr("title"));
            } else break;
        }

    }
}
