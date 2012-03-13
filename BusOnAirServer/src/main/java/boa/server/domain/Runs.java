/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package boa.server.domain;

import java.util.ArrayList;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

/**
 *
 * @author rashta
 */
public class Runs {
    private Index<Node> runsIndex;

    private static Runs instance = null;
    
    public static synchronized Runs getRuns() {
        if (instance == null) 
            instance = new Runs();
        return instance;
    }    
    
    private Runs(){
        runsIndex = DbConnection.getDb().index().forNodes("runsIndex");
    }
    
    public void addRun(Run r){
        runsIndex.add(r.getUnderlyingNode(), "id", r.getId());
    }
    
    public Run getRunById(Integer id){
        IndexHits<Node> result = runsIndex.get("id", id);
        Node n = result.getSingle();
        result.close();
        if(n == null){
            return null;
        } else {
            return new Run(n);                
        }
    }

    public ArrayList<Run> getAll() {
        ArrayList<Run> output = new ArrayList<Run>();
        IndexHits<Node> result = runsIndex.query("id", "*");
        for(Node n : result){
            output.add(new Run(n));           
        }     
        result.close();
        return output;
    }
    
    public void updateIndex(Run r){
    	runsIndex.remove(r.getUnderlyingNode());
    	addRun(r);
    }
}
