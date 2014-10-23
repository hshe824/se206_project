package model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import sun.misc.Sort;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

/**
 * 
 * 
 * @author Harry
 *
 *         Boilerplate JTable code from
 *         http://www.codejava.net/java-se/swing/editable-jtable-example
 */
public class SubtitleModel extends AbstractTableModel {
	private List<Subtitle> subtitleList = new ArrayList<Subtitle>();

	private final String[] columnNames = new String[] { "Start time:", "End time:", "Subtitles" };
	private final Class[] columnClass = new Class[] { String.class, String.class, String.class };

	public SubtitleModel() {
	}

	public SubtitleModel(List<Subtitle> sb) {
		this.subtitleList = sb;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnClass[columnIndex];
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return subtitleList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Subtitle row = subtitleList.get(rowIndex);
		if (0 == columnIndex) {
			return row.getStartTime();
		} else if (1 == columnIndex) {
			return row.getEndTime();
		} else if (2 == columnIndex) {
			return row.getSubtitle();
		}
		return null;
	}

	/*
	 * Only the subtitles are editable directly, the start and end times must be
	 * obtained from the video.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 2 ? true : false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try {
			Subtitle row = subtitleList.get(rowIndex);
			if (0 == columnIndex) {
				row.setStartTime((String) aValue);
			} else if (1 == columnIndex) {
				row.setEndTime((String) aValue);
			} else if (2 == columnIndex) {
				row.setSubtitle((String) aValue);
			}
		} catch (ArrayIndexOutOfBoundsException a) {
		}
	}

	public void addRow() {
		subtitleList.add(new Subtitle("00:00:00", "00:00:10", "Please enter subtitle here"));
	}

	public void removeRow(int selectedRow) {
		subtitleList.remove(selectedRow);
	}

	public List<Subtitle> getSubtitleList() {
		return subtitleList;
		
	}
	
	public void setSubtitleList(List<Subtitle> subtitles) {
		this.subtitleList=subtitles;
		
	}
	
}