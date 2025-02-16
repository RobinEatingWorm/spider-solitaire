package spidersolitaire.views.elements;

import spidersolitaire.controllers.GameControllerInputBoundary;


class StockElement extends LocationElement{
    private GameControllerInputBoundary inputBoundary;

    StockElement() {
        super();
        this.inputBoundary = null;
    }

    void setInputBoundary(GameControllerInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    @Override
    void updateCSS(double boardViewWidth) {
        super.updateCSS(boardViewWidth);
        this.node.setStyle("-fx-background-color: cyan;");
    }
}
