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

import org.asm.evolvo.expressiontree.vm.Stack;

public class Beta implements OperatorInterface, Serializable
{
   /** Perform the operation. */
   public void perform(Stack theStack)
   {
      double a = Math.abs(theStack.pop());
      double b = Math.abs(theStack.pop());

      theStack.push((Math.sqrt(a) * Math.sqrt(b)) / Math.sqrt(a + b));
   }

   /** Returns the operator's name. */
   public String getName()
   {
      return "beta";
   }

   /** Performs any initialization the operator requires. */
   public void init()
   {
   }

   /** Returns the number of parameters expected by the operator. */
   public int getNumberOfParameters()
   {
      return 2;
   }
}
