package com.chrisdeforest._2048;

import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

// TODO add scrollable pane for information below;

public class Controller extends Application implements PropertyChangeListener {
    private final static int SCORE_TILE_SIZE = 100, TILE_SIZE = 120, GRID_SIZE = 500;
    private final static Duration ANIMATION_TIME = Duration.seconds(0.50);
    private static Label scoreVal, bestScoreVal;
    public static Label[][] labelGrid = new Label[4][4];
    private static Game game;
    private static StackPane scoreStack = new StackPane();
    private final EventHandler<KeyEvent> keyEventHandler = keyEvent -> {
        switch (keyEvent.getCode()) {
            case UP, W, KP_UP:
                game.moveVertical(0, "up");
                break;
            case RIGHT, D, KP_RIGHT:
                game.moveHorizontal(0, "right");
                break;
            case DOWN, S, KP_DOWN:
                game.moveVertical(0, "down");
                break;
            case LEFT, A, KP_LEFT:
                game.moveHorizontal(0, "left");
                break;
        }
        System.out.println();
    };
    private static GridPane grid;
    private Scene scene;
    private StackPane windowStack;
    @Override
    public void start(Stage stage) throws IOException {
        // title, directions, how-to
        VBox info = makeInfo();
        // score and best score
        GridPane scoreAndBestScore = makeScoreGrid();
        // configuring the top section
        HBox top = makeTop(scoreAndBestScore, info);
        // main game grid
        windowStack = new StackPane();
        grid = makeGrid();
        windowStack.getChildren().addAll(grid, new Label());
        // adding all sections to the main vbox
        VBox main = new VBox();
        main.getChildren().addAll(top, windowStack);
        main.setAlignment(Pos.CENTER);
        // scene and stage settings
        scene = new Scene(main);
        scene.getStylesheets().add("file:src/main/resources/styles.css");
        scene.setOnKeyPressed(keyEventHandler);
        stage.setScene(scene);
        stage.setHeight(800);
        stage.setWidth(550);
        stage.setResizable(false);
        stage.setTitle("2048");
        stage.show();
        game.newGame();
    }

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void init() throws Exception {
        game = new Game();
        game.addPropertyChangeListener(this);
    }

