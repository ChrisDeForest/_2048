package com.chrisdeforest._2048;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Random;

public class Game {
    private PropertyChangeSupport support;
    public final static int BOARD_SIZE = 4;
    Tile[][] board;
    Random rand;

    public Game(){
        this.board = new Tile[BOARD_SIZE][BOARD_SIZE];
        this.rand = new Random();
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
        int tileValue1, tileValue2, r1, c1, r2, c2;
        tileValue1 = (rand.nextInt(2) == 0) ? 2 : 4;
        tileValue2 = (rand.nextInt(2) == 0) ? 2 : 4;
        r1 = rand.nextInt(4);
        c1 = rand.nextInt(4);
        do {
            r2 = rand.nextInt(4);
            c2 = rand.nextInt(4);
        } while (r2 == r1 && c2 == c1);
        clearBoard();
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(i == r1 && j == c1)
                    this.board[i][j].setValue(tileValue1);
                else if(i == r2 && j == c2){
                    this.board[i][j].setValue(tileValue2);
                }
            }
        }
        this.support.firePropertyChange("newGame", null, 0);
    }
    public void clearBoard(){
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                board[i][j].setValue(0);
            }
        }
    }
    @Override
    public String toString(){
        StringBuilder string = new StringBuilder();
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                string.append(String.valueOf(board[i][j].getValue())).append(" ");
            }
            string.append("\n");
        }
        return string.toString();
    }
    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.support.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener){
        this.support.removePropertyChangeListener(listener);
    }
}
