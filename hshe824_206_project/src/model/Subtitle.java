package model;

/**
 * This class represents a subtitle object which contains its startTime, endTime
 * and subtitle to be used in subtitle editing.
 * 
 * Implements the comparable interface based on starting time so that it always
 * sorts itself if out of order
 * 
 * @author Harry She
 *
 */
public class Subtitle implements Comparable<Subtitle> {

	private String startTime;
	private String endTime;
	private String subtitle;

	public Subtitle(String st, String et, String subs) {
		this.setStartTime(st);
		this.setEndTime(et);
		this.setSubtitle(subs);
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Override
	public int compareTo(Subtitle o) {
		return startTime.compareTo(o.getStartTime());
	}

}
