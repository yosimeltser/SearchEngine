import java.io.FileNotFoundException;

public class Main {

    private static final String FILENAME = "C:\\Users\\yosefmeltser\\Desktop\\study\\איחזור מידע\\עבודות\\עבודת בית 1\\FB396001\\FB396001";
    public static void main(String[] args) {
        ReadFile r=new ReadFile(FILENAME);
        Parse p =new Parse(r.fileReader());
        p.ParseNumbers();
        System.out.println("19th".matches("\\d{1,2}[th]+$"));
        System.out.println("zohar".substring(0,"zohar".length()-2));
//        System.out.println("zohar".equalsIgnoreCase("ZohaR"));
//        String [] arr= {"yosi","zohar"};
//        System.out.println(arr[0]);

    }
}

