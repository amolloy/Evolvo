package org.maloi.evolvo.test;

import java.awt.image.BufferedImage;
import java.awt.image.SinglePixelPackedSampleModel;

import org.maloi.evolvo.expressiontree.utilities.Tools;

/**
 * @author Andy Molloy
 *
 */
public class TestHSBtoRGB extends Harness
{
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

      System.err.println("fromJava: " + Integer.toHexString(fromJava));
      System.err.println("fromTools: " + Integer.toHexString(fromTools));

      return (fromJava == fromTools);

   }

   public static void main(String[] args)
   {
      TestHSBtoRGB theTest = new TestHSBtoRGB();

      theTest.run();
   }
}
