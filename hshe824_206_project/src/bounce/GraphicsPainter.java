package bounce;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

/**
 * Implementation of the Painter interface that delegates drawing to a
 * java.awt.Graphics object.
 * 
 * @author Ian Warren and Harry She
 */
public class GraphicsPainter implements Painter {
	// Delegate object.
	private Graphics _g;

	/**
	 * Creates a GraphicsPainter object and sets its Graphics delegate.
	 */
	public GraphicsPainter(Graphics g) {
		this._g = g;
	}

	/**
	 * @see bounce.Painter.drawRect
	 */
	public void drawRect(int x, int y, int width, int height) {
		_g.drawRect(x, y, width, height);
	}

	/**
	 * @see bounce.Painter.drawOval
	 */
	public void drawOval(int x, int y, int width, int height) {
		_g.drawOval(x, y, width, height);
	}

	/**
	 * @see bounce.Painter.drawLine.
	 */
	public void drawLine(int x1, int y1, int x2, int y2) {
		_g.drawLine(x1, y1, x2, y2);
	}

	public void fillRect(int x, int y, int width, int height) {
		_g.fillRect(x, y, width, height);
	}

	public Color getColor() {
		return _g.getColor();

	}

	public void setColor(Color color) {
		_g.setColor(color);
	}

	/**
	 * Draws a text string inside a given shape.
	 * 
	 * @param text
	 *            text string
	 */
	public void drawCentredText(int x, int y, int width, int height, String text) {
		FontMetrics font = _g.getFontMetrics();
		int ascent = font.getAscent();
		int descent = font.getDescent();
		int textWidth = font.stringWidth(text);
		/*
		 * If the degree by which the characters extend above the baseline is
		 * greater than the degree that the characters extend below the
		 * baseline, then draw the text at half height of the shape + the
		 * difference between the ascent and the descent.
		 * 
		 * Conversely if the degree by which the characters extend above the
		 * baseline is less than the degree that the characters extend below the
		 * baseline, then draw the text at half height of the shape + the
		 * addition of the ascent and descent of the text.
		 * 
		 * For both cases draw the text at the x position x of the shape + half
		 * the width of the shape - the width of the text.
		 */
		if (ascent > descent) {
			_g.drawString(text, x + width / 2 - textWidth / 2, y + height / 2 + (ascent - descent) / 2);
		} else if (descent > ascent) {
			_g.drawString(text, x + width / 2 - textWidth / 2, y + height / 2 + (ascent + descent) / 2);
		}
	}

	/*
	 * 
	 * 
	 * @see bounce.Painter#drawImage(java.awt.Image, int, int, int, int,
	 * java.lang.Object)
	 */
	public void drawImage(Image img, int i, int j, int _width, int _height, Object object) {
		_g.drawImage(img, i, j, _width, _height, null);
	}

}
