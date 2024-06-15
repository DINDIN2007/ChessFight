package cockyadolescents.truechessthegame;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

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
    @FXML private Button joinServer;
    @FXML private Label addressLabel;
    @FXML private Label notificationLabel;
    private String hostAddress;

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
        notificationLabel = (Label) root.lookup("#notificationLabel");
        Game.onlineGame = true; // to change some features in the normal game
    }

    // gets ip address of device and runs server
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
                        this.hostAddress = address.getHostAddress();
                        return;
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

    private Thread clientThread;
    private Thread serverThread;

    @FXML
    public void hostServer() {
        if (serverThread == null) {
            serverThread = new Thread(new Server());
            serverThread.start();
        }
    }

    @FXML
    public void joinServer() throws IOException {
        client = new Client(addressField.getText());
        Thread t = new Thread(client);
        t.start();
        onlinegame.newGame();
    }

    Timeline timeline;
    public void notification(String message) {
        if (timeline != null) timeline.stop();
        notificationLabel.setText(message);
        timeline = new Timeline(
            new KeyFrame(Duration.millis(2000), event -> {
                notificationLabel.setText("");
            })
        );
        timeline.play();
    }
}
