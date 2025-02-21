package spidersolitaire.models;

import java.util.ArrayList;

public final class Game {
    private static final int nColumns = 10;
    private static final int nRuns = 8;
    private static final int nStocks = 5;

    @SuppressWarnings("unchecked")
    private static final ArrayList<Card>[] columns = new ArrayList[Game.nColumns];

    @SuppressWarnings("unchecked")
    private static final ArrayList<Card>[] runs = new ArrayList[Game.nRuns];

    @SuppressWarnings("unchecked")
    private static final ArrayList<Card>[] stocks = new ArrayList[Game.nStocks];

    private static final ArrayList<ArrayList<MoveComponent>> moves = new ArrayList<>();


    private Game() {
        throw new UnsupportedOperationException("Cannot instantiate class Game");
    }

    public static int getNColumns() {
        return Game.nColumns;
    }

    public static int getNRuns() {
        return Game.nRuns;
    }

    public static int getNStocks() {
        return Game.nStocks;
    }

    public static int getColumnLength(int columnIndex) {
        return Game.columns[columnIndex].size();
    }

    public static int getRunLength(int runIndex) {
        return Game.runs[runIndex].size();
    }

    public static int getStockLength(int stockIndex) {
        return Game.stocks[stockIndex].size();
    }

    static ArrayList<Card>[] getColumns() {
        return Game.columns;
    }

    static ArrayList<Card>[] getRuns() {
        return Game.runs;
    }

    static ArrayList<Card>[] getStocks() {
        return Game.stocks;
    }

    static ArrayList<ArrayList<MoveComponent>> getMoves() {
        return Game.moves;
    }

    public static Card getCard(String location, int locationIndex, int stackIndex) {
        return switch (location) {
            case "columns" -> Game.columns[locationIndex].get(stackIndex);
            case "runs" -> Game.runs[locationIndex].get(stackIndex);
            case "stocks" -> Game.stocks[locationIndex].get(stackIndex);
            default -> throw new UnsupportedOperationException("Invalid location");
        };
    }
}
