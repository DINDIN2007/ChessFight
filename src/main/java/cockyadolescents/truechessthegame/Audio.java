package cockyadolescents.truechessthegame;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Audio {
    private Media[] playlist;
    private Media pieceMoved, pieceCaptured, newGame;

    public Audio() {
        this.playlist = new Media[] {
                new Media(getClass().getResource("trumpet-lofi-141049.mp3").toExternalForm()),
                new Media(getClass().getResource("lofi-jazz-rap-1-hiphop-158516.mp3").toExternalForm()),
                new Media(getClass().getResource("lofi-relax-travel-by-lofium-123560.mp3").toExternalForm()),
                new Media(getClass().getResource("ogi-feel-the-beat-jazz-expresso-191266.mp3").toExternalForm()),
        };

        // Sound Effects
        this.pieceMoved = new Media(getClass().getResource("move-self.mp3").toExternalForm());
        this.pieceCaptured =  new Media(getClass().getResource("capture.mp3").toExternalForm());
    }

    public void playMusic() {
        Media currentSong = this.playlist[(int)(Math.random() * this.playlist.length)];
        MediaPlayer mediaPlayer = new MediaPlayer(currentSong);
        mediaPlayer.play();

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                playMusic();
            }
        });
    }

    public void movePiece() {
        MediaPlayer sound = new MediaPlayer(this.pieceMoved);
        sound.play();
    }

    public void capturePiece() {
        MediaPlayer sound = new MediaPlayer(this.pieceCaptured);
        sound.play();
    }
}
