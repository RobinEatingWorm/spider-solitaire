package spidersolitaire.views.elements;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.stream.IntStream;

import spidersolitaire.constants.Constants;
import spidersolitaire.controllers.GameControllerInputBoundary;
import spidersolitaire.models.Board;


public class BoardElement {
    private final Pane node;

    private final ColumnElement[] columnElements;
    private final RunElement[] runElements;
    private final StockElement[] stockElements;

    public BoardElement() {
        this.node = new VBox();
        this.columnElements = new ColumnElement[Constants.N_COLUMNS];
        this.runElements = new RunElement[Constants.N_RUNS];
        this.stockElements = new StockElement[Constants.N_STOCKS];
        IntStream.range(0, Constants.N_COLUMNS).forEach(i -> this.columnElements[i] = new ColumnElement());
        IntStream.range(0, Constants.N_RUNS).forEach(i -> this.runElements[i] = new RunElement());
        IntStream.range(0, Constants.N_STOCKS).forEach(i -> this.stockElements[i] = new StockElement());

        this.node.setId("board-element");
        this.node.setMaxWidth(1280);
    }

    public void setInputBoundary(GameControllerInputBoundary inputBoundary) {
        IntStream.range(0, Constants.N_COLUMNS).forEach(i -> this.columnElements[i].setInputBoundary(inputBoundary));
        IntStream.range(0, Constants.N_STOCKS).forEach(i -> this.stockElements[i].setInputBoundary(inputBoundary));
    }

    public Pane getNode() {
        return this.node;
    }

    public void setNode(Board board) {
        this.node.getChildren().clear();
        HBox boardColumns = new HBox();
        HBox boardRuns = new HBox();
        HBox boardStocks = new HBox();
        IntStream.range(0, Constants.N_COLUMNS)
                .forEach(i -> this.columnElements[i].setNode(board.getColumn(i)));
        IntStream.range(0, Constants.N_RUNS).forEach(i -> this.runElements[i].setNode(board.getRun(i)));
        IntStream.range(0, Constants.N_STOCKS)
                .forEach(i -> this.stockElements[i].setNode(board.getStock(i)));
        IntStream.range(0, Constants.N_COLUMNS)
                .forEach(i -> boardColumns.getChildren().add(this.columnElements[i].getNode()));
        IntStream.range(0, Constants.N_RUNS).forEach(i -> boardRuns.getChildren().add(this.runElements[i].getNode()));
        IntStream.range(0, Constants.N_STOCKS)
                .forEach(i -> boardStocks.getChildren().add(this.stockElements[i].getNode()));
        HBox boardBottom = new HBox();
        boardBottom.getChildren().addAll(boardRuns, boardStocks);
        this.node.getChildren().addAll(boardColumns, boardBottom);
    }

    public void updateCSS(double gameViewWidth) {
        double boardViewWidth = Math.min(gameViewWidth, 1280);
        this.node.setPrefWidth(gameViewWidth);
        System.out.println(this.node.getWidth());
        this.node.setStyle("-fx-background-color: orange;");
        IntStream.range(0, Constants.N_COLUMNS).forEach(i -> this.columnElements[i].updateCSS(boardViewWidth));
        IntStream.range(0, Constants.N_RUNS).forEach(i -> this.runElements[i].updateCSS(boardViewWidth));
        IntStream.range(0, Constants.N_STOCKS).forEach(i -> this.stockElements[i].updateCSS(boardViewWidth));
    }
}
