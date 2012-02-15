package importer;

import utils.*;

public class StopSql {

    private static final java.sql.Time MIDNIGHT = java.sql.Time.valueOf("00:00:00");

    public String Id_Stop;
    public java.sql.Time time_stop;
    public int src;
    public int dst;
    public int run;
    public String line;
    public String note;
    public boolean is_weekday;
    public String importedFrom;
    public String runcode;
    
    public int getMinutesFromMidn(){
    	return DateUtil.minutesDiff(MIDNIGHT, time_stop);
    }
    
	@Override
	public String toString() {
		int diff = DateUtil.minutesDiff(MIDNIGHT, time_stop);
	
		return ("StopSql: " +
				"\n\tId_Stop: " + Id_Stop + 
				"\n\ttime_stop: " + time_stop + 
				"\n\tminFromMidn: " + diff + 
				"\n\tsrc: " + src + 
				"\n\tdst: " + dst + 
				"\n\trun: " + run + 
				"\n\tline: " + line + 
				"\n\tnote: " + note + 
				"\n\tis_weekday: " + is_weekday + 
				"\n\timportedFrom: " + importedFrom + 
				"\n\truncode: " + runcode);	
	}
}