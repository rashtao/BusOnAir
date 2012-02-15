package importer;

import java.util.ArrayList;
import java.util.List;


public class BusonairSql {

    public List<StopSql> stop_scheduleList;
    public List<RouteSql> routeList;
    public List<StationSql> stationList;

    public BusonairSql()
    {
        super();
        stop_scheduleList = new ArrayList<StopSql>();
        routeList = new ArrayList<RouteSql>();
        stationList = new ArrayList<StationSql>();
    }

    public List<StopSql> getStopList()
    {
        return stop_scheduleList;
    }    

    public List<RouteSql> getRouteList()
    {
        return routeList;
    }    

    public List<StationSql> getStationList()
    {
        return stationList;
    }    
}
