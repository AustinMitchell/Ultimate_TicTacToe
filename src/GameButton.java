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
				Draw.setStroke(new Color(255, 20, 20), 4);
				Draw.line(w.x()+10, w.y()+10, w.x()+w.w()-10, w.y()+w.h()-10);
				Draw.line(w.x()+10, w.y()+w.h()-10, w.x()+w.w()-10, w.y()+10);
			}},
		// Same as before but for Player 2 and with an O
		new CustomDraw() { public void draw(Widget w) {
				Draw.setStroke(new Color(50, 50, 255), 4);
				Draw.ovalCentered(w.x()+w.w()/2, w.y()+w.h()/2, w.w()/2-5, w.h()/2-5);
			}}
	};
	
	int state;
	
	public int getState() { return this.state; }
	
	public void setState(int state) {
		this.state = state;
		setCustomDrawAfter(STATE_DRAW[state]);
	}
	
	public GameButton() {
		super();
		
		setBorderColor(null);
		setFillColor(Color.WHITE);
		
		state = UNPLAYED;
		setCustomDrawAfter(STATE_DRAW[state]);
	}
	
	public void reset() {
		setFillColor(Color.WHITE);
		setState(UNPLAYED);
	}
}
