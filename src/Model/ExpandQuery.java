package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Scanner;

public class ExpandQuery {
    String query;
    HashMap<String, Double> term_weight;

    public ExpandQuery(String s) {
        this.query = s;
        term_weight = new HashMap<>();
    }

    public void expand() {
        try {
            String wikipediaApiJSON = "https://en.wikipedia.org/wiki/" + query;
            //connect to wikipedia with the one word query
            org.jsoup.nodes.Document doc = Jsoup.connect(wikipediaApiJSON).ignoreContentType(true).get();
            // selects all the body content
            Elements data = doc.select("#bodyContent");
            //selects all the paragraphs
            Elements paragraphs = data.select("p");
            //checks if we have the query Can be interpreted to more than one term
            if (paragraphs.first().text().contains(" may refer to:")) {
                multiple_values(data);
            } else {
                //iterates the paragraphs and takes words that are in bold or anchor
                for (Element p : paragraphs) {
                    if (term_weight.size() < 6) {
                        Elements bolds = p.getElementsByTag("b");
                        //check if we got any
                        if (bolds.size() > 0)
                            insert_bolds(bolds);
                        insert_anchors( p.getElementsByTag("a"));

                    } else break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insert_bolds(Elements bs) {
        for (Element b : bs) {
            if (term_weight.size() < 6) {
                String word = b.text();
                word = word.toLowerCase();
                //checks that the word is not equal to the query
                if (!word.equals(query)) {
                    term_weight.put(word, 1.0);
                }
            } else break;
        }
    }

    private void insert_anchors(Elements as) {
        for (Element e :as) {
            if (term_weight.size() < 6) {
                Element divGuarantee = e.parent();
                if (divGuarantee.is("sup"))
                    continue;
                else {
                    //REMEMBER TO CHANGE
                   term_weight.put(e.text(),0.7);
                }
            }
            else break;
        }
    }

    private void multiple_values(Elements data) {
        Elements li = data.select("ul").not("h2");
        Elements as = li.select("a");
        for (Element e : as) {
            if (term_weight.size() < 6) {
                Elements f = e.select("span");
                if (f.size() > 0)
                    continue;
                System.out.println(e.attr("title"));
            } else break;
        }

    }
}
