package ui.filesystem;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import model.OpenListener;
import net.miginfocom.swing.MigLayout;
import processes.file.FileChecker;
import processes.file.ImportTask;
import ui.Main;
import ui.Pane;
import ui.editors.AudioEditor;
import ui.editors.Playback;
import ui.editors.SubtitleEditor;
import ui.editors.VideoEditor;
import ui.special.Bounce;
import ui.special.Filters;

/**
 * Library class that is responsible for the main file I/O system: This includes
 * the downloading of files to the VAMIX input library, the importing of local
 * files to the input library and the passing of input files to be played or
 * have their audio or video edited.
 * 
 * To manipulate files in this application, there is a specified input and
 * output library created by the app and to be used by the user for operations.
 * This library can be accessed through the app user interface through a JTree
 * model that I have created.
 * 
 * 
 * @author Harry She
 *
 */
@SuppressWarnings("serial")
public class Library extends Pane {
	// Singleton - only need and want one library class
	private static Library theInstance = null;

	private String defaultMessageString = "INFO:\n\nPlease import files using\n\n\"Import local Files\"\n \nor\n\n\"Download\"\n";
	private String defaultOutputLibString = "INFO:\n\nListed here are in the Output library \nwill be the output files of any edit\n operation";

	public static String _currentFileString;
	private String importDirString;
	private Path importPath;
	protected String _currentFileInputString;
	protected String _currentFileOutputString;

	private DefaultMutableTreeNode _selectedFileNodeInputTree;
	private DefaultMutableTreeNode _selectedFileNodeOutputTree;
	private JTree inputTree;
	private JTree outputTree;
	private JTabbedPane tabbedPane;
	private JButton download;
	private JButton play;
	private JButton editAudio;
	private JButton editVideo;
	private JButton filters;
	private JButton btnBounce;
	private JButton btnAddSubtitles;
	private JTextArea _detailsInputArea;
	private JTextArea _detailsOutputArea;
	private JSplitPane splitPane;
	private JSplitPane splitPane_1;
	private JScrollPane _inputScrollPane;
	private JScrollPane _outputScrollPane;

