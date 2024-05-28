package cockyadolescents.truechessthegame;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Audio {
    private Media[] playlist;
    private Media pieceMoved, newGame;

    public Audio() {
        this.playlist = new Media[] {
                new Media(getClass().getResource("trumpet-lofi-141049.mp3").toExternalForm())
        };
        this.pieceMoved = new Media(getClass().getResource("move-self.mp3").toExternalForm());
    }

    public void playMusic() {
        Media currentSong = this.playlist[(int)(Math.random() * this.playlist.length)];
        MediaPlayer mediaPlayer = new MediaPlayer(currentSong);
        mediaPlayer.play();
    }

    public void movePiece() {
        MediaPlayer sound = new MediaPlayer(this.pieceMoved);
        sound.play();
    }
}
