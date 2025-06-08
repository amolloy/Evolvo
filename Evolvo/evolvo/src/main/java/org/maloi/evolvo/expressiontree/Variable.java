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


package org.maloi.evolvo.expressiontree;

import org.maloi.evolvo.expressiontree.vm.Instruction;
import org.maloi.evolvo.expressiontree.vm.Machine;
import org.maloi.evolvo.gui.SystemConsole;

/**
 * Terminal node for expressionTree that stores a variable.
 */
public class Variable extends ExpressionTree implements Cloneable
{
   private static final long serialVersionUID = -2682627840160900321L;
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
      setName(""); //$NON-NLS-1$
   }

   @Override
   public ExpressionTree getClone()
   {
      // We don't actually need to deep clone variable's, so just return this
      return this;
   }

   /** Returns the variable's name as a String. */
   @Override
   public String toString()
   {
      return name;
   }

   /** Sets the variables value from a double. */
   public void setVariable(double p)
   {
      v.setValue(p);
   }

   /** Returns the number of scalar parameters this node expects. */
   @Override
   public int getNumberOfScalarParams()
   {
      return 0;
   }

   /** Returns the number of triplet parameters this node expects. */
   @Override
   public int getNumberOfTripletParams()
   {
      return 0;
   }

   /** Returns true if this node's operator returns a triplet.  Always false. */
   @Override
   public boolean returnsTriplet()
   {
      return false;
   }

   /** Returns the parameters this node is holding. */
   @Override
   public ExpressionTree[] getParams()
   {
      return null;
   }

   @Override
   public void setParams(ExpressionTree[] dummy)
   {
   }

   /** Builds a simple stack machine to evaluate this expression.
    *  Simply creates a new machine and passes it on to 
    *  the buildMachine(machine) method.
    */
   @Override
   public Machine getMachine()
   {
      Machine myMachine = new Machine();

      compile(myMachine);

      return myMachine;
   }

   @Override
   protected void compile(Machine myMachine)
   {
      Instruction inst = new Instruction();

      inst.type = Instruction.TYPE_REGISTER;
      inst.reg = reg;

      myMachine.addInstruction(inst);
   }

   private void setName(String n)
   {
      name = n;

       switch (n) {
       //$NON-NLS-1$
           case "x":
               reg = Machine.REGISTER_X;
               break;
       //$NON-NLS-1$
           case "y":
               reg = Machine.REGISTER_Y;
               break;
       //$NON-NLS-1$
           case "r":
               reg = Machine.REGISTER_R;
               break;
       //$NON-NLS-1$
           case "theta":
               reg = Machine.REGISTER_THETA;
               break;
           default:
               console.println("Unimplemented variable: " + name); //$NON-NLS-1$
               reg = 0;
               break;
       }
   }
}
