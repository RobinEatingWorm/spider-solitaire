package spidersolitaire.views;

import spidersolitaire.controllers.Controller;
import spidersolitaire.models.Game;

import java.util.stream.IntStream;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class GameView extends ScrollPane {
    private final VBox gameView;
    private final VBox boardView;
    private final ToolBar controlView;

    private final HBox columnsView;
    private final HBox runsView;
    private final HBox stocksView;

    private final VBox[] columnViews;

    private final Button newGameView;
    private final ChoiceBox<String> nSuitsView;
    private final Button resetGameView;
    private final Button undoMoveView;

    private CardView[] dragCardViews;

    private static final double boardMaxAspectRatio = 16.0 / 9.0;
    private static final double boardMaxWidth = 1280;
    private static final double boardPaddingRatio = 100;
    private static final double cardAspectRatio = 2.5 / 3.5;
    private static final double cardSpacePaddingRatio = 100;
    private static final double columnMaxSpacing = 32;
    private static final double controlMaxFontSize = 24;
    private static final double runsMaxSpacing = 32;
    private static final double stocksMaxSpacing = 32;

    public GameView() {
        super();

        // Set up location views
        this.columnViews = new VBox[Game.getNColumns()];
        IntStream.range(0, Game.getNColumns()).forEach(i -> this.columnViews[i] = new VBox());
        this.columnsView = new HBox(this.columnViews);
        this.runsView = new HBox();
        this.stocksView = new HBox();

        // Set up board view
        Region boardBottomViewSpacerLeft = new Region();
        Region boardBottomViewSpacerCenterLeft = new Region();
        Region boardBottomViewSpacerCenterRight = new Region();
        Region boardBottomViewSpacerRight = new Region();
        HBox.setHgrow(boardBottomViewSpacerLeft, Priority.ALWAYS);
        HBox.setHgrow(boardBottomViewSpacerCenterLeft, Priority.ALWAYS);
        HBox.setHgrow(boardBottomViewSpacerCenterRight, Priority.ALWAYS);
        HBox.setHgrow(boardBottomViewSpacerRight, Priority.ALWAYS);
        HBox boardBottomView = new HBox(
            boardBottomViewSpacerLeft,
            this.runsView,
            boardBottomViewSpacerCenterLeft,
            boardBottomViewSpacerCenterRight,
            this.stocksView,
            boardBottomViewSpacerRight
        );
        this.boardView = new VBox(this.columnsView, boardBottomView);

        // Set up control view
        this.newGameView = new Button("New Game");
        this.nSuitsView = new ChoiceBox<>();
        this.nSuitsView.getItems().addAll("1 Suit", "2 Suits", "4 Suits");
        this.nSuitsView.setValue("1 Suit");
        this.resetGameView = new Button("Reset Game");
        this.undoMoveView = new Button("Undo Move");
        this.controlView = new ToolBar(this.newGameView, this.nSuitsView, this.resetGameView, this.undoMoveView);

        // No cards dragged yet
        this.dragCardViews = null;

        // Add everything to game view
        this.gameView = new VBox();
        this.gameView.getChildren().addAll(this.controlView, this.boardView);
        this.setContent(this.gameView);

        // Set style of game
        this.setFitToWidth(true);
        this.gameView.setAlignment(Pos.TOP_CENTER);
        this.boardView.setMaxWidth(GameView.boardMaxWidth);
        VBox.setVgrow(this.columnsView, Priority.ALWAYS);

        // Set board event handlers
        this.gameView.setOnMouseDragged(event -> Controller.handleMouseDraggedGame(event));
        this.gameView.setOnMouseDragReleased(event -> {
            int toColumnIndex = IntStream.range(0, Game.getNColumns())
                    .mapToObj(i -> isInColumnView(i, event.getSceneX(), event.getSceneY()))
                    .toList()
                    .indexOf(true);
            if (toColumnIndex == -1) {
                Controller.handleMouseDragReleasedGame();
            } else {
                Controller.handleMouseDragReleasedColumn(toColumnIndex);
            }
        });
        this.gameView.setOnMouseClicked(event -> Controller.handleMouseClickedGame());
        IntStream.range(0, Game.getNColumns()).forEach(i -> {
            this.columnViews[i].setOnMouseClicked(event -> {
                event.consume();
                Controller.handleMouseClickedColumn(i);
            });
        });

        // Set control event handlers
        this.newGameView.setOnMouseClicked(event -> Controller.newGame(
            switch (this.nSuitsView.getValue()) {
                case "1 Suit" -> 1;
                case "2 Suits" -> 2;
                case "4 Suits" -> 4;
                default -> throw new UnsupportedOperationException("Invalid number of suits");
            }
        ));
        this.resetGameView.setOnMouseClicked(event -> Controller.resetGame());
        this.undoMoveView.setOnMouseClicked(event -> Controller.undoMove());
    }

    public void updateGameView() {
        // Add cards to locations
        IntStream.range(0, Game.getNColumns()).forEach(i -> {
            this.columnViews[i].getChildren().clear();
            IntStream.range(0, Game.getColumnLength(i))
                    .forEach(j -> this.columnViews[i].getChildren().add(new CardView("columns", i, j)));
        });
        this.runsView.getChildren().clear();
        IntStream.range(0, Game.getNRuns()).forEach(i -> {
            if (Game.getRunLength(i) > 0) {
                this.runsView.getChildren().add(new CardView("runs", i, 0));
            }
        });
        this.stocksView.getChildren().clear();
        IntStream.range(0, Game.getNStocks()).forEach(i -> {
            if (Game.getStockLength(i) > 0) {
                this.stocksView.getChildren().add(new CardView("stocks", i, 0));
            }
        });

        // Clear previously dragged cards
        if (this.dragCardViews != null) {
            this.gameView.getChildren().removeAll(this.dragCardViews);
        }
        this.dragCardViews = null;
    }

    public void updateCSS() {
        // Set overall positioning
        this.gameView.setMinHeight(this.getViewportBounds().getHeight());

        // Calculate board measurements
        double boardWidth = this.boardView.getWidth();
        double boardMaxWidthFraction = boardWidth / GameView.boardMaxWidth;
        double boardPadding = boardWidth / GameView.boardPaddingRatio;
        double boardContentWidth = boardWidth - 2 * boardPadding;

        // Set board and control measurements
        this.boardView.setMinHeight(boardWidth / GameView.boardMaxAspectRatio);
        this.boardView.setPadding(new Insets(boardPadding));

        // Calculate card and card space measurements
        double cardSpaceWidth = boardContentWidth / Game.getNColumns();
        double cardSpacePadding = boardWidth / GameView.cardSpacePaddingRatio;
        double cardWidth = cardSpaceWidth - 2 * cardSpacePadding;
        double cardHeight = cardWidth / GameView.cardAspectRatio;
        double cardSpaceHeight = cardHeight + 2 * cardSpacePadding;
        double runsContentWidth = GameView.runsMaxSpacing * boardMaxWidthFraction * (Game.getNRuns() - 1) + cardWidth;
        double stocksContentWidth = GameView.stocksMaxSpacing * boardMaxWidthFraction * (Game.getNStocks() - 1)
                + cardWidth;

        // Set measurements of locations and cards
        IntStream.range(0, Game.getNColumns()).forEach(i -> {
            VBox columnView = this.columnViews[i];
            columnView.setPrefWidth(cardSpaceWidth);
            columnView.setPadding(new Insets(cardSpacePadding));
            columnView.setSpacing(GameView.columnMaxSpacing * boardMaxWidthFraction - cardHeight);
            IntStream.range(0, this.columnViews[i].getChildren().size()).forEach(j -> {
                CardView cardView = (CardView) columnView.getChildren().get(j);
                cardView.updateCSS(cardHeight, cardWidth, boardMaxWidthFraction);
            });
        });
        this.runsView.setPrefHeight(cardSpaceHeight);
        this.runsView.setPrefWidth(runsContentWidth + 2 * cardSpacePadding);
        this.runsView.setPadding(new Insets(cardSpacePadding));
        this.runsView.setSpacing(GameView.runsMaxSpacing * boardMaxWidthFraction - cardWidth);
        IntStream.range(0, Game.getNRuns()).forEach(i -> {
            if (Game.getRunLength(i) > 0) {
                CardView cardView = (CardView) this.runsView.getChildren().get(i);
                cardView.updateCSS(cardHeight, cardWidth, boardMaxWidthFraction);
            }
        });
        this.stocksView.setPrefHeight(cardSpaceHeight);
        this.stocksView.setPrefWidth(stocksContentWidth + 2 * cardSpacePadding);
        this.stocksView.setPadding(new Insets(cardSpacePadding));
        this.stocksView.setSpacing(GameView.stocksMaxSpacing * boardMaxWidthFraction - cardWidth);
        IntStream.range(0, Game.getNStocks()).forEach(i -> {
            if (Game.getStockLength(i) > 0) {
                CardView cardView = (CardView) this.stocksView.getChildren().get(i);
                cardView.updateCSS(cardHeight, cardWidth, boardMaxWidthFraction);
            }
        });

        // Calculate control measurements
        double controlFontSize = GameView.controlMaxFontSize * boardMaxWidthFraction;

        // Set control measurements
        this.controlView.setPadding(new Insets(boardPadding));
        this.newGameView.setStyle("-fx-font-size: " + controlFontSize + "px;");
        this.nSuitsView.setStyle("-fx-font-size: " + controlFontSize + "px;");
        this.resetGameView.setStyle("-fx-font-size: " + controlFontSize + "px;");
        this.undoMoveView.setStyle("-fx-font-size: " + controlFontSize + "px;");

        // Set positions of dragged cards
        if (Controller.getClientPosition() != null) {
            int moveColumnIndex = Controller.getMoveColumnIndex();
            int moveStackIndex = Controller.getMoveStackIndex();
            VBox columnView = this.columnViews[moveColumnIndex];
            int columnViewLength = columnView.getChildren().size();

            // Allow absolute positioning of dragged cards if not done already
            if (columnViewLength > moveStackIndex) {
                int nDragCardViews = columnViewLength - moveStackIndex;
                this.dragCardViews = new CardView[nDragCardViews];
                IntStream.range(0, nDragCardViews).forEach(i -> {
                    CardView cardView = (CardView) columnView.getChildren().removeLast();
                    cardView.setManaged(false);
                    this.dragCardViews[nDragCardViews- i - 1] = cardView;
                });
                this.gameView.getChildren().addAll(this.dragCardViews);
            }

            // Update positions of dragged cards
            IntStream.range(0, this.dragCardViews.length)
                    .forEach(i -> this.dragCardViews[i].updateCSS(cardHeight, cardWidth, boardMaxWidthFraction));
        }

        // DEBUG
        this.gameView.setStyle("-fx-background-color: red;");
        this.controlView.setStyle("-fx-background-color: orange;");
        this.boardView.setStyle("-fx-background-color: yellow;");
        this.columnsView.setStyle("-fx-background-color: lime;");
        this.columnViews[0].setStyle("-fx-background-color: #00ff17;");
        this.columnViews[1].setStyle("-fx-background-color: #00ff2f;");
        this.columnViews[2].setStyle("-fx-background-color: #00ff45;");
        this.columnViews[3].setStyle("-fx-background-color: #00ff5d;");
        this.columnViews[4].setStyle("-fx-background-color: #00ff74;");
        this.columnViews[5].setStyle("-fx-background-color: #00ff8c;");
        this.columnViews[6].setStyle("-fx-background-color: #00ffa2;");
        this.columnViews[7].setStyle("-fx-background-color: #00ffba;");
        this.columnViews[8].setStyle("-fx-background-color: #00ffd1;");
        this.columnViews[9].setStyle("-fx-background-color: #00ffe9;");
        this.runsView.setStyle("-fx-background-color: cyan;");
        this.stocksView.setStyle("-fx-background-color: blue;");
    }

    private boolean isInColumnView(int columnIndex, double sceneX, double sceneY) {
        VBox columnView = this.columnViews[columnIndex];
        Bounds sceneBounds = columnView.localToScene(columnView.getBoundsInLocal());
        return sceneBounds.getMinX() <= sceneX
                && sceneBounds.getMaxX() >= sceneX
                && sceneBounds.getMinY() <= sceneY
                && sceneBounds.getMaxY() >= sceneY;
    }
}
