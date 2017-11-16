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
           if(need_to_parse[i].matches("\\d+\\.\\d+")){

           }
       }
    }
}
}

