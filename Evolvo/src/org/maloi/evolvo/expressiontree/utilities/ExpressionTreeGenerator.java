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
import org.maloi.evolvo.expressiontree.operators.pseudo.SimpleTriplet;
import org.maloi.evolvo.settings.GlobalSettings;

/**
 * Creates a randomly generated symbolic expression and places it in an
 * expressionTree.
 *
 * The generation process is influenced by several parameters contain in a 
 * globalSettings object.  The most significant are the properties 
 * "complexity," "depreciation," and "variable.probability."  The complexity 
 * value determines how likely the node being produced is to be a terminal 
 * node, where the lower complexity's value, the more likely it is to be 
 * terminal.  Depreciation is a value that is subtracted from complexity at 
 * each level of the tree, to ensure the generation process will end within 
 * a reasonable number of tree levels.  The variable.probability determines 
 * how likely a terminal node is to be a value or a variable, where higher 
 * values mean variables are more likely to occur than values.
 *
 * All three of these settings should fall in the range of 0.0 to 1.0.  
 * Depreciation, however, should never actually be 0.0.  
 *
 */
public class ExpressionTreeGenerator
{
   /** The variables the generated expression may use. */
   static VariablePackage variables = VariablePackage.getInstance();
   static GlobalSettings settings = GlobalSettings.getInstance();
   /** A list of the available operators. */
   static OperatorInterface list[] = OperatorList.getScalarOperators();
   static SimpleTriplet simpleTriplet = new SimpleTriplet();

   /** 
    *  Just prevent anyone from constructing us
    */
   private ExpressionTreeGenerator()
   {
   }

   /** Returns a generated expressionTree based on the given Random object. */
   public static ExpressionTree generate(
      Random randomNumber,
      boolean returnsTriplet)
   {
      return generate(0.0, randomNumber, returnsTriplet);
   }

   /** Returns a generated expressionTree based on the given Random object, 
    *  starting at the level<I>th</I> level of the tree. 
    */
   public static ExpressionTree generate(
      double level,
      Random randomNumber,
      boolean returnsTriplet)
   {
      // Start by making a new expressionTree
      ExpressionTree root = new ExpressionTree(null ,null);

      double c = settings.getDoubleProperty("complexity");
      double d = settings.getDoubleProperty("depreciation");
      double v = settings.getDoubleProperty("variable.probability");

      // Since the process is recursive, and complexity (c) is not passed as 
      // a parameter, we subtract the depreciation (d) value from it, 
      // multiplied by the current level.  c may drop below zero at this 
      // point, but it is used only in a comparison, so that's okay.
      c -= d * level;

      // Decide if we should make this a terminal node
      if (randomNumber.nextDouble() > c)
      {
         if (returnsTriplet)
         {
            // We need a triplet for our terminator
            // Triplets can't really terminate an ExpressionTree
            // so we create a SimpleTriplet and populate it.
            // We'll actually just call this function recursively
            // for the three values.  Potentially that could mean
            // this will not actually be quite like a terminating
            // node.  That's okay.  This may make things more 
            // interesting.  Or maybe not.

            ExpressionTree params[] = new ExpressionTree[3];

            for (int count = 0; count < 3; count++)
            {
               params[count] = generate(level + 1.0, randomNumber, false);
            }

            root = new ExpressionTree(params, simpleTriplet);
         }
         else
         {
            // scalar constant or variable

            // Okay we want a terminal node, so decide if it should be a
            // value or variable
            if (randomNumber.nextDouble() > v)
            {
               // it's a value
               root = new Value(randomNumber.nextDouble());
            }
            else
            {
               boolean flag = false;

               while (!flag)
               {
                  // We need to decide which variable to use for this node.  
                  // Since each variable has its own chance of occuring, we need 
                  // to keep picking one and deciding based on a random number 
                  // and the variables likelyhood to occur until the program 
                  // decides to keep one.  NOTE: This could result in an 
                  // infinite loop if all variables have a probability of 0.0.  
                  // Currently, the settings package makes an attempt to prevent 
                  // this situation, though really I think a new algorithm is
                  // probably needed.

                  double variabletype = randomNumber.nextDouble();
                  double chance = randomNumber.nextDouble();
                  // This is really very messy and should be rewritten soon
                  if (variabletype < 0.25)
                  {
                     if (chance < settings.getDoubleProperty("variable.x"))
                     {
                        flag = true;
                        root = variables.getVariable("x");
                     }
                  }
                  else if (variabletype < 0.5)
                  {
                     if (chance < settings.getDoubleProperty("variable.y"))
                     {
                        flag = true;
                        root = variables.getVariable("y");
                     }

                  }
                  else if (variabletype < 0.75)
                  {
                     if (chance < settings.getDoubleProperty("variable.r"))
                     {
                        flag = true;
                        root = variables.getVariable("r");
                     }

                  }
                  else
                  {
                     if (chance < settings.getDoubleProperty("variable.theta"))
                     {
                        flag = true;
                        root = variables.getVariable("theta");
                     }
                  }
                  // messy
               }
            }
         }
      }
      else
      {
         // Okay, we decided to not make this a terminal node, so it's an 
         // operation

         root = new ExpressionTree(null, null); // make a new expressionTree
         root.setOperator(Tools.pickRandomOp(randomNumber, returnsTriplet));

         // now that we have an operator, we need to define some parameters 
         // for it

         // start with scalar parameters
         int count;
         int scalarParamCount = root.getNumberOfScalarParams();
         int tripletParamCount = root.getNumberOfTripletParams();
         int totalParamCount = scalarParamCount + tripletParamCount;

         ExpressionTree params[] = new ExpressionTree[totalParamCount];

         // first scalar parameters
         if (scalarParamCount != 0)
         {
            for (count = 0; count < scalarParamCount; count++)
            {
               params[count] = generate(level + 1.0, randomNumber, false);
            }
         }

         // then triplet parameters
         if (tripletParamCount != 0)
         {
            for (count = 0; count < tripletParamCount; count++)
            {
               params[count + scalarParamCount] =
                  generate(level + 1.0, randomNumber, true);
            }
         }
         
         root.setParams(params); // and then set the parameters
      }

      return root;
   }
}
