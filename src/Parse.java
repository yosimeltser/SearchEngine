import com.sun.xml.internal.ws.api.ha.StickyFeature;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Parse {
    private LinkedList <String> text;


   public Parse (LinkedList<String> s){
       this.text=s;
   }
public void ParseNumbers (){
    Iterator<String> itr=text.iterator();
    while (itr.hasNext()){
       String s= itr.next();
       int x;
       String [] need_to_parse=s.split("\\s+");
       for(int i=0;i<need_to_parse.length;i++ ){
           need_to_parse[i]=delCommas(need_to_parse[i]);
           need_to_parse[i]=roudUp(need_to_parse[i]);
           need_to_parse[i]=convPrecent(need_to_parse[i]);
           if ((x = checkIfMonth(need_to_parse[i])) > 0) {
               if (i < need_to_parse.length) {
                   i=month_year(x,need_to_parse,i);
               }
           }
    }
    }
}

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
        if(s.matches("\\d+\\.\\d+")) {
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
        if(s.matches("^(([-+] ?)?[0-9]+(,[0-9]+)+)?((.[0-9]+))")) {
            s.contains(",");
            return  s.replace(",","");
        }
        return s;
    }

    private  String convPrecent (String s){
        String n= s.replaceAll("%|perecentge"," percent");
        return n;
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
        if(arr[i+1].matches("^\\d{4}$|\\d{2}$")){
            if(arr[i-1].matches("^\\d{1,2}[th]+$"))
            {
                arr[i-1]=formattingDayMonth(arr[i-1]);
                s=arr[i-1].substring(0,arr[i-1].length()-2)+"/"+s+"/"+arr[i+1]  ;
                arr[i-1]=s;
                arr[i]="";
                arr[i+1]="";
                return i+2;
            }
            else if(arr[i-1].matches("^\\d{1,2}$"))
            {
                arr[i-1]=formattingDayMonth(arr[i-1]);
                if (arr[i+1].matches("^\\d{2}$")){
                    s = arr[i - 1] + "/" + s + "/" +"19"+ arr[i + 1];
                }
                else  {
                    s = arr[i - 1] + "/" + s + "/" + arr[i + 1];
                }
                arr[i-1]=s;
                arr[i]="";
                arr[i+1]="";
                return i+2;

            }
            else if (arr[i+1].matches("^\\d{4}$")){
                s=s+"/"+arr[i+1];
                arr[i]=s;
                arr[i+1]="";
            }
        }
        //Month DD , Month DD YYYY
         if (arr[i+1].matches("^\\d{1,2}[,]*")){
            arr[i+1]=formattingDayMonth(arr[i+1]);
            if (arr[i+2].matches("^\\d{4}$")){
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
        if (arr[i-1].matches("^\\d{1,2}$")) {
            arr[i-1]=formattingDayMonth(arr[i-1]);
            s = arr[i-1] + "/" + s;
            arr[i-1]=s;
            arr[i]="";
            return i;
        }
     return 0;
    }
    private String formattingDayMonth(String s) {
        if (s.matches("^\\d[th ,]+$")){
            return "0"+s;
        }
        else {
            return s;
        }
    }

}

