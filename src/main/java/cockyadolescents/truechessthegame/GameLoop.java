package cockyadolescents.truechessthegame;

import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {
    private long lastUpdateTime, delay;
    private double lastSavedIteration = 0; // Delay between updates
    private int iteration = 1;

    private Boxing boxingGameElement;
    private Game chessGameElement;
    private int typeOfGameLoop = 0;

    public GameLoop(long delay) {
        this.delay = delay;
    }

    public GameLoop(Boxing boxingGameElement, long delay) {
        this.boxingGameElement = boxingGameElement;
        this.delay = delay;
        this.typeOfGameLoop = 1;
    }

    public GameLoop(Game chessGameElement, long delay) {
        this.chessGameElement = chessGameElement;
        this.delay = delay;
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
            update(elapsedTime);
            lastSavedIteration = 0;
        }
    }

    private void update(double elapsedTime) {
        switch(typeOfGameLoop) {
            case 1: boxingGameElement.updateElement(); break;
            case 2: Game.animate(); break;
            default : System.out.println("Animation Timer Test : " + (iteration++));
        }
    }
}
