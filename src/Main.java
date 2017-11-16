import java.io.FileNotFoundException;

public class Main {


    public static void main(String[] args) throws FileNotFoundException {


            String s="6%";
            String [] need_to_parse=s.split(" +");
            for(int i=0;i<need_to_parse.length;i++ ){
                need_to_parse[i]=delCommas(need_to_parse[i]);
                need_to_parse[i]=roudUp(need_to_parse[i]);
                need_to_parse[i]=convPrecent((need_to_parse[i]));
                System.out.println(need_to_parse[i]);
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

