package ui.filesystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ui.Main;
import ui.Pane;
import ui.editors.AudioEditor;
import ui.editors.SubtitleEditor;
import ui.editors.VideoEditor;
import ui.special.Bounce;
import ui.special.Filters;

/**
 * This listener class has been created to deal with all new opening of a file
 * in the input library for each given operation to open a new tab and deal with
 * that input file.
 * 
 * @author Harry She
 *
 */
public class OpenListener implements ActionListener {

	String _tabName = "";
	Pane _currentPane;

	public OpenListener(String tabName) {
		_tabName = tabName;

		switch (tabName) {
		case "Audio Editor":
			_currentPane = AudioEditor.getInstance();
			break;
		case "Video Editor":
			_currentPane = VideoEditor.getInstance();
			break;
		case "Filters":
			_currentPane = Filters.getInstance();
			break;
		case "Subtitles":
			_currentPane = SubtitleEditor.getInstance();
			break;
		case "Bounce!":
			_currentPane = Bounce.getInstance();
			break;
		default:
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Library._currentFileString != null
				&& !Library._currentFileString.equals("")) {
			if (Main._tabbedPane.indexOfTab(_tabName) == -1) {
				Main.createNewTab(_tabName, _currentPane,
						Main._tabbedPane.getTabCount());
				Main._tabbedPane.setSelectedIndex(Main._tabbedPane
						.getTabCount() - 1);
			} else {
				Main._tabbedPane.setSelectedIndex(Main._tabbedPane
						.indexOfTab(_tabName));
			}
			_currentPane.setInputFile(Library._currentFileString);
		}
	}
}
