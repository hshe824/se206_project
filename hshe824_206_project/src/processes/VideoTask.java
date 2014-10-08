package processes;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import ui.Library;
import ui.VideoEditor;

/**
 * This class does the bash commands for video editing in the background. This
 * includes the creation of temporary video files containing the title or
 * credits and then the concatenation of the input video with this generated
 * title video.
 * 
 * @author Greggory & Harry
 *
 */
public class VideoTask extends SwingWorker<Void, Void> {
	final public static String tempDir = System.getProperty("user.home")
			+ File.separator + "vamix" + File.separator + "temp";

	private String _inputFile;
	private String _previewFile = null;
	private String _tempLocation;
	private String _editedLocation;
	private String _txt;
	private Font _font;
	private String _color;
	private Dimension _videoSize;
	private JTextArea _textArea;
	private int _size;
	private int _positionY, _positionX;
	private int _duration;
	private int _stringWidth;
	private Boolean _isTitle;
	private Boolean _isPreview;

	private boolean errorState;

	private String outString;

	public VideoTask(String inputFile, JTextArea textArea, int min, int sec,
			Boolean isTitle, Boolean isPreview) {
		_inputFile = inputFile;
		_duration = sec + min * 60;
		_isTitle = isTitle;
		_isPreview = isPreview;
		_txt = textArea.getText();
		_font = textArea.getFont();
		_size = textArea.getFont().getSize();
		_color = String
				.format("%02x%02x%02x%02x", textArea.getForeground().getRed(),
						textArea.getForeground().getGreen(), textArea
								.getForeground().getBlue(), textArea
								.getForeground().getAlpha());
		getDimensions();
		_textArea = textArea;
		getCentred();
		setUpFileLocation();
		new File(tempDir).mkdirs();
	}

	/**
	 * This method is to be called during the construction of the class. It
	 * provides the locations for the temporary files which are saved the a temp
	 * folder. The final output will be saved in the output library folder
	 */
	private void setUpFileLocation() {
		String basename = _inputFile.substring(_inputFile
				.lastIndexOf(File.separator) + 1);
		String filenameNoExtension = basename.substring(0,
				basename.lastIndexOf("."));
		String videoExtension = basename.substring(basename.lastIndexOf("."),
				basename.length());
		_tempLocation = tempDir + File.separator + filenameNoExtension
				+ "[Temp]" + videoExtension;
		_editedLocation = Library.outputDir + File.separator
				+ filenameNoExtension + "[VAMIX-TEXTEDITED].mpg";
		_previewFile = tempDir + File.separator + filenameNoExtension
				+ "[Preview]" + videoExtension;
	}

	@Override
	protected Void doInBackground() throws Exception {
		ProcessBuilder builder = null;
		errorState = false;

		FontFinder finder = new FontFinder();
		finder.setUp();
		String fontLocation = finder.getFontDirectory(_font.getName());

		File temp = new File(_tempLocation);
		if (temp.exists()) {
			temp.delete();
		}

		builder = new ProcessBuilder("/bin/bash", "-c",
				"avconv -filter_complex \"color=0x000000ff:"
						+ (int) _videoSize.getWidth() + "x"
						+ (int) _videoSize.getHeight()
						+ " [in]; [in] drawtext=fontfile='" + fontLocation
						+ "'" + ":text=" + _txt.toString() + ":fontsize="
						+ _size + ":fontcolor=" + _color + ":x=" + _positionX
						+ ":y=" + _positionY + "\" -t " + _duration + " "
						+ _tempLocation);
		process(builder);

		// Saving Task
		if (!_isPreview) {
			ProcessBuilder pb1 = new ProcessBuilder("/bin/bash", "-c",
					"avconv -i " + _tempLocation + " -y " + tempDir
							+ File.separator + "text.mpg");
			System.out.println("avconv -i " + _tempLocation + " -y " + tempDir
					+ File.separator + "text.mpg");
			pb1.redirectErrorStream(true);
			Process p1 = pb1.start();
			p1.waitFor();
			mpgCreate();
			return null;
		}
		return null;
	}

