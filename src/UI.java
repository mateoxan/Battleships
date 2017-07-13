import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.*;

import java.util.Timer;

public class UI {

	public static final int BORDER_WIDTH = 1;

	public static final Color COLOUR_BLANK_DISABLED = new Color(92,172,238);
	public static final Color COLOUR_BLANK_ENABLED = new Color(126,192,238);
	public static final Color COLOUR_HIT = new Color(205,0,0);
	public static final Color COLOUR_INVALID = Color.RED;
	public static final Color COLOUR_MISS = Color.BLUE;
	public static final Color COLOUR_OCCUPIED = new Color(44,44,44);
	public static final Color COLOUR_SHIP_NOT_SUNK = Color.BLACK;
	public static final Color COLOUR_SHIP_SUNK = Color.LIGHT_GRAY;
	public static final Color COLOUR_SUNK = Color.MAGENTA;

	public static final long COMPUTER_THINK_TIME = 250; /* Czas myœlenia komputera */
	public static final String DEFAULT_HUMAN_NAME = "Gracz";
	public static final String WINDOW_TITLE = "Staki";

	public static boolean isCheating = false;

	private String playerName = UI.DEFAULT_HUMAN_NAME;
	
		public String getPlayerName() {
			return this.playerName;
		}

		public void setPlayerName(String name) {
			this.playerName = name;
		}

		private UI() {
			this.runNewGame();
		}

		public static void main(String[] args) {
			new UI();
		}

		public void runNewGame() {
			new GameWindow(this);
		}


	/* Uruchamia GUI dla danej gry */
	private static class GameWindow {
		
		public static final int COMPUTER_INDEX = 1;
		public static final int HUMAN_INDEX = 0;

		private PlayerDisplay[] allPlayerDisplays;
		private JFrame form;
		private AttackPhaseHandler myAttackPhaseHandler;
		private DeploymentHandler myDeployer;
		private UI myUI;
		private Game myGame;
		private boolean isComplete = false;

		public PlayerDisplay getComputerDisplay() {
			return this.allPlayerDisplays[GameWindow.COMPUTER_INDEX];
		}

		public Player getComputerPlayer() {
			return this.myGame.getPlayer2();
		}

		public Color getDefaultBackgroundColour() {
			return this.form.getBackground();
		}

		public PlayerDisplay getDisplayFor(Player player) {
			return (this.allPlayerDisplays[0].getPlayer() == player) ? this.allPlayerDisplays[0]
					: this.allPlayerDisplays[1];
		}

		public PlayerDisplay getEnemyDisplayFor(Player player) {
			return (this.allPlayerDisplays[0].getPlayer() == player) ? this.allPlayerDisplays[1]
					: this.allPlayerDisplays[0];
		}

		public PlayerDisplay getHumanDisplay() {
			return this.allPlayerDisplays[GameWindow.HUMAN_INDEX];
		}

		public Player getHumanPlayer() {
			return this.myGame.getPlayer1();
		}

		public Game getGame() {
			return this.myGame;
		}

		public UI getUI() {
			return this.myUI;
		}

		public boolean isComplete() {
			return this.isComplete;
		}

		public GameWindow(UI ui) {
			this.myUI = ui;
			this.myGame = new Game(ui.getPlayerName(), "Komputer");
			this.myDeployer = new DeploymentHandler(this);
			this.myAttackPhaseHandler = new AttackPhaseHandler(this);

			this.constructGUI();
			this.startGUI();
		}

