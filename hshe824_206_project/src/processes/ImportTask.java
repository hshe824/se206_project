package processes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.SwingWorker;

import ui.Library;

/**
 * This class represents the task of importing any files into the input library of the VAMIX application.
 * This is done in the background to prevent the GUI from freezing when copying large files.
 *
 * @author Harry She
 */
public class ImportTask extends SwingWorker<Void, Void> {

	private Path _input;
	private Path _importPath;
	private boolean errorState=false;

	public ImportTask(Path input, Path importPath) {
		_input = input;
		_importPath = importPath;
	}

	@Override
	protected Void doInBackground() throws Exception {
		FileChecker fc = new FileChecker(_input.toString());
		boolean hasAudio = fc.checkAVFile("Audio");
		boolean hasVideo = fc.checkAVFile("Video");
		if (!hasVideo && !hasAudio) {
			firePropertyChange("invalid", null,null);
			errorState=true;
			return null;
		}
		
		try {
			Files.copy(_input, _importPath);
		} catch (IOException e) {
			firePropertyChange("failure", null, e.getMessage());
		}
		return null;
	}

	@Override
	protected void done() {
		Library.getInstance().refreshTree();
		if (!errorState){
		firePropertyChange("success", null, "success");
		}
	}

}
