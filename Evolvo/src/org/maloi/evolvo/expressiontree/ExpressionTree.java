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
import org.maloi.evolvo.expressiontree.utilities.VariablePackage;
import org.maloi.evolvo.expressiontree.vm.Instruction;
import org.maloi.evolvo.expressiontree.vm.Machine;

/**
 * Handles mathematic expressions in a tree structure, including
 * storage, evaluation, and display.  Will eventually provide the
 * functionality to manipulate the tree as well.
 */
public class ExpressionTree implements Serializable
{
   /** Holds parameters to this node's operator. */
   ExpressionTree[] params;
   /** Holds the operator for this node of the tree. */
   OperatorInterface operator;
   /** Holds the cached value for this branch.  */
   double cachedValue;
   boolean cacheable = false;
   /** With the new expression evaluation engine, there's no need
   *  to keep passing a variablePackage around, so we'll just
   *  keep one instance of it here.
   *
   *  TODO: Make variablePackage static
   */

   static VariablePackage vp = VariablePackage.getInstance();

   /**
    *  These are used by the map() static member
    */
   static final double map_height = Math.pow(0.5, 2);
   static final double PI_OVER_TWO = Math.PI / 2.0;

   /** Creates a new expressionTree */
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

      int numParams = operator.getNumberOfParameters();

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
      int len = operator.getNumberOfParameters();
      StringBuffer theString = new StringBuffer();

      theString.append("(" + operator.getName() + " ");

      for (count = 0; count < len; count++)
      {
         theString.append(params[count].toString());

         if (count < (len - 1))
         {
            theString.append(" ");
         }
      }

      theString.append(")");

      return theString.toString();
   }

   /** Returns the name of this node's operator. */
   public String getName()
   {
      return (operator.getName());
   }

   /** Returns the number of parameter's this node's operator expects. */
   public int getParamLength()
   {
      return (operator.getNumberOfParameters());
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
      int len = operator.getNumberOfParameters();

      params = new ExpressionTree[len];

      for (count = 0; count < len; count++)
      {
         params[count] = p[count];
      }
   }

   /** Returns the number of parameters this node expects. */
   public int getNumParams()
   {
      return operator.getNumberOfParameters();
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
      for (int i = 0; i < operator.getNumberOfParameters(); i++)
      {
         params[i].buildMachine(myMachine);
      }

      Instruction inst = new Instruction();

      inst.type = Instruction.TYPE_OPERATOR;
      inst.op = operator;

      myMachine.addInstruction(inst);
   }

   public static double map(double a)
   {
      double a_squared = Math.pow(a, 2);
      double distance = Math.sqrt(map_height + a_squared);
      double angle = Math.asin(a / distance);
      double translated = (PI_OVER_TWO - Math.abs(angle)) / PI_OVER_TWO;

      return translated;
   }
}
