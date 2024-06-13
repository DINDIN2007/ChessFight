package cockyadolescents.truechessthegame;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SecondMain extends Application {
    public static Stage window;
    public static HomePage homepage;
    public static WaitingRoom waitingroom;
    public static Game maingame;
    public static Setting settingroom;

    public static Audio music;

    @Override
    public void start(Stage stage) throws IOException {
        // Set up the main window of the game
        window = stage;
        window.setTitle("Chess Fight");

        // Set up the other pages
        homepage = new HomePage();
        waitingroom = new WaitingRoom();
        maingame = new Game();
        settingroom = new Setting();
        music = new Audio();

        // Set up the other scenes
        homepage.display();
        window.show();
        maingame.startGame();
        settingroom.setupPage();

        // Set up the icon of the game
        Image icon = new Image(Objects.requireNonNull(SecondMain.class.getResourceAsStream("images/Icon.png")));
        window.getIcons().add(icon);

        // Start music
        music.playMusic();
    }

    public static void main(String[] args) {launch();}
}
