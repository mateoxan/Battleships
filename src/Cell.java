/*
 * Kom�rka - nale�y do siatki. Wie jaki statek j� zajmuje. Obs�uguje ataki.
 *  Decyduje co powinna wy�wieli� klasa UI
 */
public class Cell {
	/* Sta�e ataku
     * U�ywame przez Cell.tryAttack
	 */
	
	public static final int ATTACK_ALREADY = 1001;
	public static final int ATTACK_HIT = 1002;
	public static final int ATTACK_MISS = 1003;
	public static final int ATTACK_SUNK = 1004;

	// Sta�e u�ywane przez Cell.displayState
	public static final int DISPLAY_BLANK = 2001;
	public static final int DISPLAY_HIT = 2002;
	public static final int DISPLAY_MISS = 2003;
	public static final int DISPLAY_OCCUPIED = 2004;

	private int x;
	private int y;
	private Ship occupyingShip;
	private Player owningPlayer;
	private boolean attacked;

	/*
	 * Konstruktor -Ustawia kom�rke na niezaatakowan�.
	 * Ustawia do kogo nale�y i jej wsp�rz�dne.
	 */
	public Cell(Player owningPlayer, int x, int y) {
		this.attacked = false;
		this.owningPlayer = owningPlayer;
		this.x = x;
		this.y = y;
	}

	/*
	 * Oblicza co interfejs powinien wy�wietli�.
	 * Cell.DISPLAY_HIT - zaatakowana i trafiona 
	 * Cell.DISPLAY_MISS - zaatakowana i nietrafiona 
	 * Cell.DISPLAY_OCCUPIED - zaj�ta przez statek ale niezaatakowana i zawodnik mo�e j� widzie�
	 * czyli do niego nale�y
	 * Cell.DISPLAY_BLANK - reszta przypadk�w
	 * viewingPlayer - zawodnik kt�ry widzi t� kom�rke
	 * zwraca jedn� z tych 4 warto�ci
	 */
	public int displayState(Player viewingPlayer) {
		if (isHit()) {
			return Cell.DISPLAY_HIT;
		} else if (attacked) {
			return Cell.DISPLAY_MISS;
		} else if (occupyingShip != null) {
			if (UI.isCheating || (owningPlayer == viewingPlayer))
				return Cell.DISPLAY_OCCUPIED;
		}
		return Cell.DISPLAY_BLANK;
	}

	public Player getPlayer() {
		return this.owningPlayer;
	}

	public Ship getOccupyingShip() {
		return this.occupyingShip;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	/* true je�li trafiona */
	public boolean isHit() {
		return (getOccupyingShip() != null) && wasAttacked();
	}

	/* ustawia kom�rke na zaj�t� przez dany statek */
	public void occupyWith(Ship ship) {
		this.occupyingShip = ship;
	}

	/* Pr�ba ataku na kom�rke. Je�li ju� zaatakowana zwraca Cell.ATTACK_ALREADY.
	 * W przeciwnym wypadku:
	 * 1. Zaznacza kom�rke jako zaatakowana.
	 * 2. Odnotowuje trafienie.
	 * 3. Zwraca Cell.ATTACK_HIT, Cell.ATTACK_MISS lub Cell.ATTACK_SUNK
	 * w zale�no�ci od wyniku ataku.
	 */
	public int tryAttack() {
		if (wasAttacked())
			return Cell.ATTACK_ALREADY;

		attacked = true;

		if (occupyingShip != null) {
			occupyingShip.hit();
			if (occupyingShip.isSunk()) {
				return Cell.ATTACK_SUNK;
			} else {
				return Cell.ATTACK_HIT;
			}
		}

		return Cell.ATTACK_MISS;
	}

	public boolean wasAttacked() {
		return attacked;
	}

}
