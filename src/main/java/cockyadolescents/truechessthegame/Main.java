package cockyadolescents.truechessthegame;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static Stage window;
    public static HomePage homepage = new HomePage();
    public static WaitingRoom waitingroom = new WaitingRoom();
    public static Game maingame = new Game();

    public static Audio music = new Audio();

    @Override
    public void start(Stage stage) throws IOException {
        window = stage;
        window.setTitle("True Chess the Game");
        homepage.display();
        window.show();
        maingame.startGame();

        // Start music
        if (!Audio.disabled) {
            music.playMusic();
        }
    }

    public static void main(String[] args) {launch();}
}
