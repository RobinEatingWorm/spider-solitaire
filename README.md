# Spider Solitaire

Spider Solitaire is a version of solitaire played with two 52-card decks where the objective is to remove all of the
cards from the board.

## Rules

### Columns

Cards are dealt in 10 columns and can be moved between them. In Spider Solitaire, aces are the lowest rank and kings are
the highest. A single card can be moved to another column if its rank is one lower than the rank of the card it is being
moved onto (regardless of their suits). A stack of cards can only be moved if all cards in the stack have the same suit
and ranks that are in order (i.e., the rank of a card in the stack must be one lower than the rank of the previous card
in the stack).

If all of the cards in a column would be face down, the last card in the column is turned face up automatically. Any
card or ordered stack of cards can be moved to an empty column.

### Runs

A run is a stack of cards sharing the same suit and ordered by rank (starting from king and ending at ace). Runs are
automatically removed from the board when they are created. Completing all runs wins the game.

### Stocks

Stock piles are located on the bottom-right. Click on a stock to add a new card to the end of each column on the board.
A stock can only be used if no columns are empty.

## Installation

Download the application's source files using the green Code button near the top of this page.

This application was created using JDK 21 and JavaFX version 23. You can download a compressed archive for JDK 21 or
newer (with a `.zip` or `.tar.gz` filename extension) from [Oracle](https://www.oracle.com/java/technologies/downloads/)
or [jdk.java.net](https://jdk.java.net/archive/). Additionally, you can download the SDK for JavaFX version 23 or newer
from [Gluon](https://gluonhq.com/products/javafx/). Extract both downloads to the `lib` folder.

### Windows

In the script `run_windows.bat`, modify the first two lines to set `JDK_DIR` to the name of the JDK directory and
`JAVAFX_DIR` to the name of the JavaFX SDK directory. You can do so using Notepad to open the file and making sure to
save the file with a `.bat` extension. Note that both directories should be found in the `lib` folder after completing
the previous step. Double-click on the script to run the application.

### macOS and Linux

Depending on your operating system, modify lines 3 and 4 of `run_macos.sh` or `run_linux.sh` to set `JDK_DIR` to the
name of the JDK directory and `JAVAFX_DIR` to the name of the JavaFX SDK directory. On macOS, you can open and save
`.sh` files with TextEdit. Note that both directories should be found in the `lib` folder after completing the previous
step. Open a terminal at the main directory of this application and type `./run_macos.sh` or `./run_linux.sh` to run the
application.


## Gameplay

- Click on cards in columns to select them. Click on a valid column when cards are selected to move them.
- You can also drag cards across columns.
- Click on the stock piles to use one if possible.
- Access game controls using the toolbar at the top. Available controls include starting a new game with the specified
  number of suits (1, 2, or 4), resetting the current game, and undoing the last move.

## Known Issues

- When switching between a windowed and fullscreen view, elements may not resize correctly at first. Make any move,
  click on any of the buttons in the toolbar, or resize the window (if not in fullscreen) to fix this issue.

## Gallery

![](img/1_suit_game_start.png)

![](img/2_suit_game_end.png)

![](img/4_suit_game_middle.png)

![](img/4_suit_game_end.png)
