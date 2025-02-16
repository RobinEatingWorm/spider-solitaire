package spidersolitaire.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;

import spidersolitaire.constants.Constants;


public class Board {
    private final Column[] columns;
    private final Run[] runs;
    private final Stock[] stocks;

    Board() {
        this.columns = new Column[Constants.N_COLUMNS];
        this.runs = new Run[Constants.N_RUNS];
        this.stocks = new Stock[Constants.N_STOCKS];
        IntStream.range(0, Constants.N_COLUMNS).forEach(i -> this.columns[i] = new Column());
        IntStream.range(0, Constants.N_RUNS).forEach(i -> this.runs[i] = new Run());
        IntStream.range(0, Constants.N_STOCKS).forEach(i -> this.stocks[i] = new Stock());
    }

    public Column getColumn(int index) {
        return this.columns[index];
    }

    public Run getRun(int index) {
        return this.runs[index];
    }

    public Stock getStock(int index) {
        return this.stocks[index];
    }

    int getNumRuns() {
        return IntStream.range(0, Constants.N_RUNS).map(i -> this.runs[i].getCards().isEmpty() ? 0 : 1).sum();
    }

    int getNumStocks() {
        return IntStream.range(0, Constants.N_STOCKS).map(i -> this.stocks[i].getCards().isEmpty() ? 0 : 1).sum();
    }

    @SuppressWarnings("unchecked")
    void createInitialBoard(int nSuits) {
        // Shuffle and split deck
        Card[] deck = shuffleDeck(nSuits);
        int deckSplit = Constants.N_CARDS - (Constants.N_STOCKS * Constants.N_COLUMNS);

        // Add cards to columns
        ArrayList<Card>[] columnCards = new ArrayList[Constants.N_COLUMNS];
        IntStream.range(0, Constants.N_COLUMNS).forEach(i -> columnCards[i] = new ArrayList<>());
        IntStream.range(0, deckSplit).forEach(i -> columnCards[i % Constants.N_COLUMNS].add(deck[i]));
        IntStream.range(0, Constants.N_COLUMNS).forEach(i -> this.columns[i].addAll(columnCards[i]));

        // Add cards to stocks
        ArrayList<Card>[] stockCards = new ArrayList[Constants.N_STOCKS];
        IntStream.range(0, Constants.N_STOCKS).forEach(i -> stockCards[i] = new ArrayList<>());
        int cardsPerStock = (Constants.N_CARDS - deckSplit) / Constants.N_STOCKS;
        IntStream.range(deckSplit, Constants.N_CARDS)
                .forEach(i -> stockCards[(i - deckSplit) / cardsPerStock].add(deck[i]));
        IntStream.range(0, Constants.N_STOCKS).forEach(i -> this.stocks[i].addAll(stockCards[i]));
    }

    private static Card[] shuffleDeck(int nSuits) {
        double[] keys = new Random().doubles(Constants.N_CARDS).toArray();
        Integer[] indices = IntStream.range(0, Constants.N_CARDS).boxed().toArray(Integer[]::new);
        Arrays.sort(indices, Comparator.comparingDouble(i -> keys[i]));
        return Arrays.stream(indices)
                .mapToInt(Integer::intValue)
                .mapToObj(i -> new Card((i % Constants.N_RANKS) + 1, i % nSuits, false))
                .toArray(Card[]::new);
    }
}
