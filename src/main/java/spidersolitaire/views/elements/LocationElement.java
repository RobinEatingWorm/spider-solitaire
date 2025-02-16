package spidersolitaire.views.elements;

import java.util.ArrayList;
import java.util.stream.IntStream;

import spidersolitaire.models.Card;
import spidersolitaire.models.Location;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


public abstract class LocationElement {
    protected final Pane node;

    protected final ArrayList<CardElement> cards;

    LocationElement() {
        this.node = new VBox();
        this.cards = new ArrayList<>();
    }

    Pane getNode() {
        return this.node;
    }

    void setNode(Location location) {
        this.node.getChildren().clear();
        ArrayList<Card> cards = location.getCards();
        ArrayList<CardElement> cardElements = new ArrayList<>();
        IntStream.range(0, cards.size()).forEach(i -> cardElements.add(new CardElement()));
        IntStream.range(0, cards.size()).forEach(i -> cardElements.get(i).setNode(cards.get(i)));
        IntStream.range(0, cards.size())
                .forEach(i -> this.node.getChildren().addAll(cardElements.get(i).getNode()));
        this.cards.clear();
        this.cards.addAll(cardElements);
    }

    void updateCSS(double boardViewWidth) {
        double locationElementWidth = boardViewWidth / 10;
        this.node.setPrefWidth(locationElementWidth);
        IntStream.range(0, this.node.getChildren().size())
                .forEach(i -> this.cards.get(i).updateCSS(locationElementWidth));
    };
}
