/**
 * Package and imports for the Controller class.
 */
package com._2048;

// JavaFX and other necessary imports
import com.socket.GameStateServer;
import com.state.GameState;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The main Controller class for the 2048 game application.
 * Implements Application for JavaFX integration and
 * PropertyChangeListener for game state updates.
 */
public class Controller extends Application implements PropertyChangeListener {

    /**
     * Event handler for keyboard input to control game movements.
     */
    private static final EventHandler<KeyEvent> keyEventHandler = keyEvent -> {
        switch (keyEvent.getCode()) {
            case UP, W, KP_UP:
                Controller.getGame().moveVertical(0, "up");
                break;
            case RIGHT, D, KP_RIGHT:
                Controller.getGame().moveHorizontal(0, "right");
                break;
            case DOWN, S, KP_DOWN:
                Controller.getGame().moveVertical(0, "down");
                break;
            case LEFT, A, KP_LEFT:
                Controller.getGame().moveHorizontal(0, "left");
                break;
        }
        keyEvent.consume();
    };

    // Static UI elements
    private final static ScrollPane scroll = new ScrollPane();
    private static Scene scene;
    private static Game game;
    private static UI ui;
    private GameStateServer gameServer;
    private StackPane windowStack;

    public static Game getGame(){ return game; }
    public static ScrollPane getScrollPane(){ return scroll; }
    public static Scene getScene(){ return scene; }
    public static EventHandler<KeyEvent> getKeyEventHandler(){ return keyEventHandler; }

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
        ui = new UI();
//        gameServer = new GameStateServer();
//        gameServer.start();
    }

    /**
     * Start method to initialize and configure the game UI.
     *
     * @param stage The primary stage for the JavaFX application.
     */
    @Override
    public void start(Stage stage) {
        // Title, directions, how-to
        VBox info = ui.createGameInfo();

        // Score and best score
        GridPane scoreAndBestScore = ui.createScoreGrid();

        // Configuring the top section
        HBox top = ui.createTop(scoreAndBestScore, info);

        // Main game grid
        windowStack = new StackPane();
        GridPane grid = ui.createGrid();
        windowStack.getChildren().addAll(grid, new Label());

        // Adding all sections to the main vbox
        VBox topAndGrid = new VBox();
        topAndGrid.getChildren().addAll(top, windowStack);
        topAndGrid.setAlignment(Pos.CENTER);

        // Creating the how-to-play section
        VBox howToPlay = ui.createHowToPlay();

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
     * Ensures resources are closed and cleaned up when the program is stopped
     */
    @Override
    public void stop(){
        if (gameServer != null) {
            gameServer.stop();
        }
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
                ui.updateTiles(newGameQuick, null);
                break;
            // If an "up" event is called, try to move the tiles up and generate a new one
            case "up":
                game.setOldScore(game.getNewScore());
                if ((int) event.getOldValue() == -1) {
                    game.moveVertical(1, "up");}
                if ((int) event.getNewValue() == 1) {
                    game.generateTile(game.getDebug());
                }
                ui.updateTiles(moveQuick, "up");
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
                ui.updateTiles(moveQuick, "right");
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
                ui.updateTiles(moveQuick, "down");
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
                ui.updateTiles(moveQuick, "left");
                game.checkForWin();
                break;
            // If a "score" event is called, update the score
            case "score":
                ui.updateScore();
                break;
            // If a "won game" event is called, display the "game won" screen and disable key presses
            case "won game":
                scene.setOnKeyPressed(null);
                windowStack.getChildren().set(1, ui.createWinScreen());
                ui.playAnimatedWinOrLoseScreen((StackPane)windowStack.getChildren().get(1));
                break;
            // If a "continue" event is called, resume the game and re-enable key presses
            case "continue":
                scene.setOnKeyPressed(keyEventHandler);
                windowStack.getChildren().set(1, new Label());
                break;
            // If a "game over" event is called, display the "game over" screen and disable key presses
            case "game over":
                scene.setOnKeyPressed(null);
                windowStack.getChildren().set(1, ui.createGameOverScreen());
                ui.playAnimatedWinOrLoseScreen((StackPane)windowStack.getChildren().get(1));
                break;
        }
        // broadcast the current game state to the socket connection
        broadcastCurrentGameState();
//        System.out.println();
    }

    /**
     * Broadcasts the current game state on a background thread to the gameServer socket
     */
    private void broadcastCurrentGameState() {
        // This runs on a background thread to avoid freezing the UI
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                GameState currentState = new GameState(
                        game.getNewScore(),
                        game.getBestScore(),
                        game.getGameOver(),
                        game.getGameWon(),
                        game.getIntBoard()
                );

                gameServer.broadcastGameState(currentState);
                return null;
            }
        };

        new Thread(task).start();
    }
}
