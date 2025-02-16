package spidersolitaire.models;

import java.util.ArrayList;


public class Stock extends Location {
    Stock() {
        super();
    }

    @Override
    boolean canAddAll(ArrayList<Card> newCards) {
        throw new UnsupportedOperationException("User cannot add cards to stock");
    }

    @Override
    boolean canRemoveEnd(int fromIndex) {
        return true;
    }
}
