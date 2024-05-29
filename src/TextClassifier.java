//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.text.Normalizer;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class TextClassifier {
//    private Map<String, Map<String, Integer>> phraseCounts = new HashMap<>();
//    private Map<String, Integer> labelCounts = new HashMap<>();
//    private int totalLabels = 0;
//    private Set<String> labels = new HashSet<>();
//    private int MINIMUM_TRAINING_DATA;
//    private Set<String> stopwords = new HashSet<>(Arrays.asList("el", "al", "e", "sus", "en", "a", "con", "se", "su", "esta", "por", "que", "del", "la", "los", "las", "un", "una", "unos", "unas", "es", "de", "cuando", "y"));
//
//    public Map<String, Map<String, Integer>> getPhraseCounts() {
//        return phraseCounts;
//    }
//
//    public void train(String phrase, String labels) {
//        phrase = normalize(phrase);
//        String[] words = phrase.split("\\s+");
//        String[] labelsArray = labels.split("\\|");
//        for (String label : labelsArray) {
//            label = label.trim();
//            labels.add(label);
//            labelCounts.put(label, labelCounts.getOrDefault(label, 0) + 1);
//            totalLabels++;
//            for (String word : words) {
//                phraseCounts.putIfAbsent(word, new HashMap<>());
//                Map<String, Integer> wordCounts = phraseCounts.get(word);
//                wordCounts.put(label, wordCounts.getOrDefault(label, 0) + 1);
//            }
//        }
//    }
//
//    public void loadTrainingData(String filename) throws IOException {
//        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split("\\|", 2); // Dividir en dos partes para manejar etiquetas múltiples
//                if (parts.length == 2) {
//                    String phrase = parts[0].trim();
//                    String labels = parts[1].trim();
//                    train(phrase, labels);
//                }
//            }
//            MINIMUM_TRAINING_DATA = totalLabels;
//        }
//    }
//
//    public String classify(String phrase) {
//        if (totalLabels < MINIMUM_TRAINING_DATA) {
//            return "Insufficient training data";
//        }
//
//        String normalizedPhrase = normalize(phrase);
//        Set<String> foundLabels = new HashSet<>();
//        String[] words = normalizedPhrase.split("\\s+");
//
//        boolean wordFound = false;
//        for (String word : words) {
//            for (String label : phraseCounts.keySet()) {
//                Map<String, Integer> wordCounts = phraseCounts.get(label);
//                if (wordCounts.containsKey(word)) {
//                    foundLabels.add(label);
//                    wordFound = true;
//                }
//            }
//        }
//
//        if (!wordFound) {
//            return "Palabra no clasificada";
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
//    public void trainTestSplit(String filePath, double trainRatio) throws IOException {
//        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//            List<String> lines = new ArrayList<>();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                lines.add(line);
//            }
//
//            Collections.shuffle(lines); // Mezcla las líneas
//
//            int trainSize = (int) (lines.size() * trainRatio);
//            List<String> trainLines = lines.subList(0, trainSize);
//            List<String> testLines = lines.subList(trainSize, lines.size());
//
//            // Entrena con las líneas de entrenamiento
//            for (String trainLine : trainLines) {
//                String[] parts = trainLine.split("\\|");
//                if (parts.length == 2) {
//                    String phrase = parts[0].trim();
//                    String label = parts[1].trim();
//                    train(phrase, label);
//                }
//            }
//
//            // Prueba con las líneas de prueba
//            int correctPredictions = 0;
//            for (String testLine : testLines) {
//                String[] parts = testLine.split("\\|");
//                if (parts.length == 2) {
//                    String phrase = parts[0].trim();
//                    String actualLabel = parts[1].trim();
//                    String predictedLabel = classify(phrase);
//                    if (predictedLabel.equals(actualLabel)) {
//                        correctPredictions++;
//                    }
//                }
//            }
//
//            double accuracy = (double) correctPredictions / testLines.size();
//            System.out.println("Accuracy on test set: " + accuracy);
//        }
//    }
//
//    public static void main(String[] args) {
//        CombinedTextClassifier classifier = new CombinedTextClassifier();
//
//        try {
//            classifier.loadTrainingData("datos.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (classifier.phraseCounts.isEmpty()) {
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
