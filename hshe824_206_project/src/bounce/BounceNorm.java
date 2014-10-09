package bounce;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import processes.VideoTask;

/**
 * This class produces a rectangle shape that contains a gif file which changes
 * to another gif file everytime it bounces on one of the walls.
 * 
 * @author: Harry She
 * 
 */
public class BounceNorm extends Shape {

	private int _state;

	ArrayList<ImageIcon> _images = new ArrayList<ImageIcon>();

	public BounceNorm(int x, int y, int deltaX, int deltaY, int width,
			int height) {
		super(x, y, deltaX, deltaY, width, height);
		_state = 0;

		//Add all gifs to the arraylist
		for (int i = 1; i < 5; i++) {
			_images.add(new ImageIcon(VideoTask.tempDir + File.separator + i
					+ ".gif"));
			System.out.println(VideoTask.tempDir + File.separator + i + ".gif");
		}

	}

	/**
	 * Paints this RectangleShape object using the supplied Painter object.
	 */

	protected void paints(Painter painter) {

		// Draw current state of image every bounce
		painter.drawRect(_x, _y, _width, _height);
		painter.drawImage(_images.get(_state).getImage(), _x, _y, _width,
				_height, null);

	}

	public void move(int _width, int _height) {
		// Change state every bounce
		super.move(_width, _height);
		if (_y == 0 || _y == _height - this._height || _x == 0
				|| _x == _width - this._width) {
			if (_state != _images.size() - 1) {
				_state++;
			} else {
				_state = 0;
			}
		}
	}
}
