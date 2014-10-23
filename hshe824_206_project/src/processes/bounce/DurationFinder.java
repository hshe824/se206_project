package processes.bounce;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.SwingWorker;

import ui.filesystem.Library;

/**
 * This class represents the task of finding the duration of a particular media file.
 * This is done in the background to prevent the GUI from freezing when copying large files.
 *
 * @author Harry She
 */
public class DurationFinder extends SwingWorker<Integer, Void> {

	private String _inputFile;

	public DurationFinder(String inputFile) {
		_inputFile = inputFile;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c","avprobe "+ _inputFile+ " 2>&1 | grep Duration");
		Process process = null;
		try {
			process = builder.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		InputStream stdout = process.getInputStream();
		BufferedReader stdoutBuffered = new BufferedReader(
				new InputStreamReader(stdout));
		String last = "";
		String line="";
		try {
			while ((line = stdoutBuffered.readLine()) != null) {
				last=line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] raw=last.split(",");
		String durationOnlyString= raw[0];
		String[] hoursMinsSecs= durationOnlyString.split(":");
		int hours=Integer.parseInt(hoursMinsSecs[1].trim());
		int mins=Integer.parseInt(hoursMinsSecs[2].trim());
		int secs=Integer.parseInt(hoursMinsSecs[3].substring(0,hoursMinsSecs[3].lastIndexOf('.')).trim());
		int finalTimeSecs= (hours*3600)+(mins*60)+secs;
		
		return finalTimeSecs;
		
	}

}
