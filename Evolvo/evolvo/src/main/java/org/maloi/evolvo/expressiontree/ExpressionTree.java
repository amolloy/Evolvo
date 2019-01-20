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

import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.operators.pseudo.SimpleTriplet;
import org.maloi.evolvo.expressiontree.utilities.VariablePackage;
import org.maloi.evolvo.expressiontree.vm.Instruction;
import org.maloi.evolvo.expressiontree.vm.Machine;
import org.maloi.evolvo.expressiontree.vm.Stack;

/**
 * Handles mathematic expressions in a tree structure, including
 * storage, evaluation, and display.  Will eventually provide the
 * functionality to manipulate the tree as well.
 */
public class ExpressionTree implements Serializable
{
   private static final long serialVersionUID = 298512536044969933L;
   /** Holds parameters to this node's operator. */
   ExpressionTree[] params;
   /** Holds the operator for this node of the tree. */
   OperatorInterface operator;

   static VariablePackage vp = VariablePackage.getInstance();

   public ExpressionTree()
   {
      params = null;
      operator = null;
   }

   /** Creates a new expressionTree with operator op and parameters prms[] */
   public ExpressionTree(ExpressionTree[] prms, OperatorInterface op)
   {
      params = prms;
      operator = op;
   }

   /** Convenience method to retrieve a clone of expressionTree as type 
    *  expressionTree. 
    */
   public ExpressionTree getClone()
   {
      int counter;

      int numParams =
         operator.getNumberOfScalarParameters()
            + operator.getNumberOfTripletParameters();

      // create a new array of expressionTrees
      ExpressionTree[] newParams = new ExpressionTree[numParams];

      for (counter = 0; counter < numParams; counter++)
      {
         // and fill it with clones of our current parameters.
         newParams[counter] = params[counter].getClone();
      }

      ExpressionTree newET = new ExpressionTree(newParams, operator);

      return newET;
   }

   /** Returns a (sort of) human readable string representation of the 
    *  expression.
    *  The resulting string contains the expression written in a lisp-like 
    *  notation.  For example, the simple equation "1+1" would be written as 
    * "(+ 1 1)".  
    */
   public String toString()
   {
      int count;
      int len = operator.getNumberOfScalarParameters();
      StringBuffer theString = new StringBuffer();

      if (operator instanceof SimpleTriplet)
      {
         theString.append("< "); //$NON-NLS-1$

         theString.append(params[0].toString()).append(", "); //$NON-NLS-1$
         theString.append(params[1].toString()).append(", "); //$NON-NLS-1$
         theString.append(params[2].toString());

         theString.append(">"); //$NON-NLS-1$
      }
      else
      {
         theString.append("(" + operator.getName() + " "); //$NON-NLS-1$ //$NON-NLS-2$

         for (count = 0; count < len; count++)
         {
            theString.append(params[count].toString());

            if (count < (len - 1))
            {
               theString.append(" "); //$NON-NLS-1$
            }
         }

         len = operator.getNumberOfTripletParameters();

         if (len > 0)
         {
            theString.append(" "); //$NON-NLS-1$

            int base = count;
            int endCount = len + base;

            for (count = base; count < endCount; count++)
            {
               theString.append(params[count].toString());

               if (count < endCount)
               {
                  theString.append(" "); //$NON-NLS-1$
               }
            }
         }

         theString.append(")"); //$NON-NLS-1$
      }

      return theString.toString();
   }

   /** Returns the name of this node's operator. */
   public String getName()
   {
      return (operator.getName());
   }

   /** Set this node's parameters. 
     * <B>Note:</B> Currently does not check that p[] has the correct number of
     * elements.  If p[] has too many elements, the extras are simply thrown 
     * out.
     * If, however, there are too few, the results are unpredicatable.
     *
     * This behavior should be modified in future versions to be more robust.
     */
   public void setParams(ExpressionTree p[])
   {
      int count;
      int len =
         operator.getNumberOfScalarParameters()
            + operator.getNumberOfTripletParameters();

      params = new ExpressionTree[len];

      for (count = 0; count < len; count++)
      {
         params[count] = p[count].getClone();
      }
   }

   /** Returns the number of scalar parameters this node expects. */
   public int getNumberOfScalarParams()
   {
      return operator.getNumberOfScalarParameters();
   }

   /** Returns the number of triplet parameters this node expects. */
   public int getNumberOfTripletParams()
   {
      return operator.getNumberOfTripletParameters();
   }

