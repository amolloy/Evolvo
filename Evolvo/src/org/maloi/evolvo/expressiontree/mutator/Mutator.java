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

package org.maloi.evolvo.expressiontree.mutator;

import java.util.Random;

import org.maloi.evolvo.expressiontree.ExpressionTree;
import org.maloi.evolvo.expressiontree.mutator.mutators.MutatorInterface;
import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.operators.OperatorList;
import org.maloi.evolvo.expressiontree.operators.pseudo.SimpleTriplet;
import org.maloi.evolvo.expressiontree.utilities.VariablePackage;
import org.maloi.evolvo.settings.GlobalSettings;

public class Mutator
{
   static GlobalSettings settings = GlobalSettings.getInstance();
   static VariablePackage variables = VariablePackage.getInstance();
   static OperatorInterface list[] = OperatorList.getScalarOperators();
   static MutatorInterface mutators[] = MutatorList.getMutatorList();

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
   private Mutator()
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
      // we only perform the mutation if:
      // a. The current node is NOT a SimpleTriplet
      // b. We pick a random number less than the change probability
      
      if (!(current.getOperator() instanceof SimpleTriplet)
         && (r.nextDouble() < change))
      {
         double whatMutation = r.nextDouble() * totalProbs;
         double runningTotal = 0.0;

         if (whatMutation < (runningTotal = new_expression))
         {
            current = mutators[0].doMutation(current.getClone(), level, r);
         }
         else if (whatMutation < (runningTotal += scalar_change_value))
         {
            current = mutators[1].doMutation(current.getClone(), level, r);
         }
         else if (whatMutation < (runningTotal += to_variable))
         {
            current = mutators[2].doMutation(current.getClone(), level, r);
         }
         else if (whatMutation < (runningTotal += to_scalar))
         {
            current = mutators[3].doMutation(current.getClone(), level, r);
         }
         else if (whatMutation < (runningTotal += change_function))
         {
            current = mutators[4].doMutation(current.getClone(), level, r);
         }
         else if (whatMutation < (runningTotal += new_expression_arg))
         {
            current = mutators[5].doMutation(current.getClone(), level, r);
         }
         else if (whatMutation < (runningTotal += become_arg))
         {
            current = mutators[6].doMutation(current.getClone(), level, r);
         }
         else if (whatMutation < (runningTotal += arg_to_child_arg))
         {
            current = mutators[7].doMutation(current.getClone(), level, r);
         }
      }

      ExpressionTree params[] = current.getParams();

      for (int i = 0;
         i
            < (current.getNumberOfScalarParams()
               + current.getNumberOfTripletParams());
         i++)
      {
         params[i] = mutate(level + 1.0, r, params[i]);
      }

      current.setParams(params);

      return current;
   }
}
