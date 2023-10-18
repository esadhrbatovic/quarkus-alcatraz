package at.ac.fhcampuswien.alcatraz.client;

import at.ac.fhcampuswien.alcatraz.client.service.ClientController;
import at.ac.fhcampuswien.alcatraz.shared.exception.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;


@ApplicationScoped
public class CommandLineInterface {
    private static final Logger log = Logger.getLogger(CommandLineInterface.class);

    @Inject
    ClientController clientController;

    String username;

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("User Interface");
            System.out.println("1. Register on Server");
            System.out.println("2. Join Game Session");
            System.out.println("3. Log Off from Server");
            System.out.println("4. Leave Game Session");
            System.out.println("0. Quit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    joinGameSession();
                    break;
                case 3:
                    logOff();
                    break;
                case 4:
                    leaveGameSession();
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

    public void register() {
        checkPrimaryIsAvailable();
        if (this.username != null) {
            System.out.println("This user is already registered");
        } else {
            try {
                System.out.print("Enter username:");
                Scanner scn = new Scanner(System.in);
                String input = scn.nextLine();
                clientController.register(input);
                this.username = input;
                System.out.println("registered user: " + this.username);
            } catch (AlcatrazException e) {
                System.out.println(e.getMessage());
            } catch (NotBoundException | RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void logOff() {
        checkPrimaryIsAvailable();
        if (this.username == null) {
            System.out.println("No user registered");
        } else {
            try {
                clientController.logOff(this.username);
                this.username = null;
                System.out.println("You have successfully logged off.");
            } catch (RemoteException | AlcatrazException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void joinGameSession() {
        checkPrimaryIsAvailable();
        if (this.username == null) {
            System.out.println("No user registered");
        } else {
            try {
                clientController.joinSession(this.username);
                System.out.println("You have joined the Game Session. Please wait until other players join");
            } catch (RemoteException | AlcatrazException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void leaveGameSession() {
        checkPrimaryIsAvailable();
        if (this.username == null) {
            System.out.println("No user registered");
        } else {
            try {
                clientController.leaveSession(this.username);
                System.out.println("Your player now has the status 'not ready'.");
            } catch (RemoteException | AlcatrazException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    private void findPrimary() throws RemoteException {
        this.clientController.findPrimary();
    }

    private void checkPrimaryIsAvailable() {
        try {
            if (this.clientController.getRegistrationService() == null || !this.clientController.getRegistrationService()
                    .isPrimary()) {
                findPrimary();
            }
        } catch (RemoteException e) {
            try {
                findPrimary();
            } catch (RemoteException ex) {
                System.out.println("No registration servers available, exiting the game.");
                log.error(ex.getMessage());
                System.exit(0);
            }
        }
    }
}
