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

package org.maloi.evolvo.expressiontree.mutator.mutators;

import java.util.Random;

import org.maloi.evolvo.expressiontree.ExpressionTree;

/**
 * @author Andy
 */
public class ArgumentToChildArgument implements MutatorInterface
{

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.mutator.mutators.MutatorInterface#doMutation(org.maloi.evolvo.expressiontree.ExpressionTree, double, java.util.Random)
    */
   public ExpressionTree doMutation(ExpressionTree old, double level, Random r)
   {
      int numScalarParams = old.getNumberOfScalarParams();
      int numTripletParams = old.getNumberOfTripletParams();

      if ((numScalarParams + numTripletParams) == 0)
      {
         // no parameters - do nothing
         return old;
      }

      int source =
         (int) (r.nextDouble() * (numScalarParams + numTripletParams));
      boolean returnsTriplet = source >= numScalarParams;

      int dest =
         (int) (r.nextDouble()
            * (returnsTriplet ? numTripletParams : numScalarParams));

      if (returnsTriplet)
      {
         dest += numScalarParams;
      }

      ExpressionTree child = old.getParams()[source];

      int childNumScalarParams = child.getNumberOfScalarParams();
      int childNumTripletParams = child.getNumberOfTripletParams();

      if ((returnsTriplet && (childNumTripletParams == 0))
         || (!returnsTriplet && (childNumScalarParams == 0)))
      {
         // the child has no arguments that can satisfy the argument of the parent
         // we wish to replace, so do nothing
         return old;
      }

      int childSource =
         (int) (r.nextDouble()
            * (returnsTriplet ? childNumTripletParams : childNumScalarParams));

      if (returnsTriplet)
      {
         childSource += childNumScalarParams;
      }

      old.getParams()[dest] = child.getParams()[childSource].getClone();

      return old;
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.mutator.mutators.MutatorInterface#getName()
    */
   public String getName()
   {
      return "argument_to_child_argument"; //$NON-NLS-1$
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.mutator.mutators.MutatorInterface#getDisplayName()
    */
   public String getDisplayName()
   {
      return "Argument To Child Argument"; //$NON-NLS-1$
   }

}
