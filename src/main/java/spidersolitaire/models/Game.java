package spidersolitaire.models;

import java.util.ArrayList;
import java.util.stream.IntStream;

public final class Game {
    private final ArrayList<Card>[] columns;
    private final ArrayList<Card>[] runs;
    private final ArrayList<Card>[] stocks;

    private final ArrayList<ArrayList<MoveComponent>> moves;

    private static final int nColumns = 10;
    private static final int nRuns = 8;
    private static final int nStocks = 5;

    @SuppressWarnings("unchecked")
    public Game() {
        this.columns = new ArrayList[Game.nColumns];
        this.runs = new ArrayList[Game.nRuns];
        this.stocks = new ArrayList[Game.nStocks];
        this.moves = new ArrayList<>();
        IntStream.range(0, Game.nColumns).forEach(i -> this.columns[i] = new ArrayList<>());
        IntStream.range(0, Game.nRuns).forEach(i -> this.runs[i] = new ArrayList<>());
        IntStream.range(0, Game.nStocks).forEach(i -> this.stocks[i] = new ArrayList<>());
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

    public ArrayList<Card>[] getColumns() {
        return this.columns;
    }

    public ArrayList<Card>[] getRuns() {
        return this.runs;
    }

    public ArrayList<Card>[] getStocks() {
        return this.stocks;
    }

    ArrayList<ArrayList<MoveComponent>> getMoves() {
        return this.moves;
    }

    public Card getCard(String location, int locationIndex, int stackIndex) {
        return switch (location) {
            case "columns" -> this.columns[locationIndex].get(stackIndex);
            case "runs" -> this.runs[locationIndex].get(stackIndex);
            case "stocks" -> this.stocks[locationIndex].get(stackIndex);
            default -> throw new UnsupportedOperationException("Invalid Game location");
        };
    }
}
