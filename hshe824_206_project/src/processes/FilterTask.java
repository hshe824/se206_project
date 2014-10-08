package processes;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import ui.Library;

public class FilterTask extends SwingWorker<Void, Void>{
	private String _inputFile;
	private String _outputFile;
	private String _cmd;
	private boolean errorState;
	private String _filter="unsharp";
	private boolean _isPreview;
	private Dimension _videoSize;
	private Color _colourTint=Color.red;
	private String _colour;
	
	
	// Replace and Overlay constructor
	public FilterTask(String inputFile, String outputFile, String cmd, boolean isPreview, Color colourTint) {
		_inputFile = inputFile;
		_outputFile = outputFile;
		_cmd = cmd;
		_isPreview=isPreview;
		_colourTint= colourTint;
		_colour = String
				.format("%02x%02x%02x%02x", _colourTint.getRed(),
						_colourTint.getGreen(), _colourTint.getBlue(), _colourTint.getAlpha());
	}

	// Strip audio track constructor
	

	/**
	 * Calls avconv command to perform a specific audio task.
	 * 
	 * Can strip audio, replace or overlay audio on another selected input file.
	 * 
	 * Relays success or errors back to EDT to deal with.
	 * 
	 */
	@Override
	protected Void doInBackground() throws Exception {
		ProcessBuilder builder = null;
		getDimensions();
		errorState = false;
		switch (_cmd) {
		case "Blur":
			_filter="boxblur=10:1:0:0:0:0";
			break;
		case "Mirror Video":
			_filter="hflip";
			break;
		case "Flip upside-down":
			_filter="vflip";
			break;
		case "Fade into start":
			_filter="\"fade=in:0:100\"";
			break;
		case "Add colour tint":
			_filter="\"color=0x"+_colour+"@0.3:"+(int)_videoSize.getWidth()+"x"+(int)_videoSize.getHeight()+":10 [color]; [in][color] overlay [out]\"";
			System.out.println(_filter);
			break;
		}
		if (_isPreview){
			builder = new ProcessBuilder("/bin/bash", "-c",  "avplay -i "
					+ _inputFile + " -strict experimental -vf " + _filter);
			process(builder);
		}else {
		builder = new ProcessBuilder("/bin/bash", "-c",  "avconv -i "
				+ _inputFile + " -strict experimental -vf " + _filter + " -y "+ _outputFile);
		System.out.println("avconv -i "
				+ _inputFile + " -strict experimental -vf " + _filter + " -y "+ _outputFile);
		process(builder);
		}
		return null;
	}

	@Override
	protected void done() {
		try {
			if (errorState == false) {
				Library.getInstance().refreshTree();
				this.get();
				firePropertyChange("success", null, "success");
			}
		} catch (CancellationException e) {
			firePropertyChange("cancelled", null, "The filtering task was stopped!");
			File toDelete= new File(_outputFile);
			toDelete.delete();
			Library.getInstance().refreshTree();
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

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
				//Debugging
				//System.out.println(line);
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
