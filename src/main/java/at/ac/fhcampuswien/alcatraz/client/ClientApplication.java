package at.ac.fhcampuswien.alcatraz.client;

import at.ac.fhcampuswien.alcatraz.client.service.ClientController;
import at.ac.fhcampuswien.alcatraz.shared.exception.messages.Messages;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

import javax.swing.*;

@QuarkusMain(name = "client")
public class ClientApplication implements QuarkusApplication {

    @Inject
    ClientController clientController;

    @Override
    public int run(String... args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ClientGui gui = new ClientGui();
                gui.initializeUI(clientController);
            } catch (Exception e) {
                System.err.println(Messages.ERROR_GUI_START);
                System.err.println(e.getMessage());
                System.exit(0);
            }
        });

        // Keep the application running.
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return 0;
    }
}
