package org.cockyadolescents.truechessthegame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Vector;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        newGame(stage);
    }

    public void newGame(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("True Chess the Game");
        stage.setScene(scene);
        stage.show();

        // THIS AREA IS TO TEST THE CHESSBOARD CLASS JAVA
        ChessBoard.newGameSample();
        System.out.println(ChessBoard.printBoard());
    }

    public static void main(String[] args) {launch();}
}