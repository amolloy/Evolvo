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

package org.asm.evolvo.expressiontree.operators;

import java.io.Serializable;
import java.util.Random;

import org.asm.evolvo.expressiontree.vm.Stack;

/**
 * Returns the value of a point in a 3 dimensional noise field.
 */
public class WarpedNoise implements OperatorInterface, Serializable
{
   /** The maximum size of the noise field. */
   static int maxNoise = 100;
   /** An array of data points in the noise field. */
   static double noiseTable[][][] = new double[maxNoise][maxNoise][maxNoise];

   /** Performs any initialization the operator requires.
     * In this case, generates a 3d array of random values. */
   public void init()
   {
      int ix, iy, iz;
      int xx, yy, zz;

      Random r = new Random();

      for (ix = 0; ix < maxNoise; ix++)
      {
         for (iy = 0; iy < maxNoise; iy++)
         {
            for (iz = 0; iz < maxNoise; iz++)
            {
               noiseTable[ix][iy][iz] = r.nextDouble() * 2.0 - 1.0;
               if (ix == maxNoise)
               {
                  xx = 0;
               }
               else
               {
                  xx = ix;
               }
               if (iy == maxNoise)
               {
                  yy = 0;
               }
               else
               {
                  yy = iy;
               }
               if (iz == maxNoise)
               {
                  zz = 0;
               }
               else
               {
                  zz = iz;
               }
               noiseTable[ix][iy][iz] = noiseTable[xx][yy][zz];
            }
         }
      }
   }

   /** Returns the fractional part of a double. */
   double frac(double r)
   {
      return (r - (double) (int) r);
   }

   /** Perform the operation. */
   public void perform(Stack theStack)
   {
      int xx, yy, zz;
      double x, y, z;
      int ix, iy, iz;
      double ox, oy, oz;
      double n;
      double n00, n01, n10, n11;
      double n0, n1;

      // First we evaluate each parameter, and find an x, y, and z value in 
      // [0,maxNoise-2]
      x = ((theStack.pop() * theStack.pop() + 1.0) / 2.0) * (maxNoise - 2.0);
      y = ((theStack.pop() * theStack.pop() + 1.0) / 2.0) * (maxNoise - 2.0);
      z = ((theStack.pop() * theStack.pop() + 1.0) / 2.0) * (maxNoise - 2.0);

      // Move the x,y,z values to integer space
      ix = (int) x % (maxNoise - 2) + 1;
      iy = (int) y % (maxNoise - 2) + 1;
      iz = (int) z % (maxNoise - 2) + 1;

      // Get the fractional part of each x,y, and z
      ox = frac(x);
      oy = frac(y);
      oz = frac(z);

      // First we get the values in the noise table that surround the actual 
      // point (x,y,z)
      n = noiseTable[ix][iy][iz];
      n00 = n + ox * (noiseTable[ix + 1][iy][iz] - n);
      n = noiseTable[ix][iy][iz + 1];
      n01 = n + ox * (noiseTable[ix + 1][iy][iz - 1] - n);
      n = noiseTable[ix][iy + 1][iz];
      n10 = n + ox * (noiseTable[ix + 1][iy + 1][iz] - n);
      n = noiseTable[ix][iy + 1][iz + 1];
      n11 = n + ox * (noiseTable[ix + 1][iy + 1][iz + 1] - n);

      n0 = n00 + oy * (n10 - n00);
      n1 = n01 + oy * (n11 - n01);

      theStack.push((n0 + oz * (n1 - n0)));
      // Return a value linearly interpolated from the noise tabled
   }

   /** Returns the operator's name. */
   public String getName()
   {
      return "warpednoise";
   }

   /** Returns the number of parameters expected by the operator. */
   public int getNumberOfParameters()
   {
      return 6;
   }
}
