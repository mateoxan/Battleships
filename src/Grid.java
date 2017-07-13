import java.util.Random;

/* Siatka - sk³ada siê z komórek, nale¿y do zawodnika
 * Siatka zajmuje siê ustawieniem statków. Mo¿e te¿ losowo ustawiæ je za gracza/
 */
public class Grid {

	private Cell[][] cells;
	private Player player;
	
	public static final int UP = 1;
	public static final int DOWN= 2;
	public static final int RIGHT = 3;
	public static final int LEFT = 4;
	
	private Ship target;
	private Cell first_hit;
	private Cell last_hit;
	private int last_dir = 0;
	private int dir = 0;
	

	/* Konstruktor - tworzy siatke komórek. Rozmiark siatki w klasie Game.
	 *  Ustawia te¿ do kogo nale¿y ta siatka.
	 */
	public Grid(Player player) {
		cells = new Cell[Game.GRID_SIZE][Game.GRID_SIZE];
		for (int i = 0; i < Game.GRID_SIZE; i++) {
			for (int j = 0; j < Game.GRID_SIZE; j++) {
				cells[i][j] = new Cell(player, i, j);
			}
		}
		this.player = player;
	}

	/* Ustawia losowo statek.
	 */
	public void autoDeploy(Ship ship) {
		Random rand = new Random(System.currentTimeMillis());

		int topLeftX;
		int topLeftY;
		int orientation;

		do {
			topLeftX = rand.nextInt(Game.GRID_SIZE - 1);
			topLeftY = rand.nextInt(Game.GRID_SIZE - 1);
			orientation = (int) (1 + rand.nextGaussian()) % 2;
		} while (!isValidDeployment(ship, topLeftX, topLeftY, orientation));

		deploy(ship, topLeftX, topLeftY, orientation);
	}

