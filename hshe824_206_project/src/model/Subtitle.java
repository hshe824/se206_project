package model;

public class Subtitle {
	
	private String startTime;
	private String endTime;
	private String subtitle;
	
	public Subtitle (String st, String et, String subs) {
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
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	

}
