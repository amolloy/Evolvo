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

package org.maloi.evolvo.expressiontree.operators;

import java.io.Serializable;

import org.maloi.evolvo.expressiontree.vm.Stack;

/**
 * An implementation of the  mandelbrot set.
 */
public class Mandel implements OperatorInterface, Serializable
{

   /** Perform the operation. */
   public void perform(Stack theStack)
   {
      double x1 = theStack.pop();
      double y1 = theStack.pop();
      double x2 = theStack.pop();
      double y2 = theStack.pop();
      double x = theStack.pop();
      double y = theStack.pop();
      double OLD_A;
      int Iteration;
      double A, B;
      double lengthz;

      double CA;
      double CBi;

      if (x1 > x2)
      {
         double temp = x1;
         x1 = x2;
         x2 = temp;
      }
      if (y1 > y2)
      {
         double temp = y1;
         y1 = y2;
         y2 = temp;
      }

      CA = (x2 - x1) * ((x + 1.0) / 2.0) + x1;
      CBi = (y2 - y1) * ((y + 1.0) / 2.0) + y1;

      A = 0.0;
      B = 0.0;

      Iteration = 0;

      do
      {
         OLD_A = A;

         A = A * A - B * B + CA;
         B = 2 * OLD_A * B + CBi;

         Iteration++;

         lengthz = A * A + B * B;
      }
      while ((lengthz <= 4.0) && (Iteration < 255));

      theStack.push((double) Iteration / 128.0 - 1.0);
   }

   /** Returns the operator's name. */
   public String getName()
   {
      return "mandel";
   }

   /** Performs any initialization the operator requires. */
   public void init()
   {
   }

   /** Returns the number of parameters expected by the operator. */
   public int getNumberOfParameters()
   {
      return 6;
   }
}
