package at.ac.fhcampuswien.alcatraz.server;

import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistrationService;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@QuarkusMain(name = "server")
public class ServerApplication implements QuarkusApplication {
    @Inject
    CommandLineInterface cli;

    @Inject
    RegistrationService registrationService;

    @Override
    public int run(String... args) throws Exception {

        Registry registry = LocateRegistry.createRegistry(1099); // RMI default port
        registry.bind("RegistrationService", registrationService);
        System.out.println("RMI Service bound");
        cli.start();

        return 0;
    }
}