	/**
	 *	Losowo wybiera cel do ataku dla komputera. W przypadku trafienia w statek gracza
	 *	nie wybiera losowego celu, ale atakuje s¹siednie pola, a¿ nie zatopi statku.
	 *	Za cel nigdy te¿ nie zostan¹ wybrane pojedyncze pola, na których nie zmieœci siê ¿aden statek
	 *	oraz pola s¹siaduj¹ce z zatopionymi statkami.
	 */
	public Cell autoTargetMe() {
		boolean directions[] = {false, false, false, false, false};
		Cell cell;
		cell=cells[0][0];
		int x, y;
		int r;
		Random rand = new Random(System.currentTimeMillis());
		if(last_hit!= null && last_hit.isHit() && (target==null || target.isSunk())){		//nowy cel
			target = last_hit.getOccupyingShip();
			first_hit = last_hit;
			r = rand.nextInt(5-1)+1;
			dir = r;
			last_dir=0;
		}
		
		
		if(target!=null && !target.isSunk()){
			do{
			x=last_hit.getX();
			y=last_hit.getY();
			cell = cells[x][y];
			if(single_cell(cell)){			//gdy nie ma dok¹d iœæ, jesteœmy na polu otoczonym ze wszystkich stron zaatakowaymi polami
				x=first_hit.getX();
				y=first_hit.getY();
			}
			
			switch(dir){
			case UP:
				if(y!=0 && last_hit.isHit() && directions[UP]==false){
					y--;
					directions[dir]=true;
				}
				else if(last_dir!=DOWN && first_hit.getY()!=Game.GRID_SIZE-1 && directions[DOWN]==false){
					y=first_hit.getY();
					x=first_hit.getX();
					dir=DOWN;
					y++;
					directions[dir]=true;
				} else {
					y=first_hit.getY();
					x=first_hit.getX();
					if(x==Game.GRID_SIZE-1){
						dir = LEFT;
						x--;
						directions[dir]=true;
					} else if(x==0){
						dir = RIGHT;
						x++;
						directions[dir]=true;
					} else{
						r = rand.nextInt(5-3)+3;
						dir = r;			//prawo lub lewo
						if(dir==RIGHT) x++;
						else x--;
						directions[dir]=true;
					}
				}
				last_dir=UP;
				break;
			
			case RIGHT:
				if(x!=Game.GRID_SIZE-1 && last_hit.isHit() && directions[RIGHT]==false){
					x++;
					directions[dir]=true;
				}
				else if(last_dir!=LEFT && first_hit.getX()!=0 && directions[LEFT]==false){
					y=first_hit.getY();
					x=first_hit.getX();
					dir=LEFT;
					x--;
					directions[dir]=true;
				} else {
					y=first_hit.getY();
					x=first_hit.getX();
					if(y==Game.GRID_SIZE-1){
						dir = UP;
						y--;
						directions[dir]=true;
					} else if(y==0){
						dir = DOWN;
						y++;
						directions[dir]=true;
					} else{
						r = rand.nextInt(3-1)+1;
						dir = r;			//dó³ lub góra
						if(dir==UP) y--;
						else y++;
						directions[dir]=true;
					}
				}
				last_dir=RIGHT;
				break;
				
			case DOWN:
				if(y!=Game.GRID_SIZE-1 && last_hit.isHit() && directions[DOWN]==false){
					y++;
					directions[dir]=true;
				}
				else if(last_dir!=UP && first_hit.getY()!=0 && directions[UP]==false){
					y=first_hit.getY();
					x=first_hit.getX();
					dir=UP;
					y--;
					directions[dir]=true;
				} else {
					y=first_hit.getY();
					x=first_hit.getX();
					if(x==Game.GRID_SIZE-1){
						dir = LEFT;
						x--;
						directions[dir]=true;
					} else if(x==0){
						dir = RIGHT;
						x++;
						directions[dir]=true;
					} else{
						r = rand.nextInt(5-3)+3;
						dir = r;			//prawo lub lewo
						if(dir==RIGHT) x++;
						else x--;
						directions[dir]=true;
					}
				}
				last_dir=DOWN;
				break;
				
			case LEFT:
				if(x!=0 && last_hit.isHit() && directions[LEFT]==false){
					x--;
					directions[dir]=true;
				}
				else if(last_dir!=RIGHT && (first_hit.getX()!=Game.GRID_SIZE-1) && directions[RIGHT]==false){
					y=first_hit.getY();
					x=first_hit.getX();
					dir=RIGHT;
					x++;
					directions[dir]=true;
				} else {
					y=first_hit.getY();
					x=first_hit.getX();
					if(y==Game.GRID_SIZE-1){
						dir = UP;
						y--;
						directions[dir]=true;
					} else if(y==0){
						dir = DOWN;
						y++;
						directions[dir]=true;
					} else{
						r = rand.nextInt(3-1)+1;
						dir = r;			//góra lub dó³
						if(dir==UP) y--;
						else y++;
						directions[dir]=true;
					}
				}
				last_dir=LEFT;
				break;
			}
			cell=cells[x][y];
			} while (cell.wasAttacked() || nextToShip(cell));
			last_hit=cells[x][y];
			clearDirs(directions);
		}
		else{
			do {
				x = rand.nextInt(Game.GRID_SIZE);
				y = rand.nextInt(Game.GRID_SIZE);
				cell = cells[x][y];
			} while (cell.wasAttacked() || single_cell(cell) || nextToShip(cell));
			last_hit = cells[x][y];
		}
		return cell;
	}
	
	
	private void clearDirs(boolean tab[]){
		for(int i=1; i<tab.length; i++)
			tab[i]=false;
	}
	/*	Sprawdza czy jest to pojedyncza komorka otoczona zaatakowanymi polami */
	
	public boolean single_cell (Cell cell){
		int x, y;
		int ncount=0;
		x=cell.getX();
		y=cell.getY();
		
		if(x!=0){ 
			Cell n1 = cells[x-1][y];
			if(n1.wasAttacked()) ncount++;
		} else ncount++;
		if(x!=Game.GRID_SIZE-1){
			Cell n2 = cells[x+1][y];
			if(n2.wasAttacked()) ncount++;
		} else ncount++;
		if(y!=0){
			Cell n3 = cells[x][y-1];
			if(n3.wasAttacked()) ncount++;
		} else ncount++;
		if(y!=Game.GRID_SIZE-1){
			Cell n4 = cells[x][y+1];
			if(n4.wasAttacked()) ncount++;
		} else ncount++;
		
		if(ncount==4) return true;
		else return false;
	}
	