	/**
	 * Helper method that converts the input file to an .mpg so that it can be
	 * concatenated with the text video
	 * 
	 * @throws InterruptedException
	 */
	private void mpgCreate() throws InterruptedException {
		ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", "avconv -i "
				+ _inputFile + " -y " + tempDir + File.separator + "input.mpg");
		System.out.println("avconv -i " + _inputFile + " -y " + tempDir
				+ File.separator + "input.mpg");
		pb2.redirectErrorStream(true);
		Process p2 = null;
		try {
			p2 = pb2.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		p2.waitFor();
		catMPG();
	}

	/**
	 * This helper method concatenates the text video to the input video
	 */
	private void catMPG() {
		if (_isTitle) {
			ProcessBuilder pb3 = new ProcessBuilder("/bin/bash", "-c", "cat "
					+ tempDir + File.separator + "text.mpg" + " " + tempDir
					+ File.separator + "input.mpg" + " > " + _editedLocation);
			pb3.redirectErrorStream(true);
			Process p3 = null;
			try {
				p3 = pb3.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				p3.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} else {
			ProcessBuilder pb3 = new ProcessBuilder("/bin/bash", "-c",
					"avconv -i concat:" + tempDir + File.separator
							+ "input.mpg" + "\\|" + tempDir + File.separator
							+ "text.mpg" + " -c copy -y " + _editedLocation);
			pb3.redirectErrorStream(true);
			process(pb3);

		}
	}

	@Override
	protected void done() {
		try {
			this.get();
			if (_isPreview) {
				VideoEditor.getInstance().startPreview(_tempLocation);
			}
			if (errorState == false) {
				Library.getInstance().refreshTree();
				firePropertyChange("success", null, "success");
			}
		} catch (CancellationException e) {
			firePropertyChange("cancelled", null,
					"The text editing task was stopped!");
			File toDelete = new File(_editedLocation);
			toDelete.delete();
			Library.getInstance().refreshTree();
			return;

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	/**
	 * From AudioTask (line 127)
	 * 
	 * @param builder
	 */
	private void process(ProcessBuilder builder) {
		Process process = null;
		builder.redirectErrorStream(true);
		try {
			process = builder.start();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		InputStream stdout = process.getInputStream();
		BufferedReader stdoutBuffered = new BufferedReader(
				new InputStreamReader(stdout));
		String line = null;
		String last = null;
		try {
			while ((line = stdoutBuffered.readLine()) != null) {
				last = line;
				if (isCancelled()) {
					process.destroy();
					return;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			if (process.waitFor() != 0) {
				firePropertyChange("failure", null, last);
				errorState = true;
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * This method returns the directory of the final output file
	 * 
	 * @return Location of final file
	 */
	public String getOutputFileName() {
		return _editedLocation;
	}

	/**
	 * This method calculates the x and y coordinates that is needed to center
	 * the text based on the fonts ascent, descent and also the width of the
	 * whole text.
	 */
	public void getCentred() {
		FontMetrics metrics = _textArea.getFontMetrics(_font);

		int yMidPoint = (int) (_videoSize.getHeight() / 2);
		int ascent = metrics.getAscent();
		int descent = metrics.getDescent();

		if (ascent > descent) {
			_positionY = yMidPoint - (ascent + descent) / 2;
		} else if (ascent < descent) {
			_positionY = yMidPoint - (descent - ascent) / 2;
		}

		_stringWidth = metrics.stringWidth(_txt);
		_positionX = ((int) _videoSize.getWidth()) / 2 - (_stringWidth / 2);
	}

	/**
	 * This method checks whether the text width is larger than the width of the
	 * video
	 * 
	 * @return isTextTooLong
	 */
	public Boolean isTextTooLong() {
		if (_stringWidth > _videoSize.width) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * This method uses avprobe to obtain the dimensions of the video
	 * 
	 * @return Dimension of the video file
	 */
	private Dimension getDimensions() {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c",
				"avprobe " + _inputFile + " 2>&1 | grep -i video");
		builder.redirectErrorStream(true);

		Pattern progressValue = Pattern.compile("(\\d*)x(\\d*)");
		StringBuffer output = new StringBuffer();
		try {
			Process process = builder.start();
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(
					new InputStreamReader(stdout));
			String line;
			while ((line = stdoutBuffered.readLine()) != null) { // Maybe not
																	// needed
				output.append(line);
				output.append(" ");
			}
			Matcher matcher = progressValue.matcher(output);
			matcher.find();
			_videoSize = new Dimension(Integer.parseInt(matcher.group(1)),
					Integer.parseInt(matcher.group(2)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}