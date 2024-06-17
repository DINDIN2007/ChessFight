package cockyadolescents.truechessthegame;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static Stage window;
    public static HomePage homepage = new HomePage();
    public static WaitingRoom waitingroom = new WaitingRoom();
    public static Setting settingroom = new Setting();
    public static Audio music = new Audio();

    public static Game maingame = new Game();
    public static OnlineGame onlinegame = new OnlineGame();
    public static Client client;
    public static Server server;

    @Override
    public void start(Stage stage) throws IOException {
        // Set up the main window of the game
        window = stage;
        window.setTitle("Chess Fight");

        // Set up the other scenes
        homepage.display();
        window.show();
        maingame.startGame();
        settingroom.setupPage();

        // Set up the icon of the game
        Image icon = new Image(getClass().getResource("images/icon.png").toExternalForm());
        window.getIcons().add(icon);

        // Start music
        music.playMusic();

        // disconnects client and closes server
        window.setOnCloseRequest(e -> {
            if (client != null && client.chatSocket != null) {
                if (!client.chatSocket.isClosed())
                    client.textOut.println("/quit");
            }
            if (server != null) {
                if (!server.closed)
                    server.shutdown();
            }
            stage.close();
            System.exit(0);
        });
    }

    public static void main(String[] args) {launch();}
}
