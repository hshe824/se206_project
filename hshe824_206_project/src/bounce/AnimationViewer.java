package bounce;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

/**
 * Simple GUI program to show an animation of shapes. Class AnimationViewer is a
 * special kind of GUI component (JPanel), and as such an instance of
 * AnimationViewer can be added to a JFrame object. A JFrame object is a window
 * that can be closed, minimised, and maximised. The state of a AnimationViewer
 * object comprises a list of Shapes and a Timer object. An AnimationViewer
 * instance subscribes to events that are published by a Timer. In response to
 * receiving an event from the Timer, the AnimationViewer iterates through a
 * list of Shapes requesting that each Shape paints and moves itself.
 * 
 * @author Ian Warren & Harry She
 */
@SuppressWarnings("serial")
public class AnimationViewer extends JPanel implements ActionListener {
	// Frequency in milliseconds to generate ActionEvents.
	private final int DELAY = 20;

	// Collection of Shapes to animate.
	private List<Shape> _shapes;

	private Timer _timer = new Timer(DELAY, this);

	private static boolean _empty = false;

	/**
	 * Creates an AnimationViewer instance with a list of Shape objects and
	 * starts the animation.
	 * 
	 */
	public AnimationViewer() {
		_shapes = new ArrayList<Shape>();
		int minXY = 0;
		int maxXY = 1000;
		int minDXY = -4;
		int maxDXY = 4;

		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.BLACK, Color.DARK_GRAY));
		// Populate the list of Shapes.
		for (int i = 0; i < 5; i++) {
			int randomX = minXY + (int) (Math.random() * ((maxXY - minXY) + 1));
			int randomY = minXY + (int) (Math.random() * ((maxXY - minXY) + 1));
			int randomDX = minDXY
					+ (int) (Math.random() * ((maxDXY - minDXY) + 1));
			int randomDY = minDXY
					+ (int) (Math.random() * ((maxDXY - minDXY) + 1));
			_shapes.add(new StaticImageShape(randomX, randomY, randomDX,
					randomDY, 320, 240,i));
		}

		if (_shapes.size() == 0) {
			_shapes.add(new RectangleShape(300, 300, 0, 0, 400, 400,
					"CONGRATULATIONS!! You found an Easter Egg!! :)"));
			_shapes.add(new OvalShape(10, 10, 5, 7, 50, 70));
			_shapes.add(new DynamicRectangleShape(0, 500, 10, 25, 40, 60,
					Color.blue));
			_shapes.add(new DynamicRectangleShape(0, 0, -5, -3, 200, 50,
					Color.green, "Now you see me..."));
			_shapes.add(new FractalShape(400, 400, -10, 0, 60, 70, 2));
			_shapes.add(new AggregateShape(250, 100, -10, 1, 120, 70, 5));
		}

		// Start the animation.
		_timer.start();
	}
	
	public AnimationViewer(boolean isEasterEgg) {
		_shapes = new ArrayList<Shape>();

		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.BLACK, Color.DARK_GRAY));
		// Populate the list of Shapes.

			_shapes.add(new RectangleShape(300, 300, 0, 0, 400, 400,
					"CONGRATULATIONS!! You found an Easter Egg!! :)"));
			_shapes.add(new OvalShape(10, 10, 5, 7, 50, 70));
			_shapes.add(new DynamicRectangleShape(0, 500, 10, 25, 40, 60,
					Color.blue));
			_shapes.add(new DynamicRectangleShape(0, 0, -5, -3, 200, 50,
					Color.green, "Now you see me..."));
			_shapes.add(new FractalShape(400, 400, -10, 0, 60, 70, 2));
			_shapes.add(new AggregateShape(250, 100, -10, 1, 120, 70, 5));
		

		// Start the animation.
		_timer.start();
	}


