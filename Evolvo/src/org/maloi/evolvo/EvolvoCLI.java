/*
 * Created on May 7, 2004
 */
package org.maloi.evolvo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import org.maloi.evolvo.expressiontree.ExpressionTree;
import org.maloi.evolvo.expressiontree.renderer.StreamRenderer;
import org.maloi.evolvo.expressiontree.utilities.ExpressionTreeParser;
import org.maloi.evolvo.expressiontree.utilities.SyntaxErrorException;
/**
 * @author amolloy
 * 
 * TODO
 */
public class EvolvoCLI
{
   public static void main(String[] args)
   {
      double[] argd = new double[4];
      int[] argi = new int[2];
      double x1, y1, x2, y2;
      int width, height;
      StreamRenderer sr;
      ExpressionTree et;
      if (args.length != 6)
      {
         System.err.println("Usage:\n");
         System.err.println("EvolvoCLI x1 y1 x2 y2 w h");
         System.exit(1);
      }
      for (int i = 0; i < 4; i++)
      {
         try
         {
            argd[i] = Double.parseDouble(args[i]);
         }
         catch (NumberFormatException nfe)
         {
            System.err.print("Invalid argument: ");
            System.err.println(args[i]);
            System.exit(2);
         }
      }
      for (int i = 4; i < 6; i++)
      {
         try
         {
            argi[i - 4] = Integer.parseInt(args[i]);
         }
         catch (NumberFormatException nfe)
         {
            System.err.print("Invalid argument: ");
            System.err.println(args[i]);
            System.exit(3);
         }
      }
      x1 = argd[0];
      y1 = argd[1];
      x2 = argd[2];
      y2 = argd[3];
      width = argi[0];
      height = argi[1];
      et = null;
      try
      {
         Reader r = new BufferedReader(new InputStreamReader(System.in));
         et = ExpressionTreeParser.parse(new StreamTokenizer(r));
      }
      catch (IOException ioe)
      {
         System.err.println("Caught IOException:");
         ioe.printStackTrace(System.err);
         System.exit(3);
      }
      catch (SyntaxErrorException see)
      {
         System.err.println("Syntax error:");
         see.printStackTrace(System.err);
         System.exit(4);
      }
      // at this point we have an ExpressionTree and the region we are to
      // render, as well as the resolution to render at
      // so, we can go ahead and render it
      sr = new StreamRenderer(et, new BufferedWriter(new OutputStreamWriter(
            System.out)), x1, y1, x2, y2, width, height);
      sr.start();
      
      System.exit(0);
   }
}