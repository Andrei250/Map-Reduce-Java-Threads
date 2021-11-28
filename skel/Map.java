import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.RecursiveTask;

// Task de tip Map.
public class Map extends RecursiveTask<MapAnswer> {
    private long start;
    private long size;
    private final String file;
    private final String delimiters = ";:/?˜\\.,><‘\\[]\\{}\\(\\)!@#$%ˆ&-+’=*”|\" \t\n\r";

    public Map(long start, long size, String file) {
        this.start = start;
        this.size = size;
        this.file = file;
    }

    @Override
    protected MapAnswer compute() {
        StringBuilder ans = new StringBuilder();
        long stop = start + size;

        try {
            RandomAccessFile seeker = new RandomAccessFile(file, "r");

            // verific daca finalul este in mijlocul unui cuvant si mut
            // pointerul la dreapta pana la finalul fisierului.
            if (stop < seeker.length()) {
                stop = getNewPosition(stop, seeker.length(), seeker);
            }

            // verific daca ma aflu in mijlocului cuvantului si nu sunt
            // la inceputul fisierului.
            // Daca nu este inceputul fisierului, mut pointerul la dreapta
            // pana ajung la finalul cuvantului
            if (start != 0) {
                start = getNewPosition(start, stop, seeker);
            }

            size = stop - start;
            seeker.seek(start);

            // citesc din fisier.
            while (size != 0) {
                ans.append((char)seeker.readByte());
                size--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // impart fragmentul in cuvinte.
        String fragment = ans.toString();
        String[] words = fragment.split("\\||\\|\\t|,|;|\\.|\\?|!|-|:|@|\\[|\\]|\\(|\\)|\\{|}|_|\\*|/| |\\n|\\r|~|>|<|`|#|$|%|^|&|\\+|\\'|=|\"");
        int maxLen = 0;
        ArrayList<String> bigWords = new ArrayList<>();
        Hashtable<Integer, Integer> occurrences = new Hashtable<>();

        // populez dictionarul de aparitii, trecand prin fiecare cuvant
        // si aflandu-i lungimea.
        for (String str : words) {
            if (str.length() < 1) {
                continue;
            }

            if (str.length() > maxLen) {
                maxLen = str.length();
                bigWords.clear();
                bigWords.add(str);
            } else if (str.length() == maxLen) {
                bigWords.add(str);
            }

            occurrences.put(str.length(), occurrences.getOrDefault(str.length(), 0) + 1);
        }

        // daca nu am niciun cuvant, returnez null.
        if (occurrences.size() < 1) {
            return null;
        }

        return new MapAnswer(file, occurrences, bigWords);
    }

    // functie pentru mutarea pointerului in fisier.
    private long getNewPosition(long lo, long hi, RandomAccessFile seeker) throws IOException {
        seeker.seek(lo);

        while (lo < hi && delimiters.indexOf((char) seeker.readByte()) == -1) {
            lo++;
        }

        return lo;
    }
}