	/**
	 * Create the panel.
	 */
	private Library() {
		setLayout(new MigLayout("", "[600px,grow]", "[550px,grow 90][300px,grow 10]"));

		// Tabs
		setupTabs();

		// Details text area to show avprobe output
		setupDetailsTextArea();

		// Instantiate buttons and icons
		download = new JButton("Download");
		play = new JButton("Play");
		editAudio = new JButton("Edit Audio");
		editVideo = new JButton("Add Title/Credits");
		filters = new JButton("Add Video Filters");
		btnBounce = new JButton("Bounce!");
		download.setVerticalTextPosition(SwingConstants.BOTTOM);
		download.setHorizontalTextPosition(SwingConstants.CENTER);
		download.setIcon(createImageIcon("download.png"));

		// Sets up the input and output Library
		setupLibraryFileSystem();

		splitPane.setLeftComponent(outputTree);
		splitPane_1.setLeftComponent(inputTree);
		splitPane_1.setRightComponent(_inputScrollPane);
		splitPane.setRightComponent(_outputScrollPane);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		buttonPanel.setToolTipText("");
		add(buttonPanel, "cell 0 1,grow");
		buttonPanel.setLayout(new MigLayout("",
				"[96.00px,grow][110.00px,grow][110px,grow][136.00px,grow][123.00px,grow][168.00px,grow]",
				"[86.00,grow 50][86,grow 50]"));

		/*
		 * This is a popup menu that the user can bring up by right clicking in
		 * order to refresh changes in the tree or to delete items from the
		 * library.
		 */
		final JPopupMenu pop = new JPopupMenu();
		JMenuItem del = new JMenuItem("Delete");
		del.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_selectedFileNodeInputTree != null) {
					File toDelete = new File(_selectedFileNodeInputTree.toString());
					toDelete.delete();
					try {
						refreshTree();
					} catch (NullPointerException ne) {
					}
					noMedia();
				}
			}
		});

		JMenuItem ref = new JMenuItem("Refresh");
		ref.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_selectedFileNodeInputTree != null) {
					refreshTree();
				}
			}
		});

		final JPopupMenu pop2 = new JPopupMenu();
		JMenuItem del2 = new JMenuItem("Delete");
		del2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_selectedFileNodeOutputTree != null) {
					File toDelete = new File(_selectedFileNodeOutputTree.toString());
					toDelete.delete();
					try {
						refreshTree();
					} catch (NullPointerException ne) {
					}
					noMedia();
				}
			}
		});

		JMenuItem ref2 = new JMenuItem("Refresh");
		ref2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_selectedFileNodeOutputTree != null) {
					refreshTree();
				}
			}
		});

		JMenuItem openInFolder = new JMenuItem("Open in folder");
		openInFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(Library.inputDir));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		JMenuItem openInFolder2 = new JMenuItem("Open in folder");
		openInFolder2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(Library.outputDir));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		pop.add(del);
		pop.add(new JSeparator());
		pop.add(ref);
		pop.add(openInFolder);
		pop2.add(del2);
		pop2.add(new JSeparator());
		pop2.add(ref2);
		pop2.add(openInFolder2);
		/*
		 * Listeners to listen for right clicks on a selected item to bring up
		 * aforementioned popup menu.
		 */
		inputTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int row = inputTree.getClosestRowForLocation(e.getX(), e.getY());
					inputTree.setSelectionRow(row);
					TreePath pathForLocation = inputTree.getPathForLocation(e.getPoint().x, e.getPoint().y);
					if (pathForLocation != null) {
						Object path = inputDir + File.separator + pathForLocation.getLastPathComponent();
						_selectedFileNodeInputTree = new DefaultMutableTreeNode(path);
						pop.show(e.getComponent(), e.getX(), e.getY());
					} else {
						_selectedFileNodeInputTree = null;
					}
				}
				super.mousePressed(e);
			}
		});
		outputTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int row = outputTree.getClosestRowForLocation(e.getX(), e.getY());
					outputTree.setSelectionRow(row);
					TreePath pathForLocation = outputTree.getPathForLocation(e.getPoint().x, e.getPoint().y);
					if (pathForLocation != null) {
						Object path = outputDir + File.separator + pathForLocation.getLastPathComponent();
						_selectedFileNodeOutputTree = new DefaultMutableTreeNode(path);
						pop2.show(e.getComponent(), e.getX(), e.getY());
					} else {
						_selectedFileNodeOutputTree = null;
					}
				}
				super.mousePressed(e);
			}
		});
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (tabbedPane.getSelectedIndex() == 0) {
					_currentFileString = _currentFileInputString;
				} else {
					_currentFileString = _currentFileOutputString;
				}
			}
		});

		/*
		 * Setup of Large buttons at the bottom of library pane.
		 */
		class downloadListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				Download dl = new Download();
				dl.setSize(750, 500);
				dl.setLocationRelativeTo(null);
				dl.setVisible(true);
				dl.setResizable(true);
			}
		}

		// DOWNLOADING BUTTON
		download.setFont(titleFont);
		buttonPanel.add(download, "cell 0 0,alignx center,height 50,aligny center,grow");
		download.addActionListener(new downloadListener());
		Main.download.addActionListener(new downloadListener());
		// IMPORT BUTTON
		class importListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooseImport = new JFileChooser();
				chooseImport.setAcceptAllFileFilterUsed(false);
				chooseImport.setFileFilter(new FileNameExtensionFilter("Audio and Video files only", _validExtensions));
				int returnValue = chooseImport.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File inputFile = chooseImport.getSelectedFile();
					importDirString = (inputFile.getAbsolutePath());
					String basename = importDirString.substring(importDirString.lastIndexOf(File.separator));
					File libraryFile = new File(inputDir + basename);
					importPath = libraryFile.toPath();
					importFile(inputFile.toPath(), importPath);
				}
			}

		}
		// allow menubar to import too
		Main.importFile.addActionListener(new importListener());

		class playListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				String basename = _currentFileString.substring(_currentFileString.lastIndexOf(File.separator) + 1);
				Playback pb = new Playback(basename);
				Main.createNewTab("Playback", pb, Main._tabbedPane.getTabCount());
				Main._tabbedPane.setSelectedIndex(Main._tabbedPane.getTabCount() - 1);
				pb.startPlayer(_currentFileString);
			}
		}
		Main.play.addActionListener(new playListener());
		play.setVerticalTextPosition(SwingConstants.BOTTOM);
		play.setHorizontalTextPosition(SwingConstants.CENTER);
		play.setIcon(createImageIcon("playMenu.png"));
		// PLAY BUTTON
		play.setEnabled(false);
		play.setFont(titleFont);
		buttonPanel.add(play, "cell 1 0 1 2,alignx center,height 50,aligny center,grow");
		play.addActionListener(new playListener());
		// EDIT AUDIO BUTTON
		Main.editAudio.addActionListener(new OpenListener("Audio Editor"));
		editAudio.setVerticalTextPosition(SwingConstants.BOTTOM);
		editAudio.setHorizontalTextPosition(SwingConstants.CENTER);
		editAudio.setIcon(createImageIcon("audio.png"));
		editAudio.setFont(titleFont);
		editAudio.setEnabled(false);
		buttonPanel.add(editAudio, "cell 3 0 1 2,alignx center,height 50,aligny center,grow");
		editAudio.addActionListener(new OpenListener("Audio Editor"));
		editVideo.setVerticalTextPosition(SwingConstants.BOTTOM);
		editVideo.setHorizontalTextPosition(SwingConstants.CENTER);
		editVideo.setIcon(createImageIcon("text.png"));
		// EDIT VIDEO BUTTON
		editVideo.setEnabled(false);
		editVideo.setFont(titleFont);
		buttonPanel.add(editVideo, "cell 5 0,alignx center,height 50,aligny center,grow");
		editVideo.addActionListener(new OpenListener("Video Editor"));
		buttonPanel.add(filters, "cell 4 0 1 2,grow");
		final JButton importButton = new JButton("Import Local files");
		importButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		importButton.setHorizontalTextPosition(SwingConstants.CENTER);
		importButton.setIcon(createImageIcon("import.png"));
		// IMPORTING BUTTON
		importButton.setFont(titleFont);
		buttonPanel.add(importButton, "cell 0 1,alignx center,height 50,aligny center,grow");
		importButton.addActionListener(new importListener());
		Main.addText.addActionListener(new OpenListener("Video Editor"));
		// BOUNCE BUTTON
		btnBounce.setFont(titleFont);
		btnBounce.setEnabled(false);
		btnBounce.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnBounce.setHorizontalTextPosition(SwingConstants.CENTER);
		btnBounce.setIcon(createImageIcon("bounce.png"));
		buttonPanel.add(btnBounce, "cell 2 0 1 2,grow");
		btnBounce.addActionListener(new OpenListener("Bounce!"));
		Main.bounce.addActionListener(new OpenListener("Bounce!"));
		// FILTERS BUTTON
		filters.setFont(titleFont);
		filters.setEnabled(false);
		filters.setVerticalTextPosition(SwingConstants.BOTTOM);
		filters.setHorizontalTextPosition(SwingConstants.CENTER);
		filters.setIcon(createImageIcon("filters.png"));
		filters.addActionListener(new OpenListener("Filters"));
		Main.addFilter.addActionListener(new OpenListener("Filters"));
		// SUBTITLES BUTTON
		btnAddSubtitles = new JButton("Add Subtitles");
		btnAddSubtitles.setFont(titleFont);
		btnAddSubtitles.setEnabled(false);
		btnAddSubtitles.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAddSubtitles.setHorizontalTextPosition(SwingConstants.CENTER);
		btnAddSubtitles.setIcon(createImageIcon("subtitles.png"));
		buttonPanel.add(btnAddSubtitles, "cell 5 1,grow");
		btnAddSubtitles.addActionListener(new OpenListener("Subtitles"));
		Main.addSubtitles.addActionListener(new OpenListener("Subtitles"));
	}

	private void setupLibraryFileSystem() {
		// Input library Tree
		inputTree = new JTree();
		inputTree.setFont(mainFont);
		inputTree.setModel(configureTree("Input"));
		inputTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent event) {
				Object file = inputTree.getLastSelectedPathComponent();
				if (file != null) {
					_currentFileInputString = inputDir + File.separator + file.toString();
					_currentFileString = _currentFileInputString;
					if (file.toString().equals(inputDir)) {
						_detailsInputArea.setText(defaultMessageString);
						noMedia();
					} else {
						_detailsInputArea.setText(getDetails(_currentFileInputString).toString());
					}
					/*
					 * Check if valid file first here: and then allow to be
					 * played/edited etc. if (File is audio or video) then
					 * enable play, edit etc...
					 */
					FileChecker fc = new FileChecker(_currentFileString);
					boolean hasAudio = fc.checkAVFile("Audio");
					boolean hasVideo = fc.checkAVFile("Video");
					if (hasAudio && hasVideo) {
						bothAV();
					} else if (hasAudio && !hasVideo) {
						audioNoVideo();
					} else if (!hasAudio && hasVideo) {
						videoNoAudio();
					} else {
						noMedia();
					}
				}
			}
		});

		// Output library Tree
		outputTree = new JTree();
		outputTree.setFont(mainFont);
		outputTree.setModel(configureTree("Output"));
		outputTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent event) {
				Object file = outputTree.getLastSelectedPathComponent();
				if (file != null) {
					_currentFileOutputString = outputDir + File.separator + file.toString();
					_currentFileString = _currentFileOutputString;
					if (file.toString().equals(outputDir)) {
						_detailsOutputArea.setText(defaultOutputLibString);
						noMedia();
					} else {
						_detailsOutputArea.setText(getDetails(_currentFileOutputString).toString());
					}
					/*
					 * Check if valid file first here: and then allow to be
					 * played/edited etc. if (File is audio or video) then
					 * enable play, edit etc...
					 */
					FileChecker fc = new FileChecker(_currentFileString);
					boolean hasAudio = fc.checkAVFile("Audio");
					boolean hasVideo = fc.checkAVFile("Video");
					if (hasAudio && hasVideo) {
						bothAV();
					} else if (hasAudio && !hasVideo) {
						audioNoVideo();
					} else if (!hasAudio && hasVideo) {
						videoNoAudio();
					} else {
						noMedia();
					}
				}
			}
		});
	}

	/**
	 * Sets up the text area that gets the information from avprobe and displays
	 * it
	 */
	private void setupDetailsTextArea() {
		_detailsInputArea = new JTextArea(defaultMessageString);
		_detailsOutputArea = new JTextArea(defaultOutputLibString);

		_detailsInputArea.setFont(sideFont);
		_detailsInputArea.setEditable(false);
		_detailsInputArea.setWrapStyleWord(true);
		_detailsOutputArea.setFont(sideFont);
		_detailsOutputArea.setEditable(false);
		_detailsOutputArea.setWrapStyleWord(true);
		_inputScrollPane = new JScrollPane(_detailsInputArea);
		_inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		_inputScrollPane.setPreferredSize(new Dimension(500, 500));
		_outputScrollPane = new JScrollPane(_detailsOutputArea);
		_outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		_outputScrollPane.setPreferredSize(new Dimension(500, 500));
	}

	/**
	 * Sets up the tabbed pane for the input and output library and the
	 * splitpane
	 */
	private void setupTabs() {
		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setBackground(Color.WHITE);
		tabbedPane.setFont(tabFont);
		add(tabbedPane, "cell 0 0,growpriox 50,grow");
		splitPane = new JSplitPane();
		splitPane_1 = new JSplitPane();
		tabbedPane.addTab(null, null, splitPane_1, null);
		JLabel lbl = new JLabel("<html><body height ='40'>Input Library</body></html>");
		lbl.setFont(mainFont);
		Icon icon = createImageIcon("library.png");
		lbl.setIcon(icon);
		lbl.setHorizontalTextPosition(SwingConstants.CENTER);
		lbl.setVerticalTextPosition(SwingConstants.TOP);
		tabbedPane.setTabComponentAt(0, lbl);
		tabbedPane.addTab(null, null, splitPane, null);
		splitPane_1.setDividerLocation(500);
		splitPane.setDividerLocation(500);
		JLabel lbl2 = new JLabel("<html><body height ='40'>Output Library</body></html>");
		lbl2.setFont(mainFont);
		lbl2.setIcon(icon);
		lbl2.setHorizontalTextPosition(SwingConstants.CENTER);
		lbl2.setVerticalTextPosition(SwingConstants.TOP);
		tabbedPane.setTabComponentAt(1, lbl2);
	}

	/**
	 * This helper method creates a subtask that is run within a swingworker to
	 * import a given file to the input library.
	 * 
	 * @param input
	 * @param importPath
	 */
	protected void importFile(final Path input, Path importPath) {
		ImportTask it = new ImportTask(input, importPath);
		it.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("failure".equals(evt.getPropertyName())) {
					JOptionPane.showMessageDialog(null, evt.getNewValue(), "Error!", JOptionPane.WARNING_MESSAGE);
					refreshTree();
					setCursor(Cursor.getDefaultCursor());
				} else if ("success".equals(evt.getPropertyName())) {
					JOptionPane.showMessageDialog(null, "Import of " + input + " to the input library was successful!",
							"Import Successful", JOptionPane.INFORMATION_MESSAGE);
					refreshTree();
					setCursor(Cursor.getDefaultCursor());
				} else if ("invalid".equals(evt.getPropertyName())) {
					JOptionPane.showMessageDialog(null, "The file " + input
							+ " does not have any video or audio streams and hence will not be copied!",
							"Error: Invalid file!", JOptionPane.WARNING_MESSAGE);
					refreshTree();
					setCursor(Cursor.getDefaultCursor());
					return;
				}
			}
		});
		it.execute();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	/**
	 * This helper method uses the avprobe command in linux to in fact grab the
	 * audio/visual information about a given selected file to be displayed by
	 * the GUI to the user.
	 * 
	 * @param file
	 * @return
	 */
	public static StringBuffer getDetails(String file) {
		ProcessBuilder builder = new ProcessBuilder("avprobe", file);
		Process process = null;
		builder.redirectErrorStream(true);
		StringBuffer sb = new StringBuffer();
		try {
			process = builder.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		InputStream stdout = process.getInputStream();
		BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
		String line = null;
		sb.append("File Details:" + System.getProperty("line.separator") + System.getProperty("line.separator"));
		try {
			while ((line = stdoutBuffered.readLine()) != null) {
				sb.append(line).append(System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb;
	}

	/**
	 * This method configures the model of the JTree that represents the input
	 * and output library
	 * 
	 * NB: Some of this code is taken from the boiler plate code for a JTree
	 * model for a file directory.
	 * 
	 * http://docs.oracle.com/javase/tutorial/uiswing/components/tree.html
	 * 
	 * @param lib
	 * @return
	 */
	private TreeModel configureTree(String lib) {
		File dir = null;
		if (lib.equals("Input")) {
			dir = new File(inputDir);
		} else {
			dir = new File(outputDir);
		}
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(dir);
		DefaultTreeModel model = new DefaultTreeModel(root);
		File[] subItems = dir.listFiles();
		for (File file : subItems) {
			String absolutePath = file.toString();
			String basename = absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1);
			File node = new File(basename);
			root.add(new DefaultMutableTreeNode(node));
		}

		return (TreeModel) model;
	}

	/**
	 * Refreshes tree model when changes need to be appropriated from model.
	 * 
	 * @return
	 */
	public void refreshTree() {
		inputTree.setModel(configureTree("Input"));
		outputTree.setModel(configureTree("Output"));
	}

	/**
	 * Creates image icons for the button icons
	 * 
	 * @param name
	 * @return
	 */
	private ImageIcon createImageIcon(String name) {
		BufferedImage image;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(File.separator + "icons" + File.separator + name));
			return new ImageIcon(image);
		} catch (IOException e) {
			System.err.println("Couldn't find file: " + name);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Grab the singleton instance of this class.
	 * 
	 * @return
	 */
	public static Library getInstance() {
		if (theInstance == null) {
			theInstance = new Library();
		}
		return theInstance;
	}

	private void bothAV() {
		Main.play.setEnabled(true);
		Main.addText.setEnabled(true);
		Main.editAudio.setEnabled(true);
		Main.addFilter.setEnabled(true);
		Main.bounce.setEnabled(true);
		Main.addSubtitles.setEnabled(true);
		btnAddSubtitles.setEnabled(true);
		play.setEnabled(true);
		editAudio.setEnabled(true);
		editVideo.setEnabled(true);
		btnBounce.setEnabled(true);
		filters.setEnabled(true);
	}

	private void audioNoVideo() {
		Main.play.setEnabled(true);
		Main.addText.setEnabled(false);
		Main.editAudio.setEnabled(true);
		Main.addFilter.setEnabled(false);
		Main.bounce.setEnabled(false);
		Main.addSubtitles.setEnabled(false);
		btnAddSubtitles.setEnabled(false);
		play.setEnabled(true);
		editAudio.setEnabled(true);
		editVideo.setEnabled(false);
		btnBounce.setEnabled(false);
		filters.setEnabled(false);
	}

	private void videoNoAudio() {
		Main.play.setEnabled(true);
		Main.addText.setEnabled(true);
		Main.editAudio.setEnabled(false);
		Main.addFilter.setEnabled(true);
		Main.bounce.setEnabled(true);
		Main.addSubtitles.setEnabled(true);
		btnAddSubtitles.setEnabled(true);
		play.setEnabled(true);
		editAudio.setEnabled(false);
		editVideo.setEnabled(true);
		btnBounce.setEnabled(true);
		filters.setEnabled(true);
	}

	private void noMedia() {
		Main.play.setEnabled(false);
		Main.addText.setEnabled(false);
		Main.editAudio.setEnabled(false);
		Main.addFilter.setEnabled(false);
		Main.bounce.setEnabled(false);
		Main.addSubtitles.setEnabled(false);
		btnAddSubtitles.setEnabled(false);
		play.setEnabled(false);
		editAudio.setEnabled(false);
		editVideo.setEnabled(false);
		btnBounce.setEnabled(false);
		filters.setEnabled(false);
	}

	@Override
	public void setInputFile(String inputFile) {
		throw new UnsupportedOperationException("Cannot set library input file");
	}

}
