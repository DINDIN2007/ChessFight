package cockyadolescents.truechessthegame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static cockyadolescents.truechessthegame.GameApplication.*;

// Online game
public class WaitingRoom {
    @FXML private TextField addressField;
    @FXML private Label addressLabel;
    private String hostAddress;
    private Thread thread;

    private Parent root;
    private Scene scene;

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
    public void getHostAddress() {
        try {
            // retrieves available network interfaces
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // obtains IP addresses associated with each interface
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    // filters out loopback addresses and returns IPv4 address
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        // assigns first one and logs the rest (debugging)
                        String hostaddress = address.getHostAddress();
                        this.hostAddress = (hostAddress == null)? hostaddress : hostAddress;
                        System.out.println(hostaddress);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void displayAddress() {
        getHostAddress();
        addressLabel.setText("Host Address: " + this.hostAddress);
    }

    @FXML
    public void hostServer() {
        if (thread == null) { // prevents hosting multiple instances of server (temporary bug fix)
         thread = new Thread(new Server());
         thread.start();
        }
    }

    @FXML
    public void joinServer() {
        Client client = new Client(addressField.getText());
        client.run();
    }
}
