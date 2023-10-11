package at.ac.fhcampuswien.alcatraz.server.spread;


import jakarta.inject.Singleton;
import spread.SpreadConnection;

import java.io.Serial;
import java.io.Serializable;

@Singleton
public class SpreadConnectionBean extends SpreadConnection implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
