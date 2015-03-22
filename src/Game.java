import java.awt.Color;
import java.awt.Font;

import simple.run.*;
import simple.gui.panel.*;
import simple.gui.*;

public class Game extends SimpleGUIApp {
	
	public static void main(String[] args) { start(new Game(), "Ultimate Tic Tac Toe"); }
	public Game() { super(800, 500, 30); }
	
	int third, ninth;
	Button[][][][] map;
	int[][][][] state;
	int winMap[][];
	CustomDraw[] stateDraw;
	
	int turn;
	Button reset;
	int gameStatus;
	
	public void setup(){
		setBackgroundColor(new Color(200, 255, 200));
		
		reset = new Button(600, 50, 100, 70, "Reset Game", null);
		
		turn = 1;
		gameStatus = -1;
		
		third = 500/3;
		ninth = 500/9;
		map = new Button[3][3][3][3];
		state = new int[3][3][3][3];
		winMap = new int[3][3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				winMap[i][j] = 0;
				for (int x=0; x<3; x++) {
					for (int y=0; y<3; y++) {
						
						map[i][j][x][y] = new Button((int)Math.floor((500.0/3.0)*i + (500.0/9.0)*x), (int)Math.floor((500.0/3.0)*j + (500.0/9.0)*y), 
									(int)Math.ceil(500.0/9.0), (int)Math.ceil(500.0/9.0), "", null);
						map[i][j][x][y].setBorderColor(null);
						state[i][j][x][y] = 0;
					}
				}
			}
		}
		
		stateDraw = new CustomDraw[3];
		stateDraw[0] = new CustomDraw() { public void draw(Widget w) {} };
		stateDraw[1] = new CustomDraw() { public void draw(Widget w) {
				draw.setStroke(new Color(255, 20, 20), 4);
				draw.line(w.getX()+10, w.getY()+10, w.getX()+w.getWidth()-10, w.getY()+w.getHeight()-10);
				draw.line(w.getX()+10, w.getY()+w.getHeight()-10, w.getX()+w.getWidth()-10, w.getY()+10);
			}};
		stateDraw[2] = new CustomDraw() { public void draw(Widget w) {
				draw.setStroke(new Color(50, 50, 255), 4);
				draw.ovalCentered(w.getX()+w.getWidth()/2, w.getY()+w.getHeight()/2, w.getWidth()/2-5, w.getHeight()/2-5);
			}};
				
	}
	public void loop(){
		update();
		draw();
	}
	
	public void update() {
		reset.update();
		if (reset.isClicked()) {
			reset();
		}
		
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				for (int x=0; x<3; x++) {
					for (int y=0; y<3; y++) {
						map[i][j][x][y].update();
						if (map[i][j][x][y].isClicked() && state[i][j][x][y] == 0) {
							state[i][j][x][y] = turn;
							map[i][j][x][y].setCustomDraw(stateDraw[state[i][j][x][y]]);
							if (gridSectionFull(i, j)) {
								winMap[i][j] = turn;
								for (int xx=0; xx<3; xx++) {
									for (int yy=0; yy<3; yy++) {
										map[i][j][xx][yy].setFillColor((turn==1?new Color(255,200,200):new Color(200,200,255)));
										map[i][j][xx][yy].setEnabled(false);
									}
								}
								if (gameOver()) {
									for (int ii=0; ii<3; ii++) {
										for (int jj=0; jj<3; jj++) {
											for (int xx=0; xx<3; xx++) {
												for (int yy=0; yy<3; yy++) {
													map[ii][jj][xx][yy].setEnabled(false);
												}
											}
										}
									}
									continue;
								} 
							}
							turn = nextTurn(turn);
							
							if (gridSectionFull(x, y)) {
								enableAll();
							} else {
								for (int ii=0; ii<3; ii++) {
									for (int jj=0; jj<3; jj++) {
										for (int xx=0; xx<3; xx++) {
											for (int yy=0; yy<3; yy++) {
												map[ii][jj][xx][yy].setEnabled((ii==x && jj==y));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void draw() {
		reset.draw();
		
		draw.setFont(new Font("Arial", Font.BOLD, 20));
		if (gameStatus==-1) {
			draw.textCentered((turn==1?"X":"O")+"'s turn", 650, 200);
		} else {
			draw.textCentered((gameStatus==0? "Tie" : (gameStatus==1? "X":"O")+" wins!"), 650, 200);
		}
		
		draw.setStroke(Color.black, 4);
		draw.setFill(null);
		draw.rect(0, 0, 500, 500);
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				for (int x=0; x<3; x++) {
					for (int y=0; y<3; y++) {
						map[i][j][x][y].draw();
						draw.setStroke(new Color(0, 0, 0), 1);
						draw.line(third*i + ninth*x, 0, third*i + ninth*x, 500);
						draw.line(0, third*j + ninth*y, 500, third*j + ninth*y);
					}
				}
				if (i>0 && j>0) {
					draw.setStroke(new Color(0, 0, 0), 4);
					draw.line(third*i, 0, third*i, 500);
					draw.line(0, third*j, 500, third*j);
				}
			}
		}
		updateView();
	}
	
	int nextTurn(int n) {
		return (n==1 ? 2 : 1);
	}
	
	boolean gridSectionFull(int x, int y) {
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				if (state[x][y][i][j] == 0) {
					return gridHasWin(x, y);
				}
			}
		}
		return true;
	}
	
	boolean gridHasWin(int x, int y) {
		for (int player=1; player<=2; player++) {
			for (int i=0; i<3; i++) {
				if ((state[x][y][i][0]==player && state[x][y][i][1]==player && state[x][y][i][2]==player) ||
						(state[x][y][0][i]==player && state[x][y][1][i]==player && state[x][y][2][i]==player)) {
					return true;
				}
			}
			if ((state[x][y][0][0]==player && state[x][y][1][1]==player && state[x][y][2][2]==player) ||
					(state[x][y][2][0]==player && state[x][y][1][1]==player && state[x][y][0][2]==player)) {
				return true;
			}
		}
		return false;
	}
	
	boolean gameOver() {
		for (int player=1; player<=2; player++) {
			for (int i=0; i<3; i++) {
				if ((winMap[i][0]==player && winMap[i][1]==player && winMap[i][2]==player) ||
						(winMap[0][i]==player && winMap[1][i]==player && winMap[2][i]==player)) {
					gameStatus = player;
					return true;
				}
			}
			if ((winMap[0][0]==player && winMap[1][1]==player && winMap[2][2]==player) ||
					(winMap[2][0]==player && winMap[1][1]==player && winMap[0][2]==player)) {
				gameStatus = player;
				return true;
			}
		}
		
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				if (winMap[i][j]==0) {
					return false;
				}
			}
		}
		gameStatus = 0;
		return true;
	}
	
	void enableAll() {
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				if (gridSectionFull(i, j)) 
					continue;
				for (int x=0; x<3; x++) {
					for (int y=0; y<3; y++) {
						map[i][j][x][y].setEnabled(true);
					}
				}
			}
		}
	}
	
	void reset() {
		turn = 1;
		gameStatus = -1;
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				winMap[i][j] = 0;
				for (int x=0; x<3; x++) {
					for (int y=0; y<3; y++) {
						map[i][j][x][y].setEnabled(true);
						map[i][j][x][y].setFillColor(Color.white);
						map[i][j][x][y].setCustomDraw(stateDraw[0]);
						state[i][j][x][y] = 0;
					}
				}
			}
		}
	}
}