//	public AnimationViewer(final PhotoCollection photoCollection,
//			int _numberOfShapes) {
//		int minXY = 0;
//		int maxXY = 750;
//		int minDXY = -10;
//		int maxDXY = 10;
//		_shapes = new ArrayList<Shape>();
//
//		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
//				Color.BLACK, Color.DARK_GRAY));
//		// Populate the list of Shapes.
//		for (int i = 0; i < _numberOfShapes; i++) {
//			int randomX = minXY + (int) (Math.random() * ((maxXY - minXY) + 1));
//			int randomY = minXY + (int) (Math.random() * ((maxXY - minXY) + 1));
//			int randomDX = minDXY
//					+ (int) (Math.random() * ((maxDXY - minDXY) + 1));
//			int randomDY = minDXY
//					+ (int) (Math.random() * ((maxDXY - minDXY) + 1));
//
//			_shapes.add(new ImageShape(randomX, randomY, randomDX, randomDY,
//					200, 200, photoCollection));
//		}
//
//		if (_empty == true) {
//			_shapes.add(new RectangleShape(300, 300, 0, 0, 400, 400,
//					"NO FAVOURITES EXIST!! So enjoy a little Easter Egg!! :)"));
//			_shapes.add(new OvalShape(10, 10, 5, 7, 50, 70));
//			_shapes.add(new DynamicRectangleShape(0, 500, 10, 25, 40, 60,
//					Color.blue));
//			_shapes.add(new DynamicRectangleShape(0, 0, -5, -3, 200, 50,
//					Color.green, "Now you see me..."));
//			_shapes.add(new FractalShape(400, 400, -10, 0, 60, 70, 2));
//			_shapes.add(new AggregateShape(250, 100, -10, 1, 120, 70, 5));
//		}
//
//		// Start the animation.
//		_timer.start();
//
//		addMouseListener(new MouseAdapter() {
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//				if (e.getModifiers() != MouseEvent.BUTTON3_MASK) {
//					int minXY = 0;
//					int maxXY = 750;
//					int minDXY = -10;
//					int maxDXY = 10;
//					int randomX = minXY
//							+ (int) (Math.random() * ((maxXY - minXY) + 1));
//					int randomY = minXY
//							+ (int) (Math.random() * ((maxXY - minXY) + 1));
//					int randomDX = minDXY
//							+ (int) (Math.random() * ((maxDXY - minDXY) + 1));
//					int randomDY = minDXY
//							+ (int) (Math.random() * ((maxDXY - minDXY) + 1));
//
//					_shapes.add(new ImageShape(randomX, randomY, randomDX,
//							randomDY, 200, 200, photoCollection));
//				}
//				if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
//					if (_shapes.size() != 0)
//						_shapes.remove(_shapes.size() - 1);
//				}
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//			}
//		});
//	}

	/**
	 * Called by the Swing framework whenever this AnimationViewer object should
	 * be repainted. This can happen, for example, after an explicit repaint()
	 * call or after the window that contains this AnimationViewer object has
	 * been exposed after being hidden by another window.
	 * 
	 */
	public void paintComponent(Graphics g) {
		// Call inherited implementation to handle background painting.
		super.paintComponent(g);

		// Calculate bounds of animation screen area.
		int width = getSize().width;
		int height = getSize().height;

		// Create a GraphicsPainter that Shape objects will use for drawing.
		// The GraphicsPainter delegates painting to a basic Graphics object.
		Painter painter = new GraphicsPainter(g);

		// Progress the animation.
		for (Shape s : _shapes) {
			s.paint(painter);
			s.move(width, height);
		}
	}

	/**
	 * Notifies this AnimationViewer object of an ActionEvent.
	 */
	public void actionPerformed(ActionEvent e) {
		// Request that the AnimationViewer repaints itself. The call to
		// repaint() will cause the AnimationViewer's paintComponent() to be
		// called.
		repaint();
	}

	public static void isEmpty() {
		_empty = true;

	}

	public static void isNotEmpty() {
		_empty = false;
	}

	public static boolean getEmptyStatus() {
		return _empty;
	}

	/**
	 * Main program method to create an AnimationViewer object and display this
	 * within a JFrame window.
	 */
	// public static void main(String[] args) {
	// JFrame frame = new JFrame("Animation viewer");
	// frame.add(new AnimationViewer());
	//
	// // Set window properties.
	// frame.setSize(500, 500);
	// frame.setVisible(true);
	// frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	// }
}