    public GridPane makeGrid() {
        GridPane grid = new GridPane();
        grid.setMinSize(GRID_SIZE, GRID_SIZE);
        grid.setMaxSize(GRID_SIZE, GRID_SIZE);
        grid.getStyleClass().addAll("grid");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Label label = new Label("0");
                if (label.getText().equals("0"))
                    label.setText("");
                label.getStyleClass().addAll("margin", "game-tile");
                label.setStyle("-fx-background-color: rgb(203, 193, 178);");
                grid.add(label, i, j);
                labelGrid[i][j] = label;
            }
        }
        return grid;
    }
    public static VBox makeInfo() {
        VBox info = new VBox();
        Label _2048 = new Label("2048");
        _2048.getStyleClass().addAll("game-title", "bold", "directions-text");
        Label directions = new Label("Join the tiles, get to ");
        directions.getStyleClass().addAll("directions-padding", "directions-text");
        Label directions1 = new Label("2048!");
        directions1.getStyleClass().addAll("bold", "directions-text");
        HBox dir = new HBox();
        dir.getChildren().addAll(directions, directions1);
        Label howTo = new Label("How to play -->\n");
        howTo.getStyleClass().addAll("directions-padding", "ul", "bold", "directions-text");
        Label blank = new Label();
        blank.setMinSize(50, 35);
        info.getChildren().addAll(_2048, dir, howTo, blank);
        return info;
    }
    public static GridPane makeScoreGrid() {
        GridPane grid = new GridPane();
        grid.setMaxSize(SCORE_TILE_SIZE * 1.5, SCORE_TILE_SIZE * 0.75);
        grid.setMinSize(SCORE_TILE_SIZE * 1.25, SCORE_TILE_SIZE * 0.75);
        grid.getStyleClass().addAll("score-grid");
        VBox left = new VBox();
        left.setMinSize(100, 60);
        left.setMaxSize(125, 60);
        Label score = new Label("SCORE");
        score.setStyle("-fx-text-fill: rgb(242, 226, 208); -fx-font-weight: bold; -fx-font-size: 11pt;");
        scoreVal = new Label("0");
        scoreVal.getStyleClass().addAll("score-text", "bold");
        scoreStack.getChildren().addAll(scoreVal);
        left.getChildren().addAll(score, scoreStack);
        left.getStyleClass().addAll("grid", "score-margin");
        left.setAlignment(Pos.CENTER);
        VBox right = new VBox();
        right.setMinSize(110, 60);
        right.setMaxSize(125, 60);
        Label best = new Label("BEST");
        best.setStyle("-fx-text-fill: rgb(242, 226, 208); -fx-font-weight: bold; -fx-font-size: 11pt;");
        bestScoreVal = new Label("0");
        bestScoreVal.getStyleClass().addAll("score-text", "bold");
        right.getChildren().addAll(best, bestScoreVal);
        right.getStyleClass().addAll("grid", "score-margin");
        right.setAlignment(Pos.CENTER);
        grid.add(left, 0, 0);
        grid.add(right, 1, 0);
        grid.setAlignment(Pos.CENTER_RIGHT);
        return grid;
    }
    public static HBox makeTop(GridPane scoreAndBestScore, VBox info) {
        Label blank = new Label();
        blank.setMinSize(105, 25);
        Label blank2 = new Label();
        blank2.setMinSize(50, 40);
        Button newGame = new Button("New Game");
        EventHandler<ActionEvent> newGameAction = event -> game.newGame();
        newGame.setOnAction(newGameAction);
        newGame.getStyleClass().addAll("game-button", "bold");
        VBox rightSide = new VBox();
        rightSide.setMinSize(175, 125);
        rightSide.setAlignment(Pos.TOP_RIGHT);
        rightSide.getChildren().addAll(scoreAndBestScore, blank2, newGame);
        HBox top = new HBox();
        top.getChildren().addAll(info, blank, rightSide);
        top.setAlignment(Pos.TOP_LEFT);
        return top;
    }
    public StackPane makeWinScreen(){
        // creating the stack pane and the background label
        StackPane stack = new StackPane();
        Label background = new Label();
        background.setMinSize(GRID_SIZE, GRID_SIZE);
        background.setMaxSize(GRID_SIZE, GRID_SIZE);
        background.setPrefSize(GRID_SIZE, GRID_SIZE);
        background.getStyleClass().addAll("game-win-lose-background");
        // creating the "You win!" label
        Label youWin = new Label("You win!");
        youWin.setMinSize(TILE_SIZE, TILE_SIZE);
        youWin.getStyleClass().addAll("bold", "game-win-lose-text");
        // creating the game menu buttons, "keep going" and "try again"
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        Button keepGoing = new Button("Keep going");
        keepGoing.getStyleClass().addAll("game-button", "bold");
        EventHandler<ActionEvent> keepGoingAction = event -> game.continueGame();
        keepGoing.setOnAction(keepGoingAction);
        Label blank2 = new Label();
        blank2.setMinSize(20, 20);
        Button tryAgain = new Button("Try again");
        EventHandler<ActionEvent> tryAgainAction = event -> game.newGame();
        tryAgain.setOnAction(tryAgainAction);
        tryAgain.getStyleClass().addAll("game-button", "bold");
        hbox.getChildren().addAll(keepGoing, blank2, tryAgain);
        // placing the prior elements into a vbox
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(youWin, hbox);
        // placing the background and vbox into the stack pane
        stack.getChildren().addAll(background, vbox);
        stack.setOpacity(0);
        return stack;
    }
    public StackPane makeGameOverScreen(){
        // creating the background label
        Label background = new Label();
        background.setMinSize(GRID_SIZE, GRID_SIZE);
        background.setMaxSize(GRID_SIZE, GRID_SIZE);
        background.setPrefSize(GRID_SIZE, GRID_SIZE);
        background.getStyleClass().addAll("game-win-lose-background");
        // creating the game over text label
        Label gameOver = new Label("Game over!");
        gameOver.setMinSize(TILE_SIZE, TILE_SIZE);
        gameOver.getStyleClass().addAll("bold", "game-win-lose-text");
        // creating the try again button
        Button tryAgain = new Button("Try again");
        EventHandler<ActionEvent> tryAgainAction = event -> game.newGame();
        tryAgain.setOnAction(tryAgainAction);
        tryAgain.getStyleClass().addAll("game-button", "bold");
        // arranging the elements within a group
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(gameOver, tryAgain);
        // stacking elements appropriately
        StackPane stack = new StackPane();
        stack.getChildren().addAll(background, vbox);
        stack.setOpacity(0);
        return stack;
    }
    private void updateTiles(boolean quick, String direction) {
        // updating the game's score and playing the score animation
        updateScore();
        // creating a ParallelTransition that will hold all the tile movement animations
        ParallelTransition tileAnimations = new ParallelTransition();
        if (direction != null) {
            if (direction.equals("up")){
                // looping through each tile in the board
                for (int i = 0; i < Game.BOARD_SIZE; i++){
                    for (int j = 0; j < Game.BOARD_SIZE; j++){
                        int spacesToMove = 0;
                        // if the tile has a number, see if it needs to be moved up
                        if (!labelGrid[j][i].getText().isEmpty()){
                            // if there is a collision, the tile can move
                            if (i > 0 && !labelGrid[j][i].getText().isEmpty() && labelGrid[j][i].getText().equals(labelGrid[j][i-1].getText()))
                                spacesToMove++;
                            // checking every tile above the current tile
                            for (int k = 0; k < i; k++){
                                // if there is any empty space, the tile can move up
                                if (labelGrid[j][k].getText().isEmpty())
                                    spacesToMove++;
                            }
                            // if the tile can move up, animate the upwards motion
                            if (spacesToMove != 0) {
                                tileAnimations.getChildren().addAll(createTileMoveAnimation(j, i, j, i - spacesToMove));
                            }
                            System.out.println("Tile @ (" + j + ", " + i + ") moves up " + spacesToMove);
                            // reset the spacesToMove counter
                            spacesToMove = 0;
                        }
                    }
                }
                System.out.println();
            } else if (direction.equals("down")){
                // looping through each tile in the board
                for (int i = 0; i < Game.BOARD_SIZE; i++){
                    for (int j = 0; j < Game.BOARD_SIZE; j++){
                        int spacesToMove = 0;
                        // if the tile has a number, see if it needs to be moved up
                        if (!labelGrid[j][i].getText().isEmpty()){
                            // if there is a collision, the tile can move
                            if (i < 3 && !labelGrid[j][i].getText().isEmpty() && labelGrid[j][i].getText().equals(labelGrid[j][i + 1].getText()))
                                spacesToMove++;
                            // checking every tile above the current tile
                            for (int k = 3; k > i; k--){
                                // if there is any empty space, the tile can move up
                                if (labelGrid[j][k].getText().isEmpty())
                                    spacesToMove++;
                            }
                            // if the tile can move up, animate the upwards motion
                            if (spacesToMove != 0) {
                                tileAnimations.getChildren().addAll(createTileMoveAnimation(j, i, j, i + spacesToMove));
                            }
                            System.out.println("Tile @ (" + j + ", " + i + ") moves down " + spacesToMove);
                            // reset the spacesToMove counter
                            spacesToMove = 0;
                        }
                    }
                }
                System.out.println();
            } else if (direction.equals("right")){
                for (int i = 0; i < Game.BOARD_SIZE; i++){
                    for (int j = 0; j < Game.BOARD_SIZE; j++){
                        int spacesToMove = 0;
                    }
                }
                System.out.println();
            } else if (direction.equals("left")){

            }
        }
        PauseTransition pause = getPauseTransition(quick);
        tileAnimations.play();
        pause.play();
    }

    @NotNull
    private PauseTransition getPauseTransition(boolean quick) {
        Duration time;
        if (quick)
            time = Duration.seconds(0);
        else
            time = ANIMATION_TIME;
        PauseTransition pause = new PauseTransition(time);
        pause.setOnFinished(e->{
            for (int i = 0; i < Game.BOARD_SIZE; i++) {
                for (int j = 0; j < Game.BOARD_SIZE; j++) {
                    labelGrid[i][j].setText(String.valueOf(game.getBoard()[j][i].getValue()));
                    String textColor = game.getBoard()[i][j].getTextColor();
                    String backgroundColor = game.getBoard()[i][j].getBackground();
                    String fontSize = game.getBoard()[i][j].getFontSize();
                    labelGrid[j][i].setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: " + textColor + ";" +
                            " -fx-font-size: " + fontSize + ";");
                    if (labelGrid[i][j].getText().equals("0"))
                        labelGrid[i][j].setText("");
                    labelGrid[j][i].getStyleClass().addAll("margin", "game-tile");
                    if (game.getBoard()[i][j].getMoveGenerated() == game.getMoveCount() && !game.getBoard()[i][j].getAnimationPlayed()) {
                        labelGrid[j][i].setOpacity(0);
                        playTileAppearAnimation(labelGrid[j][i]);
                        game.getBoard()[i][j].setAnimationPlayed(true);
                    }
                }
            }
        });
        return pause;
    }

    private void updateScore() {
        scoreVal.setText(String.valueOf(game.getNewScore()));
        bestScoreVal.setText(String.valueOf(game.getBestScore()));
        if(!game.getSameBoard() && (game.getOldScore() != game.getNewScore()))
            playAnimatedScore();
    }
    private void playAnimatedScore(){
        Label animatedScoreVal = new Label("+" + (game.getNewScore() - game.getOldScore()));
        animatedScoreVal.getStyleClass().addAll("score-text", "animated", "bold");
        animatedScoreVal.setTranslateY(0);
        TranslateTransition translate = new TranslateTransition(Duration.seconds(0.5), animatedScoreVal);
        translate.setByY(-45);
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), animatedScoreVal);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        ParallelTransition parallel = new ParallelTransition(translate, fade);
        parallel.setOnFinished(event -> {
            scoreStack.getChildren().remove(animatedScoreVal);
        });
        scoreStack.getChildren().add(animatedScoreVal);
        parallel.play();
    }
    private void playAnimatedWinOrLoseScreen(StackPane stackScreen){
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), stackScreen);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        pause.setOnFinished(event -> {
            fadeTransition.play();
        });
        pause.play();
    }
    private void playTileAppearAnimation(Label label){
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.3), label);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.3), label);
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, scaleTransition);
        parallelTransition.play();
    }
    public ParallelTransition createTileMoveAnimation(int startX, int startY, int endX, int endY){
        System.out.println("Animation playing on tile @ (" + startX + ", " + startY + ") moving to (" + endX + ", " + endY + ")");
        // disable keyboard input until animation has played
        scene.setOnKeyPressed(null);
        // calculating the distance to move the tile and bringing the label to the front
        Label label = labelGrid[startX][startY];
        label.toFront();
        double translateX = (endX - startX) * TILE_SIZE;
        double translateY = (endY - startY) * TILE_SIZE;
        // creating the TranslateTransition that moves the tile
        TranslateTransition translate = new TranslateTransition(ANIMATION_TIME, label);
        translate.setByX(translateX);
        translate.setByY(translateY);
        // creating the FadeTransition that fades the tile out
//        FadeTransition fade = new FadeTransition(ANIMATION_TIME, label);
//        fade.setFromValue(1.0);
//        fade.setToValue(0.0);
//        fade.setInterpolator(Interpolator.EASE_IN);
        // creating the parallel transition which contains the translate and fade animations
        ParallelTransition parallel = new ParallelTransition(translate);
        parallel.setOnFinished(e -> {
            label.setTranslateX(0);
            label.setTranslateY(0);
            label.setOpacity(1.0); // Reset opacity if needed
            scene.setOnKeyPressed(keyEventHandler);
        });
        return parallel;
    }
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case "newGame":
                scene.setOnKeyPressed(keyEventHandler);
                windowStack.getChildren().set(1, new Label());
                updateTiles(true, null);
                break;
            case "up":
                game.setOldScore(game.getNewScore());
                if ((int) event.getOldValue() == -1) {
                    game.moveVertical(1, "up");}
                if ((int) event.getNewValue() == 1) {
                    game.generateTile(game.getDebug());
                }
                updateTiles(false, "up");
                game.checkForWin();
                break;
            case "right":
                game.setOldScore(game.getNewScore());
                if ((int) event.getOldValue() == -1) {
                    game.moveHorizontal(1, "right");
                }
                if ((int) event.getNewValue() == 1)
                    game.generateTile(game.getDebug());
                updateTiles(false, "right");
                game.checkForWin();
                break;
            case "down":
                game.setOldScore(game.getNewScore());
                if ((int) event.getOldValue() == -1) {
                    game.moveVertical(1, "down");
                }
                if ((int) event.getNewValue() == 1)
                    game.generateTile(game.getDebug());
                updateTiles(false, "down");
                game.checkForWin();
                break;
            case "left":
                game.setOldScore(game.getNewScore());
                if ((int) event.getOldValue() == -1) {
                    game.moveHorizontal(1, "left");
                }
                if ((int) event.getNewValue() == 1)
                    game.generateTile(game.getDebug());
                updateTiles(false, "left");
                game.checkForWin();
                break;
            case "score":
                updateScore();
                break;
            case "won game":
                scene.setOnKeyPressed(null);
                windowStack.getChildren().set(1, makeWinScreen());
                playAnimatedWinOrLoseScreen((StackPane)windowStack.getChildren().get(1));
                break;
            case "continue":
                scene.setOnKeyPressed(keyEventHandler);
                windowStack.getChildren().set(1, new Label());
                break;
            case "game over":
                scene.setOnKeyPressed(null);
                windowStack.getChildren().set(1, makeGameOverScreen());
                playAnimatedWinOrLoseScreen((StackPane)windowStack.getChildren().get(1));
                break;
        }
    }
}
