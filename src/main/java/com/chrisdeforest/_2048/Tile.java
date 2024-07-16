/**
 * Tile.java
 * This class represents a tile in the 2048 game. Each tile has a value, a background color,
 * a text color, and a font size based on its value. It also tracks whether an animation
 * has been played and the move in which it was generated.
 */
package com.chrisdeforest._2048;

/**
 * The Tile class represents a tile in the 2048 game.
 * It handles the tile's value, appearance, and state.
 */
public class Tile {
    // Color codes for tiles based on their values
    private static final String COLOR_0 = "rgb(204, 192, 179)", COLOR_2 = "rgb(238, 228, 218)",
            COLOR_4 = "rgb(237, 224, 200)", COLOR_8 = "rgb(242, 177, 121)", COLOR_16 = "rgb(245, 149, 99)",
            COLOR_32 = "rgb(246, 124, 95)", COLOR_64 = "rgb(246, 94, 59)", COLOR_128 = "rgb(237, 207, 114)",
            COLOR_256 = "rgb(237, 204, 97)", COLOR_512 = "rgb(237, 200, 80)", COLOR_1024 = "rgb(237, 197, 63)",
            COLOR_2048 = "rgb(237, 194, 46)", COLOR_DEFAULT = "rgb(0, 0, 0)";
    private int value; // The value of the tile; default: 0
    private int moveGenerated; // The move number when the tile was generated; default: -1
    private boolean animationPlayed; // Whether the animation for this tile has been played

    /**
     * Default constructor. Initializes a Tile with a value of 0, generated on move -1, and
     * has not yet played an animation
     */
    public Tile() {
        this.value = 0;
        this.moveGenerated = -1;
        this.animationPlayed = false;
    }

    /**
     * Constructor that initializes the tile with a specified value
     *
     * @param v The value of the tile
     */
    public Tile(int v){
        this.value = v;
    }

    /**
     * Gets the value of the tile
     *
     * @return The value of the tile
     */
    public int getValue(){
        return this.value;
    }

    /**
     * Sets the value of the tile
     *
     * @param v The new value of the tile
     */
    public void setValue(int v){
        this.value = v;
    }

    /**
     * Gets the move number when the tile was generated
     *
     * @return The move number when the tile was generated
     */
    public int getMoveGenerated(){
        return this.moveGenerated;
    }

    /**
     * Sets the move number when the tile was generated
     *
     * @param mg The move number when the tile was generated
     */
    public void setMoveGenerated(int mg){
        this.moveGenerated = mg;
    }

    /**
     * Checks if the animation for this tile has been played
     *
     * @return True if the animation has been played, false otherwise
     */
    public boolean getAnimationPlayed(){
        return this.animationPlayed;
    }

    /**
     * Sets whether the animation for this tile has been played
     *
     * @param ap True if the animation has been played, false otherwise
     */
    public void setAnimationPlayed(boolean ap){
        this.animationPlayed = ap;
    }

    /**
     * Checks if the tile is empty (i.e., its value is 0).
     *
     * @return True if the tile is not empty, false otherwise
     */
    public boolean isNotEmpty(){
        return this.value != 0;
    }

    /**
     * Gets the background color of the tile based on its value
     *
     * @return The background color of the tile
     */
    public String getBackground() {
        return switch (this.value) {
            case 0 -> COLOR_0;
            case 2 -> COLOR_2;
            case 4 -> COLOR_4;
            case 8 -> COLOR_8;
            case 16 -> COLOR_16;
            case 32 -> COLOR_32;
            case 64 -> COLOR_64;
            case 128 -> COLOR_128;
            case 256 -> COLOR_256;
            case 512 -> COLOR_512;
            case 1024 -> COLOR_1024;
            case 2048 -> COLOR_2048;
            default -> COLOR_DEFAULT;
        };
    }

    /**
     * Gets the text color of the tile based on its value
     *
     * @return The text color of the tile
     */
    public String getTextColor(){
        if(this.value < 16)
            return "rgb(119, 110, 101)";
        else
            return "rgb(249, 246, 242)";
    }

    /**
     * Gets the font size of the tile based on its value
     *
     * @return The font size of the tile
     */
    public String getFontSize(){
        return switch (this.value){
            case 0, 2, 4, 8, 16, 32, 64, 128, 256, 512 -> "40pt";
            case 1024, 2048, 4096, 8192 -> "30pt";
            case 16384, 32768, 65536 -> "25pt";
            case 131072, 262144, 524288 -> "20pt";
            default -> "18pt";
        };
    }
}
