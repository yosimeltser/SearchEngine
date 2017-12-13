
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


public class Parse {

    private static Pattern whitespace = Pattern.compile("\\s+");
    private static HashSet<String> stopword = new HashSet<>();
    private LinkedList<ArrayList<String>> Docs;
    Pattern del_chars, round_up, yyyy_yy, dd_th, yyyy, hyphen, dot, days;
    //make data structure for stop words

    public Parse() {
        //prepare the hash for all the stop words
        DSstopwords();
        del_chars = Pattern.compile("[^\\w && [^%.-]]+");
        round_up = Pattern.compile("\\d+\\.\\d+");
        yyyy_yy = Pattern.compile("^\\d{4}?$|^\\d{2}?$");
        dd_th = Pattern.compile("^\\d{1,2}[th]+$");
        yyyy = Pattern.compile("^\\d{4}$");
        hyphen = Pattern.compile("--+");
        dot = Pattern.compile("[.]+");
        days = Pattern.compile("^\\d{1,2}$");

    }

    // inserts all the stopwords into a hash
    private static void DSstopwords() {
        String line;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader("C:\\project\\SearchEngine\\src\\resource\\stopword.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        br = new BufferedReader(fr);
        try {
            while ((line = br.readLine()) != null) {
                stopword.add(line);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ParseFile(LinkedList<String> text) {
        Docs = new LinkedList<ArrayList<String>>();
        Iterator<String> itr = text.iterator();
        // iterates all the files that came from readFile
        while (itr.hasNext()) {
            String s = itr.next();
            int x;
            ArrayList<String> need_to_parse = new ArrayList<>(Arrays.asList(whitespace.split(s)));
            //i=1 because the first term in the string is the DOC_NUMBER
            for (int i = 1; i < need_to_parse.size(); i++) {
                delTags(need_to_parse, i);
                //first thing check if the word isn't a stop word
                if (deleteStop(i, need_to_parse) && !need_to_parse.get(i).equals("")) {
                    deleteChars(need_to_parse, i);
                    USA(need_to_parse, i);
                    need_to_parse.set(i, roudUp(need_to_parse.get(i)));
                    need_to_parse.set(i, convPrecent(need_to_parse.get(i)));
                    x = checkIfMonth(need_to_parse.get(i));
                    if (x > 0) {
                        if (i < need_to_parse.size()) {
                            i = month_year(x, need_to_parse, i);
                        }
                    }
                    i = capitalLetters(need_to_parse, i);
                }
            }
            Docs.add(need_to_parse);
        }
        //sendes the chunk of documents into the stemmer
        Stemmer stem = new Stemmer();
        StemmerGenerator stemGen = new StemmerGenerator(stem, Docs);
        stemGen.chunkStem();
    }

    //Delete Every Tag that you see
    //For Example <p> will become empty string (<P>->"")
    private void delTags(ArrayList<String> need_to_parse, int i) {
        String s = need_to_parse.get(i);
        if (!(s.equals("")) && s.charAt(0) == '<' && s.charAt(s.length() - 1) == '>') {
            need_to_parse.set(i, "");
        }
    }

    //delete everything that is not alphanumeric except dots
    private void deleteChars(ArrayList<String> need_to_parse, int i) {
        need_to_parse.set(i, hyphen.matcher(need_to_parse.get(i)).replaceAll(""));
        //    String s=need_to_parse.get(i);
        need_to_parse.set(i, del_chars.matcher(need_to_parse.get(i)).replaceAll(""));
    }

    private int capitalLetters(ArrayList<String> need_to_parse, int i) {
        int index = i;
        // deleteChars(need_to_parse,index);
        String s = need_to_parse.get(i);
        StringBuilder buffer = new StringBuilder();
        boolean flag = false;
        boolean conc = false;
        if (!s.equals("") && Character.isUpperCase(s.charAt(0))) {
            //  s = s.replaceAll(dot.toString(), "");
            s = s.toLowerCase();
            need_to_parse.set(index, s);
            buffer.append(s);
            flag = true;
            while (flag) {
                if (index + 1 < need_to_parse.size()) {
                    index++;
                    deleteChars(need_to_parse, index);
                    s = need_to_parse.get(index);
                    if (!s.equals("") && Character.isUpperCase(s.charAt(0))) {
                        s = s.replaceAll(dot.toString(), "");
                        s = s.toLowerCase();
                        buffer.append(" " + s);
                        need_to_parse.set(index, s);
                        conc = true;
                    } else {
                        if (index > 0) {
                            index--;
                        }
                        flag = false;
                    }
                } else {
                    break;
                }
            }
        }
        if (conc) {
            need_to_parse.add(buffer.toString());
        }
        return index;
    }

    //deletes stop words
    private static boolean deleteStop(int i, ArrayList<String> need_to_parse) {
        if (stopword.contains(need_to_parse.get(i))) {
            need_to_parse.set(i, "");
            return false;
        }
        return true;
    }

    //NEW RULE
//We have noticed that the token U.S has a lot of instances
//So we decided that there is more chances that the user serached for "usa" instead U.S.
    private static void USA(ArrayList<String> need_to_parse, int i) {
        if (need_to_parse.get(i).equals("U.S.")) {
            need_to_parse.set(i, "usa");
        }
    }

    //returns the number of a month if  string s is a month
    private static int checkIfMonth(String s) {
        //check if to define it as static at the beginning of the class
        String[] month = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        //    s = deleteDots(s);
        for (int i = 0; i < month.length; i++) {
            if (month[i].equalsIgnoreCase(s))
                return i + 1;
        }
        return -1;
    }

    //Example: from 3.55555 -> 3.56
    //converts the second digit after the floating point up
    //PLUS IF NOT A NUMBER DELETE THE DOTS
    private String roudUp(String s) {
        if (round_up.matcher(s).matches()) {
            double x = Double.parseDouble(s);
            x = Math.round(x * 100);
            x = x / 100;
            String n = Double.toString(x);
            return n;
        } else {
            return dot.matcher(s).replaceAll("");
        }
    }

    // implements the law that every occurrences of % or "percentage"
    private String convPrecent(String s) {
        if (s.startsWith("percentage")) {
            s = s.replace("percentage", "percent");
            return s;
        } else if (s.indexOf('%') > 0) {
            s = s.replaceAll("%", " percent");
            return s;
        } else return s;

        //  return Pattern.compile("%|perecentge").matcher(s).replaceAll(" percent");
    }

    private int month_year(int x, ArrayList<String> arr, int i) {
        String s = "";
        // in order to display months with zero
        if (x < 10)
            s = "0" + x;

        else {
            s = x + "";
        }

        //fits to the next patterns
        //DDth Month YYYY, DD Month YYYY , DD Month YY , Month Year
        // TRY
        if (i + 1 < arr.size()) {
            deleteChars(arr, i + 1);
            arr.set(i + 1, roudUp(arr.get(i + 1)));

            if (i + 1 < arr.size() && yyyy_yy.matcher(arr.get(i + 1)).matches()) {
                arr.set(i + 1, /*deleteDots*/(arr.get(i + 1)));
                //DDth Month YYYY
                if (i > 0 && dd_th.matcher(arr.get(i - 1)).matches()) {
                    arr.set(i - 1, formattingDayMonth(arr.get(i - 1)));
                    s = arr.get(i - 1).substring(0, arr.get(i - 1).length() - 2) + "/" + s + "/" + arr.get(i + 1);
                    arr.set(i - 1, s);
                    arr.set(i, "");
                    arr.set(i + 1, "");
                    return i + 1;
                    //DD Month YYYY
                } else if (i > 0 && days.matcher(arr.get(i - 1)).matches()) {
                    arr.set(i - 1, formattingDayMonth(arr.get(i - 1)));
                    if (Pattern.compile("^\\d{2}$").matcher(arr.get(i + 1)).matches()) {
                        s = arr.get(i - 1) + "/" + s + "/" + "19" + arr.get(i + 1);
                    } else {
                        s = arr.get(i - 1) + "/" + s + "/" + arr.get(i + 1);
                    }
                    arr.set(i - 1, s);
                    arr.set(i, "");
                    arr.set(i + 1, "");
                    return i + 1;
                    //Month Year
                } else if (i + 1 < arr.size() && yyyy.matcher(arr.get(i + 1)).matches()) {
                    s = s + "/" + arr.get(i + 1);
                    arr.set(i, s);
                    arr.set(i + 1, "");
                    return i + 1;
                }
            }
        }
        //Month DD , Month DD YYYY
        if (i + 1 < arr.size() && days.matcher(arr.get(i + 1)).matches()) {
            //Month DD, YYYY
            if (i + 2 < arr.size()) {
                deleteChars(arr, i + 2);
                arr.set(i + 2, roudUp(arr.get(i + 2)));

                if (yyyy.matcher(arr.get(i + 2)).matches()) {
                    arr.set(i + 1, formattingDayMonth(arr.get(i + 1)));
                    arr.set(i + 2, /*deleteDots*/(arr.get(i + 2)));
                    s = arr.get(i + 1) + "/" + s + "/" + arr.get(i + 2);
                    arr.set(i, s);
                    arr.set(i + 1, "");
                    arr.set(i + 2, "");
                    return i + 2;
                }
                //Month DD
                else {
                    arr.set(i + 1, /*deleteDots*/(arr.get(i + 1)));
                    s = arr.get(i + 1) + "/" + s;
                    arr.set(i, s);
                    arr.set(i + 1, "");
                    return i + 1;
                }
            }
        }
        //DD month
        if (i > 0 && days.matcher(arr.get(i - 1)).matches()) {
            arr.set(i - 1, formattingDayMonth(arr.get(i - 1)));
            s = arr.get(i - 1) + "/" + s;
            arr.set(i - 1, s);
            arr.set(i, "");
            return i;
        }
        //NEW RULE
        //first rule
        //22-23 January => 22/01 23/01
        if (i > 0 && Pattern.compile("^\\d{2}-\\d{2}$").matcher(arr.get(i - 1)).matches()) {
            String d = arr.get(i - 1);
            arr.set(i - 1, arr.get(i - 1).substring(0, 2) + "/" + s);
            arr.set(i, d.substring(3, 5) + "/" + s);
            return i;
        }
        return i;
    }

    // responsible to add zero to dates that have days that are smaller than ten.
    private static String formattingDayMonth(String s) {
        if (Pattern.compile("^\\d([th])*$").matcher(s).matches()) {
            return "0" + s;
        } else {
            return s;
        }
    }

    //deleting dots & commas from the end of a sentence
    private static String deleteDots(String s) {
        if (s.contains(".") || s.contains(",")) {
            String news = s.substring(0, s.length() - 1);
            return news;
        }
        return s;
    }
}

