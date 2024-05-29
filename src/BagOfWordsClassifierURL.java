import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BagOfWordsClassifierURL {
    private Map<String, Integer> wordCounts;
    private Set<String> stopwords;
    private final double SMOOTHING_FACTOR = 1.0;
    private Scanner scanner;

    public BagOfWordsClassifierURL() {
        wordCounts = new HashMap<>();
        stopwords = new HashSet<>(Arrays.asList("el", "al", "e", "sus", "en", "a", "con", "se", "su", "esta", "por", "que", "del", "la", "los", "las", "un", "una", "unos", "unas", "es", "para", "de", "cuando", "y"));
        scanner = new Scanner(System.in);
    }

    public void countWords(String phrase) {
        phrase = normalize(phrase);
        String[] words = phrase.split("\\s+");
        for (String word : words) {
            wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
        }
    }

    public String normalize(String phrase) {
        phrase = phrase.toLowerCase();
        phrase = Normalizer.normalize(phrase, Normalizer.Form.NFD); // Normalizar para eliminar tildes
        phrase = phrase.replaceAll("[^\\p{ASCII}\\s]", ""); // Eliminar caracteres especiales (tildes)
        phrase = phrase.replaceAll("[^a-zA-Zá-úÁ-Ú\\s]", ""); // Conservar caracteres con tilde y mayúsculas
        phrase = Arrays.stream(phrase.split("\\s+"))
                .filter(word -> !stopwords.contains(word))
                .collect(Collectors.joining(" "));
        return phrase;
    }

    // Método para obtener contenido de una URL y extraer el texto usando Jsoup
    public List<String> getWordsFromURL(String urlString) {
        List<String> words = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(urlString).get();
            Elements paragraphElements = doc.select("p"); // Seleccionar todos los párrafos
            for (Element paragraph : paragraphElements) {
                String paragraphText = paragraph.text();
                String[] paragraphWords = normalize(paragraphText).split("\\s+");
                words.addAll(Arrays.asList(paragraphWords));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    public void processURL() {
        System.out.print("Ingrese la URL del sitio web (o escriba 'salir' para terminar): ");
        String url = scanner.nextLine();

        if (url.equalsIgnoreCase("salir")) {
            return;
        }

        // Obtener palabras de un sitio web
        List<String> words = getWordsFromURL(url);

        // Contar la frecuencia de cada palabra
        for (String word : words) {
            countWords(word);
        }

        // Ordenar el mapa de recuento de palabras por sus valores (frecuencia)
        Map<String, Integer> sortedWordCounts = wordCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        // Mostrar las palabras junto con su frecuencia en orden descendente de frecuencia
        System.out.println("Palabras y su frecuencia (ordenadas por frecuencia):");
        for (Map.Entry<String, Integer> entry : sortedWordCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public void closeScanner() {
        scanner.close();
    }

    public static void main(String[] args) {
        BagOfWordsClassifierURL classifier = new BagOfWordsClassifierURL();
        Scanner scanner = classifier.scanner;

        boolean continuar = true;

        while (continuar) {
            classifier.processURL();

            System.out.println("¿Desea ingresar otra URL? (s/n)");
            String respuesta = scanner.nextLine();
            continuar = respuesta.equalsIgnoreCase("s");
        }

        classifier.closeScanner();
    }
}
