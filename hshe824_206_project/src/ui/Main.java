package ui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.FileUtils;

import processes.video.VideoTask;
import ui.editors.AudioEditor;
import ui.editors.Playback;
import ui.editors.SubtitleEditor;
import ui.editors.VideoEditor;
import ui.filesystem.Library;
import ui.special.Filters;
import model.LookAndFeelListener;

/**
 * This the main class of the VAMIX Application. It is responsible for setting
 * the look and feel of the GUI, creating and showing the GUI and also is
 * responsible for the creation of new tabs.
 * 
 * The main entry point of the GUI is the library pane, which consists of a
 * number of features including displaying file details as well as having a
 * right click menu for file operations.
 * 
 * @author Harry She
 * 
 */
/*
 * BoilerPlate code of JFileChooser, ProcessBuilder, JColorChooser from Java API
 * and example tutorial code
 */
@SuppressWarnings("serial")
public class Main extends JFrame {

	public static Main window;

	public static JTabbedPane _tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenu edit;
	private JMenu menuPlay;
	private JMenuItem quit;
	private JMenu menuHelp;
	private JMenuItem manual;
	public static JMenuItem importFile = new JMenuItem();
	public static JMenuItem editAudio;
	public static JMenuItem download;
	public static JMenuItem play;
	public static JMenuItem addText;
	public static JMenuItem addFilter;
	public static JMenuItem bounce;
	public static JMenuItem addSubtitles;
	public static JMenuItem outputLibrary;
	public static JMenuItem inputLibrary;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Set look and feel to Nimbus
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
						if ("Nimbus".equals(info.getName())) {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch (Exception e) {
					try {
						UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					} catch (Exception ex) {
					}
				}
				try {
					window = new Main();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		pack();
		setTitle("VAMIX");
		setVisible(true);
		initialize();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		// Set up frame
		setBounds(100, 100, 1000, 700);
		setMinimumSize(new Dimension(1000, 700));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow]"));

		// Set up main library tab
		getContentPane().add(_tabbedPane, "cell 0 0,aligny top,grow");

		// Menu Bar setup
		setUpMenuBar();

		// Set up library file system.
		boolean displayHelp = librarySetUp();
		createNewTab("Library", Library.getInstance(), 0);
		if (displayHelp) {
			JOptionPane
					.showMessageDialog(
							null,
							"We have detected this is your first time running VAMIX. VAMIX requires avconv in the libav library to run.\nPlease read the User help manual by pressing Ctrl-H to learn how to use VAMIX.",
							"Please read manual", JOptionPane.INFORMATION_MESSAGE);
		}

		// Sets up the ability to dynamically change look and feel
		setupDynamicLookAndFeel();

		// Deletes temporary files on exit.
		deleteDirOnExit(new File(VideoTask.tempDir));
	}

	/**
	 * Sets up listeners for changing the look and feel dynamically.
	 */
	private void setupDynamicLookAndFeel() {
		JMenu menuThemes = new JMenu("Themes");
		menuBar.add(menuThemes);
		menuBar.add(menuHelp);

		JMenuItem metal = new JMenuItem("Metal theme");
		menuThemes.add(metal);
		metal.addActionListener(new LookAndFeelListener("javax.swing.plaf.metal.MetalLookAndFeel"));

		JMenuItem nimbus = new JMenuItem("Nimbus theme");
		menuThemes.add(nimbus);
		nimbus.addActionListener(new LookAndFeelListener("javax.swing.plaf.nimbus.NimbusLookAndFeel"));

		JMenuItem motif = new JMenuItem("Motif theme");
		menuThemes.add(motif);
		motif.addActionListener(new LookAndFeelListener("com.sun.java.swing.plaf.motif.MotifLookAndFeel"));

		JMenuItem gtk = new JMenuItem("GTK theme");
		menuThemes.add(gtk);
		gtk.addActionListener(new LookAndFeelListener("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"));
	}

	/**
	 * Sets up the JMenuBar at the top of VAMIX frame and all the shortcuts etc.
	 */
	private void setUpMenuBar() {
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuFile = new JMenu("File");
		menuBar.add(menuFile);
		edit = new JMenu("Edit");
		menuBar.add(edit);
		menuPlay = new JMenu("Play");
		menuBar.add(menuPlay);

		importFile = new JMenuItem("Import File", KeyEvent.VK_I);
		importFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		menuFile.add(importFile);

		download = new JMenuItem("Download", KeyEvent.VK_D);
		download.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		menuFile.add(download);
		menuFile.add(new JSeparator());

		inputLibrary = new JMenuItem("Open Input Library");
		inputLibrary.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(Library.inputDir));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		menuFile.add(inputLibrary);

