/* Evolvo - Image Generator
 * Copyright (C) 2000 Andrew Molloy
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/**
 *  $Id$
 */

package org.maloi.evolvo.expressiontree.utilities;

import java.awt.image.BufferedImage;
import java.awt.image.SinglePixelPackedSampleModel;
import java.util.Random;

import org.maloi.evolvo.expressiontree.ExpressionTree;
import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.operators.OperatorList;
import org.maloi.evolvo.resources.Constants;
import org.maloi.evolvo.settings.GlobalSettings;

public class Tools
{
   static OperatorInterface[] scalarList = OperatorList.getScalarOperators();
   static OperatorInterface[] vectorList = OperatorList.getTripletOperators();
   static GlobalSettings settings = GlobalSettings.getInstance();

   static int[] masks;
   static int[] offsets;

   static final double map_height = Math.pow(0.5, 2);
   static final double PI_OVER_TWO = Math.PI / 2.0;

   static {
      SinglePixelPackedSampleModel tempSM =
         (SinglePixelPackedSampleModel) (new BufferedImage(1,
            1,
            BufferedImage.TYPE_INT_RGB)
            .getSampleModel());

      masks = tempSM.getBitMasks();
      offsets = tempSM.getBitOffsets();
   }

   /** No one should construct a tools */
   private Tools()
   {
   }

   /** Returns a randomly chosen operatorInterface object. */
   public static OperatorInterface pickRandomOp(
      Random whichOp,
      boolean returnsTriplet)
   {
      boolean flag = false;
      OperatorInterface pick = scalarList[0]; // keep the compiler happy
      double chance;

      OperatorInterface[] list;

      if (returnsTriplet)
      {
         list = vectorList;
      }
      else
      {
         list = scalarList;
      }

      // I don't really like this algorithm.
      // Probably should come up with something else in the near future.
      while (!flag)
      {
         pick = list[(int) (whichOp.nextDouble() * list.length)];
         chance = whichOp.nextDouble();
         if (chance
            < settings.getDoubleProperty(
               Constants.operatorPrefix + pick.getName()))
         {
            flag = true;
         }
      }

      return pick;
   }

   public static String toString(ExpressionTree[] expressions)
   {
      StringBuffer displayString = new StringBuffer();
      int i;

      String channelNames[] = new String[] { "Hue", "Saturation", "Value" };

      for (i = 0; i < 3; i++)
      {
         displayString.append("// " + channelNames[i] + "\n");
         displayString.append(expressions[i].toString());
         displayString.append("\n\n");
      }

      return displayString.toString();
   }

   /**
    * Converts from HSV to a packed RGB integer, given the correct bitmasks and
    * offsets into the RGB pixel for each sample. Masks and offsets are expected
    * to be given in RGB order 
    */

   public static int HSVtoRGB(double h, double s, double v)
   {
      // H is given on [0, 1] or UNDEFINED. S and V are given on [0, 1].
      // RGB are each returned on [0, 1].

      double m;
      double n;
      double f;

      double r;
      double g;
      double b;

      int i;

      if (h == Double.NaN
         || h == Double.NEGATIVE_INFINITY
         || h == Double.POSITIVE_INFINITY)
      {
         r = v;
         g = v;
         b = v;
      }
      else
      {
         h *= 6.0;

         i = (int)h;

         f = h - i;

         if ((i & 1) == 0)
         {
            // if i is even

            f = 1 - f;
         }

         m = v * (1 - s);
         n = v * (1 - s * f);

         switch (i)
         {
            case 1 :
               {
                  r = n;
                  b = v;
                  g = m;
               }
               break;
            case 2 :
               {
                  r = m;
                  g = v;
                  b = n;
               }
               break;

            case 3 :
               {
                  r = m;
                  g = n;
                  b = v;
               }
               break;
            case 4 :
               {
                  r = n;
                  g = m;
                  b = v;
               }
               break;
            case 5 :
               {
                  r = v;
                  g = m;
                  b = n;
               }
               break;
            case 6 :
            case 0 :
            default :
               {
                  r = v;
                  b = n;
                  g = m;
               }
               break;
         }
      }

      // now pack it into an int
      int rInt = (int) (r * 255);
      int gInt = (int) (g * 255);
      int bInt = (int) (b * 255);

      int pixel = 0;

      pixel |= (rInt << offsets[0]) & masks[0];
      pixel |= (gInt << offsets[1]) & masks[1];
      pixel |= (bInt << offsets[2]) & masks[2];

      return pixel;
   }

   public static double map(double a)
   {
      double a_squared = Math.pow(a, 2);
      double distance = Math.sqrt(map_height + a_squared);
      double angle = Math.asin(a / distance);
      double translated = (PI_OVER_TWO - Math.abs(angle)) / PI_OVER_TWO;

      return translated;
   }
}
