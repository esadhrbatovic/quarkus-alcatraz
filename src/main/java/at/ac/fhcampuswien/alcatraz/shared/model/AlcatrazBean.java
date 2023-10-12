package at.ac.fhcampuswien.alcatraz.shared.model;

import at.falb.games.alcatraz.api.Alcatraz;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;

import java.io.Serial;
import java.io.Serializable;
//maybe not needed
@Singleton
public class AlcatrazBean extends Alcatraz implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public AlcatrazBean(){
        super();
    }
}
