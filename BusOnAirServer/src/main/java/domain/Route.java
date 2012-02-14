package domain;

import java.util.ArrayList;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

public class Route {
    private static final String TYPE = "type";
    private static final String LINE = "line";
    
    private final Node underlyingNode;
    
    private Index<Node> runIndex;
    
    public Route(Node node){
    	underlyingNode = node;
        runIndex = DbConnection.getDb().index().forNodes("runIndex" + getLine());
    }  

	public Route(Node node, String line){
            underlyingNode = node;
	    setLine(line);
	    setType();
            runIndex = DbConnection.getDb().index().forNodes("runIndex" + getLine());
	}   

	public void setType() {
            underlyingNode.setProperty(Route.TYPE, "Route");		
	}

        public void addRun(Run r){
            runIndex.add(r.getUnderlyingNode(), "id", r.getId());
            //runIndex.add(r.getUnderlyingNode(), "time", r.getFirstStop().getTime());
        }
        
        public Run getRun(int id){
            IndexHits<Node> result = runIndex.get("id", id);
            Node n = result.getSingle();
            result.close();
            if(n == null){
                return null;
            } else {
                return new Run(n);    
            }
        }
        
    public Node getUnderlyingNode(){
        return underlyingNode;
    }
		
	public String getLine(){
		return (String) underlyingNode.getProperty(LINE);
	}
		
	public void setLine(String line){
		underlyingNode.setProperty(Route.LINE, line);
	}

    @Override
    public boolean equals(final Object otherRoute){
        if (otherRoute instanceof Route){
            return underlyingNode.equals(((Route) otherRoute).getUnderlyingNode());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return underlyingNode.hashCode();
    }

    @Override
	public String toString(){
		return ("Route: " +
				"\n\tline: " + getLine());	    
    }

    public ArrayList<Run> getAllRuns() {
        ArrayList<Run> output = new ArrayList<Run>();
        IndexHits<Node> result = runIndex.query("id", "*");
        for(Node n : result){
            output.add(new Run(n));           
        }        
        result.close();
        return output;
    }

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}
}
