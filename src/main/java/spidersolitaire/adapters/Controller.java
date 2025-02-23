package spidersolitaire.adapters;

import java.util.stream.IntStream;

import javafx.scene.input.MouseEvent;

import spidersolitaire.models.Game;
import spidersolitaire.models.Logic;

public final class Controller {
    private final Game game;
    private final Logic logic;
    private final Presenter presenter;
    private final GameViewModel gameViewModel;

    public Controller(Game game, Logic logic, Presenter presenter, GameViewModel gameViewModel) {
        this.game = game;
        this.logic = logic;
        this.presenter = presenter;
        this.gameViewModel = gameViewModel;
    }

    public void handleNewGame(int nSuits) {
        this.logic.newGame(nSuits);
        this.onGameStateUpdate();
    }

    public void handleResetGame() {
        this.logic.resetGame();
        this.onGameStateUpdate();
    }

    public void handleUndoMove() {
        this.logic.undoMove();
        this.onGameStateUpdate();
    }

    public void handleMousePressedColumnCard() {
        this.presenter.handleMousePressedColumnCard();
    }

    public void handleDragDetectedColumnCard(MouseEvent event, int columnIndex, int stackIndex) {
        this.presenter.handleDragDetectedColumnCard(event, columnIndex, stackIndex);
    }

    public void handleMouseDraggedGame(MouseEvent event) {
        this.presenter.handleMouseDraggedGame(event);
    }

    public void handleMouseDragReleasedColumn(int toColumnIndex) {
        if (this.gameViewModel.getClientPosition() != null) {
            int fromColumnIndex = this.gameViewModel.getMoveColumnIndex();
            int fromStackIndex = this.gameViewModel.getMoveStackIndex();
            if (this.logic.isValidColumnMove(fromColumnIndex, fromStackIndex, toColumnIndex)) {
                this.logic.makeColumnMove(fromColumnIndex, fromStackIndex, toColumnIndex);
            }
        }
        this.onGameStateUpdate();
    }

    public void handleMouseDragReleasedGame() {
        this.onGameStateUpdate();
    }

    public void handleMouseClickedColumnCard(int columnIndex, int stackIndex) {
        boolean clickSecond = this.presenter.handleMouseClickedColumnCard(columnIndex, stackIndex);
        if (clickSecond) {
            int fromColumnIndex = this.gameViewModel.getMoveColumnIndex();
            int fromStackIndex = this.gameViewModel.getMoveStackIndex();
            if (this.logic.isValidColumnMove(fromColumnIndex, fromStackIndex, columnIndex)) {
                this.logic.makeColumnMove(fromColumnIndex, fromStackIndex, columnIndex);
                this.onGameStateUpdate();
            } else {
                this.presenter.handleMouseClickedFirstColumnCard(columnIndex, stackIndex);
            }
        }
    }

    public void handleMouseClickedStockCard() {
        if (this.logic.isValidStockMove()) {
            this.logic.makeStockMove();
            this.onGameStateUpdate();
        }
    }

    public void handleMouseClickedColumn(int toColumnIndex) {
        boolean clickSecond = this.presenter.handleMouseClickedColumn();
        if (clickSecond) {
            int fromColumnIndex = this.gameViewModel.getMoveColumnIndex();
            int fromStackIndex = this.gameViewModel.getMoveStackIndex();
            if (this.logic.isValidColumnMove(fromColumnIndex, fromStackIndex, toColumnIndex)) {
                this.logic.makeColumnMove(fromColumnIndex, fromStackIndex, toColumnIndex);
                this.onGameStateUpdate();
            } else {
                this.presenter.handleMouseClickedGame();
            }
        }
    }

    public void handleMouseClickedGame() {
        this.presenter.handleMouseClickedGame();
    }

    private void onGameStateUpdate() {
        int nColumns = Game.getNColumns();
        int[] minMovableStackIndices = new int[nColumns];
        IntStream.range(0, nColumns).forEach(i -> minMovableStackIndices[i] = this.logic.getMinMovableStackIndex(i));
        this.presenter.handleGameStateUpdate(this.game, minMovableStackIndices);
    }
}
