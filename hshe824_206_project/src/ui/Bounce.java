package ui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import processes.BounceCreator;
import processes.DurationFinder;
import processes.VideoTask;
import bounce.AnimationViewer;

/**
 * This pane contains an animation viewer that can be used to 
 * view gifs generated from an input video. They can be made to bounce
 * off the walls and change state.
 * 
 * @author Harry She
 *
 */
public class Bounce extends JPanel {

	private static Bounce theInstance = null;
	private JTextField textField;
	protected AnimationViewer animationViewer;
	private String _currentFileString = "";
	private BounceCreator bCreator;
	private Integer _duration = 0;
	private JTextField _ShapesField;
	private int _numberOfShapes;
	private JProgressBar   progressBar;

	public Bounce() {

		setLayout(new MigLayout("", "[grow]", "[grow 90][grow 10]"));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED,
				null, null, null, null), "Bounce controls",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel, "cell 0 1,growx 10,growy");
		panel.setLayout(new MigLayout("", "[grow][][grow]", "[grow][grow]"));

		_ShapesField = new JTextField("1");
		_ShapesField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (_ShapesField.getText().length() < 3) {
					if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
						e.consume();
					}
				} else {
					e.consume();
				}
			}
		});
		
		_ShapesField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (Integer.parseInt(_ShapesField.getText()) >= 0) {
					_numberOfShapes = Integer.parseInt(_ShapesField.getText());
				}
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (Integer.parseInt(_ShapesField.getText()) >= 0) {
					_numberOfShapes = Integer.parseInt(_ShapesField.getText());
				} 
			}
		});
		
	    progressBar = new JProgressBar();
		panel.add(progressBar, "cell 0 0 2 1,growx,height 30");
		
		JLabel lblEnterHowMany = new JLabel("Enter how many GIF instances to generate (1-20 max):");
		panel.add(lblEnterHowMany, "flowx,cell 2 0,alignx center");

		panel.add(_ShapesField, "cell 2 0,width 50,alignx center");
		_ShapesField.setColumns(10);

		JButton bounce = new JButton("Bounce");
		panel.add(bounce, "cell 0 1,alignx center,growy,aligny center");
		bounce.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				 bCreator = new BounceCreator(_currentFileString,
						5, _duration);
				bCreator.execute();
				progressBar.setIndeterminate(true);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if (animationViewer != null) {
					remove(animationViewer);
					validate();
					repaint();
				}
				bCreator.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if ("success".equals(evt.getPropertyName())) {
							progressBar.setIndeterminate(false);
							setCursor(Cursor.getDefaultCursor());
							animationViewer = new AnimationViewer();
							add(animationViewer, "cell 0 0,growx 90,growy");
							validate();
							repaint();
						} }
				});

			}
		});
		
		JButton btnClearScreen = new JButton("Cancel/Clear screen");
		btnClearScreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (animationViewer != null) {
					bCreator.cancel(true);
					progressBar.setIndeterminate(false);
					setCursor(Cursor.getDefaultCursor());
					remove(animationViewer);
					validate();
					repaint();
				}
			}
		});
		panel.add(btnClearScreen, "cell 1 1,growy");

		JButton btnBouncemania = new JButton("Bouncemania!");
		panel.add(btnBouncemania, "cell 2 1,alignx center,growy,aligny center");
		btnBouncemania.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bCreator = new BounceCreator(_currentFileString,
						_numberOfShapes, _duration);
				bCreator.execute();
				progressBar.setIndeterminate(true);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				progressBar.setIndeterminate(true);
				if (animationViewer != null) {
					remove(animationViewer);
					validate();
					repaint();
				}
				bCreator.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if ("success".equals(evt.getPropertyName())) {
							progressBar.setIndeterminate(false);
							setCursor(Cursor.getDefaultCursor());
							animationViewer = new AnimationViewer(_numberOfShapes);
							add(animationViewer, "cell 0 0,growx 90,growy");
							validate();
							repaint();
						}else if ("failure".equals(evt.getPropertyName())) {
								progressBar.setIndeterminate(false);
								setCursor(Cursor.getDefaultCursor());
								JOptionPane
								.showMessageDialog(
										null,
										"Please enter a valid number between 1-20!",
										"Error!",
										JOptionPane.WARNING_MESSAGE);
								if (animationViewer != null) {
									remove(animationViewer);
									validate();
									repaint();
								}
						}

						}
					
				});
			}
		});
	}

	/**
	 * Sets the current input file to bounce and finds the duration of that file
	 * 
	 * @param inputFile
	 */
	public void setInputFile(String inputFile) {
		_currentFileString = inputFile;
		DurationFinder dFinder = new DurationFinder(_currentFileString);
		dFinder.execute();
		try {
			_duration = dFinder.get();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
		new File(VideoTask.tempDir).mkdirs();
	}

	/**
	 * Grab the singleton instance of this class.
	 * 
	 * @return
	 */
	public static Bounce getInstance() {
		if (theInstance == null) {
			theInstance = new Bounce();
		}
		return theInstance;
	}

}
