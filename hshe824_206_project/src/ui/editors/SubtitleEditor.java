package ui.editors;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Subtitle;
import model.SubtitleModel;
import net.miginfocom.swing.MigLayout;
import ui.Pane;
import ui.filesystem.Library;

/**
 * This class is the subtitle pane that is where the user can choose to add
 * subtitles to a particular video file. This can then be saved and also loaded
 * from file.
 * 
 * @author Harry She
 *
 */
@SuppressWarnings("serial")
public class SubtitleEditor extends Pane {

	private static SubtitleEditor theInstance = null;

	private Playback _playback;
	private String _inputFile;
	private File _srtFile;
	private JTable _table;
	private SubtitleModel _model;
	private JPanel panel;
	private JButton browse;
	private JLabel lblNewLabel;
	private JScrollPane jScrollPane;
	private JButton setStartTime;
	private JButton addSub;
	private JButton removeSub;
	private JButton setEndTime;

	/**
	 * Create the panel.
	 */
	public SubtitleEditor() {
		setLayout(new MigLayout("", "[grow][grow]", "[350px,grow][200px,grow]"));
		// Setup the layout
		setupSubtitleLayout();
	}

	/**
	 * Method that sets up the layout and listeners for the subtitles pane
	 */
	private void setupSubtitleLayout() {
		_model = new SubtitleModel();
		_playback = new Playback();
		add(_playback, "cell 0 0 2 1,grow");

		panel = new JPanel();
		panel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null),
				"Subtitles Editing", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		add(panel, "cell 0 1 2 1,grow");
		panel.setLayout(new MigLayout("", "[550px,grow][300px,grow]", "[grow][grow][grow][grow][grow]"));

