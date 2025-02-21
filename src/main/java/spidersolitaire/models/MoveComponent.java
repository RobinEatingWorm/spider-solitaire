package spidersolitaire.models;

import java.util.ArrayList;
import java.util.stream.IntStream;

class MoveComponent {
    // Move component types include "column", "flip", "run", and "stock"
    private final String type;

    private final int[] columnIndices;
    private final int[] stackIndices;

    MoveComponent(String type, int[] columnIndices, int[] stackIndices) {
        this.type = type;
        this.columnIndices = columnIndices;
        this.stackIndices = stackIndices;
    }

    void forward() {
        switch (this.type) {
            case "column" -> {
                ArrayList<Card> cards = MoveComponent.moveFromColumn(this.columnIndices[0], this.stackIndices[0]);
                MoveComponent.moveToColumn(cards, this.columnIndices[1]);
            }
            case "flip" -> {
                Game.getCard("columns", this.columnIndices[0], this.stackIndices[0]).setFaceUp(true);
            }
            case "run" -> {
                ArrayList<Card> cards = MoveComponent.moveFromColumn(this.columnIndices[0], this.stackIndices[0]);
                MoveComponent.moveToLastRun(cards);
            }
            case "stock" -> {
                ArrayList<Card> cards = MoveComponent.moveFromLastStock();
                IntStream.range(0, cards.size()).forEach(i -> cards.get(i).setFaceUp(true));
                IntStream.range(0, Game.getNColumns()).forEach(i -> Game.getColumns()[i].add(cards.get(i)));
            }
            default -> {
                throw new UnsupportedOperationException("Invalid MoveComponent type");
            }
        }
    }

    void reverse() {
        switch (this.type) {
            case "column" -> {
                ArrayList<Card> cards = MoveComponent.moveFromColumn(this.columnIndices[1], this.stackIndices[1]);
                MoveComponent.moveToColumn(cards, this.columnIndices[0]);
            }
            case "flip" -> {
                Game.getCard("columns", this.columnIndices[0], this.stackIndices[0]).setFaceUp(false);
            }
            case "run" -> {
                ArrayList<Card> cards = MoveComponent.moveFromLastRun();
                MoveComponent.moveToColumn(cards, this.columnIndices[0]);
            }
            case "stock" -> {
                ArrayList<Card> cards = new ArrayList<>();
                IntStream.range(0, Game.getNColumns()).forEach(i -> cards.add(Game.getColumns()[i].removeLast()));
                IntStream.range(0, cards.size()).forEach(i -> cards.get(i).setFaceUp(false));
                MoveComponent.moveToLastStock(cards);
            }
            default -> {
                throw new UnsupportedOperationException("Invalid MoveComponent type");
            }
        }
    }

    private static ArrayList<Card> moveFromColumn(int columnIndex, int stackIndex) {
        ArrayList<Card> column = Game.getColumns()[columnIndex];
        ArrayList<Card> cards = new ArrayList<>(column.subList(stackIndex, column.size()));
        column.subList(stackIndex, column.size()).clear();
        return cards;
    }

    private static void moveToColumn(ArrayList<Card> cards, int columnIndex) {
        Game.getColumns()[columnIndex].addAll(cards);
    }

    private static ArrayList<Card> moveFromLastRun() {
        int runIndex = IntStream.range(0, Game.getNRuns()).map(i -> Game.getRuns()[i].isEmpty() ? 0 : 1).sum() - 1;
        ArrayList<Card> run = Game.getRuns()[runIndex];
        ArrayList<Card> cards = new ArrayList<>(run);
        run.clear();
        return cards;
    }

    private static void moveToLastRun(ArrayList<Card> cards) {
        int runIndex = IntStream.range(0, Game.getNRuns()).map(i -> Game.getRuns()[i].isEmpty() ? 0 : 1).sum();
        Game.getRuns()[runIndex].addAll(cards);
    }

    private static ArrayList<Card> moveFromLastStock() {
        int stockIndex = IntStream.range(0, Game.getNStocks())
                .map(i -> Game.getStocks()[i].isEmpty() ? 0 : 1)
                .sum() - 1;
        ArrayList<Card> stock = Game.getStocks()[stockIndex];
        ArrayList<Card> cards = new ArrayList<>(stock);
        stock.clear();
        return cards;
    }

    private static void moveToLastStock(ArrayList<Card> cards) {
        int stockIndex = IntStream.range(0, Game.getNStocks()).map(i -> Game.getStocks()[i].isEmpty() ? 0 : 1).sum();
        Game.getStocks()[stockIndex].addAll(cards);
    }
}
