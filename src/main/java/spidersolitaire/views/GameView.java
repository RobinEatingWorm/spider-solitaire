package spidersolitaire.views;

import java.util.stream.IntStream;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import spidersolitaire.adapters.Controller;
import spidersolitaire.adapters.GameViewModel;
import spidersolitaire.adapters.GameViewModelObserver;

public final class GameView extends ScrollPane implements GameViewModelObserver {
    private final Controller controller;

    private final VBox game;
    private final VBox board;
    private final ToolBar control;

    private final VBox[] columns;
    private final HBox runs;
    private final HBox stocks;

    private final Button newGame;
    private final ChoiceBox<String> nSuits;
    private final Button resetGame;
    private final Button undoMove;
    private final Text win;

    private CardView[] draggedCards;

    private static final double boardMaxAspectRatio = 16.0 / 9.0;
    private static final double boardMaxWidth = 1280;
    private static final double boardPaddingRatio = 100;
    private static final double cardAspectRatio = 2.5 / 3.5;
    private static final double cardSpacePaddingRatio = 100;
    private static final double columnMaxSpacing = 32;
    private static final double controlMaxFontSize = 24;
    private static final double runsMaxSpacing = 32;
    private static final double stocksMaxSpacing = 32;

    public GameView(Controller controller) {
        super();
        this.controller = controller;

        // Set up locations
        this.columns = new VBox[GameViewModel.getNColumns()];
        IntStream.range(0, GameViewModel.getNColumns()).forEach(i -> this.columns[i] = new VBox());
        this.runs = new HBox();
        this.stocks = new HBox();
        
        // Set up board
        HBox boardColumns = new HBox(this.columns);
        Region boardBottomLeftSpacer = new Region();
        Region boardBottomCenterLeftSpacer = new Region();
        Region boardBottomCenterRightSpacer = new Region();
        Region boardBottomRightSpacer = new Region();
        HBox.setHgrow(boardBottomLeftSpacer, Priority.ALWAYS);
        HBox.setHgrow(boardBottomCenterLeftSpacer, Priority.ALWAYS);
        HBox.setHgrow(boardBottomCenterRightSpacer, Priority.ALWAYS);
        HBox.setHgrow(boardBottomRightSpacer, Priority.ALWAYS);
        HBox boardBottom = new HBox(
            boardBottomLeftSpacer,
            this.runs,
            boardBottomCenterLeftSpacer,
            boardBottomCenterRightSpacer,
            this.stocks,
            boardBottomRightSpacer
        );
        this.board = new VBox(boardColumns, boardBottom);
        
        // Set up control
        this.newGame = new Button("New Game");
        this.nSuits = new ChoiceBox<>();
        this.nSuits.getItems().addAll("1 Suit", "2 Suits", "4 Suits");
        this.nSuits.setValue("1 Suit");
        this.resetGame = new Button("Reset Game");
        this.undoMove = new Button("Undo Move");
        this.win = new Text();
        this.control = new ToolBar(this.newGame, this.nSuits, this.resetGame, this.undoMove, new Separator(), this.win);

        // Set up game
        this.game = new VBox(this.control, this.board);
        this.setContent(this.game);

        // No dragged cards yet
        this.draggedCards = new CardView[0];

        // Set overall measurements
        this.setFitToWidth(true);
        this.game.setAlignment(Pos.TOP_CENTER);
        this.board.setMaxWidth(GameView.boardMaxWidth);
        VBox.setVgrow(boardColumns, Priority.ALWAYS);

        // Set board event handlers
        this.game.setOnMouseDragged(event -> this.controller.handleMouseDraggedGame(event));
        this.game.setOnMouseDragReleased(event -> {
            int toColumnIndex = IntStream.range(0, GameViewModel.getNColumns())
                    .mapToObj(i -> isInColumn(i, event.getSceneX(), event.getSceneY()))
                    .toList()
                    .indexOf(true);
            if (toColumnIndex == -1) {
                this.controller.handleMouseDragReleasedGame();
            } else {
                this.controller.handleMouseDragReleasedColumn(toColumnIndex);
            }
        });
        this.game.setOnMouseClicked(event -> this.controller.handleMouseClickedGame());
        IntStream.range(0, GameViewModel.getNColumns()).forEach(i -> this.columns[i].setOnMouseClicked(event -> {
            event.consume();
            this.controller.handleMouseClickedColumn(i);
        }));

        // Set control event handlers
        this.newGame.setOnMouseClicked(event -> this.controller.handleNewGame(
            switch (this.nSuits.getValue()) {
                case "1 Suit" -> 1;
                case "2 Suits" -> 2;
                case "4 Suits" -> 4;
                default -> throw new UnsupportedOperationException("Invalid GameView nSuits");
            }
        ));
        this.resetGame.setOnMouseClicked(event -> this.controller.handleResetGame());
        this.undoMove.setOnMouseClicked(event -> this.controller.handleUndoMove());
    }

