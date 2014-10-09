package bounce;

/**
 * This shape class produces a fractal shape consisting of 
 * overlapping circles.
 * 
 * 
 * @author: Harry She
 */
public class FractalShape extends Shape {
	private int radius;

	public FractalShape(int x, int y, int deltaX, int deltaY, int width,
			int height, int radius) {
		super(x, y, deltaX, deltaY, width, height);
		this.radius = radius;

	}

	public void paints(Painter painter) {
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

		if (r > 0) {
			// draw 7 circles
			painter.drawOval(x1, y1, adjR, adjR);
			painter.drawOval(x2, y2, adjR, adjR);
			painter.drawOval(x3, y3, adjR, adjR);
			painter.drawOval(x4, y4, adjR, adjR);
			painter.drawOval(x5, y5, adjR, adjR);
			painter.drawOval(x6, y6, adjR, adjR);
			painter.drawOval(x7, y7, adjR, adjR);
		}

	}
}
