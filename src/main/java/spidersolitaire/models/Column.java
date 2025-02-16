package spidersolitaire.models;

import java.util.ArrayList;
import java.util.stream.IntStream;


public class Column extends Location {
    Column() {
        super();
    }

    @Override
    boolean canAddAll(ArrayList<Card> newCards) {
        return this.cards.isEmpty() || newCards.isEmpty() || canStack(this.cards.getLast(), newCards.getFirst());
    }

    @Override
    boolean canRemoveEnd(int fromIndex) {
        return fromIndex > this.getMinIndexCanRemove();
    }

    @Override
    void addAll(ArrayList<Card> newCards) {
        super.addAll(newCards);
        IntStream.range(0, this.cards.size()).forEach(i -> this.cards.get(i).setMoveable(this.canRemoveEnd(i)));
    }

    @Override
    ArrayList<Card> removeEnd(int fromIndex) {
        ArrayList<Card> removedCards = super.removeEnd(fromIndex);
        // TODO: extract turn last logic into general game logic?
        if (!isFaceUpLast()) {
            turnLast();
        }
        return removedCards;
    }

    boolean isFaceUpLast() {
        return !this.cards.getLast().isFaceUp();
    }

    void turnLast() {
        this.cards.getLast().turn();
    }

    public int getMinIndexCanRemove() {
        boolean[] inOrder = new boolean[this.cards.size()];
        inOrder[this.cards.size() - 1] = true;
        IntStream.range(0, this.cards.size() - 1)
                .forEach(i -> inOrder[i] = isInOrder(this.cards.get(i), this.cards.get(i + 1)));
        return IntStream.range(0, this.cards.size()).filter(i -> !inOrder[i]).max().orElseThrow();
    }
}
