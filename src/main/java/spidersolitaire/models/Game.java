package spidersolitaire.models;

import java.util.ArrayList;

import spidersolitaire.constants.Constants;
import spidersolitaire.controllers.GameControllerOutputBoundary;


public class Game {
    private final Board board;
    private GameControllerOutputBoundary outputBoundary;

    public Game() {
        this.board = new Board();
        this.outputBoundary = null;
    }

    public void setOutputBoundary(GameControllerOutputBoundary outputBoundary) {
        this.outputBoundary = outputBoundary;
    }

    public void startNewGame(int nSuits) {
        this.board.createInitialBoard(nSuits);
        onGameUpdate();
    }

    public Board getBoard() {
        return this.board;
    }

    private void onGameUpdate() {
        outputBoundary.handleGameUpdate();
    }
}