		private void constructGUI() {
			final GameWindow gameWindow = this;

			JPanel displayGridsPanel = new JPanel();
			{
				PlayerDisplay humanDisplay, computerDisplay;
				this.allPlayerDisplays = new PlayerDisplay[2];

				this.allPlayerDisplays[GameWindow.HUMAN_INDEX] = humanDisplay = new PlayerDisplay(
						this, this.getHumanPlayer());
				this.allPlayerDisplays[GameWindow.COMPUTER_INDEX] = computerDisplay = new PlayerDisplay(
						this, this.getComputerPlayer());

				displayGridsPanel.setLayout(new GridLayout(1, 2));
				displayGridsPanel.add(humanDisplay.getComponent());
				displayGridsPanel.add(computerDisplay.getComponent());

				humanDisplay.setListener(this.myDeployer);
				computerDisplay.setListener(this.myAttackPhaseHandler);
			}

			JPanel buttonsPanel = new JPanel();
			{
				JButton bnAutoDeploy, bnAutoTarget, bnQuit, bnRotateShip;

				buttonsPanel.setLayout(new FlowLayout());
				buttonsPanel
						.add(bnAutoDeploy = gameWindow.myDeployer.bnAutoDeploy = new JButton(
								"Rozmieœæ losowo"));
				buttonsPanel
						.add(bnAutoTarget = gameWindow.myAttackPhaseHandler.bnAutoTarget = new JButton(
								"Atakuj losowo"));
				buttonsPanel
						.add(bnRotateShip = gameWindow.myDeployer.bnRotateShip = new JButton(
								"Obróæ statek"));
				buttonsPanel.add(bnQuit = new JButton("WyjdŸ"));

				bnAutoDeploy.setEnabled(false);
				bnAutoTarget.setEnabled(false);
				bnRotateShip.setEnabled(false);

				bnAutoDeploy.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						gameWindow.myDeployer.onAutoDeploy();
					}
				});

