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
 * Returns the arctangent of an expressionTree.
 */
public class Arctan implements OperatorInterface, Serializable
{
   /** Perform the operation. */
   public void perform(Stack theStack)
   {
      theStack.push(Math.atan(theStack.pop()));
   }

   /** Returns the operator's name. */
   public String getName()
   {
      return "atan";
   }

   /** Performs any initialization the operator requires. */
   public void init()
   {
   }

   /** Returns the number of parameters expected by the operator. */
   public int getNumberOfScalarParameters()
   {
      return 1;
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