   /** Returns true if this node's operator returns a triplet */
   public boolean returnsTriplet()
   {
      return operator.returnsTriplet();
   }

   /** Returns the parameters this node is holding. */
   public ExpressionTree[] getParams()
   {
      return params;
   }

   /** Returns the operator this node holds. */
   public OperatorInterface getOperator()
   {
      return operator;
   }

   /** Sets this node's operator.
     * <B>Note:</B> If the new operator expects more parameters than
     * the current operator, results are unpredicatable.
     *
     * This behaviour should be modified in future version to be more robust. */
   public void setOperator(OperatorInterface o)
   {
      operator = o;
   }

   /** Builds a simple stack machine to evaluate this expression.
    *  Simply creates a new machine and passes it on to 
    *  the compile(machine) method.
    */
   public Machine getMachine()
   {
      Machine myMachine = new Machine();

      compile(myMachine);

      return myMachine;
   }

   /**
    * Determines whether this branch of an expression tree can be
    * "trimmed." Any branch with variables at the leaves cannot be
    * trimmed. Any branch that has no variables in any of its leaves
    * can be trimmed.
    * 
    * @return true if the branch is trimmable
    */
   protected boolean isTrimmable()
   {
      int numScalarParams = operator.getNumberOfScalarParameters();
      int numTripletParams = operator.getNumberOfTripletParameters();

      for (int i = 0; i < numScalarParams + numTripletParams; i++)
      {
         if (!params[i].isTrimmable())
         {
            return false;
         }
      }

      return true;
   }

   /**
    * Compiles an expression tree to byte code for the Evolvo virtual machine.
    * Performs very mild optimizations (more to come possibly in the future).
    * 
    * @param myMachine
    */
   protected void compile(Machine myMachine)
   {
      int nSP = operator.getNumberOfScalarParameters();
      int nTP = operator.getNumberOfTripletParameters();

      for (int i = 0; i < nSP; i++)
      {
         if (!params[i].isTrimmable()
            || (params[i] instanceof org.maloi.evolvo.expressiontree.Value))
         {
            params[i].compile(myMachine);
         }
         else
         {
            // go ahead and execute this branch of the tree,
            // then just stick the result in the stack
            Machine tmpMachine = new Machine();
            Stack tmpStack;

            params[i].compile(tmpMachine);
            tmpStack = tmpMachine.execute();
            double d = tmpStack.pop();

            new Value(d).compile(myMachine);
         }
      }
      for (int i = 0; i < nTP; i++)
      {
         if (!params[i + nSP].isTrimmable()
            || (params[i + nSP] instanceof org.maloi.evolvo.expressiontree.Value))
         {
            params[i + nSP].compile(myMachine);
         }
         else
         {
            // go ahead and execute this branch of the tree,
            // then just stick the resulting triplet onto the
            // stack

            Machine tmpMachine = new Machine();
            Stack tmpStack;
            double[] vs;

            params[i].compile(tmpMachine);
            tmpStack = tmpMachine.execute();
            vs = tmpStack.popTriplet();

            for (int vcount = 0; vcount < 3; vcount++)
            {
               new Value(vs[vcount]).compile(myMachine);
            }
         }
      }

      // SimpleTriplet's perform function is a noop, so just leave it out
      // altogether
      if (!(operator instanceof SimpleTriplet))
      {
         Instruction inst = new Instruction();

         inst.type = Instruction.TYPE_OPERATOR;
         inst.op = operator;

         myMachine.addInstruction(inst);
      }
   }

   /**
    * Legacy version of the compile code.
    * @param myMachine
    */
   public void oldBuildMachine(Machine myMachine)
   {
      int numScalarParams = operator.getNumberOfScalarParameters();
      int numTripletParams = operator.getNumberOfTripletParameters();

      for (int i = 0; i < numScalarParams; i++)
      {
         params[i].compile(myMachine);
      }
      for (int i = 0; i < numTripletParams; i++)
      {
         params[i + numScalarParams].compile(myMachine);
      }

      // SimpleTriplet's perform function is a noop, so just leave it out
      // altogether
      if (!(operator instanceof SimpleTriplet))
      {
         Instruction inst = new Instruction();

         inst.type = Instruction.TYPE_OPERATOR;
         inst.op = operator;

         myMachine.addInstruction(inst);
      }
   }
}
