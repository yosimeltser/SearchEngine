import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    private static final String FILENAME = "C:\\Users\\yosefmeltser\\Desktop\\study\\איחזור מידע\\עבודות\\עבודת בית 1\\FB396001\\FB396001";

    public static void main(String[] args) throws FileNotFoundException {
        BufferedReader br = null;
        FileReader fr = null;
        fr = new FileReader(FILENAME);
        br = new BufferedReader(fr);
        int count = NemberOfLines(fr, br);
        String[] arrOfTexts = new String[count];
        int index = 0;
        while (index < count) {
            try {
                br.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println ("");
        System.out.println (""); System.out.println ("");
        System.out.println ("");

    }

    private static int NemberOfLines(FileReader fr, BufferedReader br) throws FileNotFoundException {

        try {
            int counter = 0;
            String line = "";
            String firstHandler = "";
            while ((line = br.readLine()) != null) {
                firstHandler = br.readLine();
                if (firstHandler.startsWith("<TEXT>")) {
                    counter++;
                }

            }
            return counter;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
