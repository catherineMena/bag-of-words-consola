import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Elige una opción:");
            System.out.println("1. BagOfWordsClassifier");
            System.out.println("2. BagOfWordsClassifierURL");
            System.out.println("3. BagOfWordsLabels");
            System.out.println("4. BagOfWordsProb");
            System.out.println("5. Salir");

            try {
                int option = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer del scanner

                switch (option) {
                    case 1:
                        // Ejecutar BagOfWordsClassifier
                        BagOfWordsClassifier.main(args);
                        System.out.println("Saliendo...");
                        return;
                    case 2:
                        // Ejecutar BagOfWordsClassifierURL
                        BagOfWordsClassifierURL.main(args);
                        System.out.println("Saliendo...");
                        return;
                    case 3:
                        // Ejecutar BagOfWordsLabels
                        BagOfWordsLabels.main(args);
                        System.out.println("Saliendo...");
                        return;
                    case 4:
                        // Ejecutar BagOfWordsProb
                        BagOfWordsProb.main(args);
                        System.out.println("Saliendo...");
                        return;

                    case 5:
                        // Salir del programa
                        System.out.println("Saliendo del programa...");
                        scanner.close(); // Cerrar el scanner antes de salir
                        return;
                    default:
                        System.out.println("Opción inválida. Por favor, elige una opción válida.");
                        break;
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingresa un número del 1 al 5.");
                scanner.nextLine(); // Limpiar el buffer del scanner
            }
        }
    }
}
