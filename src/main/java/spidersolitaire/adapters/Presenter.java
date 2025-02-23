package spidersolitaire.adapters;

import javafx.scene.input.MouseEvent;

import spidersolitaire.models.Game;

public final class Presenter {
    private final GameViewModel gameViewModel;

    public Presenter(GameViewModel gameViewModel) {
        this.gameViewModel = gameViewModel;
    }
    
    void handleMousePressedColumnCard() {
        this.gameViewModel.setPressRequest(true);
    }

    void handleDragDetectedColumnCard(MouseEvent event, int columnIndex, int stackIndex) {
        if (this.gameViewModel.getPressRequest()) {
            this.gameViewModel.setPressRequest(false);
            this.gameViewModel.setClickFirst(false);
            this.gameViewModel.setMoveColumnIndex(columnIndex);
            this.gameViewModel.setMoveStackIndex(stackIndex);
            this.gameViewModel.setClientPosition(new double[]{event.getSceneX(), event.getSceneY()});
            this.gameViewModel.notifyObserversMoveStateUpdate();
        }
    }

    void handleMouseDraggedGame(MouseEvent event) {
        if (this.gameViewModel.getClientPosition() != null) {
            this.gameViewModel.setClientPosition(new double[]{event.getX(), event.getY()});
            this.gameViewModel.notifyObserversMoveStateUpdate();
        }
    }

    void handleMouseClickedFirstColumnCard(int columnIndex, int stackIndex) {
        this.gameViewModel.setClientPosition(null);
        this.gameViewModel.setMoveColumnIndex(columnIndex);
        this.gameViewModel.setMoveStackIndex(stackIndex);
        this.gameViewModel.setClickFirst(true);
        this.gameViewModel.notifyObserversMoveStateUpdate();
    }

    boolean handleMouseClickedColumnCard(int columnIndex, int stackIndex) {
        if (this.gameViewModel.getPressRequest()) {
            this.gameViewModel.setPressRequest(false);
            if (this.gameViewModel.getClickFirst()) {
                return true;
            }
            this.handleMouseClickedFirstColumnCard(columnIndex, stackIndex);
        }
        return false;
    }

    boolean handleMouseClickedColumn() {
        if (this.gameViewModel.getClickFirst()) {
            return true;
        }
        this.gameViewModel.resetMoveState();
        this.gameViewModel.notifyObserversMoveStateUpdate();
        return false;
    }

    void handleMouseClickedGame() {
        this.gameViewModel.resetMoveState();
        this.gameViewModel.notifyObserversMoveStateUpdate();
    }

    void handleGameStateUpdate(Game game, int[] minMovableStackIndices) {
        this.gameViewModel.resetMoveState();
        this.gameViewModel.updateGameViewModel(game, minMovableStackIndices);
        this.gameViewModel.notifyObserversGameStateUpdate();;
    }
}
