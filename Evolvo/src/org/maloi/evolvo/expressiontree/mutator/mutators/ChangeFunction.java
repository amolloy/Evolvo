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
import org.maloi.evolvo.expressiontree.utilities.ExpressionTreeGenerator;
import org.maloi.evolvo.expressiontree.utilities.Tools;

/**
 * @author Andy
 */
public class ChangeFunction implements MutatorInterface
{

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.mutator.mutators.MutatorInterface#doMutation(org.maloi.evolvo.expressiontree.ExpressionTree, double, java.util.Random)
    */
   public ExpressionTree doMutation(ExpressionTree old, double level, Random r)
   {
      ExpressionTree oldParams[] = old.getParams();
      ExpressionTree newParams[];
      old.setOperator(
         Tools.pickRandomOp(r, old.getOperator().returnsTriplet()));

      newParams =
         new ExpressionTree[old.getNumberOfScalarParams()
            + old.getNumberOfTripletParams()];

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
                  new Random(r.nextLong()),
                  false);
         }
      }

      old.setParams(newParams);
      
      return old;
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.mutator.mutators.MutatorInterface#getName()
    */
   public String getName()
   {
      return "change_function";
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.mutator.mutators.MutatorInterface#getDisplayName()
    */
   public String getDisplayName()
   {
      return "Change Function";
   }

}
