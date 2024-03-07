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
        clearBoard();
        this.score = 0;
        this.support.firePropertyChange("score", null, this.score);
        generateTile();
        generateTile();
        this.support.firePropertyChange("newGame", null, rand.nextInt(10));
    }
    public void generateTile(){
        int t1 = (rand.nextInt(2) == 0) ? 2 : 4, r1 = rand.nextInt(4), c1 = rand.nextInt(4);
        while (!board[r1][c1].isEmpty()) {
            r1 = rand.nextInt(4);
            c1 = rand.nextInt(4);
        }
        this.board[r1][c1].setValue(t1);
    }
    public void moveUp(int iteration){
        boolean sameBoard = true;
        for(int i = 0; i < BOARD_SIZE; i++){
            Tile[] col = {board[0][i], board[1][i], board[2][i], board[3][i]};
            List<Tile> newCol = new LinkedList<>();
            for(Tile t: col){
                if(!t.isEmpty())
                    newCol.add(t);
            }
            // adds back the missing values once values have been moved around
            int missingTiles = 4 - newCol.size();
            for(int j = 0; j < missingTiles; j++)
                newCol.add(new Tile());
            // takes care of tile merging if tiles of the same value are moved up
            if(iteration == 0)
                newCol = condenseCol(newCol, "up");
            // updates the values in the Tile[][] board's current row to the new row values
            for(int j = 0; j < BOARD_SIZE; j++)
                this.board[j][i] = newCol.get(j);
            // checking to see whether a tile should be generated or not
            for(int j = 0; j < BOARD_SIZE; j++) {
                if (!(((col[j].getValue() == newCol.get(j).getValue())))) {
                    sameBoard = false;
                    break;
                }
            }
        }
        // sending an update whether a tile will be generated or not
        if(sameBoard && iteration == 0)
            this.support.firePropertyChange("up", -1, 0);
        else if(!sameBoard && iteration == 0)
            this.support.firePropertyChange("up", -1, 1);
        if(iteration == 1)
            this.support.firePropertyChange("up", -10, 0);
    }
    public void moveRight(){
        boolean sameBoard = true;
        for(int i = 0; i < BOARD_SIZE; i++){
            Tile[] row = {board[i][0], board[i][1], board[i][2], board[i][3]};
            List<Tile> newRow = new LinkedList<>();
            for(Tile t: row){
                if(!t.isEmpty())
                    newRow.add(t);
            }
            // adds back the missing values once values have been moved around
            int missingTiles = 4 - newRow.size();
            for(int j = 0; j < missingTiles; j++)
                newRow.addFirst(new Tile());
            // takes care of tile merging if tiles of the same value are moved right
            for(int j = 0; j < BOARD_SIZE - 1; j++){
                if(newRow.get(j).getValue() != 0 && newRow.get(j + 1).getValue() != 0 && (newRow.get(j).getValue() == newRow.get(j + 1).getValue())){
                    newRow.set(j + 1, new Tile(newRow.get(j).getValue() * 2));
                    newRow.set(j, new Tile());
                }
            }
            // updates the values in the Tile[][] board's current row to the new row values
            for(int j = 0; j < BOARD_SIZE; j++)
                this.board[i][j] = newRow.get(j);
            // checking to see whether a tile should be generated or not
            for(int j = 0; j < BOARD_SIZE; j++) {
                if (!(((row[j].getValue() == newRow.get(j).getValue())))) {
                    sameBoard = false;
                    break;
                }
            }
        }
        // sending an update whether a tile will be generated or not
        if(sameBoard)
            this.support.firePropertyChange("right", null, 0);
        else
            this.support.firePropertyChange("right", null, 1);
    }
    public void moveDown(int iteration){
        boolean sameBoard = true;
        for(int i = 0; i < BOARD_SIZE; i++){
            Tile[] col = {board[0][i], board[1][i], board[2][i], board[3][i]};
            List<Tile> newCol = new LinkedList<>();
            for(Tile t: col){
                if(!t.isEmpty())
                    newCol.add(t);
            }
            // adds back the missing values once values have been moved around
            int missingTiles = 4 - newCol.size();
            for(int j = 0; j < missingTiles; j++)
                newCol.addFirst(new Tile());
            // takes care of tile merging if tiles of the same value are moved down
            if(iteration == 0)
                newCol = condenseCol(newCol, "down");
            // updates the values in the Tile[][] board's current row to the new row values
            for(int j = 0; j < BOARD_SIZE; j++)
                this.board[j][i] = newCol.get(j);
            // checking to see whether a tile should be generated or not
            for(int j = 0; j < BOARD_SIZE; j++) {
                if (!(((col[j].getValue() == newCol.get(j).getValue())))) {
                    sameBoard = false;
                    break;
                }
            }
        }
        // sending an update whether a tile will be generated or not
        if(sameBoard && iteration == 0)
            this.support.firePropertyChange("down", -1, 0);
        else if(!sameBoard && iteration == 0)
            this.support.firePropertyChange("down", -1, 1);
        if(iteration == 1)
            this.support.firePropertyChange("down", -10, 0);
    }
    public void moveLeft(){
        boolean sameBoard = true;
        for(int i = 0; i < BOARD_SIZE; i++){
            Tile[] row = {board[i][0], board[i][1], board[i][2], board[i][3]};
            List<Tile> newRow = new LinkedList<>();
            for(Tile t: row){
                if(!t.isEmpty())
                    newRow.add(t);
            }
            // adds back the missing values once values have been moved around
            int missingTiles = 4 - newRow.size();
            for(int j = 0; j < missingTiles; j++)
                newRow.add(new Tile());
            // takes care of tile merging if tiles of the same value are moved right
            for(int j = 0; j < BOARD_SIZE - 1; j++){
                if(newRow.get(j).getValue() != 0 && newRow.get(j + 1).getValue() != 0 && (newRow.get(j).getValue() == newRow.get(j + 1).getValue())){
                    newRow.set(j, new Tile(newRow.get(j).getValue() * 2));
                    newRow.set(j + 1, new Tile());
                }
            }
            // updates the values in the Tile[][] board's current row to the new row values
            for(int j = 0; j < BOARD_SIZE; j++)
                this.board[i][j] = newRow.get(j);
            // checking to see whether a tile should be generated or not
            for(int j = 0; j < BOARD_SIZE; j++) {
                if (!(((row[j].getValue() == newRow.get(j).getValue())))) {
                    sameBoard = false;
                    break;
                }
            }
        }
        // sending an update whether a tile will be generated or not
        if(sameBoard)
            this.support.firePropertyChange("left", null, 0);
        else
            this.support.firePropertyChange("left", null, 1);
    }
    public List<Tile> condenseCol(List<Tile> list, String direction){
        if(direction.equals("up")){
            for(int j = 0; j < BOARD_SIZE - 1; j++){
                if(list.get(j).getValue() != 0 && list.get(j + 1).getValue() != 0 && (list.get(j).getValue() == list.get(j + 1).getValue())){
                    list.set(j, new Tile(list.get(j).getValue() * 2));
                    list.set(j + 1, new Tile());
                }
            }
        } else if(direction.equals("down")){
            for(int j = BOARD_SIZE - 1; j > 0; j--){
                if(list.get(j).getValue() != 0 && list.get(j - 1).getValue() != 0 && (list.get(j).getValue() == list.get(j - 1).getValue())){
                    list.set(j, new Tile(list.get(j).getValue() * 2));
                    list.set(j - 1, new Tile());
                }
            }
        }
        return list;
    }
    public List<Tile> condenseRow(List<Tile> list, String direction){
        if(direction.equals("left")){

        } else if(direction.equals("right")){

        }
        return list;
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
    @Override
    public boolean equals(Object object){
        if(object instanceof Tile[][] oldBoard) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (this.board[i][j] != oldBoard[i][j])
                        return false;
                }
            }
        }
        return true;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.support.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener){
        this.support.removePropertyChangeListener(listener);
    }
}
