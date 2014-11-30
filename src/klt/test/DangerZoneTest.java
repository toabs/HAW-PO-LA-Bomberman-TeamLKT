/**
 * 
 */
package klt.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/* ************************************************************** */
/**
 * @author ToRoSaR
 * 30.11.2014
 */
/* *********************************************************** */
public class DangerZoneTest
{

    @Test
    public void test()
    {
        int countZoneStatus = 3;
        int dangerCurrent = 0;
        int dangerTop = 0;
        int dangerBot = 0;
        int dangerLeft = 0;
        int dangerRight = 0;
        
        int result = 0;
        Set<Integer> resultSet = new HashSet<Integer>();        
        
        for(int c = 0; c < countZoneStatus; c++) {
            dangerCurrent = c;
            for(int t = 0; t < countZoneStatus; t++) {
                dangerTop = t;
                for(int b = 0; b < countZoneStatus; b++) {
                    dangerBot = b;
                    for(int l = 0; l < countZoneStatus; l++) {
                        dangerLeft = l;
                        for(int r = 0; r < countZoneStatus; r++) {
                            dangerRight = r;
                            result = 0;
                            result += (dangerCurrent * Math.pow(countZoneStatus, 0));
                            result += (dangerTop     * Math.pow(countZoneStatus, 1));
                            result += (dangerBot     * Math.pow(countZoneStatus, 2));
                            result += (dangerLeft    * Math.pow(countZoneStatus, 3));
                            result += (dangerRight   * Math.pow(countZoneStatus, 4));

                            System.out.println("DC:" + dangerCurrent);
                            System.out.println("DT:" + dangerTop);
                            System.out.println("DB:" + dangerBot);
                            System.out.println("DL:" + dangerLeft);
                            System.out.println("DR:" + dangerRight);
                            System.out.println("Result" + result);
                            
                            resultSet.add(result);
                        }
                    }
                }
            }
        }//for
        System.out.println("Count:" + resultSet.size());
    }

}
