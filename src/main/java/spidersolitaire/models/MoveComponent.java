package spidersolitaire.models;

import java.util.ArrayList;
import java.util.stream.IntStream;

final class MoveComponent {
    private final Game game;

    // Move component types include "column", "flip", "run", and "stock"
    private final String type;

    private final int[] columnIndices;
    private final int[] stackIndices;

    MoveComponent(Game game, String type, int[] columnIndices, int[] stackIndices) {
        this.game = game;
        this.type = type;
        this.columnIndices = columnIndices;
        this.stackIndices = stackIndices;
    }

    void forward() {
        switch (this.type) {
            case "column" -> {
                ArrayList<Card> cards = this.moveFromColumn(this.columnIndices[0], this.stackIndices[0]);
                this.moveToColumn(cards, this.columnIndices[1]);
            }
            case "flip" -> {
                this.game.getCard("columns", this.columnIndices[0], this.stackIndices[0]).setFaceUp(true);
            }
            case "run" -> {
                ArrayList<Card> cards = this.moveFromColumn(this.columnIndices[0], this.stackIndices[0]);
                this.moveToLastRun(cards);
            }
            case "stock" -> {
                int nColumns = Game.getNColumns();
                ArrayList<Card> cards = this.moveFromLastStock();
                IntStream.range(0, cards.size()).forEach(i -> cards.get(i).setFaceUp(true));
                IntStream.range(0, nColumns).forEach(i -> this.game.getColumns()[i].add(cards.get(i)));
            }
            default -> {
                throw new UnsupportedOperationException("Invalid MoveComponent type");
            }
        }
    }

    void reverse() {
        switch (this.type) {
            case "column" -> {
                ArrayList<Card> cards = this.moveFromColumn(this.columnIndices[1], this.stackIndices[1]);
                this.moveToColumn(cards, this.columnIndices[0]);
            }
            case "flip" -> {
                this.game.getCard("columns", this.columnIndices[0], this.stackIndices[0]).setFaceUp(false);
            }
            case "run" -> {
                ArrayList<Card> cards = this.moveFromLastRun();
                this.moveToColumn(cards, this.columnIndices[0]);
            }
            case "stock" -> {
                int nColumns = Game.getNColumns();
                ArrayList<Card> cards = new ArrayList<>();
                IntStream.range(0, nColumns).forEach(i -> cards.add(this.game.getColumns()[i].removeLast()));
                IntStream.range(0, cards.size()).forEach(i -> cards.get(i).setFaceUp(false));
                this.moveToLastStock(cards);
            }
            default -> {
                throw new UnsupportedOperationException("Invalid MoveComponent type");
            }
        }
    }

    private ArrayList<Card> moveFromColumn(int columnIndex, int stackIndex) {
        ArrayList<Card> column = this.game.getColumns()[columnIndex];
        ArrayList<Card> cards = new ArrayList<>(column.subList(stackIndex, column.size()));
        column.subList(stackIndex, column.size()).clear();
        return cards;
    }

    private void moveToColumn(ArrayList<Card> cards, int columnIndex) {
        this.game.getColumns()[columnIndex].addAll(cards);
    }

    private ArrayList<Card> moveFromLastRun() {
        int nRuns = Game.getNRuns();
        int runIndex = IntStream.range(0, nRuns).map(i -> this.game.getRuns()[i].isEmpty() ? 0 : 1).sum() - 1;
        ArrayList<Card> run = this.game.getRuns()[runIndex];
        ArrayList<Card> cards = new ArrayList<>(run);
        run.clear();
        return cards;
    }

    private void moveToLastRun(ArrayList<Card> cards) {
        int nRuns = Game.getNRuns();
        int runIndex = IntStream.range(0, nRuns).map(i -> this.game.getRuns()[i].isEmpty() ? 0 : 1).sum();
        this.game.getRuns()[runIndex].addAll(cards);
    }

    private ArrayList<Card> moveFromLastStock() {
        int nStocks = Game.getNStocks();
        int stockIndex = IntStream.range(0, nStocks).map(i -> this.game.getStocks()[i].isEmpty() ? 0 : 1).sum() - 1;
        ArrayList<Card> stock = this.game.getStocks()[stockIndex];
        ArrayList<Card> cards = new ArrayList<>(stock);
        stock.clear();
        return cards;
    }

    private void moveToLastStock(ArrayList<Card> cards) {
        int nStocks = Game.getNStocks();
        int stockIndex = IntStream.range(0, nStocks).map(i -> this.game.getStocks()[i].isEmpty() ? 0 : 1).sum();
        this.game.getStocks()[stockIndex].addAll(cards);
    }
}
