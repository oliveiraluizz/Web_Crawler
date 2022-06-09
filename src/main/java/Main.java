import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    static String url = "https://www.imdb.com/chart/bottom";

    public Main() throws IOException {
    }
    public static void main(String[] args) throws IOException {

        Filme filme = new Filme();

        filme.findElements();
    }
    public static void log(String message){
        System.out.println(message);
    }
}
