package spidersolitaire;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import spidersolitaire.controllers.GameController;
import spidersolitaire.models.Game;
import spidersolitaire.views.GameView;


public class App extends Application {
    private final Game model;
    private final GameView view;
    private final GameController controller;

    public App() {
        super();
        this.model = new Game();
        this.view = new GameView();
        this.controller = new GameController(this.model, this.view);
        this.model.setOutputBoundary(controller);
        this.view.setInputBoundary(controller);
    }

    @Override
    public void start(Stage primaryStage) {
        this.model.startNewGame(4);

        // Listener for stage width changes to update CSS
        primaryStage.widthProperty()
                .addListener((observable, oldValue, newValue) -> onCSSUpdate(newValue.doubleValue()));

        Scene scene = new Scene(this.view.getNode());
        scene.getStylesheets().addAll("/spidersolitaire/styles/board.css", "spidersolitaire/styles/card.css");
        primaryStage.setTitle("Spider Solitaire");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void onCSSUpdate(double appWidth) {
        this.controller.handleCSSUpdate(appWidth);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
