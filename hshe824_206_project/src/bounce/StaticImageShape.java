package bounce;

import java.io.File;

import javax.swing.ImageIcon;

import processes.VideoTask;
import ui.VideoEditor;

/*
 * Class that cycles through animated GIF images each bounce.
 * 
 * Author: Harry She
 * 
 */
public class StaticImageShape extends Shape {

	private int _GIFState;
	// Read in the corresponding gif files to ImageIcons.
	ImageIcon i1 = new ImageIcon(VideoTask.tempDir + File.separator + "1.gif");
	ImageIcon i2 = new ImageIcon(VideoTask.tempDir + File.separator + "2.gif");
	ImageIcon i3 = new ImageIcon(VideoTask.tempDir + File.separator + "3.gif");
	ImageIcon i4 = new ImageIcon(VideoTask.tempDir + File.separator + "4.gif");
	ImageIcon i5 = new ImageIcon(VideoTask.tempDir + File.separator + "5.gif");

	public StaticImageShape() {
		super();
	}

	public StaticImageShape(int x, int y, int deltaX, int deltaY, int width,
			int height, int Gif) {
		super(x, y, deltaX, deltaY, width, height);
		_GIFState = Gif;
	}

	/**
	 * Paints this RectangleShape object using the supplied Painter object.
	 */

	protected void paints(Painter painter) {
		// Paint corresponding current GIF file
		painter.drawRect(_x, _y, _width, _height);
		switch (_GIFState){
		case 0:
			painter.drawImage(i1.getImage(), _x, _y, _width, _height, this);
			break;
		case 1:
			painter.drawImage(i2.getImage(), _x, _y, _width, _height, this);
			break;
		case 2:
			painter.drawImage(i3.getImage(), _x, _y, _width, _height, this);
			break;
		case 3:
			painter.drawImage(i4.getImage(), _x, _y, _width, _height, this);
			break;
		case 4:
			painter.drawImage(i5.getImage(), _x, _y, _width, _height, this);
			break;
		}
	}

	public void move(int _width, int _height) {
		super.move(_width, _height);
	}
}
