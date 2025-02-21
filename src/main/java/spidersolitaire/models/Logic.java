package spidersolitaire.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;

public final class Logic {
    private static final int nCards = 52 * 2;
    private static final int nRanks = 13;

    private Logic() {
        throw new UnsupportedOperationException("Cannot instantiate class Logic");
    }

    public static void createInitialGame(int nSuits) {
        Logic.createInitialBoard(nSuits);
        Game.getMoves().clear();
    }

    public static int getMinMovableStackIndex(int columnIndex) {
        ArrayList<Card> column = Game.getColumns()[columnIndex];
        boolean[] inOrder = new boolean[column.size()];
        IntStream.range(0, column.size())
                .forEach(i -> inOrder[i] = i == 0 || Logic.isInOrder(column.get(i), column.get(i - 1)));
        return IntStream.range(0, column.size()).filter(i -> !inOrder[i]).max().orElse(0);
    }

    public static boolean isValidColumnMove(int fromColumnIndex, int fromStackIndex, int toColumnIndex) {
        ArrayList<Card> toColumn = Game.getColumns()[toColumnIndex];
        Card cardLower = Game.getCard("columns", fromColumnIndex, fromStackIndex);
        return fromColumnIndex != toColumnIndex
                && (toColumn.isEmpty() || Logic.canStack(cardLower, toColumn.getLast()));
    }

    public static boolean isValidStockMove() {
        return IntStream.range(0, Game.getNColumns()).map(i -> Game.getColumns()[i].isEmpty() ? 1 : 0).sum() == 0;
    }

    public static void makeColumnMove(int fromColumnIndex, int fromStackIndex, int toColumnIndex) {
        // Overall move
        ArrayList<MoveComponent> move = new ArrayList<>();

        // Column move component
        int toStackIndex = Game.getColumns()[toColumnIndex].size();
        int[] columnMoveColumnIndices = {fromColumnIndex, toColumnIndex};
        int[] columnMoveStackIndices = {fromStackIndex, toStackIndex};
        move.add(new MoveComponent("column", columnMoveColumnIndices, columnMoveStackIndices));
        move.getLast().forward();

        // Run move component, if exists
        if (Logic.hasRun(toColumnIndex)) {
            int[] runMoveColumnIndices = {toColumnIndex};
            int[] runMoveStackIndices = {Game.getColumns()[toColumnIndex].size() - Logic.nRanks};
            move.add(new MoveComponent("run", runMoveColumnIndices, runMoveStackIndices));
            move.getLast().forward();
        }

        // Flip move components, if any
        IntStream.of(columnMoveColumnIndices).forEach(i -> {
            if (Logic.noneFaceUp(i)) {
                int[] flipMoveColumnIndices = {i};
                int[] flipMoveStackIndices = {Game.getColumns()[i].size() - 1};
                move.add(new MoveComponent("flip", flipMoveColumnIndices, flipMoveStackIndices));
                move.getLast().forward();
            }
        });

        // Save move
        Game.getMoves().add(move);
    }

    public static void makeStockMove() {
        // Overall move
        ArrayList<MoveComponent> move = new ArrayList<>();

        // Stock move component
        move.add(new MoveComponent("stock", null, null));
        move.getLast().forward();

        // Run move components, if any
        IntStream.range(0, Game.getNColumns()).forEach(i -> {
            if (Logic.hasRun(i)) {
                int[] runMoveColumnIndices = {i};
                int[] runMoveStackIndices = {Game.getColumns()[i].size() - Logic.nRanks};
                move.add(new MoveComponent("run", runMoveColumnIndices, runMoveStackIndices));
                move.getLast().forward();
            }
        });

        // Flip move components, if any
        IntStream.range(0, Game.getNColumns()).forEach(i -> {
            if (Logic.noneFaceUp(i)) {
                int[] flipMoveColumnIndices = {i};
                int[] flipMoveStackIndices = {Game.getColumns()[i].size() - 1};
                move.add(new MoveComponent("flip", flipMoveColumnIndices, flipMoveStackIndices));
                move.getLast().forward();
            }
        });

        // Save move
        Game.getMoves().add(move);
    }

    public static void undoLastMove() {
        ArrayList<MoveComponent> move = Game.getMoves().removeLast();
        IntStream.range(0, move.size()).map(i -> move.size() - i - 1).forEach(i -> move.get(i).reverse());
    }

    public static void resetGame() {
        IntStream.range(0, Game.getMoves().size()).forEach(i -> Logic.undoLastMove());
    }

    private static void createInitialBoard(int nSuits) {
        // Shuffle and split deck
        Card[] deck = Logic.shuffleDeck(nSuits);
        int deckSplit = Logic.nCards - (Game.getNStocks() * Game.getNColumns());

        // Add cards to columns
        IntStream.range(0, Game.getNColumns()).forEach(i -> Game.getColumns()[i] = new ArrayList<>());
        IntStream.range(0, deckSplit).forEach(i -> Game.getColumns()[i % Game.getNColumns()].add(deck[i]));
        IntStream.range(0, Game.getNColumns()).forEach(i -> Game.getColumns()[i].getLast().setFaceUp(true));

        // Add empty runs
        IntStream.range(0, Game.getNRuns()).forEach(i -> Game.getRuns()[i] = new ArrayList<>());

        // Add cards to stocks
        IntStream.range(0, Game.getNStocks()).forEach(i -> Game.getStocks()[i] = new ArrayList<>());
        IntStream.range(deckSplit, Logic.nCards)
                .forEach(i -> Game.getStocks()[(i - deckSplit) % Game.getNStocks()]
                .add(deck[i]));
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

    private static boolean hasRun(int columnIndex) {
        return Game.getColumns()[columnIndex].size() - Logic.getMinMovableStackIndex(columnIndex) == Logic.nRanks;
    }

    private static boolean noneFaceUp(int columnIndex) {
        ArrayList<Card> column = Game.getColumns()[columnIndex];
        return !column.isEmpty() && column.stream().mapToInt(card -> card.getFaceUp() ? 1 : 0).sum() == 0;
    }
}
