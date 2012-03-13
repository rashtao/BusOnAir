package boa.server.domain;

import org.neo4j.graphdb.Node;

public abstract class StopAbstract{
    private static final String ID = "id";
    private static final String TYPE = "type";
        
    protected final Node underlyingNode;

    public StopAbstract(){
    	underlyingNode = null;
    }
    
    public StopAbstract(Node node) {
    	underlyingNode = node;
    }  

    public StopAbstract(Node node, int id) {
    	this(node);
    	setId(id);
    }  

    public Node getUnderlyingNode(){
        return underlyingNode;
    }

	public Integer getId(){
		return (Integer) underlyingNode.getProperty(ID);
	}
	
    public String getType(){
    	return (String) underlyingNode.getProperty(TYPE);
    }

	public void setId(int id){
		underlyingNode.setProperty(StopAbstract.ID, id);		
	}
	
	public abstract Station getStazione();
		
}
