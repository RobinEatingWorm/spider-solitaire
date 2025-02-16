package spidersolitaire.views.elements;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import spidersolitaire.controllers.GameControllerInputBoundary;
import spidersolitaire.models.Card;


public class CardElement {
    private GameControllerInputBoundary inputBoundary;

    private final Pane node;

    CardElement() {
        this.inputBoundary = null;
        this.node = new VBox();
        this.node.getStyleClass().add("card-element");
    }

    void setInputBoundary(GameControllerInputBoundary inputBoundary) {
        // TODO: set events
        this.inputBoundary = inputBoundary;
        this.node.setOnDragDetected();
        this.node.setOnDragDone();
    }

    Pane getNode() {
        return this.node;
    }

    void setNode(Card card) {
        this.node.getChildren().clear();
        if (card.isFaceUp()) {
            String rank = formatRank(card.getRank());
            String suit = formatSuit(card.getSuit());
            String color = formatColor(card.getSuit());
            HBox cardElementHeader = new HBox();
            cardElementHeader.getChildren().addAll(new Text(rank), new Text(suit));
            this.node.getChildren().addAll(cardElementHeader, new Text(suit));

            cardElementHeader.getChildren().forEach(node -> node.setStyle("-fx-fill: " + color + ";"));
            this.node.getChildren().get(1).setStyle("-fx-fill: " + color + ";");
        }
    }

    void updateCSS(double locationElementWidth) {
        this.node.setPrefWidth(locationElementWidth);
        this.node.setPrefHeight(locationElementWidth * 1.4);
        this.node.setStyle("-fx-font-size: " + locationElementWidth / (128 / 36) + "px;");
    }

    private static String formatRank(int rank) {
        return new String[]{"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"}[rank - 1];
    }

    private static String formatSuit(int suit) {
        return new String[]{"♠", "♥", "♣", "♦"}[suit];
    }

    private static String formatColor(int suit) {
        return new String[]{"black", "red"}[suit % 2];
    }
}
