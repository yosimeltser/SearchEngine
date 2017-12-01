import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


public class Parse {
    //private LinkedList <String> text;
    private static Pattern pattern = Pattern.compile("[\\s+\\+:(){}\\[\\]]");
    private static HashSet<String> stopword = new HashSet<String>();
    //make data structure of stop worlds
    private static void DSstopwords() {
        String line;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader("C:\\Users\\yosefmeltser\\Desktop\\study\\איחזור מידע\\עבודות\\עבודת בית 1\\stopwords.txt");
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
        DSstopwords();
        Iterator<String> itr = text.iterator();
        while (itr.hasNext()) {
            String s = itr.next();
            int x;
            ArrayList<String> need_to_parse = new ArrayList<String>(Arrays.asList(pattern.split(s)));
            //i=1 because the first term in the string is the DOC_NUMBER
            for (int i = 1; i < need_to_parse.size(); i++) {
                if (deleteStop(i,need_to_parse) && need_to_parse.get(i)!="") {
                    need_to_parse.set(i, delCommas(need_to_parse.get(i)));
                    need_to_parse.set(i, roudUp(need_to_parse.get(i)));
                    need_to_parse.set(i, convPrecent(need_to_parse.get(i)));
                    USA(need_to_parse, i);
                    x = checkIfMonth(need_to_parse.get(i));
                    if (x > 0) {
                        if (i < need_to_parse.size()) {
                            i = month_year(x, need_to_parse, i);
                        }
                    }
                    capitalLetters(need_to_parse,i);
                }

            }
        }
    }

    private int capitalLetters(ArrayList<String> need_to_parse, int i) {
        int index=i;
        String s=need_to_parse.get(i);
        String buffer="";
        boolean flag=false;
        boolean conc=false;
        if(!s.equals("") && Character.isUpperCase(s.charAt(0))){
            s=s.replaceAll(",","");
            s=s.toLowerCase();
            need_to_parse.set(index,s);
            buffer+=s;
            flag=true;
            while (flag){
                if (index+1<need_to_parse.size()) {
                index++;
                    s = need_to_parse.get(index);
                    if (!s.equals("") && Character.isUpperCase(s.charAt(0))) {
                        s = s.toLowerCase();
                        s = s.replaceAll(",", "");
                        buffer += " " + s;
                        need_to_parse.set(index, s);
                        conc = true;
                    }
                }
                else {
                    if (index>0) {
                        index--;
                    }
                    flag=false;
                }
            }
        }
        if (conc){
            need_to_parse.add(buffer);
        }
        return index;
    }

    //delete stop words
    private static boolean deleteStop(int i,ArrayList<String> need_to_parse){
        if (stopword.contains(need_to_parse.get(i))){
            need_to_parse.set(i,"");
            return false;
        }
        return true;
    }
    //NEW RULE
//We have noticed that the token U.S has a lot of instances
//So we decided that there is more chances that the user serached for "usa" instead U.A
    private static void USA(ArrayList<String> need_to_parse, int i) {
        if (need_to_parse.get(i).equals("U.S.")) {
            need_to_parse.set(i, "usa");
        }
    }

    //returns the number of a month if  string s is a month
    private static int checkIfMonth(String s) {
        //check if to define it as static at the beginning of the class
        String[] month = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        s = deleteDots(s);
        for (int i = 0; i < month.length; i++) {
            if (month[i].equalsIgnoreCase(s))
                return i + 1;
        }
        return -1;
    }

    //Example: from 3.55555 -> 3.56
    //converts the second digit after the floating point up
    private String roudUp(String s) {
        if (Pattern.compile("\\d+\\.\\d+").matcher(s).matches()) {
            double x = Double.parseDouble(s);
            x = Math.round(x * 100);
            x = x / 100;
            String n = Double.toString(x);
            return n;
        }
        return s;
    }

    //Example: 1,345 -> 1345
    //removes commas from the numbers
    private String delCommas(String s) {
        if (Pattern.compile("^(([-+] ?)?[0-9]+(,[0-9]+)+)?((.[0-9]+))").matcher(s).matches()) {
            s.contains(",");
            return s.replace(",", "");
        }
        return s;
    }

    private String convPrecent(String s) {
        return Pattern.compile("%|perecentge").matcher(s).replaceAll(" percent");
    }

    private int month_year(int x, ArrayList<String> arr, int i) {
        String s = "";
        if (x < 10)
            s = "0" + x;

        else {
            s = x + "";
        }

        //fits to the next patterns
        //DDth Month YYYY, DD Month YYYY , DD Month YY , Month Year
        if (i+1<arr.size() &&Pattern.compile("^\\d{4}[.,]?$|\\d{2}[.,]?$").matcher(arr.get(i + 1)).matches()) {
            arr.set(i + 1, deleteDots(arr.get(i + 1)));
            //DDth Month YYYY
            if (i > 0 && Pattern.compile("^\\d{1,2}[th]+$").matcher(arr.get(i - 1)).matches()) {
                arr.set(i - 1, formattingDayMonth(arr.get(i - 1)));
                s = arr.get(i - 1).substring(0, arr.get(i - 1).length() - 2) + "/" + s + "/" + arr.get(i + 1);
                arr.set(i - 1, s);
                arr.set(i, "");
                arr.set(i + 1, "");
                return i + 1;
            //DD Month YYYY
            } else if (i > 0 && Pattern.compile("^\\d{1,2}$").matcher(arr.get(i - 1)).matches()) {
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
            } else if (i+1<arr.size() && Pattern.compile("^\\d{4}$").matcher(arr.get(i + 1)).matches()) {
                s = s + "/" + arr.get(i + 1);
                arr.set(i, s);
                arr.set(i + 1, "");
                return i + 1;
            }
        }
        //Month DD , Month DD YYYY
        if (i+1<arr.size() && Pattern.compile("^\\d{1,2}[,]*").matcher(arr.get(i + 1)).matches()) {
            //Month DD YYYY
            if (Pattern.compile("^\\d{4}[.,]?$").matcher(arr.get(i + 2)).matches()) {
                arr.set(i + 2, deleteDots(arr.get(i + 2)));
                s = arr.get(i + 1).substring(0, arr.get(i + 1).length() - 1) + "/" + s + "/" + arr.get(i + 2);
                arr.set(i, s);
                arr.set(i + 1, "");
                arr.set(i + 2, "");
                return i + 2;
            }
            //Month DD
            else {
                arr.set(i + 1, deleteDots(arr.get(i + 1)));
                s = arr.get(i + 1) + "/" + s;
                arr.set(i, s);
                arr.set(i + 1, "");
                return i + 1;
            }
        }
        //DD month
        if (i > 0 && Pattern.compile("^\\d{1,2}$").matcher(arr.get(i - 1)).matches()) {
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

    private String formattingDayMonth(String s) {
        if (Pattern.compile("^\\d([th ,])*$").matcher(s).matches()) {
            return "0" + s;
        } else {
            return s;
        }
    }

    //deleting dots & commas from the end of a sentence
    private static String deleteDots(String s) {
        if (s.contains(".")|| s.contains(",")) {
            String news = s.substring(0, s.length() - 1);
            return news;
        }
        return s;
    }
}

