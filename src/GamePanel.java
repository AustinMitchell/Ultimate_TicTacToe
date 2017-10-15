import java.awt.Color;

import simple.gui.panel.ScaledPanel;
import simple.gui.*;

public class GamePanel extends ScaledPanel {
	// Game states
	static final int IN_PLAY = -1;
	static final int TIE = 0;
	// Not used, but for the readers reference
	static final int PLAYER1 = 1;
	static final int PLAYER2 = 2;
	
	int _turn;
	int _status;
	boolean _eventOccured;
	
	SubGamePanel[][] subGames;
	
	public int getTurn() { return _turn; }
	public int getStatus() { return _status; }
	public boolean didEventOccur() { 
		boolean temp = _eventOccured;
		return (_eventOccured = false) != temp;
	}
	
	public GamePanel(int x, int y, int w, int h) {
		super(x, y, w, h, 3, 3);
		
		_turn = 1;
		_status = IN_PLAY;
		_eventOccured = false;
		
		subGames = new SubGamePanel[3][3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				subGames[i][j] = new SubGamePanel(this);
				addWidget(subGames[i][j], i, j, 1, 1);
			}
		}
	}
	
	public void updateWidget() {
		super.updateWidget();
		
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				// checks if an inner tile was clicked
				if (subGames[i][j].wasTileClicked()) {
					int[] nextSubGame = subGames[i][j].getClickedTile();
					_status = isGameFinished();
					_eventOccured = true;
					
					// Only if game is still active
					if (_status == IN_PLAY) {
						if (subGames[nextSubGame[0]][nextSubGame[1]].getStatus() != IN_PLAY) {
							// If the next game would be unplayable, enable all playable games
							for (int ii=0; ii<3; ii++) {
								for (int jj=0; jj<3; jj++) {
									subGames[ii][jj].setEnabled(true && subGames[ii][jj].getStatus() == IN_PLAY);
								}
							}
						} else {
							// Disable all games except for the coordinates of the next game
							for (int ii=0; ii<3; ii++) {
								for (int jj=0; jj<3; jj++) {
									subGames[ii][jj].setEnabled(false);
								}
							}
							subGames[nextSubGame[0]][nextSubGame[1]].setEnabled(true);
						}
					// Disable everything, handle resetting from the outside
					} else {
						for (int ii=0; ii<3; ii++) {
							for (int jj=0; jj<3; jj++) {
								subGames[ii][jj].setEnabled(false);
							}
						}
					}
					_turn = nextTurn(_turn);
				}
			}
		}
	}
	
	public void drawWidget() {
		super.drawWidget();
		
		// Drawing the four separating gridlines for partitioning subgames
		Draw.setStroke(new Color(0, 0, 0), 4);
		for (int i=1; i<3; i++) {
			for (int j=1; j<3; j++) {
				Draw.line(subGames[i][j].x(), _y, subGames[i][j].x(), _y+_h);
				Draw.line(_x, subGames[i][j].y(), _x+_w, subGames[i][j].y());
			}
		}
		
		Draw.setColors(null, Color.BLACK, 2);
		Draw.rect(_x, _y, _w, _h);
	}
	
	public void reset() {
		_status = IN_PLAY;
		_eventOccured = false;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				subGames[i][j].reset();
			}
		}
	}
	
	// Determines the next turn from the current turn
	int nextTurn(int n) {
		return (n==1 ? 2 : 1);
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
			int check =             allEqual(subGames[0][i].getStatus(), subGames[0][i].getStatus(), subGames[2][i].getStatus());
			check = Math.max(check, allEqual(subGames[i][0].getStatus(), subGames[i][1].getStatus(), subGames[i][2].getStatus()));
			check = Math.max(check, allEqual(subGames[0][0].getStatus(), subGames[1][1].getStatus(),   subGames[2][2].getStatus()));
			check = Math.max(check, allEqual(subGames[2][0].getStatus(), subGames[1][1].getStatus(),   subGames[0][2].getStatus()));
			
			if (check > 0) {
				return check;
			}
		}
		// Checking if there are any playable games. 
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (subGames[i][j].getStatus() == IN_PLAY) {
					return IN_PLAY;
				}
			}
		}
		// If we get to this point, this subgame has resulted in a tie
		return TIE;
	}
}
