package cockyadolescents.truechessthegame;

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
    public String address;

    private Parent root;
    private Scene scene;

    @FXML
    public void home() throws IOException {
        homepage.display();
    }

    public void display() throws IOException {
        if (root != null) {
            window.setScene(scene);
            return;
        }
        root = FXMLLoader.load(getClass().getResource("waitingroom.fxml"));
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        window.setScene(scene);
        addressLabel = (Label) root.lookup("#hostAddress");
        notificationLabel = (Label) root.lookup("#notificationLabel");
        Game.onlineGame = true; // to change some features in the normal game
        getDeviceAddress();
    }

    public void disconnect() {
        try {
            waitingroom.display();
            waitingroom.notification("Disconnected");
            waitingroom.clientThread = null;
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // gets ip address of device and runs server
    public void getDeviceAddress() {
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
                        this.address = address.getHostAddress();
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
        getDeviceAddress();
        addressLabel.setText(this.address);
        addressField.setText(this.address);
    }

    public Thread clientThread;
    public Thread serverThread;

    @FXML
    public void hostServer() {
        if (serverThread == null) {
            server = new Server();
            serverThread = new Thread(server);
            serverThread.start();
        }
    }

    @FXML
    public void joinServer() throws IOException {
        String s = addressField.getText();
        if (s.isEmpty()) s = "localhost";
        client = new Client(s);
        clientThread = new Thread(client);
        clientThread.start();
    }

    // notification message that displays for 2s
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
