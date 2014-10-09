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
 * NB: Class adapted from Ian Warren's bounce code from Softeng 251.
 * All credit for the base code such as AnimationViewer, Shape, Painter and Graphics
 * Painter goes to him, but all other classes are my own work.
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

	/**
	 * Creates an AnimationViewer instance with a list of Shape objects and
	 * starts the animation.
	 * 
	 */
	public AnimationViewer() {
		_shapes = new ArrayList<Shape>();
		int minXY = 0;
		int maxXY = 1000;
		int minDXY = -2;
		int maxDXY = 2;

		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.BLACK, Color.DARK_GRAY));
		// Populate the list of Shapes.

		int randomX = minXY + (int) (Math.random() * ((maxXY - minXY) + 1));
		int randomY = minXY + (int) (Math.random() * ((maxXY - minXY) + 1));
		int randomDX = minDXY + (int) (Math.random() * ((maxDXY - minDXY) + 1));
		int randomDY = minDXY + (int) (Math.random() * ((maxDXY - minDXY) + 1));
		_shapes.add(new BounceNorm(randomX, randomY, randomDX,
				randomDY, 320, 240));
		// Start the animation.
		_timer.start();
	}

	public AnimationViewer(int numShapes) {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.BLACK, Color.DARK_GRAY));
		_shapes = new ArrayList<Shape>();
		int minXY = 0;
		int maxXY = 1000;
		int minDXY = 1;
		int maxDXY = 3;

		// Populate the list of Shapes.
		if (numShapes == 42) {

			_shapes.add(new DynamicRectangleShape(0, 0, -5, -3, 300, 150,
					Color.green, "Easter egg!!"));
			_shapes.add(new OvalShape(10, 10, 5, 7, 50, 70));
			_shapes.add(new DynamicRectangleShape(0, 500, 10, 25, 40, 60,
					Color.blue));
			_shapes.add(new FractalShape(400, 400, -10, 0, 60, 70, 4));
			_shapes.add(new AggregateShape(250, 100, -10, 1, 120, 70, 5));

			
		} else {
			for (int i = 1; i <= numShapes; i++) {
				int randomX = minXY
						+ (int) (Math.random() * ((maxXY - minXY) + 1));
				int randomY = minXY
						+ (int) (Math.random() * ((maxXY - minXY) + 1));
				int randomDX = minDXY
						+ (int) (Math.random() * ((maxDXY - minDXY) + 1));
				int randomDY = minDXY
						+ (int) (Math.random() * ((maxDXY - minDXY) + 1));
				_shapes.add(new Bouncemania(randomX, randomY, randomDX,
						randomDY, 320, 240, i, numShapes));
			}

			setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
					Color.BLACK, Color.DARK_GRAY));
			// Populate the list of Shapes.

			// Start the animation.
		}
		_timer.start();

	}

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
}
