package at.ac.fhcampuswien.alcatraz.server;

import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;
import jakarta.inject.Singleton;

import java.io.Serializable;

@Singleton
public class ServerContext implements Serializable {
    boolean isPrimary;
    Integer serverId;
    GameSession<NetPlayer> gameSession = new GameSession<>();

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

    public GameSession<NetPlayer> getSession() {
        return gameSession;
    }

    @Override
    public String toString() {
        return "ServerContext{" +
                "isPrimary=" + isPrimary +
                ", serverId=" + serverId +
                ", session=" + gameSession +
                '}';
    }

    public void setSession(GameSession<NetPlayer> gameSession) {
        this.gameSession = gameSession;
    }
}
