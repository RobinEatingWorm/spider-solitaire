package spidersolitaire.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;

public final class Logic {
    private final Game game;

    private static final int nCards = 52 * 2;
    private static final int nRanks = 13;

    public Logic(Game game) {
        this.game = game;
    }

    public void newGame(int nSuits) {
        // Get number of locations
        int nColumns = Game.getNColumns();
        int nRuns = Game.getNRuns();
        int nStocks = Game.getNStocks();

        // Clear cards from board
        IntStream.range(0, nColumns).forEach(i -> this.game.getColumns()[i].clear());
        IntStream.range(0, nRuns).forEach(i -> this.game.getRuns()[i].clear());
        IntStream.range(0, nStocks).forEach(i -> this.game.getStocks()[i].clear());

        // Clear move history
        this.game.getMoves().clear();

        // Shuffle and split decks
        Card[] deck = Logic.shuffleDeck(nSuits);
        int deckSplit = Logic.nCards - (nStocks * nColumns);

        // Add cards to columns and stocks
        IntStream.range(0, deckSplit).forEach(i -> this.game.getColumns()[i % nColumns].add(deck[i]));
        IntStream.range(0, nColumns).forEach(i -> this.game.getColumns()[i].getLast().setFaceUp(true));
        IntStream.range(deckSplit, Logic.nCards)
                .forEach(i -> this.game.getStocks()[(i - deckSplit) % nStocks].add(deck[i]));
    }

    public int getMinMovableStackIndex(int columnIndex) {
        ArrayList<Card> column = this.game.getColumns()[columnIndex];
        boolean[] inOrder = new boolean[column.size()];
        IntStream.range(0, column.size())
                .forEach(i -> inOrder[i] = i == 0 || Logic.isInOrder(column.get(i), column.get(i - 1)));
        return IntStream.range(0, column.size()).filter(i -> !inOrder[i]).max().orElse(0);
    }

    public boolean isValidColumnMove(int fromColumnIndex, int fromStackIndex, int toColumnIndex) {
        ArrayList<Card> toColumn = this.game.getColumns()[toColumnIndex];
        Card cardLower = this.game.getCard("columns", fromColumnIndex, fromStackIndex);
        return fromColumnIndex != toColumnIndex
                && (toColumn.isEmpty() || Logic.canStack(cardLower, toColumn.getLast()));
    }

    public boolean isValidStockMove() {
        int nColumns = Game.getNColumns();
        return IntStream.range(0, nColumns).map(i -> this.game.getColumns()[i].isEmpty() ? 1 : 0).sum() == 0;
    }

    public void makeColumnMove(int fromColumnIndex, int fromStackIndex, int toColumnIndex) {
        // Overall move
        ArrayList<MoveComponent> move = new ArrayList<>();

        // Column move component
        int toStackIndex = this.game.getColumns()[toColumnIndex].size();
        int[] columnMoveColumnIndices = {fromColumnIndex, toColumnIndex};
        int[] columnMoveStackIndices = {fromStackIndex, toStackIndex};
        move.add(new MoveComponent(this.game, "column", columnMoveColumnIndices, columnMoveStackIndices));
        move.getLast().forward();

        // Run move component, if one exists
        if (this.hasRun(toColumnIndex)) {
            int[] runMoveColumnIndices = {toColumnIndex};
            int[] runMoveStackIndices = {this.game.getColumns()[toColumnIndex].size() - Logic.nRanks};
            move.add(new MoveComponent(this.game, "run", runMoveColumnIndices, runMoveStackIndices));
            move.getLast().forward();
        }

        // Flip move components, if any
        for (int i : columnMoveColumnIndices) {
            if (this.noneFaceUp(i)) {
                int[] flipMoveColumnIndices = {i};
                int[] flipMoveStackIndices = {this.game.getColumns()[i].size() - 1};
                move.add(new MoveComponent(this.game, "flip", flipMoveColumnIndices, flipMoveStackIndices));
                move.getLast().forward();
            }
        }

        // Save move
        this.game.getMoves().add(move);
    }

    public void makeStockMove() {
        // Overall move
        ArrayList<MoveComponent> move = new ArrayList<>();

        // Stock move component
        move.add(new MoveComponent(this.game, "stock", null, null));
        move.getLast().forward();

        // Run move components, if any
        for (int i = 0; i < Game.getNColumns(); i++) {
            if (this.hasRun(i)) {
                int[] runMoveColumnIndices = {i};
                int[] runMoveStackIndices = {this.game.getColumns()[i].size() - Logic.nRanks};
                move.add(new MoveComponent(this.game, "run", runMoveColumnIndices, runMoveStackIndices));
                move.getLast().forward();
            }
        }

        // Flip move components, if any
        for (int i = 0; i < Game.getNColumns(); i++) {
            if (this.noneFaceUp(i)) {
                int[] flipMoveColumnIndices = {i};
                int[] flipMoveStackIndices = {this.game.getColumns()[i].size() - 1};
                move.add(new MoveComponent(this.game, "flip", flipMoveColumnIndices, flipMoveStackIndices));
                move.getLast().forward();
            }
        }

        // Save move
        this.game.getMoves().add(move);
    }

    public void undoMove() {
        if (!this.game.getMoves().isEmpty()) {
            ArrayList<MoveComponent> move = this.game.getMoves().removeLast();
            IntStream.range(0, move.size()).map(i -> move.size() - i - 1).forEach(i -> move.get(i).reverse());
        }
    }

    public void resetGame() {
        IntStream.range(0, this.game.getMoves().size()).forEach(i -> this.undoMove());
    }

    private static Card[] shuffleDeck(int nSuits) {
        double[] keys = new Random().doubles(Logic.nCards).toArray();
        Integer[] indices = IntStream.range(0, Logic.nCards).boxed().toArray(Integer[]::new);
        Arrays.sort(indices, Comparator.comparingDouble(i -> keys[i]));
        return Arrays.stream(indices)
                .map(i -> new Card((i % Logic.nRanks) + 1, i % nSuits, false))
                .toArray(Card[]::new);
    }

    private static boolean isInOrder(Card cardLower, Card cardUpper) {
        return Logic.canStack(cardLower, cardUpper) && cardLower.getSuit() == cardUpper.getSuit();
    }

    private static boolean canStack(Card cardLower, Card cardUpper) {
        return cardLower.getRank() + 1 == cardUpper.getRank() && cardLower.getFaceUp() && cardUpper.getFaceUp();
    }

    private boolean hasRun(int columnIndex) {
        return this.game.getColumns()[columnIndex].size() - this.getMinMovableStackIndex(columnIndex) == Logic.nRanks;
    }

    private boolean noneFaceUp(int columnIndex) {
        ArrayList<Card> column = this.game.getColumns()[columnIndex];
        return !column.isEmpty() && column.stream().mapToInt(card -> card.getFaceUp() ? 1 : 0).sum() == 0;
    }
}
