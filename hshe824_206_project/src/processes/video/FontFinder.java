package processes.video;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * This class provides all the fonts in the font package file.
 * {@link #setUp()} method needs to be called before use.
 * 
 * NB: taken from assignment 3
 * 
 * @author Harry & Greggory
 *
 */
public class FontFinder {

	private HashMap<String, String> _fontDirectory = new HashMap<String, String>();
	private List<Font> _fontList = new ArrayList<Font>();

	/**
	 * Loads all the Fonts into an ArrayList and the String containing the 
	 * Font directory into a HashMap
	 */
	public void setUp() {
		File folder = new File("/usr/share/fonts/truetype/dejavu");
		File[] listofFiles = folder.listFiles();

		for (int i = 0; i < listofFiles.length; i++) {
			if (listofFiles[i].isFile()) {
				try {
					Font newFont = Font.createFont(Font.TRUETYPE_FONT,
							listofFiles[i]);
					_fontList.add(Font.createFont(Font.TRUETYPE_FONT,
							listofFiles[i]));
					_fontDirectory.put(newFont.getName(), listofFiles[i].toString());
				} catch (FontFormatException | IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Returns the List containing all the font objects
	 * @return
	 */
	public List<Font> getFontList() {
		return _fontList;
	}
	
	/**
	 * Returns the required font based on the name 
	 * @param fontName
	 * @return
	 */
	public Font searchFont(String fontName){
		Font wantedFont = null;
		for (Font font :_fontList){
			if (fontName.equals(font.getName())){
				wantedFont = font.deriveFont(12);
			}
		}
		return wantedFont;
	}
	
	/**
	 * Returns the corresponding font directory
	 * @param fontName
	 * @return
	 */
	public String getFontDirectory(String fontName){
		return _fontDirectory.get(fontName);
	}
}
