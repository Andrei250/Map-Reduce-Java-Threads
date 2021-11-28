import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;

// Raspunsul oferit de un task de tipul Reduce.
// Contine rangul, indexul file-ului, dictionarul de aparitii
// si lista cu cele mai lungi cuvinte.
public class ReduceAnswer extends MapAnswer implements Comparator<ReduceAnswer>, Comparable<ReduceAnswer> {
    public final double rang;
    public int index;

    public ReduceAnswer(String file,
                        Hashtable<Integer, Integer> occurrences,
                        ArrayList<String> bigWords,
                        double rang) {
        super(file, occurrences, bigWords);
        this.rang = rang;
        index = 0;
    }

    @Override
    public String toString() {
        return super.toString() + " " + rang + " " + index + "\n";
    }

    // Comparatoare pentru a sorta in functie de rang si index.
    @Override
    public int compare(ReduceAnswer o1, ReduceAnswer o2) {
        if (Math.abs(o1.rang - o2.rang) < 0.001f) {
            if (o1.index > o2.index) {
                return 1;
            } else if (o1.index < o2.index) {
                return -1;
            }

            return 0;
        } else if (o1.rang > o2.rang) {
            return -1;
        }

        return 1;
    }

    @Override
    public int compareTo(ReduceAnswer o) {
        if (Math.abs(this.rang - o.rang) < 0.001f) {
            if (this.index > o.index) {
                return 1;
            } else if (this.index < o.index) {
                return -1;
            }

            return 0;
        } else if (this.rang > o.rang) {
            return -1;
        }

        return 1;
    }
}