    @Override
    public void onGameStateUpdate(GameViewModel gameViewModel) {
        this.updateContent(gameViewModel);
        this.updateVisuals(gameViewModel);
        this.updateMeasurements();
    }

    @Override
    public void onMoveStateUpdate(GameViewModel gameViewModel) {
        this.updateVisuals(gameViewModel);
    }

    void updateContent(GameViewModel gameViewModel) {
        // Replace cards in locations
        for (int i = 0; i < GameViewModel.getNColumns(); i++) {
            this.columns[i].getChildren().clear();
            for (int j = 0; j < gameViewModel.getColumns()[i].size(); j++) {
                CardView cardView = new CardView(gameViewModel.getCard("columns", i, j), this.controller);
                this.columns[i].getChildren().add(cardView);
            }
        }
        this.runs.getChildren().clear();
        for (int i = 0; i < GameViewModel.getNRuns(); i++) {
            if (gameViewModel.getRuns()[i].size() > 0) {
                CardView cardView = new CardView(gameViewModel.getCard("runs", i, 0), this.controller);
                this.runs.getChildren().add(cardView);
            }
        }
        this.stocks.getChildren().clear();
        for (int i = 0; i < GameViewModel.getNStocks(); i++) {
            if (gameViewModel.getStocks()[i].size() > 0) {
                CardView cardView = new CardView(gameViewModel.getCard("stocks", i, 0), this.controller);
                this.stocks.getChildren().add(cardView);
            }
        }

        // Clear previously dragged cards
        if (this.draggedCards != null) {
            this.game.getChildren().removeAll(this.draggedCards);
            this.draggedCards = new CardView[0];
        }
    }

    public void updateMeasurements() {
        // Set overall positioning
        this.game.setMinHeight(this.getViewportBounds().getHeight());

        // Calculate board measurements
        double boardWidth = this.board.getWidth();
        double boardWidthFraction = boardWidth / GameView.boardMaxWidth;
        double boardPadding = boardWidth / GameView.boardPaddingRatio;
        double boardContentWidth = boardWidth - 2 * boardPadding;

        // Set board measurements
        this.board.setMinHeight(boardWidth / GameView.boardMaxAspectRatio);
        this.board.setPadding(new Insets(boardPadding));

        // Calculate card and space measurements
        double cardSpaceWidth = boardContentWidth / GameViewModel.getNColumns();
        double cardSpacePadding = boardWidth / GameView.cardSpacePaddingRatio;
        double cardWidth = cardSpaceWidth - 2 * cardSpacePadding;
        double cardHeight = cardWidth / GameView.cardAspectRatio;
        double cardSpaceHeight = cardHeight + 2 * cardSpacePadding;
        double runsContentWidth = GameView.runsMaxSpacing * boardWidthFraction * (GameViewModel.getNRuns() - 1)
                + cardWidth;
        double stocksContentWidth = GameView.stocksMaxSpacing * boardWidthFraction * (GameViewModel.getNStocks() - 1)
                + cardWidth;
        
        // Set measurements of locations and cards
        for (int i = 0; i < GameViewModel.getNColumns(); i++) {
            VBox column = this.columns[i];
            column.setPrefWidth(cardSpaceWidth);
            column.setPadding(new Insets(cardSpacePadding));
            column.setSpacing(GameView.columnMaxSpacing * boardWidthFraction - cardHeight);
            for (int j = 0; j < column.getChildren().size(); j++) {
                CardView cardView = (CardView) column.getChildren().get(j);
                cardView.updateMeasurements(cardHeight, cardWidth, boardWidthFraction);
            }
        }
        this.runs.setPrefHeight(cardSpaceHeight);
        this.runs.setPrefWidth(runsContentWidth + 2 * cardSpacePadding);
        this.runs.setPadding(new Insets(cardSpacePadding));
        this.runs.setSpacing(GameView.runsMaxSpacing * boardWidthFraction - cardWidth);
        for (int i = 0; i < this.runs.getChildren().size(); i++) {
            CardView cardView = (CardView) this.runs.getChildren().get(i);
            cardView.updateMeasurements(cardHeight, cardWidth, boardWidthFraction);
        }
        this.stocks.setPrefHeight(cardSpaceHeight);
        this.stocks.setPrefWidth(stocksContentWidth + 2 * cardSpacePadding);
        this.stocks.setPadding(new Insets(cardSpacePadding));
        this.stocks.setSpacing(GameView.stocksMaxSpacing * boardWidthFraction - cardWidth);
        for (int i = 0; i < this.stocks.getChildren().size(); i++) {
            CardView cardView = (CardView) this.stocks.getChildren().get(i);
            cardView.updateMeasurements(cardHeight, cardWidth, boardWidthFraction);
        }

        // Calculate control measurements
        double controlFontSize = GameView.controlMaxFontSize * boardWidthFraction;

        // Set control measurements
        this.control.setPadding(new Insets(boardPadding));
        this.newGame.setFont(new Font(controlFontSize));
        this.nSuits.setStyle("-fx-font-size: " + controlFontSize + "px;");
        this.resetGame.setFont(new Font(controlFontSize));
        this.undoMove.setFont(new Font(controlFontSize));
        this.win.setFont(new Font(controlFontSize));

        // Set measurements of dragged cards
        for (int i = 0; i < this.draggedCards.length; i++) {
            CardView cardView = this.draggedCards[i];
            cardView.updateMeasurements(cardHeight, cardWidth, boardWidthFraction);
        }
    }