				bnAutoTarget.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						gameWindow.myAttackPhaseHandler.onAutoTarget();
					}
				});

				bnQuit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						gameWindow.onQuit();
					}
				});

				bnRotateShip.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						gameWindow.myDeployer.onSwitchOrientation();
					}
				});
			}

			JFrame form = this.form = new JFrame(UI.WINDOW_TITLE);
			{
				form.setLayout(new BorderLayout());
				form.add(displayGridsPanel, BorderLayout.CENTER);
				form.add(buttonsPanel, BorderLayout.SOUTH);
			}

			form
					.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			form.pack();
			form.setResizable(false);
			form.setLocationRelativeTo(null);
			form.setVisible(true);
		}

		public void onDeployComplete(DeploymentHandler handler) {
			this.myAttackPhaseHandler.startGUI();
		}

		public void onQuit() {
			this.form.dispose();
		}

		public void onWon(Player winningPlayer) {
			this.isComplete = true;
			int result = JOptionPane.showConfirmDialog(this.form, winningPlayer
					.getName()
					+ " zwyciê¿y³!\n" + "Chcia³byœ zagraæ jeszcze raz?",
					"Game over", JOptionPane.YES_NO_OPTION);
			this.form.dispose();

			if (result == JOptionPane.YES_OPTION) {
				new GameWindow(this.myUI);
			}
		}

		public Player otherPlayer(Player player) {
			Player otherPlayerTest;
			if ((otherPlayerTest = this.myGame.getPlayer1()) != player) {
				return otherPlayerTest;
			}
			return this.myGame.getPlayer2();
		}

		public void startGUI() {
			for (PlayerDisplay playerDisplay : this.allPlayerDisplays) {
				playerDisplay.status(playerDisplay.getPlayer().getName());
			}

			this.myDeployer.startGUI();
		}

		private void redraw() {
			for (PlayerDisplay playerDisplay : this.allPlayerDisplays) {
				playerDisplay.redraw();
			}
			this.myDeployer.drawOverlay();
		}
		
		/* Zajmuje siê faz¹ ataku */
		private static class AttackPhaseHandler implements
				PlayerDisplayListener {

			public JButton bnAutoTarget;

			private TimerTask myHumanTurnDisplayTask = null;
			private GameWindow myGameWindow;

			public AttackPhaseHandler(GameWindow gameWindow) {
				this.myGameWindow = gameWindow;
			}

			public void computerTakeTurn() {
				Player computerPlayer = this.myGameWindow.getComputerPlayer();
				Cell computerTarget = this.myGameWindow.otherPlayer(
						computerPlayer).getGrid().autoTargetMe();
				this.tryTakeTurn(this.myGameWindow.getComputerPlayer(),
						computerTarget.getX(), computerTarget.getY());
			}

			public void clearHumanTurnDisplayTask() {
				if (this.myHumanTurnDisplayTask == null) {
					return;
				}
				this.myHumanTurnDisplayTask.cancel();
				this.myHumanTurnDisplayTask = null;
			}

			public void onAutoTarget() {
				Cell humanTarget = this.myGameWindow.getComputerPlayer()
						.getGrid().autoTargetMe();
				this.humanTakeTurn(humanTarget.getX(), humanTarget.getY());
			}

			public void onGridClicked(PlayerDisplay playerDisplay,
					MouseEvent e, int x, int y) {
				this.humanTakeTurn(x, y);
			}

			public void onGridEntered(PlayerDisplay playerDisplay,
					MouseEvent e, int x, int y) {
			}

			public void onGridExited(PlayerDisplay playerDisplay, MouseEvent e,
					int x, int y) {
			}

			public void startGUI() {
				this.bnAutoTarget.setEnabled(true);
				this.myGameWindow.getHumanDisplay().setEnabled(false);
				this.myGameWindow.getComputerDisplay().setEnabled(true);
				this.myGameWindow.redraw();
				this.startCurrentTurn();
			}

			private Color colourAttackResult(int attackResult) {
				switch (attackResult) {
				case Cell.ATTACK_HIT:
					return UI.COLOUR_HIT;
				case Cell.ATTACK_MISS:
					return UI.COLOUR_MISS;
				case Cell.ATTACK_SUNK:
					return UI.COLOUR_SUNK;
				default:
					return SystemColor.windowText;
				}
			}

			private void displayTurnResult(Player attackingPlayer,
					int attackResult) {
				PlayerDisplay playerDisplay = this.myGameWindow
						.getEnemyDisplayFor(attackingPlayer);
				if (attackResult == Cell.ATTACK_ALREADY) {
					playerDisplay.status(attackingPlayer.getName()
							+ ", ju¿ tutaj atakowa³eœ.");
				} else {
					playerDisplay.status(attackingPlayer.getName()
							+ " zaatakowa³ - "
							+ this.labelAttackResult(attackResult), this
							.colourAttackResult(attackResult));
					playerDisplay.redraw();
				}
			}

			private void humanTakeTurn(int targetX, int targetY) {
				Player humanPlayer = this.myGameWindow.getHumanPlayer();
				boolean wasSuccessful = this.tryTakeTurn(humanPlayer, targetX,
						targetY);
				if (wasSuccessful) {
					this.clearHumanTurnDisplayTask();
				} else {
					this.myGameWindow.getEnemyDisplayFor(humanPlayer).status(
							humanPlayer.getName() + ", to nie jest twoja tura.");
				}
			}

			private String labelAttackResult(int attackResult) {
				switch (attackResult) {
				case Cell.ATTACK_HIT:
					return "trafiony";
				case Cell.ATTACK_MISS:
					return "pud³o";
				case Cell.ATTACK_SUNK:
					return "zatopiony";
				default:
					return "";
				}
			}

			private void onWon(Player winner) {
				this.myGameWindow.onWon(winner);
			}

			private void setHumanTurnDisplayTask() {
				this.clearHumanTurnDisplayTask();

				final GameWindow gameWindow = this.myGameWindow;
				final String humanName = this.myGameWindow.getHumanPlayer()
						.getName();
				this.myHumanTurnDisplayTask = new SingleTask(1000) {
					public void runSingleTask() {
						if (gameWindow.isComplete()) {
							return;
						}
						gameWindow.getComputerDisplay().status(
								humanName + ", wybierz pole do zaatakowania.");
					}
				};
			}

			private void startComputerTurn() {
				Player computerPlayer = this.myGameWindow.getComputerPlayer();
				this.myGameWindow.getEnemyDisplayFor(computerPlayer).status(
						computerPlayer.getName() + " myœli...");

				final AttackPhaseHandler attackPhaseHandler = this;
				new SingleTask(UI.COMPUTER_THINK_TIME) {
					public void runSingleTask() {
						if (attackPhaseHandler.myGameWindow.isComplete()) {
							return;
						}
						attackPhaseHandler.computerTakeTurn();
					}
				};
			}

			private void startCurrentTurn() {
				Player turnPlayer = this.myGameWindow.getGame().whoseTurn();
				if (turnPlayer == this.myGameWindow.getHumanPlayer()) {
					this.startHumanTurn();
				} else {
					this.startComputerTurn();
				}
			}

			private void startHumanTurn() {
				this.setHumanTurnDisplayTask();
			}

			private void startNextTurn() {
				Player winner = this.myGameWindow.getGame().whoWon();
				if (winner != null) {
					this.onWon(winner);
				} else {
					this.myGameWindow.getGame().nextTurn();
					this.startCurrentTurn();
				}
			}

			private boolean tryTakeTurn(Player attackingPlayer, int x, int y) {
				if (this.myGameWindow.getGame().whoseTurn() != attackingPlayer) {
					return false;
				}
				Player attackedPlayer = this.myGameWindow
						.otherPlayer(attackingPlayer);
				int result = attackedPlayer.getGrid().getCell(x, y).tryAttack();
				this.displayTurnResult(attackingPlayer, result);
				if (result != Cell.ATTACK_ALREADY) {
					this.startNextTurn();
				}
				return true;
			}
		}

		/* Faza przygotowawcza */
		private static class DeploymentHandler implements PlayerDisplayListener {
		
			public JButton bnAutoDeploy;
			public JButton bnRotateShip;

			private Cell[] currentCells = new Cell[0];
			private int currentOrientation = Ship.HORIZONTAL;
			private int currentX = -1;
			private int currentY = -1;
			private boolean isCurrentPositionValid = false;
			private SingleTask myDisplayStatusTask = null;
			private GameWindow myGameWindow;
			private int nextShipIndex = 0;

			public Ship getCurrentShip() {
				Ship[] ships = this.getHumanShips();
				if (this.nextShipIndex >= ships.length) {
					return null;
				} else {
					return ships[this.nextShipIndex];
				}
			}

			public Ship[] getHumanShips() {
				return this.myGameWindow.getHumanPlayer().getShips();
			}

			public boolean isComplete() {
				return this.nextShipIndex >= this.getHumanShips().length;
			}

			public DeploymentHandler(GameWindow gameWindow) {
				this.myGameWindow = gameWindow;
			}

			public void drawOverlay() {
				Color drawColour = this.isCurrentPositionValid ? UI.COLOUR_OCCUPIED
						: UI.COLOUR_INVALID;
				PlayerDisplay playerDisplay = this.myGameWindow
						.getHumanDisplay();
				for (Cell coveredCell : this.currentCells) {
					if (coveredCell == null) {
						continue;
					}
					playerDisplay.drawCell(coveredCell.getX(), coveredCell
							.getY(), drawColour);
				}
			}

			public void onAutoDeploy() {
				while (!this.isComplete()) {
					this.autoDeployCurrentShip();
				}
				this.complete();
			}

			public void onGridClicked(PlayerDisplay playerDisplay,
					MouseEvent e, int x, int y) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					this.rotateShip();
				} else {
					this.setDeploymentPosition(x, y);
					if (this.isCurrentPositionValid) {
						this.deployCurrentShip();
						this.redraw();

						if (this.isComplete()) {
							this.complete();
						}
					} else {
						final DeploymentHandler deploymentHandler = this;
						this.myDisplayStatusTask = new SingleTask(1000) {
							public void runSingleTask() {
								deploymentHandler.displayCurrentStatus();
							}
						};
						PlayerDisplay humanDisplay = this.myGameWindow
								.getHumanDisplay();
						humanDisplay.status(humanDisplay.getPlayer().getName()
								+ ", nie mo¿esz ustawiæ "
								+ this.getCurrentShip().getName() + " tutaj.");
					}
				}
			}

			public void onGridEntered(PlayerDisplay playerDisplay,
					MouseEvent e, int x, int y) {
				this.setDeploymentPosition(x, y);
				this.redraw();
			}

			public void onGridExited(PlayerDisplay playerDisplay, MouseEvent e,
					int x, int y) {
				this.clearDeploymentPosition();
				this.redraw();
			}

			public void onSwitchOrientation() {
				this.rotateShip();
			}

			public void redraw() {
				this.myGameWindow.getHumanDisplay().redraw();
				this.drawOverlay();
			}

			public void startGUI() {
				this.deployComputerShips();
				this.displayCurrentStatus();

				this.bnAutoDeploy.setEnabled(true);
				this.bnRotateShip.setEnabled(true);
				this.myGameWindow.getHumanDisplay().setEnabled(true);
				this.myGameWindow.redraw();
			}

			private void autoDeployCurrentShip() {
				this.myGameWindow.getHumanPlayer().getGrid().autoDeploy(
						this.getCurrentShip());
				++this.nextShipIndex;
			}

			private void clearDeploymentPosition() {
				this.setDeploymentPosition(-1, -1);
			}

			private void complete() {
				this.bnAutoDeploy.setEnabled(false);
				this.bnRotateShip.setEnabled(false);
				this.myGameWindow.getHumanDisplay().setEnabled(false);

				this.nextShipIndex = this.getHumanShips().length;
				this.clearDeploymentPosition();
				this.redraw();
				this.displayCurrentStatus();

				this.myGameWindow.onDeployComplete(this);
			}

			private void deployComputerShips() {
				Player computerPlayer = this.myGameWindow.getComputerPlayer();
				for (Ship ship : computerPlayer.getShips()) {
					computerPlayer.getGrid().autoDeploy(ship);
				}
				this.myGameWindow.getComputerDisplay().status(
						"Komputer rozstawi³ swoje statki.");
			}

			private void deployCurrentShip() {
				this.myGameWindow.getHumanPlayer().getGrid().deploy(
						this.getCurrentShip(), this.currentX, this.currentY,
						this.currentOrientation);
				++this.nextShipIndex;
				this.revalidateCurrentPosition();
				this.displayCurrentStatus();
			}

			private void displayCurrentStatus() {
				if (this.myDisplayStatusTask != null) {
					this.myDisplayStatusTask.cancel();
					this.myDisplayStatusTask = null;
				}
				Ship currentShip = this.getCurrentShip();
				if (currentShip == null) {
					this.myGameWindow.getHumanDisplay().status(
							"Rozstawianie zakoñczone.");
				} else {
					this.myGameWindow.getHumanDisplay().status(
							"Ustaw " + currentShip.getName() + ".");
				}
			}

			private void revalidateCurrentPosition() {
				if (this.currentX == -1 || this.currentY == -1
						|| this.isComplete()) {
					this.currentCells = new Cell[0];
					this.isCurrentPositionValid = false;
				} else {
					Grid humanGrid = this.myGameWindow.getHumanPlayer()
							.getGrid();
					this.currentCells = humanGrid.coveredCells(this
							.getCurrentShip(), this.currentX, this.currentY,
							this.currentOrientation);
					this.isCurrentPositionValid = humanGrid.isValidDeployment(
							this.getCurrentShip(), this.currentX,
							this.currentY, this.currentOrientation);
				}
			}

			private void rotateShip() {
				this.currentOrientation = (this.currentOrientation == Ship.HORIZONTAL) ? Ship.VERTICAL
						: Ship.HORIZONTAL;
				this.revalidateCurrentPosition();
				this.redraw();
			}

			private void setDeploymentPosition(int x, int y) {
				if (this.currentX == x && this.currentY == y) {
					return;
				}
				this.currentX = x;
				this.currentY = y;
				this.revalidateCurrentPosition();
			}
		}

		/* Wyœwietlanie informacji */
		private static class PlayerDisplay implements PlayerDisplayListener {
	
			private JButton[][] allButtons;
			private JLabel[] allShipLabels;
			private JPanel contentPanel;
			private boolean isEnabled = false;
			private JLabel laStatus;
			private PlayerDisplayListener myListener = null;
			private GameWindow myGameWindow;
			private Player myPlayer;

			public JPanel getComponent() {
				return this.contentPanel;
			}

			public boolean getEnabled() {
				return this.isEnabled;
			}

			public Grid getGrid() {
				return this.myPlayer.getGrid();
			}

			public PlayerDisplayListener getListener() {
				return this.myListener;
			}

			public Player getPlayer() {
				return this.myPlayer;
			}

			public Ship[] getShips() {
				return this.myPlayer.getShips();
			}

			public void setEnabled(boolean newEnabled) {
				this.isEnabled = newEnabled;
				for (int y = 0; y < Game.GRID_SIZE; ++y) {
					for (int x = 0; x < Game.GRID_SIZE; ++x) {
						this.allButtons[x][y].setEnabled(this.isEnabled);
					}
				}
			}

			public void setListener(PlayerDisplayListener newListener) {
				this.myListener = newListener;
			}

			public PlayerDisplay(GameWindow gameWindow, Player player) {
				this.myGameWindow = gameWindow;
				this.myPlayer = player;
				this.constructGUI();
			}

			public void drawCell(int x, int y, Color drawColour) {
				this.allButtons[x][y].setBackground(drawColour);
			}

			public void onGridClicked(PlayerDisplay playerDisplay,
					MouseEvent e, int x, int y) {
				if (this.myListener == null || !this.isEnabled) {
					return;
				} else {
					this.myListener.onGridClicked(playerDisplay, e, x, y);
				}
			}

			public void onGridEntered(PlayerDisplay playerDisplay,
					MouseEvent e, int x, int y) {
				if (this.myListener == null || !this.isEnabled) {
					return;
				} else {
					this.myListener.onGridEntered(playerDisplay, e, x, y);
				}
			}

			public void onGridExited(PlayerDisplay playerDisplay, MouseEvent e,
					int x, int y) {
				if (this.myListener == null || !this.isEnabled) {
					return;
				} else {
					this.myListener.onGridExited(playerDisplay, e, x, y);
				}
			}

			public void redraw() {
				this.redrawCells();
				this.redrawShipLabels();
			}

			public void status(String message) {
				this.status(message, SystemColor.windowText);
			}

			public void status(String message, Color backgroundColour) {
				this.laStatus.setText(message);
				this.laStatus.setForeground(backgroundColour);
			}

			private Color colourForState(int displayState) {
				switch (displayState) {
				case Cell.DISPLAY_HIT:
					return UI.COLOUR_HIT;
				case Cell.DISPLAY_MISS:
					return UI.COLOUR_MISS;
				case Cell.DISPLAY_OCCUPIED:
					return UI.COLOUR_OCCUPIED;
				default:
					return this.isEnabled ? UI.COLOUR_BLANK_ENABLED
							: UI.COLOUR_BLANK_DISABLED;
				}
			}

			private JButton constructButton(final int x, final int y) {
				final PlayerDisplay playerDisplay = this;

				JButton button = new JButton("\n");
				button.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						playerDisplay.onGridClicked(playerDisplay, e, x, y);
					}

					public void mouseEntered(MouseEvent e) {
						playerDisplay.onGridEntered(playerDisplay, e, x, y);
					}

					public void mouseExited(MouseEvent e) {
						playerDisplay.onGridExited(playerDisplay, e, x, y);
					}
				});

				return button;
			}

			private void constructGUI() {
				JPanel shipListPanel = new JPanel();
				{
					shipListPanel.setLayout(new FlowLayout());

					Ship[] ships = this.getShips();
					this.allShipLabels = new JLabel[ships.length];
					for (int i = 0; i < this.allShipLabels.length; ++i) {
						JLabel shipLabel = this.allShipLabels[i] = new JLabel(
								ships[i].getName());
						shipLabel.setBorder(new EmptyBorder(UI.BORDER_WIDTH,
								UI.BORDER_WIDTH, UI.BORDER_WIDTH,
								UI.BORDER_WIDTH));
						shipListPanel.add(shipLabel);
					}
				}

				JPanel gridPanel = new JPanel();
				{
					this.allButtons = new JButton[Game.GRID_SIZE][Game.GRID_SIZE];
					gridPanel.setLayout(new GridLayout(Game.GRID_SIZE,
							Game.GRID_SIZE));

					for (int y = 0; y < Game.GRID_SIZE; ++y) {
						for (int x = 0; x < Game.GRID_SIZE; ++x) {
							JButton button = this.constructButton(x, y);
							this.allButtons[x][y] = button;
							gridPanel.add(button);
						}
					}
				}

				this.contentPanel = new JPanel();
				{
					this.laStatus = new JLabel("\n");
					{
						this.laStatus.setBorder(new EmptyBorder(
								UI.BORDER_WIDTH, UI.BORDER_WIDTH,
								UI.BORDER_WIDTH, UI.BORDER_WIDTH));
						this.laStatus
								.setHorizontalAlignment(SwingConstants.CENTER);
					}

					this.contentPanel.setBorder(new EmptyBorder(
							UI.BORDER_WIDTH, UI.BORDER_WIDTH, UI.BORDER_WIDTH,
							UI.BORDER_WIDTH));
					this.contentPanel.setLayout(new BorderLayout());
					this.contentPanel.add(shipListPanel, BorderLayout.NORTH);
					this.contentPanel.add(gridPanel, BorderLayout.CENTER);
					this.contentPanel.add(this.laStatus, BorderLayout.SOUTH);
				}
			}

			private void redrawCell(int x, int y) {
				int displayState = this.myPlayer.getGrid().getCell(x, y)
						.displayState(this.myGameWindow.getHumanPlayer());
				Color drawColour = this.colourForState(displayState);
				this.drawCell(x, y, drawColour);
			}

			private void redrawCells() {
				for (int y = 0; y < Game.GRID_SIZE; ++y) {
					for (int x = 0; x < Game.GRID_SIZE; ++x) {
						this.redrawCell(x, y);
					}
				}
			}

			private void redrawShipLabels() {
				Ship[] ships = this.getShips();
				for (int i = 0; i < this.allShipLabels.length; ++i) {
					Color drawColour = ships[i].isSunk() ? UI.COLOUR_SHIP_SUNK
							: UI.COLOUR_SHIP_NOT_SUNK;
					this.allShipLabels[i].setForeground(drawColour);
				}
			}
		}

		/* Zdarzenia gracza */
		private static interface PlayerDisplayListener {
			public void onGridClicked(PlayerDisplay playerDisplay,
					MouseEvent e, int x, int y);

			public void onGridEntered(PlayerDisplay playerDisplay,
					MouseEvent e, int x, int y);

			public void onGridExited(PlayerDisplay playerDisplay, MouseEvent e,
					int x, int y);
		}

		private static abstract class SingleTask extends TimerTask {
			private Timer myTimer;

			public SingleTask(long delay) {
				this.myTimer = new Timer();
				this.myTimer.schedule(this, delay);
			}

			public boolean cancel() {
				boolean result = super.cancel();
				this.myTimer.cancel();
				return result;
			}

			public void run() {
				this.runSingleTask();
				this.cancel();
			}

			public abstract void runSingleTask();
		}
	}
}
