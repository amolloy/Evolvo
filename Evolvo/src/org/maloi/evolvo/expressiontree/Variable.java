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
import org.maloi.evolvo.gui.SystemConsole;

/**
 * Terminal node for expressionTree that stores a variable.
 */
public class Variable extends ExpressionTree implements Cloneable, Serializable
{
   /** The variable's value. */
   Value v;
   /** The variable's name. */
   String name;

   int reg;
   
   SystemConsole console = SystemConsole.getInstance();

   /** Class constructor, sets the value (from a double) and name for this 
    *  variable. 
    */
   public Variable(double p, String n)
   {
      v = new Value(p);
      setName(n);
   }

   /** Class constructor, sets the value (from an value object) and name for
    *  this variable. 
    */
   public Variable(Value p, String n)
   {
      v = p;
      setName(n);
   }

   public Variable()
   {
      v = new Value(0.0);
      setName("");
   }

   public ExpressionTree getClone()
   {
      // We don't actually need to deep clone variable's, so just return this
      return this;
   }

   /** Returns the variable's name as a String. */
   public String toString()
   {
      return name;
   }

   /** Sets the variables value from a double. */
   public void setVariable(double p)
   {
      v.setValue(p);
   }

   /** Returns the number of parameters this node expects. */
   public int getNumberOfScalarParams()
   {
      return 0;
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

      buildMachine(myMachine);

      return myMachine;
   }

   public void buildMachine(Machine myMachine)
   {
      Instruction inst = new Instruction();

      inst.type = Instruction.TYPE_REGISTER;
      inst.reg = reg;

      myMachine.addInstruction(inst);
   }

   private void setName(String n)
   {
      name = n;

      if (n.equals("x"))
      {
         reg = Machine.REGISTER_X;
      }
      else if (n.equals("y"))
      {
         reg = Machine.REGISTER_Y;
      }
      else if (n.equals("r"))
      {
         reg = Machine.REGISTER_R;
      }
      else if (n.equals("theta"))
      {
         reg = Machine.REGISTER_THETA;
      }
      else
      {
         console.println("Unimplemented variable: " + name);
         reg = 0;
      }
   }
}
