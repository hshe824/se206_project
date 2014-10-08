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

public class BounceCreator extends SwingWorker<Void, Void> {

		private String _inputFile;
		private String _outputFile;
		private boolean errorState;
		private Dimension _videoSize;
		private int _numShapes;
		
		
		// Replace and Overlay constructor
		public BounceCreator(String inputFile, String outputFile, int numShapes) {
			_inputFile = inputFile;
			_outputFile = outputFile;
			_numShapes=numShapes;
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
			Process process=null;
			builder = new ProcessBuilder("/bin/bash", "-c",  "avconv -i"
					+ _inputFile + " -s qvga -vf format=rgb8,format=rgb24 -pix_fmt rgb24 -r 10 -t 20 " + " -y "+ VideoTask.tempDir+"1.gif");
			System.out.println("avconv -i"
					+ _inputFile + " -s qvga -vf format=rgb8,format=rgb24 -pix_fmt rgb24 -r 10 -t 20 " + " -y "+ VideoTask.tempDir+"1.gif");
			process=builder.start();
			if (process.waitFor()==0) {
				new ProcessBuilder("/bin/bash", "-c",  "avconv -i"
						+ _inputFile + " -s qvga -vf format=rgb8,format=rgb24 -pix_fmt rgb24 -r 10 -t 20 " + " -y "+ VideoTask.tempDir+"2.gif");
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
	
	}


