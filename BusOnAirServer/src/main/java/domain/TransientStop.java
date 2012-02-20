package domain;

import org.neo4j.graphdb.Node;

public class TransientStop extends Stop {
	private int time;
	
	public TransientStop(){
		super();		
	}

	@Override
	public void setTime(int time){
		this.time = time;
	}
	
	@Override
    public Integer getTime(){
        return (Integer) time;
    }
	
}
