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

package org.maloi.evolvo.expressiontree.utilities;

import java.util.Random;

import org.maloi.evolvo.expressiontree.ExpressionTree;
import org.maloi.evolvo.expressiontree.Value;
import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.operators.OperatorList;
import org.maloi.evolvo.settings.GlobalSettings;

public class ExpressionTreeMutator
{
   static GlobalSettings settings = GlobalSettings.getInstance();
   static VariablePackage variables = VariablePackage.getInstance();
   static OperatorInterface list[] = OperatorList.getScalarOperators();

   static double scalar_change_value;
   static double to_variable;
   static double to_scalar;
   static double new_expression;
   static double change_function;
   static double new_expression_arg;
   static double become_arg;
   static double arg_to_child_arg;
   static double change;
   static double totalProbs;

   /** No one should be able to construct a mutator */
   private ExpressionTreeMutator()
   {
   }

   static {
      new_expression = settings.getDoubleProperty("mutate.new_expression");
      scalar_change_value =
         settings.getDoubleProperty("mutate.scalar_change_value");
      to_variable = settings.getDoubleProperty("mutate.to_variable");
      to_scalar = settings.getDoubleProperty("mutate.to_scalar");
      change_function = settings.getDoubleProperty("mutate.change_function");
      new_expression_arg =
         settings.getDoubleProperty("mutate.new_expression_arg");
      become_arg = settings.getDoubleProperty("mutate.become_arg");
      arg_to_child_arg = settings.getDoubleProperty("mutate.arg_to_child_arg");
      change = settings.getDoubleProperty("mutate.change");

      totalProbs =
         scalar_change_value
            + to_variable
            + to_scalar
            + new_expression
            + change_function
            + new_expression_arg
            + become_arg
            + arg_to_child_arg;
   }

   public static ExpressionTree mutate(Random r, ExpressionTree current)
   {
      return mutate(0.0, r, current);
   }

   public static ExpressionTree mutate(
      double level,
      Random r,
      ExpressionTree current)
   {
      if (r.nextDouble() < change)
      {
         double whatMutation = r.nextDouble() * totalProbs;
         double runningTotal = 0.0;

         if (whatMutation < (runningTotal = new_expression))
         {
            current =
               ExpressionTreeGenerator.generate(
                  level,
                  new Random(r.nextLong()), false);
         }
         else if (whatMutation < (runningTotal += scalar_change_value))
         {
            if (current instanceof Value)
            {
               double dummy = r.nextDouble() * 2.0 - 1.0;
               dummy += ((Value) current).getValue();
               dummy /= 2.0;
               ((Value) current).setValue(dummy);
            }
         }
         else if (whatMutation < (runningTotal += to_variable))
         {
            ExpressionTree newVar = null;
            boolean flag = false;
            while (!flag)
            {
               double variabletype = r.nextDouble();
               double chance = r.nextDouble();

               if (variabletype < 0.25)
               {
                  if (chance < settings.getDoubleProperty("variable.x"))
                  {
                     flag = true;
                     newVar = variables.getVariable("x");
                  }
               }
               else if (variabletype < 0.5)
               {
                  if (chance < settings.getDoubleProperty("variable.y"))
                  {
                     flag = true;
                     newVar = variables.getVariable("y");
                  }
               }
               else if (variabletype < 0.75)
               {
                  if (chance < settings.getDoubleProperty("variable.r"))
                  {
                     flag = true;
                     newVar = variables.getVariable("r");
                  }
               }
               else
               {
                  if (chance < settings.getDoubleProperty("variable.theta"))
                  {
                     flag = true;
                     newVar = variables.getVariable("theta");
                  }
               }
            }
            current = newVar;
         }
         else if (whatMutation < (runningTotal += to_scalar))
         {
            current = new Value(r.nextDouble() * 2.0 - 1.0);
         }
         else if (whatMutation < (runningTotal += change_function))
         {
            ExpressionTree oldParams[] = current.getParams();
            ExpressionTree newParams[];
            current.setOperator(Tools.pickRandomOp(r, false));

            newParams = new ExpressionTree[current.getNumberOfScalarParams()];

            int oldNumParams = (oldParams == null) ? 0 : oldParams.length;

            for (int i = 0; i < newParams.length; i++)
            {
               if ((oldParams != null) && (i < oldParams.length))
               {
                  newParams[i] = oldParams[i];
               }
               else
               {
                  newParams[i] =
                     ExpressionTreeGenerator.generate(
                        level,
                        new Random(r.nextLong()), false);
               }
            }

            current.setParams(newParams);
         }
         else if (whatMutation < (runningTotal += new_expression_arg))
         {
            ExpressionTree params[] = current.getParams();
            if ((params != null) && (params.length > 0))
            {
               int which = (int) (r.nextDouble() * params.length);
               params[which] =
                  ExpressionTreeGenerator.generate(
                     level,
                     new Random(r.nextLong()), false);
               current.setParams(params);
            }
         }
         else if (whatMutation < (runningTotal += become_arg))
         {
            ExpressionTree params[] = current.getParams();
            if ((params != null) && (params.length > 0))
            {
               int which = (int) (r.nextDouble() * params.length);
               current = params[which];
            }
         }
         else if (whatMutation < (runningTotal += arg_to_child_arg))
         {
            if (current.getNumberOfScalarParams() != 0)
            {
               ExpressionTree params[] = current.getParams();
               if ((params != null) && (params.length > 0))
               {
                  int which = (int) (r.nextDouble() * params.length);
                  ExpressionTree childParams[] = params[which].getParams();
                  if (childParams != null)
                  {
                     int whichTwo = (int) (r.nextDouble() * childParams.length);
                     int whichThree = (int) (r.nextDouble() * params.length);
                     params[whichThree] = childParams[whichTwo].getClone();
                  }
                  current.setParams(params);
               }
            }
         }
      }

      ExpressionTree params[] = current.getParams();

      for (int i = 0; i < current.getNumberOfScalarParams(); i++)
      {
         params[i] = mutate(level + 1.0, r, params[i]);
      }

      current.setParams(params);

      return current;
   }
}
