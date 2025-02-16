package spidersolitaire.views;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import spidersolitaire.controllers.GameControllerInputBoundary;
import spidersolitaire.models.Game;
import spidersolitaire.views.elements.BoardElement;
import spidersolitaire.views.elements.ControlElement;


public class GameView {
    private GameControllerInputBoundary inputBoundary;

    private final Pane node;
    private final ScrollPane scrollPane;

    private final BoardElement boardElement;
    private final ControlElement controlElement;

    public GameView() {
        this.inputBoundary = null;
        this.node = new VBox();
        this.scrollPane = new ScrollPane(this.node);
        this.boardElement = new BoardElement();
        this.controlElement = new ControlElement();

        this.scrollPane.setFitToWidth(true);
        ((VBox) this.node).setAlignment(Pos.TOP_CENTER);
    }

    public void setInputBoundary(GameControllerInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
        this.boardElement.setInputBoundary(inputBoundary);
        this.controlElement.setInputBoundary(inputBoundary);
    }

    public ScrollPane getNode() {
        return this.scrollPane;
    }

    private void setNode(Game game) {
        this.node.getChildren().clear();
        this.boardElement.setNode(game.getBoard());
        this.controlElement.setNode();
        this.node.getChildren().addAll(this.controlElement.getNode(), this.boardElement.getNode());
    }

    public void updateGameView(Game game) {
        setNode(game);
    }

    public void updateCSS(double appWidth) {
        this.scrollPane.setPrefWidth(appWidth);
        double gameViewWidth = this.node.getWidth();
        this.boardElement.updateCSS(gameViewWidth);
        this.controlElement.updateCSS(gameViewWidth);
        this.node.setStyle("-fx-background-color: red;");
    }
}
