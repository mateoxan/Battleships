/*
 * Komórka - nale¿y do siatki. Wie jaki statek j¹ zajmuje. Obs³uguje ataki.
 *  Decyduje co powinna wyœwieliæ klasa UI
 */
public class Cell {
	/* Sta³e ataku
     * U¿ywame przez Cell.tryAttack
	 */
	
	public static final int ATTACK_ALREADY = 1001;
	public static final int ATTACK_HIT = 1002;
	public static final int ATTACK_MISS = 1003;
	public static final int ATTACK_SUNK = 1004;

	// Sta³e u¿ywane przez Cell.displayState
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
	 * Konstruktor -Ustawia komórke na niezaatakowan¹.
	 * Ustawia do kogo nale¿y i jej wspó³rzêdne.
	 */
	public Cell(Player owningPlayer, int x, int y) {
		this.attacked = false;
		this.owningPlayer = owningPlayer;
		this.x = x;
		this.y = y;
	}

	/*
	 * Oblicza co interfejs powinien wyœwietliæ.
	 * Cell.DISPLAY_HIT - zaatakowana i trafiona 
	 * Cell.DISPLAY_MISS - zaatakowana i nietrafiona 
	 * Cell.DISPLAY_OCCUPIED - zajêta przez statek ale niezaatakowana i zawodnik mo¿e j¹ widzieæ
	 * czyli do niego nale¿y
	 * Cell.DISPLAY_BLANK - reszta przypadków
	 * viewingPlayer - zawodnik który widzi t¹ komórke
	 * zwraca jedn¹ z tych 4 wartoœci
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

	/* true jeœli trafiona */
	public boolean isHit() {
		return (getOccupyingShip() != null) && wasAttacked();
	}

	/* ustawia komórke na zajêt¹ przez dany statek */
	public void occupyWith(Ship ship) {
		this.occupyingShip = ship;
	}

	/* Próba ataku na komórke. Jeœli ju¿ zaatakowana zwraca Cell.ATTACK_ALREADY.
	 * W przeciwnym wypadku:
	 * 1. Zaznacza komórke jako zaatakowana.
	 * 2. Odnotowuje trafienie.
	 * 3. Zwraca Cell.ATTACK_HIT, Cell.ATTACK_MISS lub Cell.ATTACK_SUNK
	 * w zale¿noœci od wyniku ataku.
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
