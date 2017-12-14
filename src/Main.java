import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.regex.Pattern;

public class Main {

    private static final String FILENAME = "C:\\Users\\yosefmel\\Downloads\\corpus\\corpus";

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ReadFile r = new ReadFile(FILENAME);
        r.fileReader();
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }
}

