import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

// Task care impparte la final pe taskurid e tip Reduce.
// Aici se creeaza fiecare task de tip Reduce si se baga in pool.
public class DivideReduce extends RecursiveTask<ArrayList<ReduceAnswer>> {
    private final ArrayList<MapAnswer> ans;

    public DivideReduce(ArrayList<MapAnswer> ans) {
        this.ans = ans;
    }

    @Override
    protected ArrayList<ReduceAnswer> compute() {
        Hashtable<String, ArrayList<MapAnswer>> tasksFiles = new Hashtable<>();

        // Pentru fiecare entry, daug informatiile in dictionar la file-ul
        // potrivit.
        for (MapAnswer entry : ans) {
            ArrayList<MapAnswer> tasks = tasksFiles.getOrDefault(entry.file, new ArrayList<>());

            tasks.add(entry);
            tasksFiles.put(entry.file, tasks);
        }

        // Creez taskurile Reduce.
        ArrayList<Reduce> tasks = new ArrayList<>();
        Set<String> keys = tasksFiles.keySet();
        ArrayList<ReduceAnswer> response = new ArrayList<>();

        for (String key : keys) {
            Reduce task = new Reduce(tasksFiles.get(key), key);

            tasks.add(task);
            task.fork();
        }

        // Obtin raspunsurile de la fiecare.
        for (Reduce task : tasks) {
            response.add(task.join());
        }

        return response;
    }
}
