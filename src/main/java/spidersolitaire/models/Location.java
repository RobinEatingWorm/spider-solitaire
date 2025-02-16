package spidersolitaire.models;

import java.util.ArrayList;
import java.util.stream.IntStream;


public abstract class Location {
    protected final ArrayList<Card> cards;

    Location() {
        this.cards = new ArrayList<>();
    }

    abstract boolean canAddAll(ArrayList<Card> newCards);

    abstract boolean canRemoveEnd(int fromIndex);

    void addAll(ArrayList<Card> newCards) {
        this.cards.addAll(newCards);
    }

    ArrayList<Card> removeEnd(int fromIndex) {
        ArrayList<Card> removedCards = new ArrayList<>(this.cards.subList(fromIndex, this.cards.size()));
        IntStream.range(0, removedCards.size()).forEach(i -> this.cards.removeLast());
        return removedCards;
    }

    public ArrayList<Card> getCards() {
        return this.cards;
    }

    protected static boolean canStack(Card card1, Card card2) {
        return card1.getRank() == card2.getRank() + 1 && card1.isFaceUp() && card2.isFaceUp();
    }

    protected static boolean isInOrder(Card card1, Card card2) {
        return canStack(card1, card2) && card1.getSuit() == card2.getSuit();
    }
}
