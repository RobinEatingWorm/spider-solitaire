package spidersolitaire;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import spidersolitaire.adapters.Controller;
import spidersolitaire.adapters.GameViewModel;
import spidersolitaire.adapters.Presenter;
import spidersolitaire.models.Game;
import spidersolitaire.models.Logic;
import spidersolitaire.views.GameView;


public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Set up models
        Game game = new Game();
        Logic logic = new Logic(game);

        // Set up adapters
        GameViewModel gameViewModel = new GameViewModel();
        Presenter presenter = new Presenter(gameViewModel);
        Controller controller = new Controller(game, logic, presenter, gameViewModel);

        // Set up views
        GameView gameView = new GameView(controller);
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> gameView.updateMeasurements());
        gameViewModel.addObserver(gameView);
        controller.handleNewGame(1);

        // Start the app
        Scene scene = new Scene(gameView);
        primaryStage.setTitle("Spider Solitaire");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
