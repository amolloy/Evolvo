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

package org.maloi.evolvo.expressiontree;

import java.io.Serializable;

import org.maloi.evolvo.expressiontree.vm.Instruction;
import org.maloi.evolvo.expressiontree.vm.Machine;

/**
 * Terminal node for an expressionTree that stores a double value.
 */
public class Value extends ExpressionTree implements Serializable
{
   /** The value being stored goes into cachedValue. */
   
   private double v;

	/** Class constructor that sets the node's value. */
   public Value(double p)
   {
      v = p;
   }

   public double getValue()
   {
      return v;
   }

   public ExpressionTree getClone()
   {
      return (ExpressionTree) (new Value(v));
   }

   /** Returns the value's value as a string. */
   public String toString()
   {
      return new Double(v).toString();
   }

   /** Sets the value's value. */
   public void setValue(double p)
   {
      v = p;
   }

   /** Returns the number of scalar parameters this node expects. */
   public int getNumberOfScalarParams()
   {
      return 0;
   }

   /** Returns the number of triplet parameters this node expects. */
   public int getNumberOfTripletParams()
   {
      return 0;
   }

   /** Returns true if this node's operator returns a triplet.  Always false. */
   public boolean returnsTriplet()
   {
      return false;
   }

   /** Returns the parameters this node is holding. */
   public ExpressionTree[] getParams()
   {
      return null;
   }

   public void setParams(ExpressionTree[] dummy)
   {
   }

   /** Builds a simple stack machine to evaluate this expression.
    *  Simply creates a new machine and passes it on to 
    *  the buildMachine(machine) method.
    */
   public Machine getMachine()
   {
      Machine myMachine = new Machine();

      compile(myMachine);

      return myMachine;
   }

   protected boolean isTrimmable()
   {
	   return true;
   }

   protected void compile(Machine myMachine)
   {
      Instruction inst = new Instruction();

      inst.type = Instruction.TYPE_VALUE;
      inst.value = v;

      myMachine.addInstruction(inst);
   }
}
