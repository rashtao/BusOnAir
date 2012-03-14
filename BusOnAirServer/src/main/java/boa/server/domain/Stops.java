package boa.server.domain;

import java.util.ArrayList;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

public class Stops {
    private Index<Node> stopsIndex;

    private static Stops instance = null;
    
    public static synchronized Stops getStops() {
        if (instance == null) 
            instance = new Stops();
        return instance;
    }    
    
    private Stops(){
        stopsIndex = DbConnection.getDb().index().forNodes("stopsIndex");
    }
    
    public void addStop(Stop s){
        stopsIndex.add(s.getUnderlyingNode(), "id", s.getId());
    }
    
    public Stop getStopById(int id){
        IndexHits<Node> result = stopsIndex.get("id", id);
        Node n = result.getSingle();
        result.close();
        if(n == null){
            return null;
        } else {
            return new Stop(n);                
        }
    }

    public ArrayList<Stop> getAll() {
        ArrayList<Stop> output = new ArrayList<Stop>();
        IndexHits<Node> result = stopsIndex.query("id", "*");
        for(Node n : result){
            output.add(new Stop(n));           
        }     
        result.close();
        return output;
    }
}
