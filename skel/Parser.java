import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

// Task care parseaza fiecare fisier si face taskurile de tip Map.
// Practic Rootul argorelui de taskuri.
public class Parser extends RecursiveTask<ArrayList<MapAnswer>> {
    private final ArrayList<String> inFiles;
    private final long fragSize;

    public Parser(ArrayList<String> inFiles, long fragSize) {
        this.inFiles = inFiles;
        this.fragSize = fragSize;
    }

    @Override
    protected ArrayList<MapAnswer> compute() {
        ArrayList<Map> tasks = new ArrayList<>();

        try {
            // Aflu lungimea fiecarui fisier si il impart pe taskuri.
            // Practic impart lungimea lui la fragment size.
            // ultimul fragment este mai mic decat celalalte.
            for (String file : inFiles) {
                Path path = Paths.get(file);
                long bytes = Files.size(path);
                long chunks = bytes % fragSize != 0 ? bytes / fragSize + 1 : bytes / fragSize;

                for (long i = 0; i < chunks ; i++) {
                    Map task = new Map(i * fragSize, (i + 1) * fragSize > bytes ? bytes - i * fragSize : fragSize, file);
                    tasks.add(task);
                    task.fork();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<MapAnswer> tasksResponse = new ArrayList<>();

        // Obtin raspunruile de la fiecare task.
        // Raspunsul paote fi null, daca acesta nu are cuvinte deloc
        // in alcatuirea fragmentului.
        for (Map task : tasks) {
            MapAnswer ans = task.join();

            if (ans != null) {
                tasksResponse.add(ans);
            }
        }

        return tasksResponse;
    }
}
