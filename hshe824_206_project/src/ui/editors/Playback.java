package ui.editors;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SliderUI;

import model.RoundButton;
import net.miginfocom.swing.MigLayout;
import processes.video.RewindTask;
import ui.Pane;
import ui.filesystem.Library;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.x.XFullScreenStrategy;

import javax.swing.JButton;

/**
 * This class creates the layout of the playback panel and the listeners for the
 * components
 * 
 * NB: Taken from assignment 3.
 * 
 * 
 * @author Greggory and Harry
 *
 */
@SuppressWarnings("serial")
public class Playback extends Pane {

	private final String TEXT_PLAY = "Play";
	private final String TEXT_PAUSE = "Pause";
	private final String TEXT_UNMUTE = "Unmute";
	private final String TEXT_MUTE = "Mute";

	private RoundButton _playPauseButton = new RoundButton(createImageIcon("pause.png"),
			createImageIcon("pause_p.png"), createImageIcon("pause_r.png"), TEXT_PAUSE);
	private RoundButton _mute = new RoundButton(createImageIcon("unmute.png"), createImageIcon("unmute_p.png"),
			createImageIcon("unmute_r.png"), TEXT_UNMUTE);
	private RoundButton _stop = new RoundButton(createImageIcon("stop.png"), createImageIcon("stop_p.png"),
			createImageIcon("stop_r.png"), "Stop");
	private RoundButton _fastForward = new RoundButton(createImageIcon("fastforward.png"),
			createImageIcon("fastforward_p.png"), createImageIcon("fastforward_r.png"), "FastForward");
	private RoundButton _rewind = new RoundButton(createImageIcon("rewind.png"), createImageIcon("rewind_p.png"),
			createImageIcon("rewind_r.png"), "Rewind");

	private final JSlider _seekbar = new JSlider(0, 0, 0);
	private final JSlider _volume = new JSlider(0, 100, 50);
	private JLabel _currentTime = new JLabel("--:--:--");
	private JLabel _totalTime = new JLabel("--:--:--");
	private JLabel _status = new JLabel();
	private String _currentFileString = "";

	private Canvas _playerBG = new Canvas();
	public EmbeddedMediaPlayer _mediaPlayer;
	private MediaPlayerFactory _mediaPlayerFactory;

	private JPanel _playerPanel = new JPanel();
	private JPanel _seekPanel = new JPanel();
	private JPanel _playbackPanel = new JPanel();
	private JPanel _labelPanel = new JPanel();
	protected RewindTask rw;

	private boolean isFullScreen = false;
	private final JButton effects = new JButton("Effects");

	/**
	 * This constructor is for the main playback tab
	 */
	public Playback(String file) {
		_currentFileString = file;
		setLayout(new MigLayout("", "[grow]", "[520px,grow][20px][40px][20px]"));
		_seekPanel.setLayout(new MigLayout("", "[40px,grow 10][700px,grow 90][40px,grow 10]", "[20px]"));
		_playbackPanel
				.setLayout(new MigLayout("", "[40px][40px][10px][40px][40px][440px,grow][40px][160px]", "[40px]"));
		_playbackPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		_labelPanel.setLayout(new MigLayout("", "[grow]", "[20px]"));
		_playerBG.setMinimumSize(new Dimension(950, 430));

		setUp(true);
	}

	/**
	 * This constructor is for the mini version of playback
	 */
	public Playback() {
		setLayout(new MigLayout("", "[grow]", "[220px,grow][20px][40px][20px]"));
		_seekPanel.setLayout(new MigLayout("", "[20px,grow 10][350px,grow 90][20px,grow 10]", "[10px]"));
		_playbackPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		_playbackPanel.setLayout(new MigLayout("", "[20px][20px][5px][20px][20px][220px,grow][20px][80px]", "[20px]"));
		_playerBG.setMinimumSize(new Dimension(360, 220));

		setUp(false);
	}

	private void setUp(Boolean isPlayback) {
		add(_playerPanel, "cell 0 0,grow");
		add(_seekPanel, "cell 0 1,growx");
		add(_playbackPanel, "cell 0 2,growx");
		if (isPlayback) {
			add(_labelPanel, "cell 0 3,growx");
		}

		effects.setIcon(createImageIcon("effects.png"));
		effects.setFont(mainFont);

		_playerBG.setBackground(Color.BLACK);
		_playerBG.setVisible(true);
		_mediaPlayerFactory = new MediaPlayerFactory();

		// BoilerPlate code for media player set up taken
		// from vlcj api
		_mediaPlayer = _mediaPlayerFactory.newEmbeddedMediaPlayer();
		_mediaPlayer.setVideoSurface(_mediaPlayerFactory.newVideoSurface(_playerBG));
		_playerPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		_playerPanel.add(_playerBG, "cell 0 0, grow");

		_playbackPanel.add(_playPauseButton, "cell 0 0");
		_playbackPanel.add(_stop, "cell 1 0");
		_playbackPanel.add(_rewind, "cell 3 0");
		_playbackPanel.add(_fastForward, "cell 4 0");

		_playbackPanel.add(effects, "cell 5 0,alignx right");
		_playbackPanel.add(_mute, "cell 6 0");
		_playbackPanel.add(_volume, "cell 7 0");

		_status.setOpaque(true);
		_status.setBackground(Color.black);
		_status.setForeground(Color.white);
		_status.setText(_currentFileString);
		if (isPlayback) {
			_labelPanel.add(_status, "grow");
		}
		_seekPanel.add(_currentTime, "cell 0 0, alignx center");
		_seekPanel.add(_seekbar, "cell 1 0,grow");
		_seekPanel.add(_totalTime, "cell 2 0, alignx center");

		addListeners(isPlayback);
	}

