package bounce;

import java.io.File;

import javax.swing.ImageIcon;

import processes.video.VideoTask;

/**
 * Class that displays an animated GIF image of the video passed.
 * 
 * @author: Harry She
 * 
 */
public class Bouncemania extends Shape {

	private int _GIFState;
	private ImageIcon _imageIcon;

	public Bouncemania() {
		super();
	}

	public Bouncemania(int x, int y, int deltaX, int deltaY, int width,
			int height, int Gif, int numShapes) {
		super(x, y, deltaX, deltaY, width, height);
		_GIFState = Gif;
		
		//Gets gif image to display in this instance
		_imageIcon=new ImageIcon(VideoTask.tempDir + File.separator + Gif +".gif");
		
	}

	/**
	 * Paints this RectangleShape object using the supplied Painter object.
	 */
	protected void paints(Painter painter) {
		// Paint corresponding current GIF file
		painter.drawRect(_x, _y, _width, _height);
		painter.drawImage(_imageIcon.getImage(), _x, _y, _width, _height,
				null);		
		}
	

	public void move(int _width, int _height) {
		super.move(_width, _height);
	}
}
