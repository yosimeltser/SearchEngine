import java.util.Iterator;
import java.util.LinkedList;

public class Parse {
    private LinkedList <String> text;

   public Parse (LinkedList<String> s){
       this.text=s;
   }
public void ParseNumbers (){
    Iterator<String> itr=text.iterator();
    while (itr.hasNext()){
       String s= itr.next();
       String [] need_to_parse=s.split("( )|(  )");
       for(int i=0;i<need_to_parse.length;i++ ){
           if(need_to_parse[i].matches("\\d+\\.\\d+")) {
           }
    }
    }
}
    //Example: from 3.55555 -> 3.56
    //converts the second digit after the floating point up
    private static String roudUp(String s) {
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

    private static String delCommas(String s) {
        if(s.matches("^(([-+] ?)?[0-9]+(,[0-9]+)+)?((.[0-9]+))")) {
            s.contains(",");
            return  s.replace(",","");
        }
        return s;
    }
    private static String convPrecent (String s){
        String n= s.replaceAll("%|perecentge"," percent");
        return n;
    }
}