		outputLibrary = new JMenuItem("Open Output Library");
		outputLibrary.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(Library.outputDir));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		menuFile.add(outputLibrary);
		menuFile.add(new JSeparator());

		quit = new JMenuItem("Quit", KeyEvent.VK_Q);
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		menuFile.add(quit);
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});

		editAudio = new JMenuItem("Edit Audio", KeyEvent.VK_E);
		editAudio.setEnabled(false);
		editAudio.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		edit.add(editAudio);
		edit.add(new JSeparator());

		addText = new JMenuItem("Add Video text", KeyEvent.VK_T);
		addText.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		addText.setEnabled(false);
		edit.add(addText);

		addSubtitles = new JMenuItem("Add Subtitles", KeyEvent.VK_S);
		addSubtitles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		addSubtitles.setEnabled(false);
		edit.add(addSubtitles);

		addFilter = new JMenuItem("Add Video filters", KeyEvent.VK_F);
		addFilter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		addFilter.setEnabled(false);
		edit.add(addFilter);

		play = new JMenuItem("Play", KeyEvent.VK_P);
		play.setEnabled(false);
		play.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		menuPlay.add(play);
		menuPlay.add(new JSeparator());

		bounce = new JMenuItem("Bounce!", KeyEvent.VK_B);
		bounce.setEnabled(false);
		bounce.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
		menuPlay.add(bounce);

		menuHelp = new JMenu("Help");
		manual = new JMenuItem("Help", KeyEvent.VK_H);
		manual.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		manual.addActionListener(new ActionListener() {
			// Read VAMIX PDF
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
					try {
						File file = new File(System.getProperty("user.home") + File.separator + "vamix"
								+ File.separator + "VAMIX_User_Manual.pdf");
						Desktop.getDesktop().open(file);
					} catch (IOException ex) {
					}
				}
			}
		});
		menuHelp.add(manual);
	}

	/**
	 * Method to create tabs for specific panes, such as Audio Editor, Playback,
	 * Video Editor. Creates a cross-off option for every tab except the main
	 * library tab which shouldn't ever be closed in the application.
	 * 
	 * @param tabName
	 * @param pane
	 * @param num
	 */
	public static void createNewTab(String tabName, final Pane pane, int num) {

		// Only create new tab if there isn't the same tab already open
		if (_tabbedPane.indexOfTab(tabName) == -1 || pane instanceof Playback) {
			JLabel title = new JLabel(tabName);
			JPanel tab = new JPanel();
			tab.setOpaque(false);
			tab.add(title);
			_tabbedPane.addTab(tabName, pane);
			_tabbedPane.setTabComponentAt(num, tab);
			// If not Library create X button
			if (!(pane instanceof Library)) {
				JButton x = new JButton("X");
				tab.add(x, "align left");
				x.setContentAreaFilled(true);
				x.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// Stop players
						if (pane instanceof Playback) {
							((Playback) pane).stopPlayer();
						} else if (pane instanceof AudioEditor) {
							((AudioEditor) pane).get_miniPlayback().stopPlayer();
						} else if (pane instanceof VideoEditor) {
							((VideoEditor) pane).stopAllPlayers();
						} else if (pane instanceof Filters) {
							((Filters) pane).stopAllPlayers();
						} else if (pane instanceof SubtitleEditor) {
							((SubtitleEditor) pane).stopAllPlayers();
						}
						_tabbedPane.remove(pane);
					}
				});
			}
		}
	}

	/**
	 * Sets up the library directories if they don't exist on the user's
	 * computer.
	 * 
	 * @return
	 */
	public boolean librarySetUp() {
		File _input = new File(Library.inputDir);
		File _output = new File(Library.outputDir);
		boolean firstTime = false;

		if (!_input.exists()) {
			_input.mkdirs();
			firstTime = true;
		}
		if (!_output.exists()) {
			_output.mkdirs();
			firstTime = true;
		}

		URL inputUrl = getClass().getResource(File.separator + "manual" + File.separator + "VAMIX_User_Manual.pdf");
		File dest = new File(System.getProperty("user.home") + File.separator + "vamix" + File.separator
				+ "VAMIX_User_Manual.pdf");
		try {
			FileUtils.copyURLToFile(inputUrl, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return firstTime;
	}

	/**
	 * Method used to delete temporary directory after the user exits the
	 * application.
	 * 
	 * Boilerplate code from
	 * http://www.coderanch.com/t/278832/java-io/java/delete-directory-VM-exits;
	 * 
	 * @param dir
	 */
	private static void deleteDirOnExit(File dir) {
		// call deleteOnExit for the folder first, so it will get deleted last
		dir.deleteOnExit();
		File[] files = dir.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					deleteDirOnExit(f);
				} else {
					f.deleteOnExit();
				}
			}
		}
	}
}
