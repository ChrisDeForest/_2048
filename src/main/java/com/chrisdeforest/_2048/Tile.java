package com.chrisdeforest._2048;

import javafx.scene.paint.Color;

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
    public Color getBackground(){
        return switch (this.value) {
            case 0 -> Color.color(204, 192, 179);
            case 2 -> Color.color(238, 228, 218);
            case 4 -> Color.color(237, 224, 200);
            case 8 -> Color.color(242, 177, 121);
            case 16 -> Color.color(245, 149, 99);
            case 32 -> Color.color(246, 124, 95);
            case 64 -> Color.color(246, 94, 59);
            case 128 -> Color.color(237, 207, 114);
            case 256 -> Color.color(237, 204, 97);
            case 512 -> Color.color(237, 200, 80);
            case 1024 -> Color.color(237, 197, 63);
            case 2048 -> Color.color(237, 194, 46);
            default -> Color.BLACK;
        };
    }
    public Color getForeground(){
        if(this.value < 16)
            return Color.color(119, 110, 101);
        else
            return Color.color(249, 246, 242);
    }
}
