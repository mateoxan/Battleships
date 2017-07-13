/*
 * Klasa zawodnika. Ka�dy zawodnik posiada imie, 5 statk�w i siatke.
 */
public class Player {

	private String name;
	private Ship[] ships;
	private Grid grid;

	/* Konstruktor gracza
	 * 1.Ustawia imie zawodnika
	 * 2.Inicjalizuje pi�� statk�w gracza:
	 * 3.Tworzy siatke gracza
	 */
	public Player(String name) {
		// 1
		this.name = name;

		// 2
		ships = new Ship[] { new AircraftCarrier(), new Battleship(),
				new Submarine(), new Destroyer(), new PatrolBoat() };

		// 3
		grid = new Grid(this);
	}

	/* zwraca siatke gracza */
	public Grid getGrid() {
		return grid;
	}

	/* zwraca imie gracza */
	public String getName() {
		return this.name;
	}

	/* zwraca tablice statk�w */
	public Ship[] getShips() {
		return ships;
	}

	/* zwraca true je�li gracz zosta� pokonany */
	public boolean isDefeated() {
		int sunkCount = 0;

		for (int i = 0; i < ships.length; i++) {
			if (ships[i].isSunk()) {
				sunkCount++;
			}
		}

		return sunkCount == ships.length;
	}
}
