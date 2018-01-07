package Model;

import java.io.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SearchDoc {
    private static Pattern whitespace = Pattern.compile("\\s+");
    private LinkedHashMap<String, Double> weight_sen;

    public SearchDoc() {
        weight_sen = new LinkedHashMap<>();
    }

    public void rank(String doc) {
        String document = geText(doc);
        Document d = new Document(doc);
        //split to words in the sentences
        String[] words = document.split(whitespace.toString());
        for (int i = 0; i < words.length; i++) {
            d.add(words[i], i);
        }
        d.setMaxTf();
        //split to sentences
        String[] sen = document.split("(?<=[a-z])\\.\\s+");
        for (int j = 0; j < sen.length; j++) {
            weight_sen.put(sen[j], 0.0);
        }
        int counter = 0;

        for (Map.Entry<String, Double> entry : weight_sen.entrySet()) {
            counter++;
            int place = 0;
            if (counter > 1 && counter < 16)
                place = 1;
            //to check if the sentence is between 2-20
            String sent = entry.getKey();
            String[] word_sen = sent.split(whitespace.toString());
            //iterate on each word in the sentence
            double w = 0;
            for (int i = 0; i < word_sen.length; i++) {

                if (d.contains(word_sen[i])) {
                    w += 0.5 * (d.getTf(word_sen[i]) / d.maxTermFr) + place * 0.5;
                }
            }
            weight_sen.put(sent, w);
        }


    }


    //CHANGE TO THAT THE PATH TO THE CORPUS IS THE PATH OF THE PROJECT
    private String geText(String d) {
        String file = getDirectory(d);
        String path = file + "\\" + file;
        StringBuilder doc = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader("D:\\שנה ג\\IR\\corpus\\" + path));
            String line = "";
            while ((line = br.readLine()) != null) {
                // takes the doc ID and checks if it's the desirable document
                if (line.startsWith("<DOCNO>")) {
                    line = Pattern.compile("<DOCNO>").matcher(line).replaceAll("");
                    line = Pattern.compile("</DOCNO>").matcher(line).replaceAll("");
                    line = Pattern.compile(" ").matcher(line).replaceAll("");
                    if (d.equals(line)) {
                        while ((line = br.readLine()) != null) {
                            if (line.startsWith("<TEXT>")) {
                                while (((line = br.readLine()) != null) && (!line.startsWith("</TEXT>"))) {
                                    line += "\n";
                                    //concatenate all the document as one string
                                    doc.append(line);
                                }
                                return doc.toString();
                            }
                        }
                    }
                }
            }
            //free resources
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    // finds the file where the document exists
    private static String getDirectory(String d) {
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("docs_path.txt"));
            while ((line = reader.readLine()) != null) {
                String[] dp = line.split(" ");
                if (dp[0].equals(d)) {
                    return dp[1];
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
