
package at.ac.fhcampuswien.alcatraz.client;

import at.ac.fhcampuswien.alcatraz.shared.rmi.ClientService;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

@QuarkusMain(name = "client")
public class ClientApplication implements QuarkusApplication{

    @Inject
    CommandLineInterface cli;


    @Override
    public int run(String... args) throws Exception {
        cli.start();
        return 0;
    }
}
