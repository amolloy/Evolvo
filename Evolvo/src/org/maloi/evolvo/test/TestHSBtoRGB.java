package org.maloi.evolvo.test;

import org.maloi.evolvo.expressiontree.utilities.Tools;
import org.maloi.evolvo.gui.SystemConsole;

/**
 * @author Andy Molloy
 *
 */
public class TestHSBtoRGB extends Harness
{
   SystemConsole console = SystemConsole.getInstance();
   
   public void run()
   {
      doTest(1, test1());
   }

   public boolean test1()
   {
      double h = 0.2;
      double s = 0.4;
      double v = 0.5;

      int fromJava = java.awt.Color.HSBtoRGB((float) h, (float) s, (float) v);

      int fromTools = Tools.HSVtoRGB(h, s, v);

      console.println("fromJava: " + Integer.toHexString(fromJava)); //$NON-NLS-1$
      console.println("fromTools: " + Integer.toHexString(fromTools)); //$NON-NLS-1$

      return (fromJava == fromTools);

   }

   public static void main(String[] args)
   {
      TestHSBtoRGB theTest = new TestHSBtoRGB();

      theTest.run();
   }
}
