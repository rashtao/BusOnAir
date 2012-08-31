package boa.server.domain.utils;

import java.util.Comparator;

import boa.server.domain.Stop;

     public class TimeComparator implements Comparator{
        
        @Override 
        public int compare(Object o1, Object o2){

            int time1 = ((Stop)o1).getTime();
            int time2 = ((Stop)o2).getTime();

            if(time1 > time2)
                return 1;
            else if(time1 < time2)
                return -1;
            else
                return 0;    
        }

    }