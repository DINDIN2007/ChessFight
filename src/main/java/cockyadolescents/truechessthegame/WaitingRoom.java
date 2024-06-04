package cockyadolescents.truechessthegame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static cockyadolescents.truechessthegame.Main.*;

// Online game
public class WaitingRoom {
    @FXML private TextField addressField;
    @FXML private Button hostServer;
    @FXML private Label addressLabel;
    Parent root;
    Scene scene;

    @FXML
    public void home() throws IOException {
        homepage.display();
    }

    public void display() throws IOException {
        root = FXMLLoader.load(getClass().getResource("waitingroom.fxml"));
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        window.setScene(scene);
        addressLabel = (Label) root.lookup("#hostAddress");
        Game.onlineGame = true; // to change some features in the normal game
    }

    // gets ip address of host and runs server
    public String getHostAddress() {
        try {
            // retrieves available network interfaces
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // obtains IP addresses associated with each interface
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    // filters out loopback addresses and returns IPv4 addresses
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    public void hostServer() {
        String hostAddress = getHostAddress();
        addressLabel.setText("Host Address: " + hostAddress);
        Server server = new Server();
        Client client = new Client(hostAddress);
    }

    @FXML
    public void joinServer() {
        Client client = new Client(addressField.getText());
    }
}
