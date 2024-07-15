package com.chrisdeforest._2048;

import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Controller extends Application implements PropertyChangeListener {
    private final static int SCORE_TILE_SIZE = 100, TILE_SIZE = 120, GRID_SIZE = 500, SCROLLING_DISTANCE = 2;
    private final static Duration ANIMATION_TIME = Duration.seconds(0.10),
            SCROLLING_ANIMATION_TIME = Duration.seconds(2);
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
    };
    private static final StackPane scoreStack = new StackPane();
    public static Label[][] labelGrid = new Label[4][4];
    private final ScrollPane scroll = new ScrollPane();
    private static Label scoreVal, bestScoreVal;
    private static Game game;
    private StackPane windowStack;
    private Scene scene;
    @Override
    public void start(Stage stage) {
        // title, directions, how-to
        VBox info = createGameInfo();
        // score and best score
        GridPane scoreAndBestScore = createScoreGrid();
        // configuring the top section
        HBox top = createTop(scoreAndBestScore, info);
        // main game grid
        windowStack = new StackPane();
        GridPane grid = createGrid();
        windowStack.getChildren().addAll(grid, new Label());
        // adding all sections to the main vbox
        VBox topAndGrid = new VBox();
        topAndGrid.getChildren().addAll(top, windowStack);
        topAndGrid.setAlignment(Pos.CENTER);
        // creating the how-to-play section
        VBox howToPlay = createHowToPlay();
        // adding the sections together in a vbox
        VBox content = new VBox();
        content.getChildren().addAll(topAndGrid, howToPlay);
        // adding the main and how-to-play sections to a scrollable pane
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setPannable(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setContent(content);
        // scene and stage settings
        scene = new Scene(scroll);
        scene.getStylesheets().add("file:src/main/resources/styles.css");
        scene.setOnKeyPressed(keyEventHandler);
        stage.setScene(scene);
        stage.setHeight(800);
        stage.setWidth(550);
        stage.setResizable(false);
        stage.setTitle("2048 FX");
        stage.show();
        game.newGame();
    }
    public static void main(String[] args) {
        Application.launch();
    }
    @Override
    public void init() {
        game = new Game();
        game.addPropertyChangeListener(this);
    }
    @NotNull
    private GridPane createGrid() {
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
    @NotNull
    private VBox createGameInfo() {
        VBox info = new VBox();
        Label _2048 = new Label("2048");
        VBox.setMargin(_2048, new Insets(25, 0, 0, 0));
        _2048.getStyleClass().addAll("game-title", "bold", "game-text");
        Label directions = new Label("Join the tiles, get to ");
        directions.getStyleClass().addAll("directions-padding", "game-text");
        Label directions1 = new Label("2048!");
        directions1.getStyleClass().addAll("bold", "game-text");
        HBox dir = new HBox();
        dir.getChildren().addAll(directions, directions1);
        Label howTo = new Label("How to play -->\n");
        howTo.getStyleClass().addAll("directions-padding", "underline", "bold", "game-text");
        howTo.setOnMouseClicked(event ->{
            Timeline timeLine = new Timeline();
            KeyFrame keyFrame = new KeyFrame(SCROLLING_ANIMATION_TIME,
                    new javafx.animation.KeyValue(scroll.vvalueProperty(), SCROLLING_DISTANCE, Interpolator.EASE_IN));
            timeLine.getKeyFrames().add(keyFrame);
            timeLine.play();
        });
        Label blank = new Label();
        blank.setMinSize(50, 35);
        info.getChildren().addAll(_2048, dir, howTo, blank);
        return info;
    }
    @NotNull
    private GridPane createScoreGrid() {
        GridPane grid = new GridPane();
        grid.setMaxSize(SCORE_TILE_SIZE * 1.5, SCORE_TILE_SIZE * 0.75);
        grid.setMinSize(SCORE_TILE_SIZE * 1.25, SCORE_TILE_SIZE * 0.75);
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
    @NotNull
    private HBox createTop(GridPane scoreAndBestScore, VBox info) {
        Label blank = new Label();
        blank.setMinSize(105, 25);
        Label blank2 = new Label();
        blank2.setMinSize(50, 40);
        VBox.setMargin(blank, new Insets(45, 0, 0, 0));
        Button newGame = new Button("New Game");
        EventHandler<ActionEvent> newGameAction = event -> game.newGame();
        newGame.setOnAction(newGameAction);
        newGame.getStyleClass().addAll("game-button", "bold");
        VBox rightSide = new VBox();
        rightSide.setMinSize(175, 125);
        rightSide.setAlignment(Pos.TOP_RIGHT);
        rightSide.getChildren().addAll(scoreAndBestScore, blank2, newGame);
        VBox.setMargin(scoreAndBestScore, new Insets(25, 0, 0, 0));
        HBox top = new HBox();
        top.getChildren().addAll(info, blank, rightSide);
        top.setAlignment(Pos.TOP_LEFT);
        return top;
    }
    @NotNull
    private StackPane createWinScreen(){
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
    @NotNull
    private StackPane createGameOverScreen(){
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
    private VBox createHowToPlay(){
        VBox howToPlay = new VBox();
        howToPlay.setAlignment(Pos.CENTER);
        howToPlay.getStyleClass().addAll();
        HBox line1 = new HBox();
        line1.setAlignment(Pos.CENTER);
        Label line1_1 = new Label("HOW TO PLAY: "), line1_2 = new Label("Use "),
                line1_3 = new Label("WASD "), line1_4 = new Label("or "),
                line1_5 = new Label("ARROW KEYS "), line1_6 = new Label("to move the tiles.");
        line1_1.getStyleClass().addAll("bold");
        line1_3.getStyleClass().addAll("bold");
        line1_5.getStyleClass().addAll("bold");
        line1.getChildren().addAll(line1_1, line1_2, line1_3, line1_4, line1_5, line1_6);
        HBox line2 = new HBox();
        line2.setAlignment(Pos.CENTER);
        Label line2_1 = new Label("Tiles with the same number "), line2_2 = new Label("merge into one "),
                line2_3 = new Label("when they touch.");
        line2_2.getStyleClass().addAll("bold");
        line2.getChildren().addAll(line2_1, line2_2, line2_3);
        HBox line3 = new HBox();
        line3.setAlignment(Pos.CENTER);
        Label line3_1 = new Label("Add them up to reach "), line3_2 = new Label("2048!");
        line3_2.getStyleClass().addAll("bold");
        line3.getChildren().addAll(line3_1, line3_2);
        HBox line4 = new HBox();
        line4.setAlignment(Pos.CENTER);
        Label line4_1 = new Label("Return to top");
        line4_1.setOnMouseClicked(event -> {
            Timeline timeLine = new Timeline();
            KeyFrame keyFrame = new KeyFrame(SCROLLING_ANIMATION_TIME,
                    new javafx.animation.KeyValue(scroll.vvalueProperty(), -1 * SCROLLING_DISTANCE, Interpolator.EASE_IN));
            timeLine.getKeyFrames().add(keyFrame);
            timeLine.play();
        });
        line4_1.getStyleClass().addAll("underline", "bold");
        line4.getChildren().addAll(line4_1);
        VBox.setMargin(line1, new Insets(35, 0, 0, 0));
        VBox.setMargin(line3, new Insets( 0, 0, 35, 0));
        VBox.setMargin(line4, new Insets(0, 0, 35, 0));
        howToPlay.getChildren().addAll(line1, line2, line3, line4);
        return howToPlay;
    }
    private void updateTiles(boolean quick, String direction) {
        // updating the game's score and playing the score animation
        updateScore();
        // creating a ParallelTransition that will hold all the tile movement animations
        ParallelTransition tileAnimations = new ParallelTransition();
        if (direction != null && !game.getGameWon()) {
            switch (direction) {
                case "up" -> {
                    // looping through each tile in the board
                    for (int i = 0; i < Game.BOARD_SIZE; i++) {
                        for (int j = 0; j < Game.BOARD_SIZE; j++) {
                            int spacesToMove = 0;
                            // if the tile has a number, see if it needs to be moved up
                            if (!labelGrid[j][i].getText().isEmpty()) {
                                // if there is a collision, the tile can move up again
                                if (i == 1 && !labelGrid[j][i].getText().isEmpty() &&
                                        labelGrid[j][i].getText().equals(labelGrid[j][i - 1].getText()))
                                    spacesToMove++;
                                if (i == 2 && !labelGrid[j][i].getText().isEmpty() &&
                                        ((labelGrid[j][i].getText().equals(labelGrid[j][i - 2].getText()) &&
                                                labelGrid[j][i - 1].getText().isEmpty()) ||
                                                labelGrid[j][i].getText().equals(labelGrid[j][i - 1].getText())))
                                    spacesToMove++;
                                if (i == 3 && !labelGrid[j][i].getText().isEmpty() &&
                                        ((labelGrid[j][i].getText().equals(labelGrid[j][i - 3].getText()) &&
                                                labelGrid[j][i - 2].getText().isEmpty() &&
                                                labelGrid[j][i - 1].getText().isEmpty()) ||
                                                (labelGrid[j][i].getText().equals(labelGrid[j][i - 2].getText()) &&
                                                        labelGrid[j][i - 1].getText().isEmpty()) ||
                                                labelGrid[j][i].getText().equals(labelGrid[j][i - 1].getText())))
                                    spacesToMove++;
                                // checking every tile above the current tile
                                for (int k = 0; k < i; k++) {
                                    // if there is any empty space, the tile can move up
                                    if (labelGrid[j][k].getText().isEmpty())
                                        spacesToMove++;
                                }
                                // animate the tile moving up
                                tileAnimations.getChildren().addAll(createTileMoveAnimation(j, i, j, i - spacesToMove));
                            }
                        }
                    }
                }
                case "down" -> {
                    // looping through each tile in the board
                    for (int i = 0; i < Game.BOARD_SIZE; i++) {
                        for (int j = 0; j < Game.BOARD_SIZE; j++) {
                            int spacesToMove = 0;
                            // if the tile has a number, see if it needs to be moved down
                            if (!labelGrid[j][i].getText().isEmpty()) {
                                // if there is a collision, the tile can move down again
                                if (i == 2 && !labelGrid[j][i].getText().isEmpty() &&
                                        labelGrid[j][i].getText().equals(labelGrid[j][i + 1].getText()))
                                    spacesToMove++;
                                if (i == 1 && !labelGrid[j][i].getText().isEmpty() &&
                                        (labelGrid[j][i].getText().equals(labelGrid[j][i + 1].getText()) ||
                                                (labelGrid[j][i].getText().equals(labelGrid[j][i + 2].getText()) &&
                                                        labelGrid[j][i + 1].getText().isEmpty())))
                                    spacesToMove++;
                                if (i == 0 && !labelGrid[j][i].getText().isEmpty() &&
                                        (labelGrid[j][i].getText().equals(labelGrid[j][i + 1].getText()) ||
                                                (labelGrid[j][i].getText().equals(labelGrid[j][i + 2].getText()) &&
                                                        labelGrid[j][i + 1].getText().isEmpty()) ||
                                                (labelGrid[j][i].getText().equals(labelGrid[j][i + 3].getText()) &&
                                                        labelGrid[j][i + 2].getText().isEmpty() &&
                                                        labelGrid[j][i + 1].getText().isEmpty())))
                                    spacesToMove++;
                                // checking every tile below the current tile
                                for (int k = 3; k > i; k--) {
                                    // if there is any empty space, the tile can move down
                                    if (labelGrid[j][k].getText().isEmpty())
                                        spacesToMove++;
                                }
                                // animate the tile moving down
                                tileAnimations.getChildren().addAll(createTileMoveAnimation(j, i, j, i + spacesToMove));
                            }
                        }
                    }
                }
                case "right" -> {
                    // looping through each tile in the board
                    for (int i = 0; i < Game.BOARD_SIZE; i++) {
                        for (int j = 0; j < Game.BOARD_SIZE; j++) {
                            int spacesToMove = 0;
                            // if the tile has a number, see if it needs to be moved right
                            if (!labelGrid[j][i].getText().isEmpty()) {
                                // if there is a collision, the tile can move right again
                                if (j == 2 && !labelGrid[j][i].getText().isEmpty() &&
                                        (labelGrid[j][i].getText().equals(labelGrid[j + 1][i].getText())))
                                    spacesToMove++;
                                if (j == 1 && !labelGrid[j][i].getText().isEmpty() &&
                                        ((labelGrid[j][i].getText().equals(labelGrid[j + 2][i].getText()) &&
                                                labelGrid[j + 1][i].getText().isEmpty()) ||
                                                labelGrid[j][i].getText().equals(labelGrid[j + 1][i].getText())))
                                    spacesToMove++;
                                if (j == 0 && !labelGrid[j][i].getText().isEmpty() &&
                                        ((labelGrid[j][i].getText().equals(labelGrid[j + 3][i].getText()) &&
                                                labelGrid[j + 2][i].getText().isEmpty() &&
                                                labelGrid[j + 1][i].getText().isEmpty()) ||
                                                (labelGrid[j][i].getText().equals(labelGrid[j + 2][i].getText()) &&
                                                        labelGrid[j + 1][i].getText().isEmpty()) ||
                                                labelGrid[j][i].getText().equals(labelGrid[j + 1][i].getText())))
                                    spacesToMove++;
                                // checking every tile right of the current tile
                                for (int k = 3; k > j; k--) {
                                    // if there is any empty space, the tile can move right
                                    if (labelGrid[k][i].getText().isEmpty())
                                        spacesToMove++;
                                }
                                // animate the tile moving right
                                tileAnimations.getChildren().addAll(createTileMoveAnimation(j, i, j + spacesToMove, i));
                            }
                        }
                    }
                }
                case "left" -> {
                    // looping through each tile in the board
                    for (int i = 0; i < Game.BOARD_SIZE; i++) {
                        for (int j = 0; j < Game.BOARD_SIZE; j++) {
                            int spacesToMove = 0;
                            // if the tile has a number, see if it needs to be moved left
                            if (!labelGrid[j][i].getText().isEmpty()) {
                                // if there is a collision, the tile can move left again
                                if (j == 1 && !labelGrid[j][i].getText().isEmpty() &&
                                        (labelGrid[j][i].getText().equals(labelGrid[j - 1][i].getText())))
                                    spacesToMove++;
                                if (j == 2 && !labelGrid[j][i].getText().isEmpty() &&
                                        ((labelGrid[j][i].getText().equals(labelGrid[j - 2][i].getText()) &&
                                                labelGrid[j - 1][i].getText().isEmpty()) ||
                                                labelGrid[j][i].getText().equals(labelGrid[j - 1][i].getText())))
                                    spacesToMove++;
                                if (j == 3 && !labelGrid[j][i].getText().isEmpty() &&
                                        ((labelGrid[j][i].getText().equals(labelGrid[j - 3][i].getText()) &&
                                                labelGrid[j - 2][i].getText().isEmpty() &&
                                                labelGrid[j - 1][i].getText().isEmpty()) ||
                                                (labelGrid[j][i].getText().equals(labelGrid[j - 2][i].getText()) &&
                                                        labelGrid[j - 1][i].getText().isEmpty()) ||
                                                labelGrid[j][i].getText().equals(labelGrid[j - 1][i].getText())))
                                    spacesToMove++;
                                // checking every tile left of the current tile
                                for (int k = 0; k < j; k++) {
                                    // if there is any empty space, the tile can move left
                                    if (labelGrid[k][i].getText().isEmpty())
                                        spacesToMove++;
                                }
                                // animate the tile moving left
                                tileAnimations.getChildren().addAll(createTileMoveAnimation(j, i, j - spacesToMove, i));
                            }
                        }
                    }
                }
            }
        }
        // creating a PauseTransition that pauses GUI updating until the tile's moving animation completes
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
        parallel.setOnFinished(event -> scoreStack.getChildren().remove(animatedScoreVal));
        scoreStack.getChildren().add(animatedScoreVal);
        parallel.play();
    }
    private void playAnimatedWinOrLoseScreen(StackPane stackScreen){
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), stackScreen);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        pause.setOnFinished(event -> fadeTransition.play());
        pause.play();
    }
    private void playTileAppearAnimation(Label label){
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.2), label);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), label);
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, scaleTransition);
        parallelTransition.play();
    }
    @NotNull
    public TranslateTransition createTileMoveAnimation(int startX, int startY, int endX, int endY){
        Label label = labelGrid[startX][startY];
        // disable keyboard input until animation has played
        scene.setOnKeyPressed(null);
        // calculating the distance to move the tile and bringing the label to the front
        label.toFront();
        double translateX = (endX - startX) * TILE_SIZE;
        double translateY = (endY - startY) * TILE_SIZE;
        // creating the TranslateTransition that moves the tile
        return getTranslateTransition(label, translateX, translateY);
    }
    @NotNull
    private TranslateTransition getTranslateTransition(Label label, double translateX, double translateY) {
        TranslateTransition translate = new TranslateTransition(ANIMATION_TIME, label);
        // assigning values to various translation properties
        translate.setByX(translateX);
        translate.setByY(translateY);
        translate.setOnFinished(e -> {
            label.setTranslateX(0);
            label.setTranslateY(0);
            scene.setOnKeyPressed(keyEventHandler);
        });
        return translate;
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
                windowStack.getChildren().set(1, createWinScreen());
                playAnimatedWinOrLoseScreen((StackPane)windowStack.getChildren().get(1));
                break;
            case "continue":
                scene.setOnKeyPressed(keyEventHandler);
                windowStack.getChildren().set(1, new Label());
                break;
            case "game over":
                scene.setOnKeyPressed(null);
                windowStack.getChildren().set(1, createGameOverScreen());
                playAnimatedWinOrLoseScreen((StackPane)windowStack.getChildren().get(1));
                break;
        }
    }
}