		browse = new JButton("Browse Subtitle file (.srt)");
		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser _chooseInputFile = new JFileChooser(Library.inputDir);
				_chooseInputFile.setAcceptAllFileFilterUsed(false);
				_chooseInputFile.setFileFilter(new FileNameExtensionFilter("Subtitle files", "srt"));
				int returnValue = _chooseInputFile.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File inputFile = _chooseInputFile.getSelectedFile();
					_srtFile = inputFile;
					updateTable();
				}
			}
		});
		panel.add(browse, "flowx,cell 0 0,alignx center");
		_table = new JTable(_model);
		_table.setFont(mainFont);
		_table.setRowHeight(25);

		lblNewLabel = new JLabel("Set times from video:");
		panel.add(lblNewLabel, "cell 1 0,alignx center");

		jScrollPane = new JScrollPane(_table);
		jScrollPane
				.setToolTipText("Use the side buttons to set start and end times. Double click on subtitles column to change subtitles.");
		panel.add(jScrollPane, "cell 0 1 1 4,grow");

		setStartTime = new JButton("Set start time");
		setStartTime.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				long currentTime = _playback.getMediaPlayer().getTime();
				String timeFormattedString = time(currentTime);
				System.out.println(timeFormattedString);
				int selectedRow = _table.getSelectedRow();
				try {
					_model.setValueAt(timeFormattedString, selectedRow, 0);
					sortSubtitles(_model.getSubtitleList());
				} catch (NullPointerException ne) {
				}
				_model.fireTableDataChanged();
			}
		});

		panel.add(setStartTime, "flowx,cell 1 1 1 2,alignx center,growy");
		addSub = new JButton("Add subtitle row");
		panel.add(addSub, "cell 1 3,alignx center");
		addSub.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_model.addRow();
				sortSubtitles(_model.getSubtitleList());
				_model.fireTableDataChanged();
			}
		});

		removeSub = new JButton("Remove subtitle row");
		panel.add(removeSub, "cell 1 4,alignx center");
		removeSub.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = _table.getSelectedRow();
				if (selectedRow != -1) {
					_model.removeRow(selectedRow);
					sortSubtitles(_model.getSubtitleList());
					_model.fireTableDataChanged();
				}
			}
		});

		setEndTime = new JButton("Set end time");
		setEndTime.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				long currentTime = _playback.getMediaPlayer().getTime();
				String timeFormattedString = time(currentTime);
				System.out.println(timeFormattedString);
				int selectedRow = _table.getSelectedRow();
				try {
					_model.setValueAt(timeFormattedString, selectedRow, 1);
				} catch (NullPointerException ne) {
				}
				_model.fireTableDataChanged();
			}
		});
		panel.add(setEndTime, "cell 1 1,alignx center,growy");
		JButton btnSaveSubtitleFile = new JButton("Save subtitle file");
		panel.add(btnSaveSubtitleFile, "cell 0 0,alignx center");

		btnSaveSubtitleFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_srtFile.exists()) {
					_srtFile.delete();
				}
				try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(_srtFile, true)))) {
					List<Subtitle> subs = _model.getSubtitleList();
					sortSubtitles(subs);
					for (int i = 0; i < subs.size(); i++) {
						Subtitle s = subs.get(i);
						out.println((i + 1) + "\n" + s.getStartTime() + ",000" + " --> " + s.getEndTime() + ",000"
								+ "\n" + s.getSubtitle() + "\n");
					}
					JOptionPane.showMessageDialog(null, "Saving of .srt file: " + _srtFile
							+ " to the Input Library was successful!", "Save Successful",
							JOptionPane.INFORMATION_MESSAGE);
					_playback.stopPlayer();
				} catch (IOException ie) {
				}
			}

		});
	}

	/**
	 * Helper method that updates the table whenever a change is made to the
	 * table model.
	 */
	protected void updateTable() {
		List<Subtitle> sb = new ArrayList<Subtitle>();
		String fullSubs = readText().replaceAll(" --> ", "\n").replaceAll("\n\n", "\n").replaceAll(",000", "");

		String[] subtitlesStrings = fullSubs.split("\n");
		for (int i = 0; i <= subtitlesStrings.length; i += 4) {
			if ((i + 4) > subtitlesStrings.length) {
				break;
			}
			sb.add(new Subtitle(subtitlesStrings[i + 1], subtitlesStrings[i + 2], subtitlesStrings[i + 3]));
		}
		Collections.sort(sb);
		_model = new SubtitleModel(sb);
		_table.setModel(_model);
	}

	/**
	 * This method reads the text saved in the .srt file
	 * 
	 * @return Text in the cfg file
	 */
	private String readText() {
		StringBuffer text = new StringBuffer();

		try {
			BufferedReader br = new BufferedReader(new FileReader(_srtFile));
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				text.append(currentLine);
				text.append("\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text.toString().trim();
	}

	/**
	 * This method sets up the srt file in the
	 */
	public void setUpSubtitles() {
		String basename = _inputFile.substring(_inputFile.lastIndexOf(File.separator) + 1);
		String filenameNoExtension = basename.substring(0, basename.lastIndexOf("."));
		String output = Library.inputDir + File.separator + filenameNoExtension + ".srt";
		_srtFile = new File(output);
		if (!_srtFile.exists()) {
			_srtFile = new File(output);
			try (PrintWriter out = new PrintWriter(_srtFile)) {
				out.println("1");
				out.println("00:00:00,000 --> 00:00:10,000");
				out.println("Please enter subtitles here");
				out.close();
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
		updateTable();

	}

	/**
	 * This method sorts the subtitles by startTime so that they are saved
	 * properly
	 * 
	 * @param subs
	 */
	private void sortSubtitles(List<Subtitle> subs) {
		Collections.sort(subs);
		_model.setSubtitleList(subs);
	}

	public void setInputFile(String inputFile) {
		_inputFile = inputFile;
		_playback.startPlayer(inputFile);
		setUpSubtitles();
	}

	/**
	 * This method stops all the players in Subtitle Editor
	 */
	public void stopAllPlayers() {
		_playback.stopPlayer();
	}

	/**
	 * This method takes the length in millisecond and converts it into the
	 * format "HOUR:MINUTER:SECOND"
	 * 
	 * @param millisec
	 * @return
	 */
	private String time(long millisec) {
		String time = "";
		int duration = (int) (millisec / 1000.00);
		int sec = (duration % 3600) % 60;
		int min = (duration % 3600) / 60;
		int hour = duration / 3600;
		DecimalFormat formatter = new DecimalFormat("00");
		time = formatter.format(hour) + ":" + formatter.format(min) + ":" + formatter.format(sec);

		return time;
	}

	public static SubtitleEditor getInstance() {
		if (theInstance == null) {
			theInstance = new SubtitleEditor();
		}
		return theInstance;
	}

}
