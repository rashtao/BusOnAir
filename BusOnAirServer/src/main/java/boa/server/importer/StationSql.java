package boa.server.importer;

public class StationSql {
    public int Id_Station;
    public String name;
    public double lat;
    public double lng;
    public boolean is_school;
    public boolean is_terminal;
        
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
