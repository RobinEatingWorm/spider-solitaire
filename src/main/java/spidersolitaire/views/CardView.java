package spidersolitaire.views;

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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import spidersolitaire.adapters.CardViewModel;
import spidersolitaire.adapters.Controller;

class CardView extends VBox {
    private final Controller controller;

    private final int rank;
    private final int suit;
    private final boolean faceUp;

    private final String location;
    private final int locationIndex;
    private final int stackIndex;

    private final Text cardHeaderRank;
    private final Text cardHeaderSuit;
    private final Text cardBodySuit;

    private double[] dragOffset;

    private static final double cardBorderMaxWidth = 2.5;
    private static final double cardHeaderMaxFontSize = 24;
    private static final double cardBodyMaxFontSize = 36;

    CardView(CardViewModel cardViewModel, Controller controller) {
        super();
        this.controller = controller;

        // Set card information
        this.rank = cardViewModel.getRank();
        this.suit = cardViewModel.getSuit();
        this.faceUp = cardViewModel.getFaceUp();
        this.location = cardViewModel.getLocation();
        this.locationIndex = cardViewModel.getLocationIndex();
        this.stackIndex = cardViewModel.getStackIndex();

        // Set a dummy border and background
        this.setBorder(Border.stroke(Color.TRANSPARENT));
        this.setBackground(Background.fill(Color.TRANSPARENT));

        // Set up card view and default style if face up
        if (this.faceUp) {
            this.cardHeaderRank = new Text(this.formatRank());
            this.cardHeaderSuit = new Text(this.formatSuit());
            this.cardBodySuit = new Text(this.formatSuit());
            Region cardHeaderSpacer = new Region();
            HBox.setHgrow(cardHeaderSpacer, Priority.ALWAYS);
            HBox cardHeader = new HBox(this.cardHeaderRank, cardHeaderSpacer, this.cardHeaderSuit);
            Region cardSpacer = new Region();
            VBox.setVgrow(cardHeader, Priority.ALWAYS);
            VBox.setVgrow(cardSpacer, Priority.ALWAYS);
            this.getChildren().addAll(cardHeader, this.cardBodySuit, cardSpacer);
            this.setAlignment(Pos.CENTER);
        } else {
            this.cardHeaderRank = null;
            this.cardHeaderSuit = null;
            this.cardBodySuit = null;
        }

        // Set event handlers
        if (cardViewModel.getColumnMovable()) {
            this.setOnMousePressed(event -> this.controller.handleMousePressedColumnCard());
            this.setOnDragDetected(event -> {
                this.startFullDrag();
                this.controller.handleDragDetectedColumnCard(event, this.locationIndex, this.stackIndex);
            });
            this.setOnMouseClicked(event -> {
                event.consume();
                this.controller.handleMouseClickedColumnCard(this.locationIndex, this.stackIndex);
            });
        } else if (this.location.equals("stocks")) {
            this.setOnMouseClicked(event -> {
                event.consume();
                this.controller.handleMouseClickedStockCard();
            });
        }
    }

    String getLocation() {
        return this.location;
    }

    int getLocationIndex() {
        return this.locationIndex;
    }

    int getStackIndex() {
        return this.stackIndex;
    }

    void updateMeasurements(double cardHeight, double cardWidth, double boardWidthFraction) {
        // Set card dimensions
        this.setPrefHeight(cardHeight);
        this.setPrefWidth(cardWidth);

        // Set card border and background sizes
        double cardBorderWidth = CardView.cardBorderMaxWidth * boardWidthFraction;
        Paint currentStroke = this.getBorder().getStrokes().getFirst().getBottomStroke();
        BorderStrokeStyle currentStyle = this.getBorder().getStrokes().getFirst().getBottomStyle();
        this.setBorder(new Border(new BorderStroke(
            currentStroke,
            currentStyle,
            new CornerRadii(cardBorderWidth),
            new BorderWidths(cardBorderWidth)
        )));
        Paint currentFill = this.getBackground().getFills().getFirst().getFill();
        this.setBackground(new Background(new BackgroundFill(
            currentFill,
            new CornerRadii(cardBorderWidth * 2),
            new Insets(0)
        )));

        // Set card padding and card text font size if face up
        if (this.faceUp) {
            double cardHeaderFontSize = CardView.cardHeaderMaxFontSize * boardWidthFraction;
            double cardBodyFontSize = CardView.cardBodyMaxFontSize * boardWidthFraction;
            this.cardHeaderRank.setFont(new Font(cardHeaderFontSize));
            this.cardHeaderSuit.setFont(new Font(cardHeaderFontSize));
            this.cardBodySuit.setFont(new Font(cardBodyFontSize));
            this.setPadding(new Insets(2 * cardBorderWidth));
        }
    }

    void updateVisuals(boolean isInClickMove, boolean isInDragMove, double[] clientPosition) {
        // Set card border and background colors
        CornerRadii currentBorderRadii = this.getBorder().getStrokes().getFirst().getRadii();
        BorderWidths currentWidths = this.getBorder().getStrokes().getFirst().getWidths();
        this.setBorder(new Border(new BorderStroke(
            isInClickMove ? Color.GRAY : Color.BLACK,
            BorderStrokeStyle.SOLID,
            currentBorderRadii,
            currentWidths
        )));
        CornerRadii currentBackgroundRadii = this.getBackground().getFills().getFirst().getRadii();
        Insets currentInsets = this.getBackground().getFills().getFirst().getInsets();
        this.setBackground(new Background(new BackgroundFill(
            this.faceUp ? isInClickMove ? Color.BLACK : Color.WHITE : Color.GREEN,
            currentBackgroundRadii,
            currentInsets
        )));

        // Set card text color if face up
        if (this.faceUp) {
            this.cardHeaderRank.setFill(this.formatColor(isInClickMove));
            this.cardHeaderSuit.setFill(this.formatColor(isInClickMove));
            this.cardBodySuit.setFill(this.formatColor(isInClickMove));
        }

        // Set absolute position of dragged cards
        if (isInDragMove) {
            if (this.dragOffset == null) {
                // Set drag offset (relative to local coordinates) upon detecting a drag
                Point2D dragOffsetPoint = this.sceneToLocal(clientPosition[0], clientPosition[1]);
                this.dragOffset = new double[]{dragOffsetPoint.getX(), dragOffsetPoint.getY()};
            } else {
                // Set new position (relative to scene coordinates) otherwise
                this.relocate(clientPosition[0] - this.dragOffset[0], clientPosition[1] - this.dragOffset[1]);
            }
        }
    }

    private String formatRank() {
        return new String[]{"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"}[this.rank - 1];
    }

    private String formatSuit() {
        return new String[]{"♠", "♥", "♣", "♦"}[this.suit];
    }

    private Color formatColor(boolean invert) {
        return invert
                ? new Color[]{Color.WHITE, Color.CYAN}[this.suit % 2] 
                : new Color[]{Color.BLACK, Color.RED}[this.suit % 2];
    }
}
