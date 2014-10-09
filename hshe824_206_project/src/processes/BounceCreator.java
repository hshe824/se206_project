package processes;

import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import bounce.AnimationViewer;

public class BounceCreator extends SwingWorker<Void, Void> {

	private String _inputFile;
	private boolean errorState;
	private int _numShapes;
	protected AnimationViewer animationViewer;
	private int _duration;

	// Replace and Overlay constructor
	public BounceCreator(String inputFile, int numShapes, int duration) {
		_inputFile = inputFile;
		_numShapes = numShapes;
		_duration = duration;
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
		Process process = null;
		if (_numShapes == 42){
			return null;
		} else {
		for (int i = 1; i <= _numShapes; i++) {
			int _startTimeRnd = (int) (Math.random() * ((_duration) + 1));
			builder = new ProcessBuilder(
					"/bin/bash",
					"-c",
					"avconv -ss "
							+ _startTimeRnd
							+ " -i "
							+ _inputFile
							+ " -vf scale=320:-1,format=rgb8,format=rgb24 -t 10 -r 10 -y "
							+ VideoTask.tempDir + File.separator + i + ".gif");
			process = builder.start();
			process.waitFor();
		}
		}
		return null;

	}

	@Override
	protected void done() {
		try {
			if (errorState == false) {
				this.get();
				firePropertyChange("success", null, "success");
			}
		} catch (CancellationException e) {
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
