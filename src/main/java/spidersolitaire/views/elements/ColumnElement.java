package spidersolitaire.views.elements;

import java.util.stream.IntStream;

import spidersolitaire.controllers.GameControllerInputBoundary;
import spidersolitaire.models.Column;
import spidersolitaire.models.Location;


class ColumnElement extends LocationElement {
    private GameControllerInputBoundary inputBoundary;

    ColumnElement() {
        super();
        this.inputBoundary = null;
    }

    void setInputBoundary(GameControllerInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    @Override
    void setNode(Location location) {
        super.setNode(location);
        IntStream.range(((Column) location).getMinIndexCanRemove(), location.getCards().size())
                .forEach(i -> this.cards.get(i).setInputBoundary(this.inputBoundary));
    }

    @Override
    void updateCSS(double boardViewWidth) {
        super.updateCSS(boardViewWidth);
        this.node.setStyle("-fx-background-color: lime;");
    }
}
