package at.ac.fhcampuswien.alcatraz.server;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Scanner;

@ApplicationScoped
public class CommandLineInterface {

    @Inject
    ServerState serverState;

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Server Interface");
            System.out.println("1. Print Server State");
            System.out.println("0. Quit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    printServerState();
                    break;
                case 0:
                    System.out.println("Program finished");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private void printServerState() {
        System.out.println(serverState.toString());
    }
}
