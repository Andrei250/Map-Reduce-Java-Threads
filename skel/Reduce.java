import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

// Task de tip reduce.
// Parametrii: informatiile obtinute in urma taskului de tip Map,
//              numele fisierului.
public class Reduce extends RecursiveTask<ReduceAnswer> {
    private final ArrayList<MapAnswer> entries;
    private final String file;

    public Reduce(ArrayList<MapAnswer> entries, String file) {
        this.entries = entries;
        this.file = file;
    }

    // Functie pentru calcularea sirului lui fibonacci.
    private long getFibo(int number) {
        if (number == 1) {
            return 1;
        } else if (number == 2) {
            return 2;
        }

        return getFibo(number - 1) + getFibo(number - 2);
    }

    @Override
    protected ReduceAnswer compute() {
        Hashtable<Integer, Integer> occurrences = new Hashtable<>();
        ArrayList<String> maximumStrings = null;
        long numberOfWords = 0;

        // Combinarea intregilro dictionare in unul singur.
        // Obtinerea listei de cuvinte de lungime maxima.
        for (MapAnswer entry : entries) {
            Set<Integer> keys = entry.occurrences.keySet();

            for (Integer key : keys) {
                numberOfWords = numberOfWords + entry.occurrences.get(key);
                occurrences.put(key, occurrences.getOrDefault(key, 0) + entry.occurrences.get(key));
            }

            if (maximumStrings == null || maximumStrings.get(0).length() < entry.bigWords.get(0).length()) {
                maximumStrings = entry.bigWords;
            } else if (maximumStrings.get(0).length() == entry.bigWords.get(0).length()) {
                maximumStrings.addAll(entry.bigWords);
            }
        }

        // Procesarea tuturor informatiilor si calcularea rangului.
        Set<Integer> keys = occurrences.keySet();
        double sum = 0;

        for (Integer key : keys) {
            sum = sum + getFibo(key) * occurrences.get(key);
        }

        // Returnez un nou obiect de tip ReduceAnswer.
        return new ReduceAnswer(file, occurrences, maximumStrings, sum / numberOfWords);
    }
}
