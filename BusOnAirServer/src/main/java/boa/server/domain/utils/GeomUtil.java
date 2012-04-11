package boa.server.domain.utils;

public class GeomUtil {
    
    public static Coordinate proiezione(Coordinate cp1, Coordinate cp2, Coordinate cp3) {
    	// proietta cp3 sulla retta passante per cp1-cp2
    	// cp1 è il pto in comune tra i 2 segmenti
    	
    	double distance = GeoUtil.getDistance2(cp1.lat, cp1.lon, cp3.lat, cp3.lon);
    	double rapporto = distance / GeoUtil.getDistance2(cp1.lat, cp1.lon, cp2.lat, cp2.lon);
    	
    	Coordinate d = new Coordinate();
    	
    	d.lat = cp1.lat * (1 - rapporto) + cp2.lat * rapporto;
    	d.lon = cp1.lon * (1 - rapporto) + cp2.lon * rapporto;
    	
    	return d;
    }
    
}
