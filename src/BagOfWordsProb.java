import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BagOfWordsProb {
    private Map<String, Integer> hamWordCounts;
    private Map<String, Integer> spamWordCounts;
    private Set<String> stopwords;
    private int totalHamWords;
    private int totalSpamWords;
    private int totalWords;

    public BagOfWordsProb() {
        hamWordCounts = new HashMap<>();
        spamWordCounts = new HashMap<>();
        stopwords = new HashSet<>(Arrays.asList("el", "al", "e", "sus", "en", "a", "con", "se", "su", "esta", "por", "que", "del", "la", "los", "las", "un", "una", "unos", "unas", "es", "para", "de", "cuando", "y"));
        totalHamWords = 0;
        totalSpamWords = 0;
    }

    public void countWords(String phrase, String label) {
        Map<String, Integer> wordCounts;
        if (label.equalsIgnoreCase("ham")) {
            wordCounts = hamWordCounts;
            totalHamWords++;
        } else if (label.equalsIgnoreCase("spam")) {
            wordCounts = spamWordCounts;
            totalSpamWords++;
        } else {
            // Si la etiqueta no es "ham" ni "spam", no hacemos nada
            return;
        }

        phrase = normalize(phrase);
        String[] words = phrase.split("\\s+");
        for (String word : words) {
            wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
        }
    }

    public double calculateProbability(Map<String, Integer> wordCounts, int totalWords) {
        return (double) totalWords / getTotalWords();
    }

    public int getTotalWords() {
        return totalHamWords + totalSpamWords;
    }

    public int getTotalWords(String label) {
        return label.equalsIgnoreCase("ham") ? totalHamWords : totalSpamWords;
    }

    public String normalize(String phrase) {
        phrase = phrase.toLowerCase();
        phrase = phrase.replaceAll("[^a-zA-Zá-úÁ-Ú\\s]", ""); // Conservar caracteres con tilde y mayúsculas
        phrase = Arrays.stream(phrase.split("\\s+"))
                .filter(word -> !stopwords.contains(word))
                .collect(Collectors.joining(" "));
        return phrase;
    }

    // Método para cargar palabras desde un arreglo de strings
    public void loadWords(String[] words, String label) {
        for (String word : words) {
            countWords(word, label);
        }
    }

    // Método para imprimir la frecuencia de cada palabra en general
    public void printWordFrequencies() {
        // Combinar los recuentos de palabras de spam y ham
        Map<String, Integer> combinedWordCounts = new HashMap<>(hamWordCounts);
        spamWordCounts.forEach((word, count) -> combinedWordCounts.merge(word, count, Integer::sum));

        System.out.println("Frecuencia de palabras:");
        for (Map.Entry<String, Integer> entry : combinedWordCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        BagOfWordsProb classifier = new BagOfWordsProb();

        // Definir palabras de spam y ham
        String[] spamWords = {"oferta", "gratis", "descuento"};
        String[] hamWords = {"cliente", "gracias", "promoción", "nuevo", "producto", "venta"};

        // Contar palabras de spam y ham
        classifier.loadWords(spamWords, "spam");
        classifier.loadWords(hamWords, "ham");

        // Calcular probabilidades para todas las palabras en spam y ham
        double spamProbability = classifier.calculateProbability(classifier.spamWordCounts, classifier.getTotalWords("spam"));
        double hamProbability = classifier.calculateProbability(classifier.hamWordCounts, classifier.getTotalWords("ham"));

        // Imprimir las probabilidades
        System.out.println("Probabilidad en spam: " + spamProbability + "%");
        System.out.println("Probabilidad en ham: " + hamProbability + "%");

        // Imprimir el total de frases en spam y ham
        System.out.println("Palabras en spam: " + classifier.getTotalWords("spam"));
        System.out.println("Palabras en ham: " + classifier.getTotalWords("ham"));

        // Imprimir el total de palabras en general
        System.out.println("Total de palabras: " + classifier.getTotalWords());

        // Imprimir la frecuencia de cada palabra en spam y en ham
        classifier.printWordFrequencies();

        // Pedir al usuario palabras para calcular sus probabilidades
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.print("Ingresa una nueva palabra para calcular su probabilidad (o escribe 'salir' para salir): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("salir")) {
                exit = true;
            } else {
                // Verificar si la palabra está en spam o en ham y calcular su probabilidad
                double wordSpamProbability = classifier.calculateSpamProbability(input);
                double wordHamProbability = classifier.calculateHamProbability(input);

                // Imprimir la probabilidad de la palabra en spam y en ham
                if (wordSpamProbability == 0 && wordHamProbability == 0) {
                    System.out.println("No hay ninguna probabilidad para la palabra ingresada.");
                } else {
                    System.out.println("Probabilidad de '" + input + "' en spam: " + wordSpamProbability + "%");
                    System.out.println("Probabilidad de '" + input + "' en ham: " + wordHamProbability + "%");
                }
            }
        }
    }

    // Método para calcular la probabilidad de una palabra en ham
    public double calculateHamProbability(String word) {
        int wordCountInHam = hamWordCounts.getOrDefault(word, 0);
        return (double) wordCountInHam / totalHamWords;
    }

    // Método para calcular la probabilidad de una palabra en spam
    public double calculateSpamProbability(String word) {
        int wordCountInSpam = spamWordCounts.getOrDefault(word, 0);
        return (double) wordCountInSpam / totalSpamWords;
    }
}
