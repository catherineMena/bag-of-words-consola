//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import org.bson.Document;
//
//import java.text.Normalizer;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class BagOfWordsClassifierBD {
//    private Map<String, Map<String, Integer>> wordCountsByLabel;
//    private Map<String, Integer> labelCounts;
//    private Set<String> stopwords;
//    private final double SMOOTHING_FACTOR = 1.0;
//    private final double LAMBDA = 0.1; // Factor de regularización
//
//    public BagOfWordsClassifierBD() {
//        wordCountsByLabel = new HashMap<>();
//        labelCounts = new HashMap<>();
//        stopwords = new HashSet<>(Arrays.asList("el", "al", "e", "sus", "en", "a", "con", "se", "su", "esta", "por", "que", "del", "la", "los", "las", "un", "una", "unos", "unas", "es", "de", "cuando", "y"));
//    }
//
//    public void train(String phrase, String labels) {
//        phrase = normalize(phrase);
//        String[] words = phrase.split("\\s+");
//        String[] labelsArray = labels.split("\\|");
//        for (String label : labelsArray) {
//            label = label.trim();
//            wordCountsByLabel.putIfAbsent(label, new HashMap<>());
//            Map<String, Integer> wordCounts = wordCountsByLabel.get(label);
//            for (String word : words) {
//                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
//            }
//            labelCounts.put(label, labelCounts.getOrDefault(label, 0) + 1);
//        }
//    }
//
//    public void loadTrainingDataFromDB() {
//        String connectionString = "mongodb://localhost:27017"; // Cambia esto según tu configuración
//        String dbName = "BagOfWordsDB";
//        String collectionName = "trainingData";
//
//        MongoClient mongoClient = MongoClients.create(connectionString);
//        MongoDatabase database = mongoClient.getDatabase(dbName);
//        MongoCollection<Document> collection = database.getCollection(collectionName);
//
//        for (Document doc : collection.find()) {
//            String phrase = doc.getString("phrase");
//            String labels = doc.getString("labels");
//            train(phrase, labels);
//        }
//
//        mongoClient.close();
//    }
//
//    public String classify(String phrase) {
//        String normalizedPhrase = normalize(phrase);
//        String[] words = normalizedPhrase.split("\\s+");
//        Set<String> foundLabels = new HashSet<>();
//
//        // Verificar si al menos una de las palabras está presente en los datos de entrenamiento
//        boolean wordFound = false;
//        for (String word : words) {
//            for (String label : wordCountsByLabel.keySet()) {
//                Map<String, Integer> wordCounts = wordCountsByLabel.get(label);
//                if (wordCounts.containsKey(word)) {
//                    foundLabels.add(label);
//                    wordFound = true;
//                }
//            }
//        }
//
//        if (!wordFound) {
//            return "Palabra no clasificada"; // Devolver si ninguna palabra está presente en los datos de entrenamiento
//        }
//
//        return String.join(", ", foundLabels);
//    }
//
//    public String normalize(String phrase) {
//        phrase = phrase.toLowerCase();
//        phrase = Normalizer.normalize(phrase, Normalizer.Form.NFD); // Normalizar para eliminar tildes
//        phrase = phrase.replaceAll("[^\\p{ASCII}\\s]", ""); // Eliminar caracteres especiales (tildes)
//        phrase = phrase.replaceAll("[^a-zA-Z\\s]", ""); // Eliminar caracteres que no sean letras o espacios
//        phrase = Arrays.stream(phrase.split("\\s+"))
//                .filter(word -> !stopwords.contains(word))
//                .collect(Collectors.joining(" "));
//        return phrase;
//    }
//
//    private int sum(Collection<Integer> values) {
//        int sum = 0;
//        for (int value : values) {
//            sum += value;
//        }
//        return sum;
//    }
//
//    public static void main(String[] args) {
//        BagOfWordsClassifierBD classifier = new BagOfWordsClassifierBD();
//
//        classifier.loadTrainingDataFromDB();
//
//        if (classifier.wordCountsByLabel.isEmpty()) {
//            System.out.println("No hay suficientes frases o palabras.");
//            return;
//        }
//
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Ingresa una palabra para clasificar (o 'salir' para terminar):");
//        while (true) {
//            String input = scanner.nextLine();
//            if (input.equalsIgnoreCase("salir")) {
//                break;
//            }
//            String classification = classifier.classify(input);
//            if (!classification.equals("Palabra no clasificada")) {
//                System.out.println("La palabra \"" + input + "\" es clasificada como: " + classification);
//                // Preguntar si desea normalizar la palabra
//                System.out.println("¿Deseas normalizar la palabra? (s/n)");
//                String choice = scanner.nextLine();
//                if (choice.equalsIgnoreCase("s")) {
//                    String normalizedWord = classifier.normalize(input);
//                    System.out.println("La palabra normalizada es: " + normalizedWord);
//                }
//            } else {
//                System.out.println("La palabra \"" + input + "\" no está clasificada.");
//            }
//
//            System.out.println("Ingresa otra palabra (o 'salir' para terminar):");
//        }
//        scanner.close();
//    }
//}
