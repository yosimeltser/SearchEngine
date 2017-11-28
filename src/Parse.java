import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


public class Parse {
    //private LinkedList <String> text;
    private static Pattern pattern = Pattern.compile("\\s+");
    private static HashSet<String> stopword=new HashSet<String>();
    private static void DSstopwords()  {
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
        while ((line = br.readLine()) != null ){
            stopword.add(line);
        }
        fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ParseFile (LinkedList<String> text){
        Iterator<String> itr=text.iterator();
        while (itr.hasNext()){
            String s= itr.next();
            int x;
            //List<String> myList = new ArrayList<String>(Arrays.asList(pattern.split(s)));
            String [] need_to_parse=pattern.split(s);
            for(int i=0;i<need_to_parse.length;i++ ){
                need_to_parse[i]=delCommas(need_to_parse[i]);
                need_to_parse[i]=roudUp(need_to_parse[i]);
                need_to_parse[i]=convPrecent(need_to_parse[i]);
                USA(need_to_parse,i);
                if ((x = checkIfMonth(need_to_parse[i])) > 0) {
                    if (i < need_to_parse.length) {
                        i=month_year(x,need_to_parse,i);
                    }
                }
            }
        }
    }
    //NEW RULE
//We have noticed that the token U.S has a lot of instances
//So we decided that there is more chances that the user serached for "usa" instead U.A
    private static void USA (String [] need_to_parse,int i) {
        if (need_to_parse[i].equals("U.S")) {
            need_to_parse[i]= "usa";
        }
    }
    //returns the number of a month if  string s is a month
    private static int checkIfMonth(String s){
        String[]month={"January","February","March","April","May","June","July","August","September","October","November","December"};
        for (int i = 0; i <month.length ; i++) {
            if(month[i].equalsIgnoreCase(s))
                return i+1;
        }
        return  -1;
    }
    //Example: from 3.55555 -> 3.56
    //converts the second digit after the floating point up
    private  String roudUp(String s) {
        if(Pattern.compile("\\d+\\.\\d+").matcher(s).matches()) {
            double x=Double.parseDouble(s);
            x = Math.round(x*100);
            x=x/100 ;
            String n= Double.toString(x);
            return n;
        }
        return s;
    }
    //Example: 1,345 -> 1345
    //removes commas from the numbers
    private  String delCommas(String s) {
        if(Pattern.compile("^(([-+] ?)?[0-9]+(,[0-9]+)+)?((.[0-9]+))").matcher(s).matches()) {
            s.contains(",");
            return  s.replace(",","");
        }
        return s;
    }

    private  String convPrecent (String s){
        return Pattern.compile("%|perecentge").matcher(s).replaceAll(" percent");
    }
    private int month_year(int x, String[] arr, int i){
        String s="";
        if(x<10)
            s="0"+x;

        else {
            s = x+"";
        }

        //fits to the next pattens
        //DDth Month YYYY, DD Month YYYY , DD Month YY , Month Year
        if(Pattern.compile("^\\d{4}$|\\d{2}$").matcher(arr[i+1]).matches()){
            if(i>0 && Pattern.compile("^\\d{1,2}[th]+$").matcher(arr[i-1]).matches())
            {
                arr[i-1]=formattingDayMonth(arr[i-1]);
                s=arr[i-1].substring(0,arr[i-1].length()-2)+"/"+s+"/"+arr[i+1]  ;
                arr[i-1]=s;
                arr[i]="";
                arr[i+1]="";
                return i+1;
            }
            else if(i>0 && Pattern.compile("^\\d{1,2}$").matcher(arr[i-1]).matches())
            {
                arr[i-1]=formattingDayMonth(arr[i-1]);
                if ( Pattern.compile("^\\d{2}$").matcher(arr[i+1]).matches()){
                    s = arr[i - 1] + "/" + s + "/" +"19"+ arr[i + 1];
                }
                else  {
                    s = arr[i - 1] + "/" + s + "/" + arr[i + 1];
                }
                arr[i-1]=s;
                arr[i]="";
                arr[i+1]="";
                return i+1;

            }
            else if ( Pattern.compile("^\\d{4}$").matcher(arr[i+1]).matches()){
                s=s+"/"+arr[i+1];
                arr[i]=s;
                arr[i+1]="";
                return i+1;
            }
        }
        //Month DD , Month DD YYYY
        if (Pattern.compile("^\\d{1,2}[,]*").matcher(arr[i+1]).matches()){
            if ( Pattern.compile("^\\d{4}$").matcher(arr[i+2]).matches()){
                s = arr[i+1].substring(0,arr[i+1].length()-1) + "/" + s + "/" + arr[i + 2];
                arr[i]=s;
                arr[i+1]="";
                arr[i+2]="";
                return i+2;
            }
            else{
                s = arr[i+1] + "/" + s;
                arr[i]=s;
                arr[i+1]="";
                return i+1;
            }
        }
        if (i>0 && Pattern.compile("^\\d{1,2}$").matcher(arr[i-1]).matches()) {
            arr[i-1]=formattingDayMonth(arr[i-1]);
            s = arr[i-1] + "/" + s;
            arr[i-1]=s;
            arr[i]="";
            return i;
        }
        //NEW RULE
        //first rule
        //22-23 January => 22/01 23/01
        if (i>0 &&Pattern.compile("^\\d{2}-\\d{2}$").matcher(arr[i-1]).matches()){
            arr[i-1]=arr[i-1].substring(0,2)+"/"+s;
            arr[i]=arr[i-1].substring(3,5)+"/"+s;
            return i;
        }
        return i;
    }
    private String formattingDayMonth(String s) {
        if (Pattern.compile("^\\d([th ,])*$").matcher(s).matches()){
            return "0"+s;
        }
        else {
            return s;
        }
    }
}

