package at.ac.fhcampuswien.alcatraz.client;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

@QuarkusMain(name="client")
public class ClientApplication implements QuarkusApplication{

    @Inject
    CommandLineInterface cli;

    @Override
    public int run(String... args) {
        cli.start();
        return 0;
    }
}
