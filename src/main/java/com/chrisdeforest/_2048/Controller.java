package com.chrisdeforest._2048;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class Controller extends Application implements PropertyChangeListener {
    // constants
    private final static int SCORE_SIZE = 100;
    private final static int TILE_SIZE = 120;
    private final static int MAIN_GRID_SIZE = 500;
    private Label status = new Label("Game not started yet");
    private static Game game;
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("START");
        // title, directions, how-to
        VBox info = makeInfo();
        // score and best score
        GridPane scoreAndBestScore = makeScoreGrid();
        // configuring the top section
        HBox top = makeTop(scoreAndBestScore, info);
        // main game grid
        GridPane grid = makeGrid();
        // adding all sections to the main vbox
        VBox main = new VBox();
        main.getChildren().addAll(top, grid, status);
        main.setAlignment(Pos.CENTER);
        // scene and stage settings
        Scene scene = new Scene(main);
        scene.getStylesheets().add("file:src/main/resources/styles.css");
        stage.setScene(scene);
        stage.setHeight(800);
        stage.setWidth(600);
        stage.setResizable(false);
        stage.setTitle("2048");
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void init() throws Exception{
        System.out.println("INIT");
        game = new Game();
        game.addPropertyChangeListener(this);
        game.initializeBoard();

    }
    public static VBox makeInfo(){
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
        blank.setMinSize(50,35);
        info.getChildren().addAll(_2048, dir, howTo, blank);
        return info;
    }

    public static GridPane makeGrid(){
        GridPane grid = new GridPane();
        grid.setMinSize(MAIN_GRID_SIZE, MAIN_GRID_SIZE);
        grid.setMaxSize(MAIN_GRID_SIZE, MAIN_GRID_SIZE);
        grid.getStyleClass().addAll("grid");
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                Label label = new Label();
                label.setMinSize(TILE_SIZE, TILE_SIZE);
                label.getStyleClass().addAll("margin", "game-label");
                label.setAlignment(Pos.CENTER);
                grid.add(label, i, j);
            }
        }
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    public static GridPane makeScoreGrid(){
        GridPane grid = new GridPane();
        grid.setMaxSize(SCORE_SIZE * 1.5, SCORE_SIZE * 0.75);
        grid.setMinSize(SCORE_SIZE * 1.25, SCORE_SIZE * 0.75);
        grid.getStyleClass().addAll("score-grid");
        VBox left = new VBox();
        left.setMinSize(100,60);
        left.setMaxSize(125, 60);
        Label score = new Label("SCORE");
        score.setStyle("-fx-text-fill: rgb(242, 226, 208); -fx-font-weight: bold; -fx-font-size: 11pt;");
        // TODO do something with this so it can be updated later on
        Label value = new Label("0");
        value.getStyleClass().addAll("score-text", "bold");
        left.getChildren().addAll(score, value);
        left.getStyleClass().addAll( "grid", "score-margin");
        left.setAlignment(Pos.CENTER);
        VBox right = new VBox();
        right.setMinSize(110,60);
        right.setMaxSize(125, 60);
        Label best = new Label("BEST");
        best.setStyle("-fx-text-fill: rgb(242, 226, 208); -fx-font-weight: bold; -fx-font-size: 11pt;");
        // TODO do something with this so it can be updated later on
        Label bestVal = new Label("0");
        bestVal.getStyleClass().addAll("score-text", "bold");
        right.getChildren().addAll(best, bestVal);
        right.getStyleClass().addAll( "grid", "score-margin");
        right.setAlignment(Pos.CENTER);
        grid.add(left, 0, 0);
        grid.add(right, 1, 0);
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    public static HBox makeTop(GridPane scoreAndBestScore, VBox info){
        Label blank = new Label();
        blank.setMinSize(105, 25);
        Label blank2 = new Label();
        blank2.setMinSize(50,40);
        Button newGame = new Button("New Game");
        EventHandler<ActionEvent> newGameAction = event -> {
            game.newGame();
        };
        newGame.setOnAction(newGameAction);
        newGame.getStyleClass().addAll("game-new-game", "bold");
        VBox rightSide = new VBox();
        rightSide.setMinSize(175, 125);
        rightSide.setAlignment(Pos.TOP_RIGHT);
        rightSide.getChildren().addAll(scoreAndBestScore, blank2, newGame);
        HBox top = new HBox();
        top.getChildren().addAll(info, blank, rightSide);
        top.setAlignment(Pos.TOP_LEFT);
        return top;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if ("value".equals(event.getPropertyName())) {
            int newValue = (int) event.getNewValue();
            status.setText(String.valueOf(newValue));
        } else if("newGame".equals(event.getPropertyName())){
            status.setText("New game has been started: " + event.getNewValue());
        }
    }
}
