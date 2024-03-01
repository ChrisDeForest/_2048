package com.chrisdeforest._2048;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Random;

public class Game {
    private PropertyChangeSupport support;
    private final static int BOARD_SIZE = 4;
    Tile[][] board;

    public Game(){
        this.board = new Tile[BOARD_SIZE][BOARD_SIZE];
        this.support = new PropertyChangeSupport(this);
    }
    public void initializeBoard(){
        int count = 0;
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                this.board[i][j] = new Tile();
                this.support.firePropertyChange("value", count, ++count);
            }
        }
    }
    public int getValue(int r, int c){
        return this.board[r][c].getValue();
    }
    public void setValue(int r, int c, int v){
        this.board[r][c].setValue(v);
    }
    public void newGame(){
        Random rand = new Random();
        this.support.firePropertyChange("newGame", null, rand.nextInt(100));
    }
    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.support.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener){
        this.support.removePropertyChangeListener(listener);
    }
}
