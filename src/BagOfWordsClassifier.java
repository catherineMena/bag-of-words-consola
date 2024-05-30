import java.io.*;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class BagOfWordsClassifier {
    private Map<String, Map<String, Integer>> wordCountsByLabel;
    private Map<String, Integer> labelCounts;
    private Set<String> stopwords;
    private final double SMOOTHING_FACTOR = 1.0;

    private Scanner scanner = new Scanner(System.in);
    private final double LAMBDA = 0.1; // Factor de regularización

    public BagOfWordsClassifier() {
        wordCountsByLabel = new HashMap<>();
        labelCounts = new HashMap<>();
        stopwords = new HashSet<>(Arrays.asList("el", "al", "e", "sus", "en", "a", "con", "se", "su", "esta", "por", "que", "del", "la", "los", "las", "un", "una", "unos", "unas", "es", "de", "cuando", "y"));
    }

    //---------------------------------------METODOS DE ENTRENAMIENTO------------------------
    public void train(String phrase, String labels) {
        phrase = normalize(phrase);
        String[] words = phrase.split("\\s+");
        String[] labelsArray = labels.split("\\|");
        for (String label : labelsArray) {
            label = label.trim();
            wordCountsByLabel.putIfAbsent(label, new HashMap<>());
            Map<String, Integer> wordCounts = wordCountsByLabel.get(label);
            for (String word : words) {
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }
            labelCounts.put(label, labelCounts.getOrDefault(label, 0) + 1);
        }
    }

    //---------------------------------------METODOS DE CARGA DE DATOS------------------------
    public void loadTrainingData(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 2); // Dividir en dos partes para manejar etiquetas múltiples
                if (parts.length == 2) {
                    String phrase = parts[0].trim();
                    String labels = parts[1].trim();
                    train(phrase, labels);
                }
            }
        }
    }

    //---------------------------------------METODOS DE RE-ENTRENO------------------------
    // Recopilar datos de entrenamiento del usuario para mejorar la precisión del clasificador de palabras.
    public void trainWithUserInput() {
        // Solicita al usuario que introduzca una frase para entrenar el clasificador
        System.out.println("Introduce una frase para entrenar el clasificador:");
        // Lee la frase introducida por el usuario
        String phrase = scanner.nextLine();

        // Solicita al usuario que introduzca las etiquetas para la frase
        // Las etiquetas deben estar separadas por '|'
        System.out.println("Introduce las etiquetas para la frase (separadas por '|'):");
        // Lee las etiquetas introducidas por el usuario
        String labels = scanner.nextLine();

        // Llama al método train con la frase y las etiquetas introducidas por el usuario
        // El método train se encarga de normalizar la frase, dividirla en palabras y actualizar
        // los conteos de palabras y etiquetas
        train(phrase, labels);
    }

    public String classify(String phrase) {
        String normalizedPhrase = normalize(phrase);
        String[] words = normalizedPhrase.split("\\s+");
        Map<String, Double> labelProbabilities = new HashMap<>();

        for (String label : wordCountsByLabel.keySet()) {
            double labelProbability = (double) labelCounts.get(label) / sum(labelCounts.values());
            for (String word : words) {
                Map<String, Integer> wordCounts = wordCountsByLabel.get(label);
                int wordCount = wordCounts.getOrDefault(word, 0);
                double wordProbability = (wordCount + SMOOTHING_FACTOR) / (sum(wordCounts.values()) + SMOOTHING_FACTOR * wordCounts.size());
                labelProbability *= wordProbability;
            }
            labelProbabilities.put(label, labelProbability);
        }

        // Encontrar la etiqueta con la mayor probabilidad
        return labelProbabilities.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey() + " (Probabilidad: " + (entry.getValue() / sum(labelCounts.values())) + ")")
                .orElse("Palabra no clasificada");
    }

    //---------------------------------------METODOS DE NORMALIZACION------------------------
    public String normalize(String phrase) {
        phrase = phrase.toLowerCase();
        phrase = Normalizer.normalize(phrase, Normalizer.Form.NFD); // Normalizar para eliminar tildes
        phrase = phrase.replaceAll("[^\\p{ASCII}]", ""); // Eliminar caracteres especiales (tildes)
        phrase = phrase.replaceAll("[^a-zA-Z\\s]", ""); // Eliminar caracteres que no sean letras o espacios
        phrase = Arrays.stream(phrase.split("\\s+"))
                .filter(word -> !stopwords.contains(word))
                .collect(Collectors.joining(" "));
        return phrase;
    }

    private int sum(Collection<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).sum();
    }

    private double sumDoubles(Collection<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }

// ...

    public static void main(String[] args) {
        BagOfWordsClassifier classifier = new BagOfWordsClassifier();

        try {
            classifier.loadTrainingData("datos.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Arranque en frío: Si no hay suficientes datos de entrenamiento, se solicita al usuario que ingrese datos
        if (classifier.wordCountsByLabel.isEmpty()) {
            System.out.println("No hay suficientes frases o palabras. Por favor, introduce más datos de entrenamiento.");
            classifier.trainWithUserInput();
        }

        System.out.println("Ingresa una frase para clasificar (o 'salir' para terminar):");
        while (true) {
            String input = classifier.scanner.nextLine();
            if (input.equalsIgnoreCase("salir")) {
                break;
            }
            String classification = classifier.classify(input);
            System.out.println("La frase \"" + input + "\" es clasificada como: " + classification);

            // Preguntar si desea normalizar la frase
            System.out.println("¿Deseas normalizar la frase? (s/n)");
            String choice = classifier.scanner.nextLine();
            if (choice.equalsIgnoreCase("s")) {
                String normalizedPhrase = classifier.normalize(input);
                System.out.println("La frase normalizada es: " + normalizedPhrase);
            }

            System.out.println("Ingresa otra frase (o 'salir' para terminar):");
        }
        classifier.scanner.close();
    }
}
