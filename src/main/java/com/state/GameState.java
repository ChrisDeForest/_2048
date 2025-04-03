package com.state;

import java.util.Arrays;

public class GameState {
    private int currentScore, bestScore;
    private boolean gameOver;
    private int[][] boardState;

    public GameState(int currentScore, int bestScore, boolean gameOver, int[][] boardState) {
        this.currentScore = currentScore;
        this.bestScore = bestScore;
        this.gameOver = gameOver;
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
    public int[][] getBoardState() {
        return boardState;
    }
    public void setBoardState(int[][] boardState) { this.boardState = boardState; }

    @Override
    public String toString() {
        return "GameState{ " +
                "currentScore = " + this.currentScore +
                "bestScore = " + this.bestScore +
                "isGameOver = " + this.gameOver +
                "boardState = " + Arrays.deepToString(this.boardState) +
                "}";
    }
}
