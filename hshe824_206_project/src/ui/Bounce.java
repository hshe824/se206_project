package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import bounce.AnimationViewer;

import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;
import javax.swing.JButton;
import javax.swing.JTextField;

public class Bounce extends JPanel {

	private static Bounce theInstance = null;
	private JTextField textField;
	protected AnimationViewer animationViewer;

	public Bounce() {

		setLayout(new MigLayout("", "[grow]", "[grow 90][grow 10]"));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED,
				null, null, null, null), "Bounce controls",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel, "cell 0 1,growx 10,growy");
		panel.setLayout(new MigLayout("", "[grow][grow]", "[grow][grow]"));

		textField = new JTextField();
		panel.add(textField, "cell 0 0 2 1,alignx center");
		textField.setColumns(10);

		JButton bounce = new JButton("Bounce");
		panel.add(bounce, "cell 0 1,alignx center,aligny center");
		bounce.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (animationViewer != null) {
					remove(animationViewer);
				}

				animationViewer = new AnimationViewer();
				add(animationViewer, "cell 0 0,growx 90,growy");
				validate();
				repaint();
			}
		});

		JButton btnBouncemania = new JButton("Bouncemania!");
		panel.add(btnBouncemania, "cell 1 1,alignx center,aligny center");
		btnBouncemania.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (animationViewer != null) {
					remove(animationViewer);
				}

				animationViewer = new AnimationViewer(true);
				add(animationViewer, "cell 0 0,growx 90,growy");
				validate();
				repaint();
			}
		});
	}

	/**
	 * Grab the singleton instance of this class.
	 * 
	 * @return
	 */
	public static Bounce getInstance() {
		if (theInstance == null) {
			theInstance = new Bounce();
		}
		return theInstance;
	}

}
