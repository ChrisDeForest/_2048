/**
 * Game.java
 * This class represents the main game logic for the 2048 game.
 * It handles the game state, tile generation, board manipulation,
 * and game-over/win conditions.
 */
package com._2048;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * The Game class encapsulates the state and logic of a 2048 game.
 * It manages the board, scores, and game state (e.g., whether the game
 * has been won or is over). It also supports property change listeners
 * for updating the UI or other components when the game state changes.
 */
public class Game {
    /**
     *  Debug flag for custom tile generation.
     *  ONLY CHANGE DEBUG IF YOU KNOW WHAT YOU ARE DOING
     *  See generateTile() method for more options
     */
    private boolean debug = false;

    public final static int BOARD_SIZE = 4; // The size of the game board (4x4)
    private final static int WINNING_SCORE = 2048; // The score needed to winn the game
    private final PropertyChangeSupport support; // Property change support for notifying listeners of changes
    private final Tile[][] board; // The game board, represented as a 2D array of Tiles
    private final Random rand; // Random number generator for tile placement
    // Game state variables
    private boolean gameWon, continued, gameOver, sameBoard;
    private int oldScore, newScore, bestScore, moveCount;

    /**
     * Default constructor. Initializes a new game with an empty board.
     */
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

    /**
     * Copy constructor. Initializes a new game with the state of the given game.
     *
     * @param game The game to copy the state from
     */
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

    /**
     * Initializes the game board with empty tiles.
     */
    public void initializeBoard(){
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                this.board[i][j] = new Tile();
            }
        }
    }

    /**
     * Clears the game board, resetting all tiles to their default values.
     */
    public void clearBoard(){
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                board[i][j].setValue(0);
                board[i][j].setMoveGenerated(-1);
                board[i][j].setAnimationPlayed(false);
            }
        }
    }

    /**
     * Starts a new game by clearing the board, resetting scores and game state,
     * and generating the initial two tiles
     */
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

    /**
     * Generates a new tile on the board. If debug mode is enabled,
     * custom tiles are generated at specific positions.
     *
     * @param debug Flag indicating whether to generate custom tiles for debugging.
     */
    public void generateTile(boolean debug){
        // Debug mode: Custom tile generation for testing
        if(debug){
            int pos = 0, val = 2;
            this.board[pos][pos].setValue(val);
            this.board[pos][pos].setMoveGenerated(this.moveCount);
            this.board[pos][pos+1].setValue(val);
            this.board[pos][pos+1].setMoveGenerated(this.moveCount);
            this.board[pos][pos+2].setValue(val);
            this.board[pos][pos+2].setMoveGenerated(this.moveCount);
            this.board[pos][pos+3].setValue(val);
            this.board[pos][pos+3].setMoveGenerated(this.moveCount);
            System.out.println(this);
        } else { // Default random tile generation
            if ((!gameWon || continued) && !gameOver) {
                int t1 = ((rand.nextInt(1, 6) % 4) == 0) ? 4 : 2, r1 = rand.nextInt(4), c1 = rand.nextInt(4);
                while (board[r1][c1].isNotEmpty()) {
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

    /**
     * Determines whether to generate a new tile based on the state of the board
     * and the current move direction.
     *
     * @param sameBoard Flag indicating whether the board state is the same as before the move.
     * @param iteration The current iteration of the move (0 for the initial move, 1 for final check).
     * @param direction The direction of the move ("up", "down", "left", or "right").
     */
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

    /**
     * Moves tiles vertically based on the specified direction.
     *
     * @param iteration The current iteration of the move (0 for the initial move, 1 for final check).
     * @param direction The direction of the move ("up" or "down").
     */
    public void moveVertical(int iteration, String direction) {
        sameBoard = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            Tile[] col = {board[0][i], board[1][i], board[2][i], board[3][i]};
            List<Tile> newCol = new LinkedList<>();
            for (Tile t : col) {
                if (t.isNotEmpty())
                    newCol.add(t);
            }
            // Adds back the missing values once values have been moved around
            int missingTiles = 4 - newCol.size();
            for (int j = 0; j < missingTiles; j++) {
                if (direction.equals("up"))
                    newCol.add(new Tile());
                else {
                    newCol.addFirst(new Tile());
                }
            }
            // Handles tile merging if tiles of the same value are moved up or down
            if (iteration == 0)
                newCol = condense(newCol, direction);
            // Updates the values in the Tile[][] board's current row to the new row values
            for (int j = 0; j < BOARD_SIZE; j++)
                this.board[j][i] = newCol.get(j);
            // Checking to see whether a tile should be generated or not
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (!(((col[j].getValue() == newCol.get(j).getValue())))) {
                    sameBoard = false;
                    break;
                }
            }
        }
        // Sending an update whether a tile will be generated or not
        generateTileDecision(sameBoard, iteration, direction);
    }

    /**
     * Moves tiles horizontally based on the specified direction.
     *
     * @param iteration The current iteration of the move (0 for the initial move, 1 for final check).
     * @param direction The direction of the move ("left" or "right").
     */
    public void moveHorizontal(int iteration, String direction){
        sameBoard = true;
        for(int i = 0; i < BOARD_SIZE; i++) {
            Tile[] row = {board[i][0], board[i][1], board[i][2], board[i][3]};
            List<Tile> newRow = new LinkedList<>();
            for (Tile t : row) {
                if (t.isNotEmpty())
                    newRow.add(t);
            }
            // Adds back the missing values once values have been moved around
            int missingTiles = 4 - newRow.size();
            for (int j = 0; j < missingTiles; j++){
                if(direction.equals("left"))
                    newRow.add(new Tile());
                else {
                    newRow.addFirst(new Tile());
                }
            }
            // Handles tile merging if tiles of the same values are moved right or left
            if(iteration == 0)
                newRow = condense(newRow, direction);
            // Updates the values in the Tile[][] board's current row to the new row values
            for(int j = 0; j < BOARD_SIZE; j++)
                this.board[i][j] = newRow.get(j);
            // Checking to see whether a tile should be generated or not
            for(int j = 0; j < BOARD_SIZE; j++) {
                if (!(((row[j].getValue() == newRow.get(j).getValue())))) {
                    sameBoard = false;
                    break;
                }
            }
        }
        // Sending an update whether a tile will be generated or not
        generateTileDecision(sameBoard, iteration, direction);
    }

    /**
     * Condenses a list of tiles by merging adjacent tiles of the same value.
     * Updates the score based on the merged values.
     *
     * @param list The list of tiles to condense
     * @param direction The direction of the move ("up", "down", "left", "right").
     * @return The condensed list of tiles
     */
    public List<Tile> condense(List<Tile> list, String direction) {
        // remove empty tiles
        List<Tile> nonEmpty = new LinkedList<>();
        for (Tile t : list) {
            if (t.isNotEmpty()) nonEmpty.add(t);
        }

        // continuously merge adjacent equal tiles
        List<Tile> merged = new LinkedList<>();
        int i = 0;
        while (i < nonEmpty.size()) {
            if (i + 1 < nonEmpty.size() &&
                    nonEmpty.get(i).getValue() == nonEmpty.get(i + 1).getValue()) {
                int mergedValue = nonEmpty.get(i).getValue() * 2;
                merged.add(new Tile(mergedValue));
                this.newScore += mergedValue;
                i += 2; // skip next tile
            } else {
                merged.add(new Tile(nonEmpty.get(i).getValue()));
                i++;
            }
        }

        // pad empty spaces with empty tiles
        int missing = BOARD_SIZE - merged.size();
        for (int j = 0; j < missing; j++) {
            if (direction.equals("up") || direction.equals("left"))
                merged.add(new Tile());
            else
                merged.add(0, new Tile());
        }

        // update score and notify win-check
        if (this.newScore >= this.bestScore)
            this.setBestScore(this.newScore);
        this.support.firePropertyChange("score", null, 0);
        checkForWin();

        return merged;
    }


    /**
     * Checks if the game has been won (i.e., a tile with the winning score has been created).
     */
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

    /**
     * Checks if the game is over (i.e., no more moves can be made).
     */
    public void checkForGameOver(){
        int zeros = 0;
        boolean canMove = true;
        // Checking the board for zeroes
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                if(board[i][j].getValue() == 0)
                    zeros++;
            }
        }
        // Checking the board for possible moves if the board is full
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
        // If there are no zero tiles left and no moves are possible, then the game is over
        if(zeros == 0 && !canMove) {
            this.gameOver = true;
            this.support.firePropertyChange("game over", null, 0);
        }
    }

    /**
     * Continues the game after reaching the winning score.
     */
    public void continueGame(){
        this.continued = true;
        this.support.firePropertyChange("continue", null, 0);
    }

    // Getter and Setter methods

    /**
     * Returns the current game board.
     *
     * @return The game board.
     */
    public Tile[][] getBoard(){
        return this.board;
    }

    public int[][] getIntBoard() {
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = this.board[i][j].getValue();
            }
        }
