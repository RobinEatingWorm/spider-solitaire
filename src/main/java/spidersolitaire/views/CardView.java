package spidersolitaire.views;

import spidersolitaire.controllers.Controller;
import spidersolitaire.models.Card;
import spidersolitaire.models.Game;
import spidersolitaire.models.Logic;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

class CardView extends VBox {
    private final Card card;

    private final String location;
    private final int locationIndex;
    private final int stackIndex;

    private final Text cardViewHeaderRank;
    private final Text cardViewHeaderSuit;
    private final Text cardViewBodySuit;
    private final HBox cardViewHeader;

    private double[] dragOffset;

    private static final double cardHeaderMaxFontSize = 24;
    private static final double cardHeaderMaxPadding = 5;
    private static final double cardBodyMaxFontSize = 36;
    private static final double cardBorderMaxWidth = 2.5;

    CardView(String location, int locationIndex, int stackIndex) {
        super();
        this.location = location;
        this.locationIndex = locationIndex;
        this.stackIndex = stackIndex;
        this.dragOffset = null;
        this.card = Game.getCard(this.location, this.locationIndex, this.stackIndex);

        // Set up card view and default style
        if (this.card.getFaceUp()) {
            // Position text on card
            this.cardViewHeaderRank = new Text(this.formatRank());
            this.cardViewHeaderSuit = new Text(this.formatSuit());
            this.cardViewBodySuit = new Text(this.formatSuit());
            Region cardViewHeaderSpacer = new Region();
            HBox.setHgrow(cardViewHeaderSpacer, Priority.ALWAYS);
            this.cardViewHeader = new HBox(this.cardViewHeaderRank, cardViewHeaderSpacer, this.cardViewHeaderSuit);
            Region cardViewSpacer = new Region();
            VBox.setVgrow(this.cardViewHeader, Priority.ALWAYS);
            VBox.setVgrow(cardViewSpacer, Priority.ALWAYS);
            this.getChildren().addAll(this.cardViewHeader, this.cardViewBodySuit, cardViewSpacer);
            this.setAlignment(Pos.CENTER);
        } else {
            // No text to set
            this.cardViewHeaderRank = null;
            this.cardViewHeaderSuit = null;
            this.cardViewBodySuit = null;
            this.cardViewHeader = null;
        }

        // Set event handlers
        if (this.isMovable()) {
            this.setOnMousePressed(event -> {
                // this.setMouseTransparent(true);
                Controller.handleMousePressedColumnCard();
            });
            this.setOnDragDetected(event -> {
                this.startFullDrag();
                Controller.handleDragDetectedColumnCard(event, this.locationIndex, this.stackIndex);
            });
            this.setOnMouseClicked(event -> {
                event.consume();
                Controller.handleMouseClickedColumnCard(this.locationIndex, this.stackIndex);
            });
        } else if (this.location.equals("stocks")) {
            this.setOnMouseClicked(event -> {
                event.consume();
                Controller.handleMouseClickedStockCard();
            });
        }
    }

    public void updateCSS(double cardHeight, double cardWidth, double boardMaxWidthFraction) {
        // Set card dimensions
        this.setPrefHeight(cardHeight);
        this.setPrefWidth(cardWidth);

        // Set card border and background
        boolean isInClickMove = this.isInMove() && Controller.getClickFirst();
        double cardBorderWidth = CardView.cardBorderMaxWidth * boardMaxWidthFraction;
        this.setBorder(new Border(new BorderStroke(
            isInClickMove ? Color.GRAY : Color.BLACK,
            BorderStrokeStyle.SOLID,
            new CornerRadii(cardBorderWidth),
            new BorderWidths(cardBorderWidth)
        )));
        this.setBackground(new Background(new BackgroundFill(
            this.card.getFaceUp() ? isInClickMove ? Color.BLACK : Color.WHITE : Color.GREEN,
            new CornerRadii(cardBorderWidth * 2),
            new Insets(0)
        )));

        // Set card text style if face up
        if (this.card.getFaceUp()) {
            double cardHeaderFontSize = CardView.cardHeaderMaxFontSize * boardMaxWidthFraction;
            double cardBodyFontSize = CardView.cardBodyMaxFontSize * boardMaxWidthFraction;
            this.cardViewHeaderRank.setFill(this.formatColor(isInClickMove));
            this.cardViewHeaderSuit.setFill(this.formatColor(isInClickMove));
            this.cardViewBodySuit.setFill(this.formatColor(isInClickMove));
            this.cardViewHeaderRank.setStyle("-fx-font-size: " + cardHeaderFontSize  + "px;");
            this.cardViewHeaderSuit.setStyle("-fx-font-size: " + cardHeaderFontSize  + "px;");
            this.cardViewBodySuit.setStyle("-fx-font-size: " + cardBodyFontSize  + "px;");
            this.cardViewHeader.setPadding(new Insets(CardView.cardHeaderMaxPadding * boardMaxWidthFraction));
            this.setPadding(new Insets(0, 0, this.cardViewHeader.getHeight(), 0));
        }

        // Set card absolute position if dragged
        boolean isInDragMove = this.isInMove() && Controller.getClientPosition() != null;
        if (isInDragMove) {
            double[] clientPosition = Controller.getClientPosition();

            // Set drag offset (upon detecting a drag) or new position (during the drag)
            if (this.dragOffset == null) {
                Point2D dragOffsetPoint = this.sceneToLocal(clientPosition[0], clientPosition[1]);
                this.dragOffset = new double[]{dragOffsetPoint.getX(), dragOffsetPoint.getY()};
            } else {
                this.relocate(clientPosition[0] - this.dragOffset[0], clientPosition[1] - this.dragOffset[1]);
            }
        }
    }

    private String formatRank() {
        return new String[]{"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"}[this.card.getRank() - 1];
    }

    private String formatSuit() {
        return new String[]{"♠️", "♥️", "♣️", "♦️"}[this.card.getSuit()];
    }

    private Color formatColor(boolean invert) {
        return invert
                ? new Color[]{Color.WHITE, Color.CYAN}[this.card.getSuit() % 2]
                : new Color[]{Color.BLACK, Color.RED}[this.card.getSuit() % 2];
    }

    private boolean isMovable() {
        return this.location.equals("columns") && Logic.getMinMovableStackIndex(this.locationIndex) <= this.stackIndex;
    }

    private boolean isInMove() {
        return this.location.equals("columns")
                && Controller.getMoveColumnIndex() != null
                && Controller.getMoveColumnIndex() == this.locationIndex
                && Controller.getMoveStackIndex() != null
                && Controller.getMoveStackIndex() <= this.stackIndex;
    }
}
