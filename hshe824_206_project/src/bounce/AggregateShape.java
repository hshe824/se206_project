package bounce;

import java.awt.Color;

/**
 * This shape class produces an aggregate fractal shape consisting of varying
 * numbers of overlapping circles that change colour and state upon each bounce
 * with a wall.
 * 
 * @author Harry She
 */
public class AggregateShape extends Shape {

	private int radius;
	private int _state;

	public AggregateShape(int x, int y, int deltaX, int deltaY, int width, int height, int radius) {
		super(x, y, deltaX, deltaY, width, height);
		this.radius = radius;
		_state = 1;
	}

	protected void paints(Painter painter) {
		int r = radius;
		int adjR = 16 * r;
		// x values of circle centres
		int x1 = _x + 8 * r;
		int x2 = _x + 8 * r;
		int x3 = _x + r;
		int x4 = _x + 15 * r;
		int x5 = _x + r;
		int x6 = _x + 15 * r;
		int x7 = _x + 8 * r;
		// y values of circle centres
		int y1 = _y - 8 * r;
		int y2 = _y;
		int y3 = _y - 4 * r;
		int y4 = _y - 4 * r;
		int y5 = _y - 12 * r;
		int y6 = _y - 12 * r;
		int y7 = _y - 16 * r;

		// Different aggregate fractal shapes
		if (_state == 1) {
			painter.setColor(Color.red);
			painter.drawOval(x5, y5, adjR, adjR);
			painter.drawOval(x6, y6, adjR, adjR);
		}

		if (_state == 2) {
			painter.setColor(Color.orange);
			painter.drawOval(x1, y1, adjR, adjR);
			painter.drawOval(x2, y2, adjR, adjR);
			painter.drawOval(x3, y3, adjR, adjR);
		}

		if (_state == 3) {
			painter.setColor(Color.green);
			painter.drawOval(x2, y2, adjR, adjR);
			painter.drawOval(x3, y3, adjR, adjR);
			painter.drawOval(x4, y4, adjR, adjR);
			painter.drawOval(x5, y5, adjR, adjR);
			painter.drawOval(x6, y6, adjR, adjR);
			painter.drawOval(x7, y7, adjR, adjR);
		}

		if (_state == 4) {
			// draw 7 circles
			painter.setColor(Color.blue);
			painter.drawOval(x1, y1, adjR, adjR);
			painter.drawOval(x2, y2, adjR, adjR);
			painter.drawOval(x3, y3, adjR, adjR);
			painter.drawOval(x4, y4, adjR, adjR);
			painter.drawOval(x5, y5, adjR, adjR);
			painter.drawOval(x6, y6, adjR, adjR);
			painter.drawOval(x7, y7, adjR, adjR);
		}

	}

	public void move(int width, int height) {
		super.move(width, height);
		if (_y == 0 || _y == height - this._height) {
			if (_state != 4) {
				_state++;
			} else {
				_state = 1;
			}
		}
		if (_x == 0 || _x == width - this._width) {
			if (_state != 4) {
				_state++;
			} else {
				_state = 1;
			}
		}
	}
}
