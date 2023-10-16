package at.ac.fhcampuswien.alcatraz.server.spread;

import at.ac.fhcampuswien.alcatraz.server.ServerState;
import at.ac.fhcampuswien.alcatraz.server.spread.service.SpreadMessageHandler;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Random;

import org.jboss.logging.Logger;
import spread.*;

@Singleton
public class SpreadCommunicator implements AdvancedMessageListener, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    SpreadMessageHandler spreadMessageHandler;

    @Inject
    ServerState serverState;

    @ConfigProperty(name = "spread-server")
    String spreadServer;

    SpreadGroupBean spreadGroup;
    SpreadConnectionBean spreadConnection;

    private static final Logger log = Logger.getLogger(SpreadCommunicator.class);

    @PostConstruct
    public void initSpread() {
        this.spreadConnection = establishSpreadConnection();
        this.spreadGroup = joinSpreadGroup();
    }

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
        if (spreadMessage.getType() == 1) { //sync
            handleSyncMessage(spreadMessage);
        }
    }

    private void handleSyncMessage(SpreadMessage spreadMessage) {
        try {
            Object object = deserializeObject(spreadMessage.getData());
            if (object instanceof GameSession) {
                log.info("Recieved GameSession Object");
                log.info(object);
                spreadMessageHandler.handleSyncSession((GameSession) object);
            }
        } catch (SpreadException e) {
            log.error("Error handling sync message", e);
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {
        try {
            spreadMessageHandler.handleMembershipMessage(spreadConnection, spreadGroup, spreadMessage);
        } catch (Exception e) {
            log.error("Error handling membership message", e);
        }
    }

    public void sendMessageToSpread(GameSession<NetPlayer> session) {
        spreadMessageHandler.syncGameSessionWithGroup(spreadConnection, spreadGroup, session);
    }

    private SpreadConnectionBean establishSpreadConnection() {
        SpreadConnectionBean connection = new SpreadConnectionBean();
        try {
            int privateName = generatePrivateName();
            InetAddress hostName = InetAddress.getByName(spreadServer);
            connection.connect(hostName, 0, Integer.toString(privateName), true, true);
            this.serverState.setServerId(privateName);
        } catch (Exception e) {
            log.error("Error establishing Spread connection", e);
            System.exit(0);
        }
        connection.add(this);
        return connection;
    }

    private int generatePrivateName() {
        return new Random().nextInt(99999);
    }

    private SpreadGroupBean joinSpreadGroup() {
        SpreadGroupBean group = new SpreadGroupBean();
        try {
            group.join(spreadConnection, "group");
        } catch (SpreadException e) {
            log.error("Error joining Spread group", e);
            System.exit(0);
        }
        return group;
    }

    private Object deserializeObject(byte[] data) throws SpreadException {
        ByteArrayInputStream var1 = new ByteArrayInputStream(data);

        ObjectInputStream var2;
        try {
            var2 = new ObjectInputStream(var1);
        } catch (IOException var8) {
            throw new SpreadException("ObjectInputStream(): " + var8);
        }

        Object var3;
        try {
            var3 = var2.readObject();
        } catch (ClassNotFoundException | IOException var6) {
            throw new SpreadException("readObject(): " + var6);
        }

        try {
            var2.close();
            var1.close();
            return var3;
        } catch (IOException var5) {
            throw new SpreadException("close/close(): " + var5);
        }
    }

}