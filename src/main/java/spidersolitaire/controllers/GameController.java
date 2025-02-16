package spidersolitaire.controllers;

import spidersolitaire.models.Game;
import spidersolitaire.views.GameView;


public class GameController implements GameControllerInputBoundary, GameControllerOutputBoundary {
    private final Game model;
    private final GameView view;

    public GameController(Game model, GameView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void handleGameUpdate() {
        this.view.updateGameView(this.model);
    }

    public void handleCSSUpdate(double appWidth) {
        this.view.updateCSS(appWidth);
    }
}
