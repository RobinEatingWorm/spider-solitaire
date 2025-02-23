package spidersolitaire.adapters;

public interface GameViewModelObserver {
    public void onGameStateUpdate(GameViewModel gameViewModel);
    public void onMoveStateUpdate(GameViewModel gameViewModel);
}
