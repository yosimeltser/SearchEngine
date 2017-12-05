import java.io.FileNotFoundException;

public class Main {

    private static final String FILENAME = "C:\\Users\\yosef\\Desktop\\study\\אחזור\\פרויקט מנוע\\corpus\\corpus";
    public static void main(String[] args) {
        ReadFile r=new ReadFile(FILENAME);
        r.fileReader();
    }
}

