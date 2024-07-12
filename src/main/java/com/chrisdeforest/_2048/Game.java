package com.chrisdeforest._2048;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Game {
    /**
     *  ONLY CHANGE DEBUG IF YOU KNOW WHAT YOU ARE DOING
     *  See generateTile() method for options
     */
    private boolean debug = false;

    public final static int BOARD_SIZE = 4, WINNING_SCORE = 2048;
    private PropertyChangeSupport support;
    private Tile[][] board;
    private Random rand;
    private boolean gameWon, continued, gameOver, sameBoard;
    private int oldScore, newScore, bestScore, moveCount;
    public Game(){
        this.board = new Tile[BOARD_SIZE][BOARD_SIZE];
        this.rand = new Random();
        this.support = new PropertyChangeSupport(this);
        this.gameWon = false;
        this.continued = false;
        this.gameOver = false;
        this.sameBoard = true;
        this.moveCount = 0;
        initializeBoard();
    }
    public Game(Game game){
        this.board = new Tile[BOARD_SIZE][BOARD_SIZE];
        this.rand = new Random();
        this.support = new PropertyChangeSupport(this);
        this.gameWon = game.gameWon;
        this.continued = game.continued;
        this.gameOver = game.gameOver;
        this.oldScore = game.oldScore;
        this.newScore = game.newScore;
        this.bestScore = game.bestScore;
        this.sameBoard = game.sameBoard;
        this.moveCount = game.moveCount;
        this.debug = game.debug;
        initializeBoard();
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                this.board[i][j].setValue(game.board[i][j].getValue());
            }
        }
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
                board[i][j].setMoveGenerated(-1);
            }
        }
    }
    public void newGame(){
        clearBoard();
        this.oldScore = 0;
        this.newScore = 0;
        this.support.firePropertyChange("score", null, this.newScore);
        this.gameWon = false;
        this.continued = false;
        this.gameOver = false;
        this.moveCount = 0;
        generateTile(this.debug);
        generateTile(this.debug);
        this.support.firePropertyChange("newGame", null, rand.nextInt(10));
    }
    public void generateTile(boolean debug){
        /* enabling debug allows for the customization of where tiles spawn, their value, and their moveCount
         * it also prints the board's values to the console
         */
        if(debug){
            int pos = 1, num = 2;
            this.board[pos-1][pos].setValue(num*16);
            this.board[pos-1][pos].setMoveGenerated(this.moveCount);
            this.board[pos][pos].setValue(num*4);
            this.board[pos][pos].setMoveGenerated(this.moveCount);
            this.board[pos+1][pos].setValue(num);
            this.board[pos+1][pos].setMoveGenerated(this.moveCount);
            this.board[pos+2][pos].setValue(num*4);
            this.board[pos+2][pos].setMoveGenerated(this.moveCount);
            System.out.println(this);
        } else { // default random tile generation
            if ((!gameWon || continued) && !(gameOver)) {
                int t1 = ((rand.nextInt(1, 5) % 4) == 0) ? 2 : 4, r1 = rand.nextInt(4), c1 = rand.nextInt(4);
                while (!board[r1][c1].isEmpty()) {
                    r1 = rand.nextInt(4);
                    c1 = rand.nextInt(4);
                }
                this.board[r1][c1].setValue(t1);
                this.board[r1][c1].setMoveGenerated(this.moveCount);
            }
        }
        // check if the game is over after a tile generates
        checkForGameOver();
    }
    public void generateTileDecision(boolean sameBoard, int iteration, String direction){
        if(sameBoard && iteration == 0) {
            this.support.firePropertyChange(direction, -1, 0);
        } else if(!sameBoard && iteration == 0) {
            incrementMoveCount();
            this.support.firePropertyChange(direction, -1, 1);
        } if(iteration == 1) {
            this.support.firePropertyChange(direction, -10, 0);
        }
    }
    public void moveVertical(int iteration, String direction){
        sameBoard = true;
        for(int i = 0; i < BOARD_SIZE; i++) {
            Tile[] col = {board[0][i], board[1][i], board[2][i], board[3][i]};
            List<Tile> newCol = new LinkedList<>();
            for (Tile t : col) {
                if (!t.isEmpty())
                    newCol.add(t);
            }
            // adds back the missing values once values have been moved around
            int missingTiles = 4 - newCol.size();
            for(int j = 0; j < missingTiles; j++){
                if(direction.equals("up"))
                    newCol.add(new Tile());
                else {
                    newCol.addFirst(new Tile());
                }
            }
            // takes care of tile merging if tiles of the same value are moved up
            if(iteration == 0)
                newCol = condense(newCol, direction);
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
        generateTileDecision(sameBoard, iteration, direction);
    }
    public void moveHorizontal(int iteration, String direction){
        sameBoard = true;
        for(int i = 0; i < BOARD_SIZE; i++) {
            Tile[] row = {board[i][0], board[i][1], board[i][2], board[i][3]};
            List<Tile> newRow = new LinkedList<>();
            for (Tile t : row) {
                if (!t.isEmpty())
                    newRow.add(t);
            }
            // adds back the missing values once values have been moved around
            int missingTiles = 4 - newRow.size();
            for (int j = 0; j < missingTiles; j++){
                if(direction.equals("left"))
                    newRow.add(new Tile());
                else {
                    newRow.addFirst(new Tile());
                }
            }
            // takes care of tile merging if tiles of the same value are moved right
            if(iteration == 0)
                newRow = condense(newRow, direction);
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
        generateTileDecision(sameBoard, iteration, direction);
    }
    public List<Tile> condense(List<Tile> list, String direction){
        if(direction.equals("up") || direction.equals("left")){
            for(int j = 0; j < BOARD_SIZE - 1; j++){
                if(list.get(j).getValue() != 0 && list.get(j + 1).getValue() != 0 && (list.get(j).getValue() == list.get(j + 1).getValue())){
                    list.set(j, new Tile(list.get(j).getValue() * 2));
                    list.set(j + 1, new Tile());
                    this.newScore = this.newScore + (list.get(j).getValue());
                    break;
                }
            }
        } else if(direction.equals("down") || direction.equals("right")){
            for(int j = BOARD_SIZE - 1; j > 0; j--){
                if(list.get(j).getValue() != 0 && list.get(j - 1).getValue() != 0 && (list.get(j).getValue() == list.get(j - 1).getValue())){
                    list.set(j, new Tile(list.get(j).getValue() * 2));
                    list.set(j - 1, new Tile());
                    this.newScore = this.newScore + (list.get(j).getValue());
                    break;
                }
            }
        }
        if(this.newScore >= this.bestScore)
            this.setBestScore(this.newScore);
        this.support.firePropertyChange("score", null, 0);
        checkForWin();
        return list;
    }
    public void checkForWin(){
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(board[i][j].getValue() == WINNING_SCORE){
                    if(!this.continued) {
                        this.gameWon = true;
                        this.support.firePropertyChange("won game", null, WINNING_SCORE);
                    }
                }
            }
        }
    }
    public void checkForGameOver(){
        int zeros = 0;
        boolean canMove = true;
        // checking for zeroes
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(board[i][j].getValue() == 0)
                    zeros++;
            }
        }
        // checking the board for possible moves if the board is full
        if(zeros == 0) {
            Game newGame = new Game(this);
            newGame.moveVertical(0, "up");
            if (newGame.equals(this)) {
                newGame.moveVertical(0, "down");
                if (newGame.equals(this)) {
                    newGame.moveHorizontal(0, "right");
                    if (newGame.equals(this)) {
                        newGame.moveHorizontal(0, "left");
                        if (newGame.equals(this)) {
                            canMove = false;
                        }
                    }
                }
            }
        }
        // if there are no zero tiles left and no moves are possible, then the game is over
        if(zeros == 0 && !canMove) {
            this.gameOver = true;
            this.support.firePropertyChange("game over", null, 0);
        }
    }
    public void continueGame(){
        this.continued = true;
        this.support.firePropertyChange("continue", null, 0);
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
    public boolean getGameWon(){
        return this.gameWon;
    }
    public void setGameWon(boolean gw){
        this.gameWon = gw;
    }
    public boolean getContinued(){
        return this.continued;
    }
    public void setContinued(boolean c){
        this.continued = c;
    }
    public int getOldScore(){
        return this.oldScore;
    }
    public void setOldScore(int s){
        this.oldScore = s;
    }
    public int getNewScore(){
        return this.newScore;
    }
    public void setNewScore(int s){
        this.newScore = s;
    }
    public int getBestScore(){
        return this.bestScore;
    }
    public void setBestScore(int bs){
        this.bestScore = bs;
    }
    public boolean getSameBoard(){
        return this.sameBoard;
    }
    public void setSameBoard(boolean sb){
        this.sameBoard = sb;
    }
    public int getMoveCount(){
        return this.moveCount;
    }
    public void setMoveCount(int mc){
        this.moveCount = mc;
    }
    public void incrementMoveCount(){
        this.moveCount++;
    }
    public boolean getDebug(){
        return this.debug;
    }
    public void setDebug(boolean dbg){
        this.debug = dbg;
    }
    @Override
    public String toString(){
        StringBuilder string = new StringBuilder();
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if (board[i][j].getValue() >= 1024)
                    string.append(board[i][j].getValue()).append("\t");
                else
                    string.append(board[i][j].getValue()).append("\t\t");
            }
            string.append("\n");
        }
        return string.toString();
    }
    @Override
    public boolean equals(Object object){
        if(object instanceof Game game) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (this.board[i][j].getValue() != game.getBoard()[i][j].getValue())
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
