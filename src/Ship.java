/* Abstrakcyjna klasa statk�w. Zawiera pola - nazwa statku, dlugosc, liczba trafie�
 */
public abstract class Ship {
	/* Sta�e opisuj�ce orientacje statku */
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	private String name;
	private int length;
	private int hitCount;

	/* Konstruktor - nazwa, dlugosc, liczba trafie� */
	public Ship(String name, int length) {
		this.name = name;
		this.length = length;
		this.hitCount = 0;
	}

	public String getName() {
		return this.name;
	}

	public int getLength() {
		return this.length;
	}

	/* zwi�ksza liczbe trafie� */
	public void hit() {
		hitCount++;
	}

	/* zwraca true jesli zatopiony */
	public boolean isSunk() {
		return this.hitCount == length;
	}
	
	
	/* zwraca ilo�� trafie�*/
	public int hits() {
		return this.hitCount;
	}

}
