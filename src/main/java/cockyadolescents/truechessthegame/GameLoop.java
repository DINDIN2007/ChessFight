package cockyadolescents.truechessthegame;

import javafx.animation.AnimationTimer;

import java.io.IOException;

public class GameLoop extends AnimationTimer {
    private long lastUpdateTime;
    private double lastSavedIteration = 0, delay; // Delay between updates
    private int iteration = 1;

    private Boxing boxingGameElement;
    private OfflineGame chessGameElement;
    private int typeOfGameLoop = 0;

    public GameLoop(double delay) {
        this.delay = delay;
    }

    public GameLoop(Boxing boxingGameElement, double delay) {
        this.boxingGameElement = boxingGameElement;
        this.delay = delay / 1000;
        this.typeOfGameLoop = 1;
    }

    public GameLoop(OfflineGame chessGameElement, double delay) {
        this.chessGameElement = chessGameElement;
        this.delay = delay / 1000;
        this.typeOfGameLoop = 2;
    }

    @Override
    public void handle(long currentNanoTime) {
        // Find the time that passed between last update (in Seconds)
        double elapsedTime = (currentNanoTime - lastUpdateTime) / Math.pow(10.0,9);
        lastUpdateTime = currentNanoTime;

        lastSavedIteration += elapsedTime;

        // Update elements and canvas if passed delay
        if (lastSavedIteration > delay) {
            try {
                update(elapsedTime);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            lastSavedIteration = 0;
        }
    }

    private void update(double elapsedTime) throws IOException {
        switch(typeOfGameLoop) {
            case 1: boxingGameElement.updateElement(); break;
            case 2: OfflineGame.update(); break;
            default : System.out.println("Animation Timer Test : " + (iteration++));
        }
    }
}
