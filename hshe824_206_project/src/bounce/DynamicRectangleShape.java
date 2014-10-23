package bounce;

import java.awt.Color;

/**
 * This shape class produces a dynamic rectangle shape that changes to a regular
 * rectangle shape appearance when bouncing off the top and bottom walls, and
 * changes to a filled rectangle shape when it bounces off the side walls.
 * 
 * 
 * @author: Harry She
 */

public class DynamicRectangleShape extends Shape {
	private String _rectType;
	private Color _color;

	public DynamicRectangleShape() {
		super();
	}

	/**
	 * Creates a RectangleShape instance with specified values for instance
	 * variables.
	 * 
	 * @param x
	 *            x position.
	 * @param y
	 *            y position.
	 * @param deltaX
	 *            speed and direction for horizontal axis.
	 * @param deltaY
	 *            speed and direction for vertical axis.
	 * @param width
	 *            width in pixels.
	 * @param height
	 *            height in pixels.
	 * @param color
	 *            color of dynamic rectangle.
	 * 
	 */
	public DynamicRectangleShape(int x, int y, int deltaX, int deltaY, int width, int height, Color color) {
		super(x, y, deltaX, deltaY, width, height);
		_rectType = "drawNormRect";
		_color = color;
	}

	public DynamicRectangleShape(int x, int y, int deltaX, int deltaY, int width, int height, Color color, String text) {
		super(x, y, deltaX, deltaY, width, height, text);
		_rectType = "drawNormRect";
		_color = color;
		_text = text;
	}

	/*
	 * Paints this RectangleShape object using the supplied Painter object.
	 */
	protected void paints(Painter painter) {
		Color originalColor = painter.getColor();
		painter.drawRect(_x, _y, _width, _height);
		if (_rectType.equals("drawColorRect")) {
			painter.setColor(this._color);
			painter.fillRect(_x, _y, _width, _height);
		}
		painter.setColor(originalColor);
	}

	public void move(int _width, int _height) {
		super.move(_width, _height);
		if (_y == 0 || _y == _height - this._height) {
			_rectType = "drawNormRect";
		}
		if (_x == 0 || _x == _width - this._width) {
			_rectType = "drawColorRect";
		}
	}

	public String get_rectType() {
		return _rectType;
	}

	public Color get_color() {
		return _color;
	}
}
