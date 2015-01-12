/**
 * An enum to define which actions are available.
 */
package klt.util;

/**
 * @author LarsE
 * 17.11.2014
 */
public enum Actions_E
{
   STAY, UP, DOWN, LEFT, RIGHT, BOMB;
   
   
   public static int getActionCount() {
       return 6;
   }
}