package ui;

import java.awt.Font;
import java.io.File;

import javax.swing.JPanel;

/**
 * The Abstract class that all main panels in the VAMIX application extend. This
 * is used to store common fields such as the valid file extensions, and fonts.
 * It require that its concrete subclasses implement the setInputFile method
 * 
 * 
 * @author Harry She
 *
 */
@SuppressWarnings("serial")
public abstract class Pane extends JPanel {

	// Main library input directory
	public static final String inputDir = System.getProperty("user.home") + File.separator + "vamix" + File.separator
			+ "InputLibrary";

	// Main library output directory
	public static final String outputDir = System.getProperty("user.home") + File.separator + "vamix" + File.separator
			+ "OutputLibrary";

	// Valid media extensions
	public static final String[] _validExtensions = { "mp3", "mp4", "avi", "mkv", "wmv", "wav", "wma", "ra", "ram",
			"rm", "mid", "ogg", "3gp", "aac", "m4a", "m4p", "msv", "vox", "webm", "flv", "ogv", "mov", "qt", "mpg",
			"mp2", "mpeg", "mpg", "m4v", "svi" };

	// Valid video extensions
	public static final String[] _validVideoOnly = { "mp4", "avi", "mkv", "flv", "ogv", "ogg", "rm", "m4v", "wmv",
			"m4p", "mpg", "svi", "3gp" };

	// Fonts used
	protected final static int _titleFontSize = 16;
	protected final static int _bodyFontSize = 14;
	protected final static String _font = "DejaVu Sans";
	protected Font titleFont = new Font(_font, Font.BOLD, _titleFontSize);
	protected Font mainFont = new Font(_font, Font.BOLD, _bodyFontSize);
	protected Font sideFont = new Font(_font, Font.PLAIN, _bodyFontSize);
	protected Font tabFont = new Font(_font, Font.BOLD, _bodyFontSize);
	protected Font normFont = new Font(_font, Font.BOLD, 12);

	// All concrete subclasses must implement this
	public abstract void setInputFile(String inputFile);
}
