package spidersolitaire.views.elements;

import java.util.ArrayList;
import java.util.stream.IntStream;

import spidersolitaire.models.Card;


class RunElement extends LocationElement {
    RunElement() {
        super();
    }

    @Override
    void updateCSS(double boardViewWidth) {
        super.updateCSS(boardViewWidth);
        this.node.setStyle("-fx-background-color: blue;");
    }
}
