package com.chrisdeforest._2048;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Game {
    public final static int BOARD_SIZE = 4;
    private PropertyChangeSupport support;
    private Tile[][] board;
    private Random rand;
    private Boolean wonGame, continued;
    private int score, bestScore;
    public Game(){
        this.board = new Tile[BOARD_SIZE][BOARD_SIZE];
        this.rand = new Random();
        this.support = new PropertyChangeSupport(this);
        this.wonGame = false;
        this.continued = false;
        initializeBoard();
    }
    public void initializeBoard(){
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                this.board[i][j] = new Tile();
            }
        }
    }
    public void clearBoard(){
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                board[i][j].setValue(0);
            }
        }
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
        this.support.firePropertyChange("newGame", null, rand.nextInt(10));
    }
    public void moveVertical(String dir){
        for(int i = 0; i < BOARD_SIZE; i++){
            Tile[] column = {board[0][i], board[1][i], board[2][i], board[3][i]};
            List<Tile> newColumn = new LinkedList<>();
            for(Tile t: column){
                if(!t.isEmpty())
                    newColumn.add(t);
            }
            int missing = 4 - newColumn.size();
            for(int j = 0; j < missing; j++) {
                if(dir.equals("up"))
                    newColumn.add(new Tile());
                else {
                    newColumn.addFirst(new Tile());
                }
            }
            for(int j = 0; j < BOARD_SIZE; j++)
                board[j][i] = newColumn.get(j);
        }
        this.support.firePropertyChange(dir, null, 0);
    }
    public void moveHorizontal(String dir){
        for(int i = 0; i < BOARD_SIZE; i++){
            Tile[] row = {board[i][0], board[i][1], board[i][2], board[i][3]};
            List<Tile> newRow = new LinkedList<>();
            for(Tile t: row){
                if(!t.isEmpty())
                    newRow.add(t);
            }
            int missing = 4 - newRow.size();
            for(int j = 0; j < missing; j++){
                if(dir.equals("right"))
                    newRow.addFirst(new Tile());
                else {
                    newRow.add(new Tile());
                }
            }
            for(int j = 0; j < BOARD_SIZE; j++)
                board[i][j] = newRow.get(j);
        }
        this.support.firePropertyChange(dir, null, 0);
    }
    public int getValue(int r, int c){
        return this.board[r][c].getValue();
    }
    public void setValue(int r, int c, int v){
        this.board[r][c].setValue(v);
    }
    public Tile[][] getBoard(){
        return this.board;
    }
    public void setBoard(Tile[][] b){
        this.board = b;
    }
    public boolean getWonGame(){
        return this.wonGame;
    }
    public void setWonGame(boolean wg){
        this.wonGame = wg;
    }
    public boolean getContinued(){
        return this.continued;
    }
    public void setContinued(boolean c){
        this.continued = c;
    }
    public int getScore(){
        return this.score;
    }
    public void setScore(int s){
        this.score = s;
    }
    public int getBestScore(){
        return this.bestScore;
    }
    public void setBestScore(int bs){
        this.bestScore = bs;
    }
    @Override
    public String toString(){
        StringBuilder string = new StringBuilder();
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                string.append(board[i][j].getValue()).append(" ");
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
