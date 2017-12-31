package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Scanner;

public class ExpandQuery {

    public  void expand(){
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Type something that you want me to search on the internet...");
            String nextLine = scanner.nextLine();
            if (nextLine == null)
                System.out.println("enter query");
            System.out.println("Searching on the web....");
            String wikipediaApiJSON = "https://en.wikipedia.org/wiki/"
                    + nextLine;
            //connect to wikipedia with the one word query
            org.jsoup.nodes.Document doc = Jsoup.connect(wikipediaApiJSON).ignoreContentType(true).get();
            // selects all the body content
            Elements data = doc.select("#bodyContent");
            //selects all the paragraphs
            Elements paragraphs = data.select("p");
            //checks if we have if the query Can be interpreted to more than one term
            if (paragraphs.first().text().contains(" may refer to:")) {
                multiple_values(data);
            } else {
                //iterates the paragraphs and takes words that are in bold or anchor
                for (Element p : paragraphs) {
                    System.out.println(p.getElementsByTag("b").text());
                    for (Element e : p.getElementsByTag("a")) {
                        Element divGuarantee = e.parent();
                        if (divGuarantee.is("sup"))
                            continue;
                        else {
                            System.out.println(e.text());
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void multiple_values(Elements data) {
        Elements li = data.select("ul").not("h2");
        Elements as = li.select("a");

        for (Element e : as) {
            Elements f= e.select("span");
            if(f.size()>0)
                continue;
            System.out.println(e.attr("title"));

        }
    }
}
