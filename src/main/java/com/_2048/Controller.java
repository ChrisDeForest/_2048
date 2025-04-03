/**
 * Package and imports for the Controller class.
 */
package com._2048;

// JavaFX and other necessary imports
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

/**
 * The main Controller class for the 2048 game application.
 * Implements Application for JavaFX integration and
 * PropertyChangeListener for game state updates.
 */
public class Controller extends Application implements PropertyChangeListener {
    // Constants for sizes and durations used throughout the class
    private final static int SCORE_TILE_SIZE = 100, TILE_SIZE = 120, GRID_SIZE = 500, SCROLLING_DISTANCE = 2;
    private final static Duration ANIMATION_TIME = Duration.seconds(0.10),
            SCROLLING_ANIMATION_TIME = Duration.seconds(2);

    /**
     * Event handler for keyboard input to control game movements.
     */
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
        keyEvent.consume();
    };

    // Static UI elements
    private static final StackPane scoreStack = new StackPane();
    public static Label[][] labelGrid = new Label[4][4];
    private final ScrollPane scroll = new ScrollPane();
    private static Label scoreVal, bestScoreVal;
    private static Game game;
    private StackPane windowStack;
    private Scene scene;

    /**
     * Start method to initialize and configure the game UI.
     *
     * @param stage The primary stage for the JavaFX application.
     */
    @Override
    public void start(Stage stage) {
        // Title, directions, how-to
        VBox info = createGameInfo();

        // Score and best score
        GridPane scoreAndBestScore = createScoreGrid();

        // Configuring the top section
        HBox top = createTop(scoreAndBestScore, info);

        // Main game grid
        windowStack = new StackPane();
        GridPane grid = createGrid();
        windowStack.getChildren().addAll(grid, new Label());

        // Adding all sections to the main vbox
        VBox topAndGrid = new VBox();
        topAndGrid.getChildren().addAll(top, windowStack);
        topAndGrid.setAlignment(Pos.CENTER);

        // Creating the how-to-play section
        VBox howToPlay = createHowToPlay();

        // Adding the sections together in a vbox
        VBox content = new VBox();
        content.getChildren().addAll(topAndGrid, howToPlay);

        // Adding the main and how-to-play sections to a scrollable pane
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setPannable(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setContent(content);
        // prevent ScrollPane from scrolling with arrow keys
        scroll.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                case KP_UP:
                case KP_DOWN:
                case KP_LEFT:
                case KP_RIGHT:
                case W:
                case A:
                case S:
                case D:
                    // Handle the key event
                    keyEventHandler.handle(event);

                    // Prevent the ScrollPane from handling it
                    event.consume();
                    break;
            }
        });

        // Scene and stage settings
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

    /**
     * Main method to launch the JavaFX application.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        Application.launch();
    }

    /**
     * Initialization method to create a new Game instance and register as a property change listener.
     */
    @Override
    public void init() {
        game = new Game();
        game.addPropertyChangeListener(this);
    }

    /**
     * Creates and initializes the game grid with tiles.
     *
     * @return The GridPane containing the initialized game grid.
     */
    @NotNull
    private GridPane createGrid() {
        // Creating and styling the game's grid
        GridPane grid = new GridPane();
        grid.setMinSize(GRID_SIZE, GRID_SIZE);
        grid.setMaxSize(GRID_SIZE, GRID_SIZE);
        grid.getStyleClass().addAll("grid");

        // Looping through the grid
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // Creating and styling default labels for the grid
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

    /**
     * Creates the game information section with title, directions, and how-to-play instructions.
     *
     * @return The VBox containing the game information.
     */
    @NotNull
    private VBox createGameInfo() {
        // Creating a container for all the game's information
        VBox info = new VBox();

        // Creating the game's main label
        Label _2048 = new Label("2048");
        VBox.setMargin(_2048, new Insets(25, 0, 0, 0));
        _2048.getStyleClass().addAll("game-title", "bold", "game-text");

        // Creating basic game instructions
        Label directions = new Label("Join the tiles, get to ");
        directions.getStyleClass().addAll("directions-padding", "game-text");
        Label directions1 = new Label("2048!");
        directions1.getStyleClass().addAll("bold", "game-text");

        // Creating a container to hold directions and adding those directions to it
        HBox dir = new HBox();
        dir.getChildren().addAll(directions, directions1);

        // Creating a clickable "How to play" button
        Label howTo = new Label("How to play -->\n");
        howTo.getStyleClass().addAll("directions-padding", "underline", "bold", "game-text", "how-to-play");
        howTo.setOnMouseClicked(event ->{
            Timeline timeLine = new Timeline();
            KeyFrame keyFrame = new KeyFrame(SCROLLING_ANIMATION_TIME,
                    new javafx.animation.KeyValue(scroll.vvalueProperty(), SCROLLING_DISTANCE, Interpolator.EASE_IN));
            timeLine.getKeyFrames().add(keyFrame);
            timeLine.play();
        });

        // Creating a blank label for spacing (I should really use VBox.setMargin() but oh well)
        Label blank = new Label();
        blank.setMinSize(50, 35);

        // Adding all children elements to the container
        info.getChildren().addAll(_2048, dir, howTo, blank);

        return info;
    }

    /**
     * Creates the score grid section with score and best score labels.
     *
     * @return The GridPane containing the score grid.
     */
    @NotNull
    private GridPane createScoreGrid() {
        // Creating a grid to hold the two scores
        GridPane grid = new GridPane();
        grid.setMaxSize(SCORE_TILE_SIZE * 1.5, SCORE_TILE_SIZE * 0.75);
        grid.setMinSize(SCORE_TILE_SIZE * 1.25, SCORE_TILE_SIZE * 0.75);

        // Creating a VBox container for the score
        VBox left = new VBox();
        left.setMinSize(100, 60);
        left.setMaxSize(125, 60);

        // Creating and styling the score
        Label score = new Label("SCORE");
        score.setStyle("-fx-text-fill: rgb(242, 226, 208); -fx-font-weight: bold; -fx-font-size: 11pt;");
        scoreVal = new Label("0");
        scoreVal.getStyleClass().addAll("score-text", "bold");
        scoreStack.getChildren().addAll(scoreVal);

        // Adding the score to the left side VBox
        left.getChildren().addAll(score, scoreStack);
        left.getStyleClass().addAll("grid", "score-margin");
        left.setAlignment(Pos.CENTER);

        // Creating a VBox container for the best score
        VBox right = new VBox();
        right.setMinSize(110, 60);
        right.setMaxSize(125, 60);

        // Creating and styling the best score
        Label best = new Label("BEST");
        best.setStyle("-fx-text-fill: rgb(242, 226, 208); -fx-font-weight: bold; -fx-font-size: 11pt;");
        bestScoreVal = new Label("0");
        bestScoreVal.getStyleClass().addAll("score-text", "bold");

        // Adding the best score to the right side VBox
        right.getChildren().addAll(best, bestScoreVal);
        right.getStyleClass().addAll("grid", "score-margin");
        right.setAlignment(Pos.CENTER);

        // Adding both containers to the original grid
        grid.add(left, 0, 0);
        grid.add(right, 1, 0);
        grid.setAlignment(Pos.CENTER_RIGHT);

        return grid;
    }

    /**
     * Creates the top section containing game info and score grid.
     *
     * @param scoreAndBestScore The GridPane containing the score and best score labels.
     * @param info              The VBox containing the game information.
     * @return The HBox containing the top section.
     */
    @NotNull
    private HBox createTop(GridPane scoreAndBestScore, VBox info) {
        // Creating blanks for spacing (I should really use VBox.setMargin() but oh well)
        Label blank = new Label();
        blank.setMinSize(105, 25);
        Label blank2 = new Label();
        blank2.setMinSize(50, 40);
        VBox.setMargin(blank, new Insets(45, 0, 0, 0));

        // Creating, styling, and placing actions on the "New Game" button
        Button newGame = new Button("New Game");
        EventHandler<ActionEvent> newGameAction = event -> game.newGame();
        newGame.setOnAction(newGameAction);
        newGame.getStyleClass().addAll("game-button", "bold");

        // Creating the VBox container
        VBox rightSide = new VBox();
        rightSide.setMinSize(175, 125);
        rightSide.setAlignment(Pos.TOP_RIGHT);
        rightSide.getChildren().addAll(scoreAndBestScore, blank2, newGame);
        VBox.setMargin(scoreAndBestScore, new Insets(25, 0, 0, 0));

        // Creating an HBox container
        HBox top = new HBox();
        top.getChildren().addAll(info, blank, rightSide);
        top.setAlignment(Pos.TOP_LEFT);

        return top;
    }

    /**
     * Creates a StackPane for displaying the win screen when the game is won.
     *
     * @return The StackPane containing the win screen UI elements.
     */
    @NotNull
    private StackPane createWinScreen(){
        // Creating the stack pane and the background label
        StackPane stack = new StackPane();
        Label background = new Label();
        background.setMinSize(GRID_SIZE, GRID_SIZE);
        background.setMaxSize(GRID_SIZE, GRID_SIZE);
        background.setPrefSize(GRID_SIZE, GRID_SIZE);
        background.getStyleClass().addAll("game-win-lose-background");

        // Creating the "You win!" label
        Label youWin = new Label("You win!");
        youWin.setMinSize(TILE_SIZE, TILE_SIZE);
        youWin.getStyleClass().addAll("bold", "game-win-lose-text");

        // Creating a container for the buttons
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);

        // Creating and styling the "Keep Going" button
        Button keepGoing = new Button("Keep going");
        keepGoing.getStyleClass().addAll("game-button", "bold");
        EventHandler<ActionEvent> keepGoingAction = event -> game.continueGame();
        keepGoing.setOnAction(keepGoingAction);

        // Creating a blank for spacing (I should really use HBox.setMargin() but oh well)
        Label blank2 = new Label();
        blank2.setMinSize(20, 20);

        // Creating and styling the "Try Again" button
        Button tryAgain = new Button("Try again");
        EventHandler<ActionEvent> tryAgainAction = event -> game.newGame();
        tryAgain.setOnAction(tryAgainAction);
        tryAgain.getStyleClass().addAll("game-button", "bold");

        // Adding all the buttons to the HBox
        hbox.getChildren().addAll(keepGoing, blank2, tryAgain);

        // Placing the prior elements into a vbox
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(youWin, hbox);

        // Placing the background and vbox into the stack pane
        stack.getChildren().addAll(background, vbox);
        stack.setOpacity(0);

        return stack;
    }

    /**
     * Creates a StackPane for displaying the game over screen when the game ends.
     *
     * @return The StackPane containing the game over screen UI elements.
     */
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

    /**
     * Creates a VBox containing the "How to play" instructions for the game.
     *
     * @return The VBox containing the "How to play" instructions.
     */
    private VBox createHowToPlay(){
        VBox howToPlay = new VBox();
        howToPlay.setAlignment(Pos.CENTER);
        howToPlay.getStyleClass().addAll();

        // Line 1: How to play instructions
        HBox line1 = new HBox();
        line1.setAlignment(Pos.CENTER);
        Label line1_1 = new Label("HOW TO PLAY: "), line1_2 = new Label("Use "),
                line1_3 = new Label("WASD "), line1_4 = new Label("or "),
                line1_5 = new Label("ARROW KEYS "), line1_6 = new Label("to move the tiles.");
        line1_1.getStyleClass().addAll("bold");
        line1_3.getStyleClass().addAll("bold");
        line1_5.getStyleClass().addAll("bold");
        line1.getChildren().addAll(line1_1, line1_2, line1_3, line1_4, line1_5, line1_6);

        // Line 2: Tile merging instructions
        HBox line2 = new HBox();
        line2.setAlignment(Pos.CENTER);
        Label line2_1 = new Label("Tiles with the same number "), line2_2 = new Label("merge into one "),
                line2_3 = new Label("when they touch.");
        line2_2.getStyleClass().addAll("bold");
        line2.getChildren().addAll(line2_1, line2_2, line2_3);

        // Line 3: Objective instructions
        HBox line3 = new HBox();
        line3.setAlignment(Pos.CENTER);
        Label line3_1 = new Label("Add them up to reach "), line3_2 = new Label("2048!");
        line3_2.getStyleClass().addAll("bold");
        line3.getChildren().addAll(line3_1, line3_2);

        // Return to top instruction (scroll)
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
        line4_1.getStyleClass().addAll("underline", "bold", "return-to-top");
        line4.getChildren().addAll(line4_1);

        // Creating appropriate margins around the lines
        VBox.setMargin(line1, new Insets(35, 0, 0, 0));
        VBox.setMargin(line3, new Insets( 0, 0, 35, 0));
        VBox.setMargin(line4, new Insets(0, 0, 35, 0));

        // Adding all lines to the VBox
        howToPlay.getChildren().addAll(line1, line2, line3, line4);
        return howToPlay;
    }

    /**
     * Updates the game tiles based on the movement direction.
     * Animates tile movements and updates game score.
     *
     * @param quick     True if the update should be quick (no animation), false otherwise.
     * @param direction The direction of movement ("up", "down", "left", "right").
     */
    private void updateTiles(boolean quick, String direction) {
        // Updating the game's score and playing the score animation
        updateScore();
        // Creating a ParallelTransition that will hold all the tile movement animations
        ParallelTransition tileAnimations = new ParallelTransition();
        if (direction != null && !game.getGameWon()) {
            switch (direction) {
                case "up" -> {
                    // Looping through each tile in the board
                    for (int i = 0; i < Game.BOARD_SIZE; i++) {
                        for (int j = 0; j < Game.BOARD_SIZE; j++) {
                            int spacesToMove = 0;
                            // If the tile has a number, see if it needs to be moved up
                            if (!labelGrid[j][i].getText().isEmpty()) {
                                // If there is a collision, the tile can move up again
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
                                // Checking every tile above the current tile
                                for (int k = 0; k < i; k++) {
                                    // If there is any empty space, the tile can move up
                                    if (labelGrid[j][k].getText().isEmpty())
                                        spacesToMove++;
                                }
                                // Animate the tile moving up
                                tileAnimations.getChildren().addAll(createTileMoveAnimation(quick, j, i, j, i - spacesToMove));
                            }
                        }
                    }
                }
                case "down" -> {
                    // Looping through each tile in the board
                    for (int i = 0; i < Game.BOARD_SIZE; i++) {
                        for (int j = 0; j < Game.BOARD_SIZE; j++) {
                            int spacesToMove = 0;
                            // If the tile has a number, see if it needs to be moved down
                            if (!labelGrid[j][i].getText().isEmpty()) {
                                // If there is a collision, the tile can move down again
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
                                // Checking every tile below the current tile
                                for (int k = 3; k > i; k--) {
                                    // If there is any empty space, the tile can move down
                                    if (labelGrid[j][k].getText().isEmpty())
                                        spacesToMove++;
                                }
                                // Animate the tile moving down
                                tileAnimations.getChildren().addAll(createTileMoveAnimation(quick, j, i, j, i + spacesToMove));
                            }
                        }
                    }
                }
                case "right" -> {
                    // Looping through each tile in the board
                    for (int i = 0; i < Game.BOARD_SIZE; i++) {
                        for (int j = 0; j < Game.BOARD_SIZE; j++) {
                            int spacesToMove = 0;
                            // If the tile has a number, see if it needs to be moved right
                            if (!labelGrid[j][i].getText().isEmpty()) {
                                // If there is a collision, the tile can move right again
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
                                // Checking every tile right of the current tile
                                for (int k = 3; k > j; k--) {
                                    // If there is any empty space, the tile can move right
                                    if (labelGrid[k][i].getText().isEmpty())
                                        spacesToMove++;
                                }
                                // Animate the tile moving right
                                tileAnimations.getChildren().addAll(createTileMoveAnimation(quick, j, i, j + spacesToMove, i));
                            }
                        }
                    }
                }
                case "left" -> {
                    // Looping through each tile in the board
                    for (int i = 0; i < Game.BOARD_SIZE; i++) {
                        for (int j = 0; j < Game.BOARD_SIZE; j++) {
                            int spacesToMove = 0;
                            // If the tile has a number, see if it needs to be moved left
                            if (!labelGrid[j][i].getText().isEmpty()) {
                                // If there is a collision, the tile can move left again
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
                                // Checking every tile left of the current tile
                                for (int k = 0; k < j; k++) {
                                    // If there is any empty space, the tile can move left
                                    if (labelGrid[k][i].getText().isEmpty())
                                        spacesToMove++;
                                }
                                // Animate the tile moving left
                                tileAnimations.getChildren().addAll(createTileMoveAnimation(quick, j, i, j - spacesToMove, i));
                            }
                        }
                    }
                }
            }
        }
        // Creating a PauseTransition that pauses GUI updating until the tile's moving animation completes
        PauseTransition pause = getPauseTransition(quick);
        tileAnimations.play();
        pause.play();
    }

    /**
     * Gets a PauseTransition object based on animation speed.
     *
     * @param quick If true, returns a pause of 0 seconds.
     * @return PauseTransition object.
     */
    @NotNull
    private PauseTransition getPauseTransition(boolean quick) {
        // Determining the speed at which the animation plays
        Duration time;
        time = quick ? Duration.seconds(0) : ANIMATION_TIME;

        // Creating a pause animation to pause GUI updates
        PauseTransition pause = new PauseTransition(time);

        // Once the animation finishes, manually reset every board tile to its correct representation
        pause.setOnFinished(e->{
            // Looping through the board
            for (int i = 0; i < Game.BOARD_SIZE; i++) {
                for (int j = 0; j < Game.BOARD_SIZE; j++) {
                    // Updating the tile's value, text color, background color, and font size
                    labelGrid[i][j].setText(String.valueOf(game.getBoard()[j][i].getValue()));
                    String textColor = game.getBoard()[i][j].getTextColor();
                    String backgroundColor = game.getBoard()[i][j].getBackground();
                    String fontSize = game.getBoard()[i][j].getFontSize();
                    labelGrid[j][i].setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: " + textColor + ";" +
                            " -fx-font-size: " + fontSize + ";");
                    if (labelGrid[i][j].getText().equals("0"))
                        labelGrid[i][j].setText("");

                    // Adding proper styles to each tile
                    labelGrid[j][i].getStyleClass().addAll("margin", "game-tile");

                    // If the tile has just been generated, play it's appear animation
                    if (game.getBoard()[i][j].getMoveGenerated() == game.getMoveCount() && !game.getBoard()[i][j].getAnimationPlayed()) {
                        labelGrid[j][i].setOpacity(0);
                        playTileAppearAnimation(quick, labelGrid[j][i]);
                        game.getBoard()[i][j].setAnimationPlayed(true);
                    }
                }
            }
        });
        return pause;
    }

    /**
     * Updates the score displayed on the GUI.
     */
    private void updateScore() {
        scoreVal.setText(String.valueOf(game.getNewScore()));
        bestScoreVal.setText(String.valueOf(game.getBestScore()));
        if(!game.getSameBoard() && (game.getOldScore() != game.getNewScore()))
            playAnimatedScore();
    }

    /**
     * Plays an animated score update on the GUI.
     */
    private void playAnimatedScore(){
        // Creating and styling a new label containing the change in score values
        Label animatedScoreVal = new Label("+" + (game.getNewScore() - game.getOldScore()));
        animatedScoreVal.getStyleClass().addAll("score-text", "animated", "bold");
        animatedScoreVal.setTranslateY(0);

        // Creating a transition to move the label
        TranslateTransition translate = new TranslateTransition(Duration.seconds(0.5), animatedScoreVal);
        translate.setByY(-45);

        // Creating a transition to fade the label out
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), animatedScoreVal);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        // Creating a parallel transition to run the translate and fade transitions simultaneously
        ParallelTransition parallel = new ParallelTransition(translate, fade);
        parallel.setOnFinished(event -> scoreStack.getChildren().remove(animatedScoreVal));
        scoreStack.getChildren().add(animatedScoreVal);

        // Playing the animations
        parallel.play();
    }

    /**
     * Plays a fade-in animation for win or lose screens.
     *
     * @param stackScreen StackPane to animate.
     */
    private void playAnimatedWinOrLoseScreen(StackPane stackScreen){
        // Creates a pause and fade transition
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), stackScreen);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);

        // Once the pause has completed, fade the appropriate screen into view
        pause.setOnFinished(event -> fadeTransition.play());

        // Play the animation
        pause.play();
    }

    /**
     * Plays an animation for a tile appearing on the board.
     *
     * @param label Label representing the tile.
     */
    private void playTileAppearAnimation(boolean quick, Label label){
        Duration time = quick ? Duration.seconds(0.001) : Duration.seconds(0.2);

        // Creates a fade transition to fade the tile into view
        FadeTransition fadeTransition = new FadeTransition(time, label);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);

        // Creates a scale transition to scale the tile
        ScaleTransition scaleTransition = new ScaleTransition(time, label);
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);

        // Creates a parallel transition to play both transitions simultaneously
        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, scaleTransition);

        // Plays the animation
        parallelTransition.play();
    }

    /**
     * Creates a TranslateTransition animation for moving a tile on the board.
     *
     * @param startX Starting X coordinate of the tile.
     * @param startY Starting Y coordinate of the tile.
     * @param endX   Ending X coordinate of the tile.
     * @param endY   Ending Y coordinate of the tile.
     * @return TranslateTransition animation object.
     */
    @NotNull
    public TranslateTransition createTileMoveAnimation(boolean quick, int startX, int startY, int endX, int endY){
        Label label = labelGrid[startX][startY];
        // Disable keyboard input until animation has played
        scene.setOnKeyPressed(null);

        // Calculating the distance to move the tile and bringing the label to the front
        label.toFront();
        double translateX = (endX - startX) * TILE_SIZE;
        double translateY = (endY - startY) * TILE_SIZE;

        // Creating the TranslateTransition that moves the tile
        return getTranslateTransition(quick, label, translateX, translateY);
    }

    /**
     * Creates a TranslateTransition animation for moving a tile.
     *
     * @param label     Label representing the tile to move.
     * @param translateX Amount to translate on the X axis.
     * @param translateY Amount to translate on the Y axis.
     * @return TranslateTransition animation object.
     */
    @NotNull
    private TranslateTransition getTranslateTransition(boolean quick, Label label, double translateX, double translateY) {
        // Creating a translation transition which will move the tiles
        TranslateTransition translate;
        translate = quick ? new TranslateTransition(Duration.seconds(0), label) : new TranslateTransition(ANIMATION_TIME, label);

        // Assigning values to various translation properties
        translate.setByX(translateX);
        translate.setByY(translateY);

        // Once the animation is finished, reset the tile's animation parameters and re-enable key presses
        translate.setOnFinished(e -> {
            label.setTranslateX(0);
            label.setTranslateY(0);
            scene.setOnKeyPressed(keyEventHandler);
        });

        return translate;
    }

    /**
     * Handles property change events from the game.
     *
     * @param event PropertyChangeEvent containing event details.
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        // boolean flags for animation speed
        boolean newGameQuick = true, moveQuick = false;
        // Switch statement regarding the contents of the event change
        switch (event.getPropertyName()) {
            // If a "newGame" event is called, reset and start a new game
            case "newGame":
                scene.setOnKeyPressed(keyEventHandler);
                windowStack.getChildren().set(1, new Label());
                updateTiles(newGameQuick, null);
                break;
            // If an "up" event is called, try to move the tiles up and generate a new one
            case "up":
                game.setOldScore(game.getNewScore());
                if ((int) event.getOldValue() == -1) {
                    game.moveVertical(1, "up");}
                if ((int) event.getNewValue() == 1) {
                    game.generateTile(game.getDebug());
                }
                updateTiles(moveQuick, "up");
                game.checkForWin();
                break;
            // If a "right" event is called, try to move the tiles right and generate a new one
            case "right":
                game.setOldScore(game.getNewScore());
                if ((int) event.getOldValue() == -1) {
                    game.moveHorizontal(1, "right");
                }
                if ((int) event.getNewValue() == 1)
                    game.generateTile(game.getDebug());
                updateTiles(moveQuick, "right");
                game.checkForWin();
                break;
            // If a "down" event is called, try to move the tiles down and generate a new one
            case "down":
                game.setOldScore(game.getNewScore());
                if ((int) event.getOldValue() == -1) {
                    game.moveVertical(1, "down");
                }
                if ((int) event.getNewValue() == 1)
                    game.generateTile(game.getDebug());
                updateTiles(moveQuick, "down");
                game.checkForWin();
                break;
            // If a "left" event is called, try to move the tiles left and generate a new one
            case "left":
                game.setOldScore(game.getNewScore());
                if ((int) event.getOldValue() == -1) {
                    game.moveHorizontal(1, "left");
                }
                if ((int) event.getNewValue() == 1)
                    game.generateTile(game.getDebug());
                updateTiles(moveQuick, "left");
                game.checkForWin();
                break;
            // If a "score" event is called, update the score
            case "score":
                updateScore();
                break;
            // If a "won game" event is called, display the "game won" screen and disable key presses
            case "won game":
                scene.setOnKeyPressed(null);
                windowStack.getChildren().set(1, createWinScreen());
                playAnimatedWinOrLoseScreen((StackPane)windowStack.getChildren().get(1));
                break;
            // If a "continue" event is called, resume the game and re-enable key presses
            case "continue":
                scene.setOnKeyPressed(keyEventHandler);
                windowStack.getChildren().set(1, new Label());
                break;
            // If a "game over" event is called, display the "game over" screen and disable key presses
            case "game over":
                scene.setOnKeyPressed(null);
                windowStack.getChildren().set(1, createGameOverScreen());
                playAnimatedWinOrLoseScreen((StackPane)windowStack.getChildren().get(1));
                break;
        }
    }
}
