package spidersolitaire.controllers;

import javafx.scene.input.MouseEvent;
import spidersolitaire.models.Logic;
import spidersolitaire.views.GameView;

public final class Controller {
    private static final GameView gameView = new GameView();

    private static Integer moveColumnIndex = null;
    private static Integer moveStackIndex = null;

    private static boolean pressRequest = false;
    private static boolean clickFirst = false;
    private static double[] clientPosition = null;

    private Controller() {
        throw new UnsupportedOperationException("Cannot instantiate class Controller");
    }

    public static GameView getGameView() {
        return Controller.gameView;
    }

    public static Integer getMoveColumnIndex() {
        return Controller.moveColumnIndex;
    }

    public static Integer getMoveStackIndex() {
        return Controller.moveStackIndex;
    }

    public static boolean getClickFirst() {
        return Controller.clickFirst;
    }

    public static double[] getClientPosition() {
        return Controller.clientPosition;
    }

    public static void newGame(int nSuits) {
        Logic.createInitialGame(nSuits);
        Controller.handleGameUpdate();
    }

    public static void resetGame() {
        Logic.resetGame();
        Controller.handleGameUpdate();
    }

    public static void undoMove() {
        Logic.undoLastMove();
        Controller.handleGameUpdate();
    }

    public static void handleMousePressedColumnCard() {
        System.out.println("MOUSE_PRESSED Column Card");
        Controller.pressRequest = true;
    }

    public static void handleDragDetectedColumnCard(MouseEvent event, int columnIndex, int stackIndex) {
        if (!Controller.pressRequest) {
            return;
        }
        System.out.println("DRAG_DETECTED Column Card");
        Controller.pressRequest = false;
        Controller.clickFirst = false;
        Controller.moveColumnIndex = columnIndex;
        Controller.moveStackIndex = stackIndex;
        Controller.clientPosition = new double[]{event.getSceneX(), event.getSceneY()};
        Controller.handleCSSUpdate();
    }

    public static void handleMouseDraggedGame(MouseEvent event) {
        if (Controller.clientPosition == null) {
            return;
        }
        // System.out.println("MOUSE_DRAGGED Game");
        Controller.clientPosition[0] = event.getX();
        Controller.clientPosition[1] = event.getY();
        Controller.handleCSSUpdate();
    }

    public static void handleMouseDragReleasedColumn(int toColumnIndex) {
        if (Controller.clientPosition == null) {
            return;
        }
        System.out.println("MOUSE-DRAG_RELEASED Column");
        Controller.makeColumnMove(Controller.moveColumnIndex, Controller.moveStackIndex, toColumnIndex);
    }

    public static void handleMouseDragReleasedGame() {
        if (Controller.clientPosition == null) {
            return;
        }
        System.out.println("MOUSE-DRAG_RELEASED Game");
        Controller.handleGameUpdate();
    }

    public static void handleMouseClickedColumnCard(int columnIndex, int stackIndex) {
        if (!Controller.pressRequest) {
            return;
        }
        System.out.println("MOUSE_CLICKED Column Card");
        Controller.pressRequest = false;
        if (Controller.clickFirst
                && Logic.isValidColumnMove(Controller.moveColumnIndex, Controller.moveStackIndex, columnIndex)) {
            Controller.makeColumnMove(Controller.moveColumnIndex, Controller.moveStackIndex, columnIndex);
        } else {
            Controller.clickFirst = true;
            Controller.moveColumnIndex = columnIndex;
            Controller.moveStackIndex = stackIndex;
            Controller.clientPosition = null;
            Controller.handleCSSUpdate();
        }
    }

    public static void handleMouseClickedStockCard() {
        System.out.println("MOUSE_CLICKED Stock Card");
        Controller.makeStockMove();
    }

    public static void handleMouseClickedColumn(int toColumnIndex) {
        System.out.println("MOUSE_CLICKED Column");
        if (!Controller.clickFirst) {
            Controller.resetMove();
            Controller.handleCSSUpdate();
        } else {
            Controller.makeColumnMove(Controller.moveColumnIndex, Controller.moveStackIndex, toColumnIndex);
        }
    }

    public static void handleMouseClickedGame() {
        System.out.println("MOUSE_CLICKED Game");
        Controller.resetMove();
        Controller.handleCSSUpdate();
    }

    public static void handleCSSUpdate() {
        Controller.gameView.updateCSS();
    }

    private static void makeColumnMove(int fromColumnIndex, int fromStackIndex, int toColumnIndex) {
        if (Logic.isValidColumnMove(fromColumnIndex, fromStackIndex, toColumnIndex)) {
            Logic.makeColumnMove(fromColumnIndex, fromStackIndex, toColumnIndex);
        }
        Controller.handleGameUpdate();
    }

    private static void makeStockMove() {
        if (Logic.isValidStockMove()) {
            Logic.makeStockMove();
        }
        Controller.handleGameUpdate();
    }

    private static void handleGameUpdate() {
        Controller.resetMove();
        Controller.gameView.updateGameView();
        Controller.gameView.updateCSS();
    }

    private static void resetMove() {
        Controller.moveColumnIndex = null;
        Controller.moveStackIndex = null;
        Controller.pressRequest = false;
        Controller.clickFirst = false;
        Controller.clientPosition = null;
    }
}
