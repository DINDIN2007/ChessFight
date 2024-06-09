package cockyadolescents.truechessthegame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static cockyadolescents.truechessthegame.Boxing.*;

public class BoxingPiece {
    // Piece position and dimensions
    public double x, y;
    private int width = 48, height = 48; // To be change to scale the piece
    public double gloveX, gloveY, gloveSize = 48;

    // Piece information
    private int pieceType, pieceColor;
    private int speed = 5;
    public boolean isDefeated = false;

    // Attack information
    public boolean canAttack = true, isAttacking = false;
    private double punchingReach = 20, punchingWidth = 0;

    // Movement booleans
    public boolean moveUp, moveDown, moveLeft, moveRight;

    public BoxingPiece(double x, double y, String pieceType, String pieceColor) {
        // Sets the starting positions of the piece
        this.x = x; this.y = y;

        // Find the piece type and its start x position on the source image
        switch(pieceType){
            case "Pawn":    this.pieceType = 48*0; break;
            case "Rook":    this.pieceType = 48*1; break;
            case "Knight":  this.pieceType = 48*2; break;
            case "Bishop":  this.pieceType = 48*3; break;
            case "Queen":   this.pieceType = 48*4; break;
            case "King":    this.pieceType = 48*5; break;
        }

        // Find the piece color and its start y position on the source image
        switch(pieceColor){
            case "White":   this.pieceColor = 48; break;
            case "Black":   this.pieceColor = 0;  break;
        }
    }

    // Updates the piece actions on the canvas
    public void update(BoxingPiece opponent){
        this.updatePosition();
        this.checkCollisions(opponent);

        // this.drawHitbox();
        this.drawPiece();
        this.glove();
    }

    // Update piece position
    private void updatePosition() {
        if (this.moveUp) this.y -= this.speed;
        if (this.moveDown) this.y += this.speed;
        if (this.moveRight) this.x += this.speed;
        if (this.moveLeft) this.x -= this.speed;
    }

    // Detects hitbox collisions
    private void checkCollisions(BoxingPiece opponent) {
        // Detects collision between the two pieces

        // Detects collision between the piece and the opponents glove
        if (opponent.isAttacking) {
            if (opponent.gloveX < this.x + this.width &&
                opponent.gloveX + gloveSize > this.x &&
                opponent.gloveY < this.y + this.height &&
                opponent.gloveY + gloveSize > this.y) {
                    this.isDefeated = true;
            }
        }

        // Detects collision between the piece and the border
        if (this.x + this.width >= canvas.getWidth()) { // Right Border
            this.x = canvas.getWidth() - this.width;
        }
        if (this.y + this.height >= canvas.getHeight()) { // Bottom Border
            this.y = canvas.getHeight() - this.height;
        }
        if (this.x <= 0) { // Left Border
            this.x = 0;
        }
        if (this.y <= 0){ // High Border
            this.y = 0;
        }
    }

    // Runs attack animation
    public void launchAttack() {
        if (this.canAttack) {
            this.isAttacking = true;
            this.canAttack = false;

            this.punchingWidth = 0;

            // Set cooldown timer
            Timeline cooldownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> this.canAttack = true));
            cooldownTimeline.setCycleCount(1);
            cooldownTimeline.play();

            // Punch comes back
            Timeline punchTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> this.isAttacking = false));
            punchTimeline.setCycleCount(1);
            punchTimeline.play();
        }
    }

    // Draws glove sprite
    private void glove() {
        if (this.isAttacking) {
            if (this.punchingWidth < this.punchingReach) {
                this.punchingWidth += 5;
            }
            if (this.y < canvas.getHeight() / 2) {
                this.gloveX = this.x + 12;
                this.gloveY = this.y + this.height + this.punchingWidth;
            }
            else {
                this.gloveX = this.x + 12;
                this.gloveY = this.y - this.punchingWidth;
            }
            graphicsContext.drawImage(source2, 48, 24, 24, 24, this.gloveX, this.gloveY, this.gloveSize, this.gloveSize);
        }

        else {
            if (this.y < canvas.getHeight() / 2) {
                this.gloveX = this.x + 12;
                this.gloveY = this.y + this.height;
            }
            else {
                this.gloveX = this.x + 12;
                this.gloveY = this.y;
            }

            graphicsContext.drawImage(source2, 48, 0, 24, 24, this.gloveX, this.gloveY, this.gloveSize, this.gloveSize);
        }
            /*this.gloveX = this.x + 12;
            this.gloveY = this.y - this.punchingWidth;
        }
        else {
            this.gloveX = this.x + 12;
            this.gloveY = this.y - 10;
        }*/
    }

    // Draws the piece on the canvas
    private void drawPiece() {
        graphicsContext.drawImage(source, this.pieceType, this.pieceColor,48, 48, this.x, this.y, this.width, this.height);
    }

    // Draws hitboxes for debug
    private void drawHitbox() {
        graphicsContext.setFill(Color.BLUE);
        graphicsContext.fillRect(this.x, this.y, this.width, this.height);

        graphicsContext.setFill(Color.RED);
        graphicsContext.fillRect(this.gloveX, this.gloveY, this.gloveSize, this.gloveSize);
    }
}