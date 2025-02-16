package spidersolitaire.views.elements;

import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import spidersolitaire.controllers.GameControllerInputBoundary;


public class ControlElement {
    private GameControllerInputBoundary inputBoundary;

    private final Pane node;

    public ControlElement() {
        this.inputBoundary = null;
        this.node = new HBox();
        Button newGameButton = new Button("New Game");
        ToggleGroup nSuitsGroup = new ToggleGroup();
        RadioButton oneSuitButton = new RadioButton("One Suit");
        RadioButton twoSuitsButton = new RadioButton("Two Suits");
        RadioButton fourSuitsButton = new RadioButton("Four Suits");
        oneSuitButton.setToggleGroup(nSuitsGroup);
        twoSuitsButton.setToggleGroup(nSuitsGroup);
        fourSuitsButton.setToggleGroup(nSuitsGroup);
        oneSuitButton.setSelected(true);
        VBox nSuitsButtons = new VBox();
        nSuitsButtons.getChildren().addAll(oneSuitButton, twoSuitsButton, fourSuitsButton);
        Button resetGameButton = new Button("Reset Game");
        Button undoMoveButton = new Button("Undo Move");
        this.node.getChildren().addAll(newGameButton, nSuitsButtons, resetGameButton, undoMoveButton);
    }

    public void setInputBoundary(GameControllerInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    public Pane getNode() {
        return this.node;
    }

    public void setNode() {}

    public void updateCSS(double gameViewWidth) {
        this.node.setStyle("-fx-background-color: yellow;");
    }
}
