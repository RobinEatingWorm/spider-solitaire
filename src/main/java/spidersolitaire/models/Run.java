package spidersolitaire.models;

import java.util.ArrayList;
import java.util.stream.IntStream;

import spidersolitaire.constants.Constants;


public class Run extends Location {
    Run() {
        super();
    }

    @Override
    boolean canAddAll(ArrayList<Card> newCards) {
        return this.cards.isEmpty()
                && newCards.size() == Constants.N_RANKS
                && IntStream.range(0, newCards.size() - 1)
                .allMatch(i -> Location.isInOrder(newCards.get(i), newCards.get(i + 1)));
    }

    @Override
    boolean canRemoveEnd(int fromIndex) {
        throw new UnsupportedOperationException("User cannot remove cards from run");
    }
}
