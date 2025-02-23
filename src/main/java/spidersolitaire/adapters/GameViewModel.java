package spidersolitaire.adapters;

import java.util.ArrayList;
import java.util.stream.IntStream;

import spidersolitaire.models.Game;

public final class GameViewModel {
    private final ArrayList<GameViewModelObserver> observers;

    private final ArrayList<CardViewModel>[] columns;
    private final ArrayList<CardViewModel>[] runs;
    private final ArrayList<CardViewModel>[] stocks;

    private Integer moveColumnIndex;
    private Integer moveStackIndex;
    private boolean pressRequest;
    private boolean clickFirst;
    private double[] clientPosition;

    @SuppressWarnings("unchecked")
    public GameViewModel() {
        this.observers = new ArrayList<>();
        this.columns = new ArrayList[Game.getNColumns()];
        this.runs = new ArrayList[Game.getNRuns()];
        this.stocks = new ArrayList[Game.getNStocks()];
        IntStream.range(0, Game.getNColumns()).forEach(i -> this.columns[i] = new ArrayList<>());
        IntStream.range(0, Game.getNRuns()).forEach(i -> this.runs[i] = new ArrayList<>());
        IntStream.range(0, Game.getNStocks()).forEach(i -> this.stocks[i] = new ArrayList<>());
        this.resetMoveState();
    }

    public void addObserver(GameViewModelObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(GameViewModelObserver observer) {
        this.observers.remove(observer);
    }

    public static int getNColumns() {
        return Game.getNColumns();
    }

    public static int getNRuns() {
        return Game.getNRuns();
    }

    public static int getNStocks() {
        return Game.getNStocks();
    }

    public ArrayList<CardViewModel>[] getColumns() {
        return this.columns;
    }

    public ArrayList<CardViewModel>[] getRuns() {
        return this.runs;
    }

    public ArrayList<CardViewModel>[] getStocks() {
        return this.stocks;
    }

    public CardViewModel getCard(String location, int locationIndex, int stackIndex) {
        return switch (location) {
            case "columns" -> this.columns[locationIndex].get(stackIndex);
            case "runs" -> this.runs[locationIndex].get(stackIndex);
            case "stocks" -> this.stocks[locationIndex].get(stackIndex);
            default -> throw new UnsupportedOperationException("Invalid GameViewModel location");
        };
    }

    public Integer getMoveColumnIndex() {
        return this.moveColumnIndex;
    }

    void setMoveColumnIndex(Integer moveColumnIndex) {
        this.moveColumnIndex = moveColumnIndex;
    }

    public Integer getMoveStackIndex() {
        return this.moveStackIndex;
    }

    void setMoveStackIndex(Integer moveStackIndex) {
        this.moveStackIndex = moveStackIndex;
    }

    boolean getPressRequest() {
        return this.pressRequest;
    }

    void setPressRequest(boolean pressRequest) {
        this.pressRequest = pressRequest;
    }

    boolean getClickFirst() {
        return this.clickFirst;
    }

    void setClickFirst(boolean clickFirst) {
        this.clickFirst = clickFirst;
    }

    public double[] getClientPosition() {
        return this.clientPosition;
    }

    void setClientPosition(double[] clientPosition) {
        this.clientPosition = clientPosition;
    }

    void notifyObserversGameStateUpdate() {
        IntStream.range(0, this.observers.size()).forEach(i -> this.observers.get(i).onGameStateUpdate(this));
    }

    void notifyObserversMoveStateUpdate() {
        IntStream.range(0, this.observers.size()).forEach(i -> this.observers.get(i).onMoveStateUpdate(this));
    }

    void updateGameViewModel(Game game, int[] minMovableStackIndices) {
        // Clear cards from board
        IntStream.range(0, Game.getNColumns()).forEach(i -> this.columns[i].clear());
        IntStream.range(0, Game.getNRuns()).forEach(i -> this.runs[i].clear());
        IntStream.range(0, Game.getNStocks()).forEach(i -> this.stocks[i].clear());

        // Add cards to board
        for (int i = 0; i < Game.getNColumns(); i++) {
            for (int j = 0; j < game.getColumns()[i].size(); j++) {
                boolean movable = minMovableStackIndices[i] <= j;
                this.columns[i].add(new CardViewModel(game.getCard("columns", i, j), "columns", i, j, movable));
            }
        }
        for (int i = 0; i < Game.getNRuns(); i++) {
            for (int j = 0; j < game.getRuns()[i].size(); j++) {
                this.runs[i].add(new CardViewModel(game.getCard("runs", i, j), "runs", i, j, false));
            }
        }
        for (int i = 0; i < Game.getNStocks(); i++) {
            for (int j = 0; j < game.getStocks()[i].size(); j++) {
                this.stocks[i].add(new CardViewModel(game.getCard("stocks", i, j), "stocks", i, j, false));
            }
        }
    }

    void resetMoveState() {
        this.moveColumnIndex = null;
        this.moveStackIndex = null;
        this.pressRequest = false;
        this.clickFirst = false;
        this.clientPosition = null;
    }

    public boolean isInClickMove(String location, int locationIndex, int stackIndex) {
        return isInMove(location, locationIndex, stackIndex) && this.clickFirst;
    }

    public boolean isInDragMove(String location, int locationIndex, int stackIndex) {
        return isInMove(location, locationIndex, stackIndex) && this.clientPosition != null;
    }

    private boolean isInMove(String location, int locationIndex, int stackIndex) {
        return location.equals("columns")
                && this.moveColumnIndex != null
                && this.moveColumnIndex == locationIndex
                && this.moveStackIndex <= stackIndex;
    }
}
