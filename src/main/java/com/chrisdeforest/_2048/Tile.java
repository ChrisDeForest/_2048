package com.chrisdeforest._2048;

public class Tile {
    private int value;
    public Tile() {
        this.value = 0;
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
    public boolean isEmpty(){
        return this.value == 0;
    }
    public String getBackground(){
        return switch (this.value) {
            case 0 -> "rgb(204, 192, 179)";
            case 2 -> "rgb(238, 228, 218)";
            case 4 -> "rgb(237, 224, 200)";
            case 8 -> "rgb(242, 177, 121)";
            case 16 -> "rgb(245, 149, 99)";
            case 32 -> "rgb(246, 124, 95)";
            case 64 -> "rgb(246, 94, 59)";
            case 128 -> "rgb(237, 207, 114)";
            case 256 -> "rgb(237, 204, 97)";
            case 512 -> "rgb(237, 200, 80)";
            case 1024 -> "rgb(237, 197, 63)";
            case 2048 -> "rgb(237, 194, 46)";
            default -> "rgb(0, 0, 0)";
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
