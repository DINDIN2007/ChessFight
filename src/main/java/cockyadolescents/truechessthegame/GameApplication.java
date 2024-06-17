package cockyadolescents.truechessthegame;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Objects;

public class GameApplication extends Application {
    public static Stage window;
    public static HomePage homepage;
    public static WaitingRoom waitingroom;
    public static Setting settingroom;
    public static Audio music;

    public static OfflineGame maingame;
    public static OnlineGame onlinegame;
    public static Client client;
    public static Server server;

    @Override
    public void start(Stage stage) throws IOException {
        // Set up the main window of the game
        window = stage;
        window.setTitle("Chess Fight");

        // Set up the other pages
        homepage = new HomePage();
        waitingroom = new WaitingRoom();
        maingame = new OfflineGame();
        onlinegame = new OnlineGame();
        settingroom = new Setting();
        music = new Audio();

        // Set up the other scenes
        homepage.display();
        window.show();
        maingame.startGame();
        settingroom.setupPage();

        // Set up the icon of the game
        Image icon = new Image(Objects.requireNonNull(GameApplication.class.getResourceAsStream("images/Icon.png")));
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

    public static void main(String[] args) {
        launch();
    }
}