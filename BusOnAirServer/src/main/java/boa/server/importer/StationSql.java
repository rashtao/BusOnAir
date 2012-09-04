package boa.server.importer;
import boa.server.domain.json.Coordinate;
import boa.server.domain.json.Station;

public class StationSql {
    public int Id_Station;
    public String name;
    public double lat;
    public double lng;
    public boolean is_school;
    public boolean is_terminal;
        
    
    public Station toJSON(){
    	return new Station(Id_Station, name, new Coordinate(lat, lng));  
    }
    
    @Override
    public String toString() {
		return ("StationSql: " +
				"\n\tId_Station: " + Id_Station + 
				"\n\tname: " + name + 
				"\n\tlat: " + lat + 
				"\n\tlng: " + lng + 
				"\n\tis_school: " + is_school + 
				"\n\tis_terminal: " + is_terminal);	
    }
}
