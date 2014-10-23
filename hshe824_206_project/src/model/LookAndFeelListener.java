package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ui.Main;

/**
 * Listener class responsible for dynamically changing the theme (look and feel)
 * of the program when requested by the user.
 * 
 * @author Harry
 *
 */
public class LookAndFeelListener implements ActionListener {
	private String _lookAndFeel;

	public LookAndFeelListener(String lookAndFeel) {
		_lookAndFeel = lookAndFeel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			UIManager.setLookAndFeel(_lookAndFeel);
		} catch (ClassNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Warning: The look and feel: " + _lookAndFeel
					+ " was not found on this machine", "Warning: Theme not found!", JOptionPane.WARNING_MESSAGE);
			return;
		} catch (InstantiationException e1) {
		} catch (IllegalAccessException e1) {
		} catch (UnsupportedLookAndFeelException e1) {
		}
		SwingUtilities.updateComponentTreeUI(Main.window);
	}
}