	private boolean nextToShip (Cell cell){
		int x = cell.getX();
		int y = cell.getY();
		for(int i=x-1; i<=x+1; i++)
			for(int j=y-1; j<=y+1; j++){
				if((i!=x || j!=y) && i>=0 && i<Game.GRID_SIZE && j>=0 && j<Game.GRID_SIZE){
					Cell neighbor = this.cells[i][j];
					if (neighbor.getOccupyingShip()!=null && neighbor.getOccupyingShip().isSunk())
						return true;
				}
			}
		return false;		
	}

	/* Tworzy tablice komórek który okupowa³by statek o podanych wspó³rzêdnych
	 * zwraca t¹ tablice
	 */
	public Cell[] coveredCells(Ship ship, int topLeftX, int topLeftY,
			int orientation) {
		Cell[] shipCells = new Cell[ship.getLength()];
		int length = ship.getLength();
		int c = 0;

		switch (orientation) {

		case Ship.HORIZONTAL:
			int maxX = Math.min(topLeftX + length, Game.GRID_SIZE);
			for (int i = topLeftX; i < maxX; i++) {
				shipCells[c++] = this.cells[i][topLeftY];
			}
			break;

		case Ship.VERTICAL:
			int maxY = Math.min(topLeftY + length, Game.GRID_SIZE);
			for (int i = topLeftY; i < maxY; i++) {
				shipCells[c++] = this.cells[topLeftX][i];
			}
			break;
		}

		return shipCells;
	}

	/*
	 * Ustawia statek
	 * topLeftX - wspó³rzêdna x górnego koñca statku
	 * topLeftY
	 * zwraca true jeœli statek zosta³ pomyœlnie ustawiony
	 */
	public boolean deploy(Ship ship, int topLeftX, int topLeftY, int orientation) {
		Cell[] cells = coveredCells(ship, topLeftX, topLeftY, orientation);
		for (int i = 0; i < cells.length; i++) {
			cells[i].occupyWith(ship);
		}
		return true;
	}

	public Cell getCell(int x, int y) {
		return cells[x][y];
	}

	public Player getPlayer() {
		return player;
	}

	/* Sprawdza czy mo¿na ustawiæ statek na podanych wspó³rzêdnych
	 * Sprawdza czy nie ma kolizji ze statkiem i czy nie wykracza poza siatke.
	 * zwraca true jeœli Ok
	 */
	public boolean isValidDeployment(Ship ship, int topLeftX, int topLeftY,
			int orientation) {

		Cell[] coveredCells = this.coveredCells(ship, topLeftX, topLeftY,
				orientation);

		for (int i = 0; i < coveredCells.length; i++) {
			Cell cell = coveredCells[i];
			// (b)
			if (cell == null) {
				return false;
			}
			// (a)
			Ship occupyingShip = cell.getOccupyingShip();
			if ((occupyingShip != null) && (occupyingShip != ship)) {
				return false;
			}
			
			if (checkNeighbors(cell)==false)
				return false;
		}

		return true;
	}
	
	
	//sprawdza czy na s¹siednich polach s¹ statki
	
		public boolean checkNeighbors (Cell cell){
			int x = cell.getX();
			int y = cell.getY();
			for(int i=x-1; i<=x+1; i++)
				for(int j=y-1; j<=y+1; j++){
					if((i!=x || j!=y) && i>=0 && i<Game.GRID_SIZE && j>=0 && j<Game.GRID_SIZE){
						Cell neighbor = this.cells[i][j];
						if (neighbor.getOccupyingShip()!=null)
							return false;
					}
				}
			return true;		
		}

}

