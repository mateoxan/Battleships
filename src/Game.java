/* Klasa gry. Decyduje o tym czyja jest tura i czy jeden z zawodników wygra³. */
public class Game {

	public static final int GRID_SIZE = 10;

	Player player1;
	Player player2;
	Player currentTurn;

	/* Konstruktor. Ustawia nazwy. Zaczyna pierwszy zawodnik. */
	public Game(String player1Name, String player2Name) {
		player1 = new Player(player1Name);
		player2 = new Player(player2Name);
		currentTurn = player2;
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	/* tura nastêpnego zawodnika */
	public void nextTurn() {
		if (currentTurn == player2) {
			currentTurn = player1;
		} else {
			currentTurn = player2;
		}
	}

	/*zwraca zawodnika który wygra³, null w przeciwnym wypadku */
	public Player whoWon() {
		if (player1.isDefeated()) {
			return player2;
		} else if (player2.isDefeated()) {
			return player1;
		} else {
			return null;
		}
	}
	
	public Player whoseTurn() {
		return currentTurn;
	}
}

