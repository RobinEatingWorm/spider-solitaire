package spidersolitaire;

import spidersolitaire.controllers.Controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Listeners for viewport changes to update CSS
        primaryStage.heightProperty()
                .addListener((observable, oldValue, newValue) -> onCSSUpdate());
        primaryStage.widthProperty()
                .addListener((observable, oldValue, newValue) -> onCSSUpdate());

        // Start the app
        Controller.newGame(1);
        Scene scene = new Scene(Controller.getGameView());
        primaryStage.setTitle("Spider Solitaire");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void onCSSUpdate() {
        Controller.handleCSSUpdate();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
