import java.util.ArrayList;
import java.util.Hashtable;

// Raspunsul unui task de tip Map.
public class MapAnswer {
    public String file;
    public Hashtable<Integer, Integer> occurrences;
    public ArrayList<String> bigWords;

    public MapAnswer(String file, Hashtable<Integer, Integer> occurrences, ArrayList<String> bigWords) {
        this.file = file;
        this.occurrences = occurrences;
        this.bigWords = bigWords;
    }

    @Override
    public String toString() {
        return "Fisier: " + file + " tabel: " +
                occurrences + " words: " + bigWords + "\n";
    }
}
