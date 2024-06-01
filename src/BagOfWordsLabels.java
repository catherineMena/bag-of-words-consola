import java.io.*;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class BagOfWordsLabels {
    private Map<String, Set<String>> phrasesByLabel;
    private Set<String> stopwords;

    public BagOfWordsLabels() {
        phrasesByLabel = new HashMap<>();
        stopwords = new HashSet<>(Arrays.asList("el", "al", "e", "sus", "en", "a", "con", "se", "su", "esta", "por", "que", "del", "la", "los", "las", "un", "una", "unos", "unas", "es", "de", "cuando", "y"));
    }

    public void loadTrainingData(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    String phrase = parts[0].trim();
                    String labels = parts[1].trim();
                    addPhrase(phrase, labels);
                }
            }
        }
    }

    public void addPhrase(String phrase, String labels) {
        phrase = normalize(phrase);
        String[] labelsArray = labels.split("\\|");
        for (String label : labelsArray) {
            label = label.trim();
            phrasesByLabel.putIfAbsent(label, new HashSet<>());
            Set<String> phrases = phrasesByLabel.get(label);
            phrases.add(phrase);
        }
    }

    public String normalize(String phrase) {
        phrase = phrase.toLowerCase();
        phrase = Normalizer.normalize(phrase, Normalizer.Form.NFD); // Normalizar para eliminar tildes
        phrase = phrase.replaceAll("[^\\p{ASCII}\\s]", ""); // Eliminar caracteres especiales (tildes)
        phrase = phrase.replaceAll("[^a-zA-Z\\s]", ""); // Eliminar caracteres que no sean letras o espacios
        return phrase;
    }

    public Set<String> getPhrasesByLabel(String label) {
        return phrasesByLabel.getOrDefault(label, new HashSet<>());
    }

    public Map<String, Integer> countPhrasesByLabel() {
        Map<String, Integer> countMap = new HashMap<>();
        for (String label : phrasesByLabel.keySet()) {
            countMap.put(label, phrasesByLabel.get(label).size());
        }
        return countMap;
    }

    public void printAllPhrasesWithLabels() {
        for (String label : phrasesByLabel.keySet()) {
            System.out.println("Etiqueta: " + label);
            System.out.println("Frases:");
            Set<String> phrases = phrasesByLabel.get(label);
            for (String phrase : phrases) {
                System.out.println("- " + phrase);
            }
            System.out.println("Total de frases en esta etiqueta: " + phrases.size() + "\n");
        }
    }

    public static void main(String[] args) {
        BagOfWordsLabels classifier = new BagOfWordsLabels();

        try {
            classifier.loadTrainingData("datos.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (classifier.phrasesByLabel.isEmpty()) {
            System.out.println("No hay suficientes frases o palabras.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese una etiqueta para mostrar las frases asociadas (o 'salir' para terminar):");
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("salir")) {
                break;
            } else if (input.equalsIgnoreCase("todas")) {
                classifier.printAllPhrasesWithLabels();
            } else {
                Set<String> phrases = classifier.getPhrasesByLabel(input);
                if (!phrases.isEmpty()) {
                    System.out.println("Frases con la etiqueta '" + input + "':");
                    for (String phrase : phrases) {
                        System.out.println("- " + phrase);
                    }
                    System.out.println("Total de frases en esta etiqueta: " + phrases.size());
                } else {
                    System.out.println("No hay frases asociadas a la etiqueta '" + input + "'.");
                }
            }
            System.out.println("Ingrese otra etiqueta o 'todas' para ver todas las frases con sus etiquetas (o 'salir' para terminar):");
        }
        scanner.close();
    }
}
