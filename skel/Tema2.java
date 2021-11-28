import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Tema2 {
    // Cateva variabile necesare functiei.
    public static Integer numberOfFiles;
    public static long fragmentSize;
    public static ArrayList<String> files = new ArrayList<>();
    public static Hashtable<String, Integer> fileIndex = new Hashtable<String, Integer>();

    public static void main(String[] args) {
        // Daca este rulat gresit, se iasa din functie.
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        // Am extras din argumente informatiile necesare.
        int workers = Integer.parseInt(args[0]);
        String inFile = args[1];
        String outFile = args[2];
        Integer index = 0; // Ordinea din citire a fisierelor.

        // Citesc fisierul de intrare si retin marimea fragmentului,
        // numarul de fisiere si numele fisierelor.
        try {
            Scanner scanner = new Scanner(new File(inFile));

            fragmentSize = Long.parseLong(scanner.nextLine());
            numberOfFiles = Integer.parseInt(scanner.nextLine());

            while (scanner.hasNext()) {
                String fileName = scanner.nextLine();
                files.add(fileName);
                fileIndex.put(fileName, index++);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Pool pentru taskurile de MAP.
        // Initial dau drumul doar la un task de tipul Parser.
        // Acesta este coordonatorul, care baga in pool celalalte taskuri de
        // tip Map.
        // Am optat pentru forkjoinpool, deoarece pot adauga taskurile initial
        // si apoi sa le dau join, fara a aparea probleme in legatura cu
        // shutdown.
        ForkJoinPool fjp = new ForkJoinPool(workers);
        ArrayList<MapAnswer> ans = fjp.invoke(new Parser(files, fragmentSize));
        fjp.shutdown();

        // Same as above, dar cu taskurile de reduce.
        fjp = new ForkJoinPool(workers);
        ArrayList<ReduceAnswer> response = fjp.invoke(new DivideReduce(ans));
        fjp.shutdown();

        // Adaug indexul fiecarui raspuns pentru a putea sorta.
        for (int i = 0; i < response.size(); ++i) {
            response.get(i).index = fileIndex.get(response.get(i).file);
        }

        // Sortez raspunsurile.
        Collections.sort(response);

        // Deschid fisierul de iesire.
        Path path = Paths.get(outFile);
        StringBuilder toPrint = new StringBuilder();

        // Creez un text final pentru fisier.
        for (ReduceAnswer entry : response) {
            String[] pathElements = entry.file.split("/");
            String stringPrint = String.format("%s,%.2f,%d,%d\n",
                    pathElements[pathElements.length - 1],
                    entry.rang,
                    entry.bigWords.get(0).length(),
                    entry.bigWords.size());

            toPrint.append(stringPrint);
        }

        // Scriu in fisier.
        try {
            Files.writeString(path, toPrint.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
