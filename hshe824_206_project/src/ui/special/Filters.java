package ui.special;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import processes.video.FilterTask;
import processes.video.VideoTask;
import ui.Pane;
import ui.editors.Playback;
import ui.filesystem.Library;

/**
 * Class that deals with the text editing of videos. Ie. The creation of title
 * and credits.
 *
 * @author Harry She and Greggory Tan
 *
 */
@SuppressWarnings("serial")
public class Filters extends Pane {

	private static Filters theInstance = null;

	private String _inputFile;
	protected Playback _originalPlayback;
	private JButton _saveButton;
	JButton _previewButton;
	private ButtonGroup _group;
	JProgressBar _textProgressBar;
	protected VideoTask videoTask;

	private String _cmd="Blur"; 
	private FilterTask filterTask;

	private String _outputLocationFiltered;


	private JTextArea _details;

	private Dimension _videoSize;

	protected Color _colourTint=Color.red;

	

	/**
	 * Create the panel.
	 * 
	 */
	private Filters() {
		setLayout(new MigLayout("", "[400px,grow][400px,grow]", "[400px,grow][250px,grow]"));

		// Original video is displayed in this panel
		JPanel player = new JPanel(new MigLayout("", "[]", "[]"));
		player.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED, null, null), "Original Video",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		_originalPlayback = new Playback();
		player.add(_originalPlayback, "cell 0 0");
		add(player, "cell 0 0,grow");
		
		_details = new JTextArea();
		_details.setEditable(false);
		_details.setWrapStyleWord(true);
		JScrollPane _detailsScrollPane = new JScrollPane(_details);
		_detailsScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(_detailsScrollPane, "cell 1 0,grow");

		JPanel textPanel = new JPanel();
		textPanel.setBorder(new TitledBorder(new BevelBorder(
				BevelBorder.LOWERED, null, null, null, null),
				"Filters to apply", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		add(textPanel, "cell 0 1 2 1,alignx center,grow");
		textPanel.setLayout(new MigLayout("", "[200px,grow][200px,grow][200px,grow][200px,grow]", "[50px,grow][50px,grow][50px,grow][50px,grow]"));

		JLabel typeLabel = new JLabel("Type:");
		textPanel.add(typeLabel, "cell 0 0 2 1,alignx center");

		_previewButton = new JButton("Preview");
		textPanel.add(_previewButton, "flowx,cell 2 0,alignx center");

		_saveButton = new JButton("Save");
		textPanel.add(_saveButton, "cell 3 0,alignx center");

		_textProgressBar = new JProgressBar();
		textPanel.add(_textProgressBar, "cell 2 1 2 2,growx,alignx center,height 50");

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					filterTask.cancel(true);
				} catch (NullPointerException ne) {
				}
			}
		});
		
		_group = new ButtonGroup();

		JRadioButton blur = new JRadioButton("Blur");
		blur.setSelected(true);
		blur.addActionListener(new FilterListener());
		textPanel.add(blur, "cell 0 1,alignx center");
		_group.add(blur);

		JRadioButton horFlip = new JRadioButton("Mirror Video");
		horFlip.addActionListener(new FilterListener());
		textPanel.add(horFlip, "flowx,cell 1 1,alignx center");
		_group.add(horFlip);

		JRadioButton negate = new JRadioButton("Negative");
		negate.addActionListener(new FilterListener());
		textPanel.add(negate, "cell 0 2,alignx center");
		_group.add(negate);


		JRadioButton split = new JRadioButton("Fade into start");
		split.addActionListener(new FilterListener());
		textPanel.add(split, "cell 1 2,alignx center");
		_group.add(split);
		
		JRadioButton rdbtnAddColourTint = new JRadioButton("Add colour tint");
		textPanel.add(rdbtnAddColourTint, "cell 0 3,alignx center,growy");
		rdbtnAddColourTint.addActionListener(new FilterListener());
		_group.add(rdbtnAddColourTint);

		
		JButton _colourButton = new JButton();
		_colourButton.setText("Choose colour tint");
		textPanel.add(_colourButton, "cell 1 3,alignx center,growy");
		_colourButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color newCol = JColorChooser.showDialog(
						Filters.getInstance(), "Choose font colour",
						Color.red);
				if (newCol != null){
					_colourTint=newCol;
				}
			}
		});


