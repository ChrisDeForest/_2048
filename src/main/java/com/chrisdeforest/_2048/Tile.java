package com.chrisdeforest._2048;

public class Tile {
    private static final String COLOR_0 = "rgb(204, 192, 179)", COLOR_2 = "rgb(238, 228, 218)",
            COLOR_4 = "rgb(237, 224, 200)", COLOR_8 = "rgb(242, 177, 121)", COLOR_16 = "rgb(245, 149, 99)",
            COLOR_32 = "rgb(246, 124, 95)", COLOR_64 = "rgb(246, 94, 59)", COLOR_128 = "rgb(237, 207, 114)",
            COLOR_256 = "rgb(237, 204, 97)", COLOR_512 = "rgb(237, 200, 80)", COLOR_1024 = "rgb(237, 197, 63)",
            COLOR_2048 = "rgb(237, 194, 46)", COLOR_DEFAULT = "rgb(0, 0, 0)";
    private int value, moveGenerated;
    private boolean animationPlayed;
    public Tile() {
        this.value = 0;
        this.moveGenerated = -1;
        this.animationPlayed = false;
    }
    public Tile(int v){
        this.value = v;
    }
    public int getValue(){
        return this.value;
    }
    public void setValue(int v){
        this.value = v;
    }
    public int getMoveGenerated(){
        return this.moveGenerated;
    }
    public void setMoveGenerated(int mg){
        this.moveGenerated = mg;
    }
    public boolean getAnimationPlayed(){
        return this.animationPlayed;
    }
    public void setAnimationPlayed(boolean ap){
        this.animationPlayed = ap;
    }
    public boolean isEmpty(){
        return this.value == 0;
    }
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
    public String getTextColor(){
        if(this.value < 16)
            return "rgb(119, 110, 101)";
        else
            return "rgb(249, 246, 242)";
    }
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