//        System.out.println("BOARD");
//        System.out.println(Arrays.deepToString(board)); // todo remove this once testing is done
        return board;
    }

    /**
     * Returns whether the game has been won.
     *
     * @return True if the game has been won, false otherwise.
     */
    public boolean getGameWon(){
        return this.gameWon;
    }

    /**
     * Returns whether the game is over.
     *
     * @return True if the game is over, false otherwise
     */
    public boolean getGameOver() { return this.gameOver; }

    /**
     * Returns the old score (before the last move).
     *
     * @return The old score.
     */
    public int getOldScore(){
        return this.oldScore;
    }

    /**
     * Sets the old score (before the last move).
     *
     * @param s The old score.
     */
    public void setOldScore(int s){
        this.oldScore = s;
    }

    /**
     * Returns the new score (after the last move).
     *
     * @return The new score.
     */
    public int getNewScore(){
        return this.newScore;
    }

    /**
     * Returns the best score achieved in the game.
     *
     * @return The best score.
     */
    public int getBestScore(){
        return this.bestScore;
    }

    /**
     * Sets the best score achieved in the game.
     *
     * @param bs The best score.
     */
    public void setBestScore(int bs){
        this.bestScore = bs;
    }

    /**
     * Returns whether the board state is the same as before the last move.
     *
     * @return True if the board state is the same, otherwise false.
     */
    public boolean getSameBoard(){
        return this.sameBoard;
    }

    /**
     * Returns the current move count.
     *
     * @return The move count.
     */
    public int getMoveCount(){
        return this.moveCount;
    }

    /**
     * Increments the move count by 1.
     */
    public void incrementMoveCount(){
        this.moveCount++;
    }

    /**
     * Returns the debug flag.
     *
     * @return True if debug mode is enabled, otherwise false.
     */
    public boolean getDebug(){
        return this.debug;
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

    /**
     * Adds a property change listener to the game.
     *
     * @param listener The property change listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.support.addPropertyChangeListener(listener);
    }
}