//		JRadioButton radioButton = new JRadioButton("Credits");
//		textPanel.add(radioButton, "cell 0 4,alignx center");
//
//		JRadioButton radioButton_1 = new JRadioButton("fdsafd");
//		textPanel.add(radioButton_1, "cell 1 4,alignx center");

		textPanel.add(cancelButton, "cell 2 3 2 1,alignx center,grow");

		

		setUpListener();

	}
	
	class FilterListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
				_cmd=e.getActionCommand();
		}
		
	}

	/**
	 * This method sets up the listeners for the components
	 */
	public void setUpListener() {
	
	 _saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Check if file exists first
				File _file = new File(_outputLocationFiltered);
				if (_file.exists()) {
					Object[] options = {"Overwrite", "Cancel" };
					int action = JOptionPane
							.showOptionDialog(
									null,
									"File: "
											+ _file
											+ " already exists, do you wish to overwrite?",
									"ERROR: File already exists:",
									JOptionPane.CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE, null,
									options, options[1]);
					if (action == JOptionPane.YES_OPTION) {
						// Overwrite
						_file.delete();
					} else if (action == JOptionPane.NO_OPTION) {
						// Cancel
						return;
					}
				}
				filterTask = new FilterTask(_inputFile, _outputLocationFiltered, _cmd,false, _colourTint);
				_textProgressBar.setIndeterminate(true);
				filterTask.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if ("failure".equals(evt.getPropertyName())) {
							setCursor(Cursor.getDefaultCursor());
							_textProgressBar.setIndeterminate(false);
							JOptionPane.showMessageDialog(null,
									evt.getNewValue(), "Error!",
									JOptionPane.WARNING_MESSAGE);
						} else if ("success".equals(evt.getPropertyName())) {
							setCursor(Cursor.getDefaultCursor());
							_textProgressBar.setIndeterminate(false);
							_textProgressBar.setValue(100);
							JOptionPane
									.showMessageDialog(
											null,
											"Adding of filter to:" + _inputFile+ " was successful!",
											"Filter added!",
											JOptionPane.INFORMATION_MESSAGE);
							_textProgressBar.setValue(0);
						} else if ("cancelled".equals(evt.getPropertyName())) {
							setCursor(Cursor.getDefaultCursor());
							_textProgressBar.setIndeterminate(false);
							JOptionPane
							.showMessageDialog(
									null,
									evt.getNewValue(),
									"Cancelled!",
									JOptionPane.WARNING_MESSAGE);
						}	
					}
				});
				filterTask.execute();
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			}
		});
	 
	 _previewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_originalPlayback.stopPlayer();
				filterTask = new FilterTask(_inputFile, _outputLocationFiltered, _cmd,true,_colourTint);
				filterTask.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if ("failure".equals(evt.getPropertyName())) {
							JOptionPane.showMessageDialog(null,
									evt.getNewValue(), "Error!",
									JOptionPane.WARNING_MESSAGE);
						} else if ("cancelled".equals(evt.getPropertyName())) {
							JOptionPane
							.showMessageDialog(
									null,
									evt.getNewValue(),
									"Cancelled!",
									JOptionPane.WARNING_MESSAGE);
						}	
					}
				});
				filterTask.execute();
			}
		});
	}
	 /**
		 * Mutator method for setting the input file of this audio editor (current
		 * file being edited) by the library class. It also assigns values to the
		 * appropriate name fields for the locations and basenames of the input file
		 * as well as the details of that file. It then also starts the small
		 * preview player with this input file.
		 * 
		 * @param _inputFile
		 */
		public void setInputFile(String _inputFile) {
			this._inputFile = _inputFile;
			String basename = _inputFile.substring(_inputFile
					.lastIndexOf(File.separator) + 1);
			_details.setText(Library.getDetails(_inputFile).toString());
			String filenameNoExtension = basename.substring(0,
					basename.lastIndexOf("."));
			String Extension = basename.substring(basename.lastIndexOf("."));
			_outputLocationFiltered = Library.outputDir + File.separator
					+ filenameNoExtension + "[FILTER_ADDED-VAMIX].mp4";
			_originalPlayback.startPlayer(_inputFile);

		}

	/**
	 * This method stops all the players in VideoEditor
	 */
	public void stopAllPlayers() {
		_originalPlayback.stopPlayer();
	}
	
	

	public static Filters getInstance() {
		if (theInstance == null) {
			theInstance = new Filters();
		}
		return theInstance;
	}
}
