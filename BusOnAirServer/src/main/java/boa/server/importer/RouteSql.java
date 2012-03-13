package boa.server.importer;

public class RouteSql {

    public int Id_Route;
    public int Id_Station;
    public int order;
    public String line;
  
    @Override
    public String toString() {
		return ("RouteSql: " +
				"\n\tId_Route: " + Id_Route + 
				"\n\tId_Station: " + Id_Station + 
				"\n\torder: " + order + 
				"\n\tline: " + line);
	}
}
