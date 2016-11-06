import java.awt.Color;

import simple.gui.*;

public class GameButton extends Button {
	static final int UNPLAYED = 0;
	// Not used, but for the readers reference
	static final int PLAYER1  = 1;
	static final int PLAYER2  = 2;
	
	final static CustomDraw[] STATE_DRAW = {
		new CustomDraw() { public void draw(Widget w) {} },
		// Player 1 is x, so it stores the x drawing method. This will be given to any button object that needs to show an x on it
		new CustomDraw() { public void draw(Widget w) {
				draw.setStroke(new Color(255, 20, 20), 4);
				draw.line(w.getX()+10, w.getY()+10, w.getX()+w.getWidth()-10, w.getY()+w.getHeight()-10);
				draw.line(w.getX()+10, w.getY()+w.getHeight()-10, w.getX()+w.getWidth()-10, w.getY()+10);
			}},
		// Same as before but for Player 2 and with an O
		new CustomDraw() { public void draw(Widget w) {
				draw.setStroke(new Color(50, 50, 255), 4);
				draw.ovalCentered(w.getX()+w.getWidth()/2, w.getY()+w.getHeight()/2, w.getWidth()/2-5, w.getHeight()/2-5);
			}}
	};
	
	int state;
	
	public int getState() { return this.state; }
	
	public void setState(int state) {
		this.state = state;
		setCustomDraw(STATE_DRAW[state]);
	}
	
	public GameButton() {
		super();
		
		setBorderColor(null);
		setFillColor(Color.WHITE);
		
		state = UNPLAYED;
		setCustomDraw(STATE_DRAW[state]);
	}
	
	public void reset() {
		setFillColor(Color.WHITE);
		setState(UNPLAYED);
	}
}
