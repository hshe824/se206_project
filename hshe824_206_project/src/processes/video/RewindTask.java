package processes.video;

import javax.swing.SwingWorker;

import ui.editors.Playback;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * The class that represents the worker that performs the rewinding in the
 * playback pane.
 * 
 * @author Harry She
 *
 */
public class RewindTask extends SwingWorker<Void, Void> {

	EmbeddedMediaPlayer _mediaPlayer;

	public RewindTask(EmbeddedMediaPlayer mediaPlayer) {
		_mediaPlayer = mediaPlayer;
	}

	@Override
	protected Void doInBackground() throws Exception {
		while (!isCancelled()) {

			_mediaPlayer.skip(-1000);
			Thread.sleep(100);
		}
		return null;
	}

}
