package at.ac.fhcampuswien.alcatraz.server.spread;


import jakarta.inject.Singleton;
import spread.SpreadGroup;

import java.io.Serial;
import java.io.Serializable;

@Singleton
public class SpreadGroupBean extends SpreadGroup implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


}