	/**
	 * This method starts the EmbeddedMediaPlayer component and also sets up the
	 * seeker bar
	 * 
	 * @param file
	 */
	public void startPlayer(String file) {
		_mediaPlayer.prepareMedia(file);
		_mediaPlayer.play();

		long length = 0;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_mediaPlayer.parseMedia();
		length = _mediaPlayer.getLength();

		_mediaPlayer.mute(false);
		_volume.setValue(50);
		_mediaPlayer.setVolume(50);

		_totalTime.setText(time(length));
		_seekbar.setMaximum((int) length);

		// Code taken for Nasser's Lecture
		Timer updater = new Timer(250, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				long current = _mediaPlayer.getTime();
				_currentTime.setText(time(current));
				_seekbar.setValue((int) current);
			}
		});
		updater.start();
	}

	/**
	 * This method stops the player
	 */
	public void stopPlayer() {
		_mediaPlayer.stop();
	}

	/**
	 * Creates image icons for the button icons
	 * 
	 * @param name
	 * @return
	 */
	private ImageIcon createImageIcon(String name) {
		BufferedImage image;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(File.separator + "icons" + File.separator + name));
			return new ImageIcon(image);
		} catch (IOException e) {
			System.err.println("Couldn't find file: " + name);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method switches the icons & the description in the RoundButton
	 * 
	 * @param button
	 * @param type
	 */
	private void changeButton(RoundButton button, String type) {
		if (type.equals("")) {
			// Null checking
		} else if (type.equals(TEXT_PLAY)) {
			button.setIcons(createImageIcon("play.png"), createImageIcon("play_p.png"), createImageIcon("play_r.png"),
					TEXT_PLAY);
		} else if (type.equals(TEXT_PAUSE)) {
			button.setIcons(createImageIcon("pause.png"), createImageIcon("pause_p.png"),
					createImageIcon("pause_r.png"), TEXT_PAUSE);
		} else if (type.equals(TEXT_MUTE)) {
			button.setIcons(createImageIcon("mute.png"), createImageIcon("mute_p.png"), createImageIcon("mute_r.png"),
					TEXT_MUTE);
		} else if (type.equals(TEXT_UNMUTE)) {
			button.setIcons(createImageIcon("unmute.png"), createImageIcon("unmute_p.png"),
					createImageIcon("unmute_r.png"), TEXT_UNMUTE);
		}
	}

	/**
	 * This method adds the listeners for all the components
	 */
	private void addListeners(Boolean isPlayback) {

		effects.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame effectsFrame = new JFrame("Effects");
				effectsFrame.setMinimumSize(new Dimension(500, 300));
				effectsFrame.getContentPane().add(new MarqueeControls(_mediaPlayer));
				effectsFrame.setVisible(true);
			}
		});

		_mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			public void finished(MediaPlayer mediaPlayer) {
				_stop.doClick();
			}
		});

		_fastForward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					rw.cancel(true);
				} catch (NullPointerException ne) {
				}
				_rewind.setEnabled(true);
				if (_mediaPlayer.getRate() == 1.0) {
					_mediaPlayer.setRate((float) 2.0);
				} else if (_mediaPlayer.getRate() == 2.0) {
					_mediaPlayer.setRate((float) 4.0);
				} else if (_mediaPlayer.getRate() == 4.0) {
					_mediaPlayer.setRate((float) 8.0);
				}
				changeButton(_playPauseButton, TEXT_PLAY);
				_playPauseButton.validate();
			}
		});

		_rewind.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_mediaPlayer.getRate() == 2.0) {
					_mediaPlayer.setRate((float) 1.0);
				} else if (_mediaPlayer.getRate() == 4.0) {
					_mediaPlayer.setRate((float) 2.0);
				} else if (_mediaPlayer.getRate() == 8.0) {
					_mediaPlayer.setRate((float) 4.0);
				} else {
					_mediaPlayer.pause();
					rw = new RewindTask(_mediaPlayer);
					rw.execute();
					_rewind.setEnabled(false);
					changeButton(_playPauseButton, TEXT_PLAY);
					_playPauseButton.validate();
				}
			}
		});

		_playPauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent action) {
				try {
					rw.cancel(true);
				} catch (NullPointerException ne) {
				}
				_rewind.setEnabled(true);
				if (((RoundButton) action.getSource()).getDescription().equals(TEXT_PLAY)) {
					if (_mediaPlayer.getRate() > 1.0) {
						_mediaPlayer.setRate((float) 1.0);
						changeButton(_playPauseButton, TEXT_PAUSE);
						_playPauseButton.validate();
						_mediaPlayer.skip(-1);
						return;
					} else {
						_mediaPlayer.play();
						changeButton(_playPauseButton, TEXT_PAUSE);
						_playPauseButton.validate();
					}
				} else {
					_mediaPlayer.pause();
					changeButton(_playPauseButton, TEXT_PLAY);
					_playPauseButton.validate();
				}
			}
		});

		_stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					rw.cancel(true);
				} catch (NullPointerException ne) {
				}
				_rewind.setEnabled(true);
				stopPlayer();
				changeButton(_playPauseButton, TEXT_PLAY);
			}
		});

		_mute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (_mediaPlayer.isMute()) {
					_mediaPlayer.mute(false);
					changeButton(_mute, TEXT_UNMUTE);
				} else {
					_mediaPlayer.mute(true);
					changeButton(_mute, TEXT_MUTE);
				}
			}
		});

		_volume.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (_mediaPlayer.isPlaying() && source.getValueIsAdjusting()) {
					_mediaPlayer.setVolume(source.getValue());
				}
			}
		});

		_volume.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point p = e.getPoint();
				JSlider source = (JSlider) e.getSource();
				double percent = p.x / ((double) source.getWidth());
				int range = source.getMaximum() - source.getMinimum();
				double newVal = range * percent;
				int result = (int) (source.getMinimum() + newVal);
				source.setValue(result);
			}
		});

		_seekbar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (source.getValueIsAdjusting()) {
					_mediaPlayer.setTime((long) source.getValue());
				}
			}
		});

		_seekbar.addMouseListener(new MouseAdapter() {
			// Edited version of the code on
			// http://stackoverflow.com/questions/7095428/jslider-clicking-makes-the-dot-go-towards-that-direction
			@Override
			public void mousePressed(MouseEvent e) {
				Point p = e.getPoint();
				double percent = p.x / ((double) _seekbar.getWidth());
				int range = _seekbar.getMaximum() - _seekbar.getMinimum();
				double newVal = range * percent;
				int result = (int) (_seekbar.getMinimum() + newVal);
				_seekbar.setValue(result);
			}
		});

		/*
		 * Boilerplate fullscreen code adapted from
		 * http://stackoverflow.com/questions
		 * /21546540/vlcj-full-screen-video-player
		 */
		if (isPlayback) {
			_playerBG.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2 && !isFullScreen) {
						isFullScreen = true;
						long currentTime = _mediaPlayer.getTime();
						_mediaPlayer.pause();
						final JFrame frame = new JFrame("VAMIX FUllscreen");
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
						Canvas c = new Canvas();
						c.setBackground(Color.black);
						JPanel p = new JPanel();
						p.setLayout(new BorderLayout());
						p.add(c, BorderLayout.CENTER);
						frame.getContentPane().add(p, BorderLayout.CENTER);
						final EmbeddedMediaPlayer mediaPlayer = mediaPlayerFactory
								.newEmbeddedMediaPlayer(new DefaultFullScreenStrategy(frame));
						mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(c));
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						frame.setVisible(true);
						frame.addKeyListener(new KeyAdapter() {
							@Override
							public void keyPressed(KeyEvent e) {
								if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_Q) {
									_mediaPlayer.setTime(mediaPlayer.getTime());
									mediaPlayer.stop();
									frame.dispose();
									_mediaPlayer.play();
									isFullScreen = false;
								}
							}
						});

						c.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								if (e.getClickCount() == 2) {
									_mediaPlayer.setTime(mediaPlayer.getTime());
									mediaPlayer.stop();
									frame.dispose();
									_mediaPlayer.play();
									isFullScreen = false;
								}
							}
						});
						mediaPlayer.setFullScreen(true);
						mediaPlayer.playMedia(Library._currentFileString);
						mediaPlayer.setTime(currentTime);
					}
				}
			});
		}
	}

	/**
	 * This method takes the length in millisecond and converts it into the
	 * format "HOUR:MINUTER:SECOND"
	 * 
	 * @param millisec
	 * @return
	 */
	private String time(long millisec) {
		String time = "";
		int duration = (int) (millisec / 1000.00);
		int sec = (duration % 3600) % 60;
		int min = (duration % 3600) / 60;
		int hour = duration / 3600;
		DecimalFormat formatter = new DecimalFormat("00");
		if (duration < 3600) {
			time = formatter.format(min) + ":" + formatter.format(sec);
		} else {
			time = formatter.format(hour) + ":" + formatter.format(min) + ":" + formatter.format(sec);
		}
		return time;
	}

	public EmbeddedMediaPlayer getMediaPlayer() {
		return this._mediaPlayer;
	}

	@Override
	public void setInputFile(String inputFile) {
		throw new UnsupportedOperationException("Cannot set input file for playback this way");
	}

}
