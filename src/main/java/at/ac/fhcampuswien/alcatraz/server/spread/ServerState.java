package at.ac.fhcampuswien.alcatraz.server.spread;

import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.Session;
import jakarta.inject.Singleton;

import java.io.Serializable;

@Singleton
public class ServerState implements Serializable {
    boolean isPrimary;
    Integer serverId;
    Session<NetPlayer> session;

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public Session<NetPlayer> getSession() {
        return session;
    }

    @Override
    public String toString() {
        return "ServerState{" +
                "isPrimary=" + isPrimary +
                ", serverId=" + serverId +
                ", session=" + session +
                '}';
    }

    public void setSession(Session<NetPlayer> session) {
        this.session = session;
    }
}
