package at.ac.fhcampuswien.alcatraz.server;

import at.ac.fhcampuswien.alcatraz.server.spread.ServerState;
import at.ac.fhcampuswien.alcatraz.shared.cli.Menu;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@ApplicationScoped
public class CommandLineInterface {

    @Inject
    ServerState serverState;
    public void start() {
        Menu<Runnable> menu = new Menu<>("Server Interface");
        menu.setTitle("Choose an option:");

        menu.insert("a", "Print Server State", this::printServerState);
        menu.insert("q", "Quit", null);
        Runnable choice;

        while ((choice = menu.exec()) != null) {
            choice.run();
        }
        System.out.println("Program finished");
        System.exit(0);

    }

    private void printServerState() {
        System.out.println(serverState.toString());
    }
}
