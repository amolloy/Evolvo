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

package org.maloi.evolvo.expressiontree.operators.scalar;

import java.io.Serializable;

import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.vm.Stack;
/**
 *  This is not really at all an implementation of an IFS fractal.
 */
public class Ifs implements OperatorInterface, Serializable
{
   /** Perform the operation. */
   public void perform(Stack theStack)
   {
      double x = (theStack.pop() + 1.0) / 2.0;
      double y = (theStack.pop() + 1.0) / 2.0;
      double choice = theStack.pop();
      double result = 0.0;

      double trans[] = new double[6];

      for (int i = 0; i < 6; i++)
      {
         trans[i] = (theStack.pop() + 1.0) / 2.0;
      }

      if (choice > 0.0)
      {
         result = ((trans[0] * x + trans[1] * y + trans[4]) * 2.0 - 1.0);
      }
      result = ((trans[2] * x + trans[3] * y + trans[5]) * 2.0 - 1.0);

      theStack.push(result);

   }

   /** Returns the operator's name. */
   public String getName()
   {
      return "ifs";
   }

   /** Performs any initialization the operator requires. */
   public void init()
   {
   }

   /** Returns the number of parameters expected by the operator. */
   public int getNumberOfScalarParameters()
   {
      return 9;
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#getNumberOfTripletParameters()
    */
   public int getNumberOfTripletParameters()
   {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#returnsTriplet()
    */
   public boolean returnsTriplet()
   {
      return false;
   }
}