    void updateVisuals(GameViewModel gameViewModel) {
        // Get the client position, if any
        double[] clientPosition = gameViewModel.getClientPosition();

        // Set visuals of cards depending on the move
        for (int i = 0; i < GameViewModel.getNColumns(); i++) {
            VBox column = this.columns[i];
            for (int j = 0; j < column.getChildren().size(); j++) {
                CardView cardView = (CardView) column.getChildren().get(j);
                String location = cardView.getLocation();
                int locationIndex = cardView.getLocationIndex();
                int stackIndex = cardView.getStackIndex();
                boolean isInClickMove = gameViewModel.isInClickMove(location, locationIndex, stackIndex);
                boolean isInDragMove = gameViewModel.isInDragMove(location, locationIndex, stackIndex);
                cardView.updateVisuals(isInClickMove, isInDragMove, clientPosition);
            }
        }
        for (int i = 0; i < this.runs.getChildren().size(); i++) {
            CardView cardView = (CardView) this.runs.getChildren().get(i);
            cardView.updateVisuals(false, false, clientPosition);
        }
        for (int i = 0; i < this.stocks.getChildren().size(); i++) {
            CardView cardView = (CardView) this.stocks.getChildren().get(i);
            cardView.updateVisuals(false, false, clientPosition);
        }

        // Detach dragged cards present in columns, if any
        if (clientPosition != null) {
            int moveColumnIndex = gameViewModel.getMoveColumnIndex();
            int moveStackIndex = gameViewModel.getMoveStackIndex();
            VBox column = this.columns[moveColumnIndex];
            int columnLength = column.getChildren().size();
            if (columnLength > moveStackIndex) {
                int nDraggedCards = columnLength - moveStackIndex;
                this.draggedCards = new CardView[nDraggedCards];
                for (int i = 0; i < nDraggedCards; i++) {
                    CardView cardView = (CardView) column.getChildren().removeLast();
                    cardView.setManaged(false);
                    this.draggedCards[nDraggedCards - i - 1] = cardView;
                }
                this.game.getChildren().addAll(this.draggedCards);
            }
        }

        // Set visuals and positions of dragged cards
        for (int i = 0; i < this.draggedCards.length; i++) {
            CardView cardView = this.draggedCards[i];
            cardView.updateVisuals(false, true, clientPosition);
        }

        // Set control win message
        this.win.setText(this.runs.getChildren().size() == GameViewModel.getNRuns() ? "You win!" : "");

        /*
        // DEBUG: Distinct background colors of each region
        this.game.setStyle("-fx-background-color: red;");
        this.control.setStyle("-fx-background-color: orange;");
        this.board.setStyle("-fx-background-color: yellow;");
        for (int i = 0; i < GameViewModel.getNColumns(); i++) {
            String b = Long.toHexString(Math.round(i * 255.0 / (GameViewModel.getNColumns() - 1)));
            this.columns[i].setStyle("-fx-background-color: #00ff" + (b.length() == 1 ? "0" + b : b) + ";");
        }
        this.runs.setStyle("-fx-background-color: blue;");
        this.stocks.setStyle("-fx-background-color: magenta;");
        */
    }

    private boolean isInColumn(int columnIndex, double sceneX, double sceneY) {
        VBox column = this.columns[columnIndex];
        Bounds sceneBounds = column.localToScene(column.getBoundsInLocal());
        return sceneBounds.getMinX() <= sceneX
                && sceneBounds.getMaxX() >= sceneX
                && sceneBounds.getMinY() <= sceneY
                && sceneBounds.getMaxY() >= sceneY;
    }
}
