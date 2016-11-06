import java.awt.Color;

import simple.gui.panel.ScaledPanel;

public class SubGamePanel extends ScaledPanel {
	// Subgame states
	static final int IN_PLAY = -1;
	static final int TIE = 0;
	// Not used, but for the readers reference
	static final int PLAYER1 = 1;
	static final int PLAYER2 = 2;
	
	static final Color[] GAME_COMPLETE_COLOR = {
			new Color(150, 150, 150),
			new Color(255,200,200),
			new Color(200,200,255)
		};
	
	GameButton[][] tiles;
	GamePanel parentGame;
	
	int status;
	
	int clickedX, clickedY;
	boolean tileWasClicked;

	public boolean wasTileClicked() { return tileWasClicked; }
	public int[] getClickedTile() { 
		tileWasClicked = false;
		int[] tile = {clickedX, clickedY};
		return tile; 
	}
	
	public int getStatus() { return status; }
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				// Only enables tiles that haven't been played on
				tiles[i][j].setEnabled(enabled && tiles[i][j].getState()==GameButton.UNPLAYED);
			}
		}
	}
	
	public SubGamePanel(GamePanel gamePanel) {
		// Partitions panel into a 3x3 grid
		super(3, 3);
		
		status = IN_PLAY;
	
		parentGame = gamePanel;
		
		tiles = new GameButton[3][3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				tiles[i][j] = new GameButton();
				// Adds to partition (i, j) with a width and height of 1, so a third the size of the panel
				addWidget(tiles[i][j], i, j, 1, 1);
			}
		}
	}
	
	public void update() {
		super.update();
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (tiles[i][j].isClicked()) {
					clickedX = i;
					clickedY = j;
					tileWasClicked = true;
					
					tiles[i][j].setState(parentGame.getTurn());
					status = isGameFinished();
					
					colorSet: {
						if (status == IN_PLAY) break colorSet;
						for (int ii = 0; ii < 3; ii++) {
							for (int jj = 0; jj < 3; jj++) {
								tiles[ii][jj].setFillColor(GAME_COMPLETE_COLOR[status]);
							}
						}
					}
				}
			}
		}
	}
	
	public void draw() {
		super.draw();
		
		// Drawing the four separating gridlines in the subgame
		draw.setStroke(new Color(0, 0, 0), 1);
		for (int i=1; i<3; i++) {
			for (int j=1; j<3; j++) {
				draw.line(tiles[i][j].getX(), y+5, tiles[i][j].getX(), y+h-5);
				draw.line(x+5, tiles[i][j].getY(), x+w-5, tiles[i][j].getY());
			}
		}
	}
	
	public void reset() {
		status = IN_PLAY;
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				tiles[i][j].reset();
			}
		}
		setEnabled(true);
	}
	
	// We only care if they're all equal and greater than 0, for each tile 0 means nothing has happened to it
	private int allEqual(int a, int b, int c) {
		if (a==b && b==c) return a;
		else              return 0;
	}
	// Checks if a game is complete. Returns 1 or 2 for player number, 0 for tie, or -1 for game not finished
	private int isGameFinished() {
		// Checking for a player win
		for (int i = 0; i < 3; i++) {
			// Note that you'll never get a situation where check is already 1 and you replace it with 2, by nature of the game
			// This is to generalize the checking as much as possible
			int check =             allEqual(tiles[0][i].getState(), tiles[1][i].getState(), tiles[2][i].getState());
			check = Math.max(check, allEqual(tiles[i][0].getState(), tiles[i][1].getState(), tiles[i][2].getState()));
			check = Math.max(check, allEqual(tiles[0][0].getState(), tiles[1][1].getState(),   tiles[2][2].getState()));
			check = Math.max(check, allEqual(tiles[2][0].getState(), tiles[1][1].getState(),   tiles[0][2].getState()));
			
			if (check > 0) {
				return check;
			}
		}
		// Checking if there are any unused tiles. This would mean the game is still technically playable
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (tiles[i][j].getState() == GameButton.UNPLAYED) {
					return IN_PLAY;
				}
			}
		}
		// If we get to this point, this subgame has resulted in a tie
		return TIE;
	}
}
