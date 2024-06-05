package cockyadolescents.truechessthegame;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Audio {
    public static boolean disabled = true;
    private Media[] playlist;
    private Media pieceMoved, pieceCaptured, newGame;
    private MediaPlayer songPlayer;

    public Audio() {
        this.playlist = new Media[] {
                new Media(getClass().getResource("audio/trumpet-lofi-141049.mp3").toExternalForm()),
                new Media(getClass().getResource("audio/lofi-jazz-rap-1-hiphop-158516.mp3").toExternalForm()),
                new Media(getClass().getResource("audio/lofi-relax-travel-by-lofium-123560.mp3").toExternalForm()),
                new Media(getClass().getResource("audio/ogi-feel-the-beat-jazz-expresso-191266.mp3").toExternalForm()),
        };

        // Sound Effects
        this.pieceMoved = new Media(getClass().getResource("audio/move-self.mp3").toExternalForm());
        this.pieceCaptured =  new Media(getClass().getResource("audio/capture.mp3").toExternalForm());
        this.newGame = new Media(getClass().getResource("audio/board-start.mp3").toExternalForm());

        // Set songPlayer to anything
        songPlayer = new MediaPlayer(this.newGame);
    }

    public void playMusic() {
        // Stops previous music
        songPlayer.stop();

        // Chooses random new song
        Media currentSong = this.playlist[(int)(Math.random() * this.playlist.length)];
        songPlayer = new MediaPlayer(currentSong);
        songPlayer.play();

        if (!disabled) {
            songPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                    playMusic();
                }
            });
        }
    }

    public void movePiece() {
        MediaPlayer sound = new MediaPlayer(this.pieceMoved);
        sound.play();
    }

    public void capturePiece() {
        MediaPlayer sound = new MediaPlayer(this.pieceCaptured);
        sound.play();
    }

    public void startGame() {
        MediaPlayer sound = new MediaPlayer(this.newGame);
        sound.play();
    }
}
