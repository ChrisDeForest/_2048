package com.state;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

import static com._2048.Game.BOARD_SIZE;

public class GameState {
    @SerializedName("currentScore")
    private int currentScore;

    @SerializedName("bestScore")
    private int bestScore;

    @SerializedName("gameOver")
    private boolean gameOver;

    @SerializedName("gameWon")
    private boolean gameWon;

    @SerializedName("boardState")
    private int[][] boardState;

    public GameState(int currentScore, int bestScore, boolean gameOver, boolean gameWon, int[][] boardState) {
        this.currentScore = currentScore;
        this.bestScore = bestScore;
        this.gameOver = gameOver;
        this.gameWon = gameWon;
        this.boardState = boardState;
    }

    public int getCurrentScore() {
        return currentScore;
    }
    public void setCurrentScore(int currentScore) { this.currentScore = currentScore; }
    public int getBestScore() {
        return bestScore;
    }
    public void setBestScore(int bestScore) { this.bestScore = bestScore; }
    public boolean isGameOver() {
        return gameOver;
    }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    public boolean isGameWon() { return gameWon; }
    public void setGameWon(boolean gameWon) { this.gameWon = gameWon; }
    public int[][] getBoardState() {
        return boardState;
    }
    public void setBoardState(int[][] boardState) { this.boardState = boardState; }

    @Override
    public String toString() {
        return "GameState{ " +
                "currentScore=" + this.currentScore +
                ", bestScore=" + this.bestScore +
                ", isGameOver=" + this.gameOver +
                ", boardState=" + Arrays.deepToString(this.boardState) +
                "}";
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof GameState gameState) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (this.boardState[i][j] != gameState.getBoardState()[i][j])
                        return false;
                }
            }
        }
        return true;
    }
}
