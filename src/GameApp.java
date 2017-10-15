import java.awt.Color;
import java.awt.Font;

import simple.gui.*;
import simple.gui.panel.*;
import simple.gui.textarea.*;
import simple.run.SimpleGUIApp;


public class GameApp extends SimpleGUIApp {
	public static void main(String[] args) { start(new GameApp(), "Ultimate Tic-Tac-Toe"); }
	public GameApp() { super(1200, 800, 30); }
	
	GamePanel game;
	Button reset; 
	Label statusLabel;
	BasicPanel screen;
	
	@Override
	public void setup() {
		final int border = getHeight();
		
		game = new GamePanel(0, 0, border, border);
				
		screen = new BasicPanel(0, 0, getWidth(), getHeight()) {{
			addWidget(game);
			
			final int sidePanelWidth = _w-border;
			addWidget(new ScaledPanel(border, 0, sidePanelWidth, getHeight()) {{
					setDrawContainingPanel(true);
					setFillColor(new Color(200, 255, 200));
					setBorderColor(null);
				
					reset = new Button("Reset Game");
					
					statusLabel = new Label("");
					statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
					
					addWidget(reset, 35, 10, 30, 6);
					addWidget(statusLabel, 20, 25, 60, 20);
				}});
		}};
		
		updateLabel();
	}

	@Override
	public void loop() {
		screen.update();
		
		if (reset.clicked()) {
			game.reset();
			updateLabel();
		}
		
		if (game.didEventOccur()) {
			updateLabel();
		}
		
		screen.draw();
		updateView();
	}

	public void updateLabel() {
		switch(game.getStatus()) {
		case GamePanel.IN_PLAY:
			statusLabel.setText(((game.getTurn()==1)?"X":"O") + "'s turn");
			break;
		case GamePanel.TIE:
			statusLabel.setText("Game Tied!");
			break;
		case GamePanel.PLAYER1:
			statusLabel.setText("X wins!");
			break;
		case GamePanel.PLAYER2:
			statusLabel.setText("O wins!");
			break;
		}
	}
}
