package boa.server.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Time
{

    private long time;

    public Time(){
		super();
	}

	public Time(long time) {
		super();
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

    
}
