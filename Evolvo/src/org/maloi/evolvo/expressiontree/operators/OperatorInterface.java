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

import org.maloi.evolvo.expressiontree.vm.Stack;

/**
 * Defines the interface which all operators must implement
 */
public interface OperatorInterface
{
   /** Returns the name of the operator. */
   public String getName();
   /** Performs any initialization the operator may require. */
   public void init();
   /** Returns the number of parameters the operator expects. */
   public int getNumberOfParameters();
   /** Executes this operator on values in the given stack.
    *  Pushes its result back onto the stack.
    */
   public void perform(Stack theStack);
}
