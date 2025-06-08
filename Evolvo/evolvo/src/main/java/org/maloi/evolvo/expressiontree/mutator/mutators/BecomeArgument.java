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


package org.maloi.evolvo.expressiontree.mutator.mutators;

import java.util.Random;

import org.maloi.evolvo.expressiontree.ExpressionTree;

/**
 * @author Andy
 */
public class BecomeArgument implements MutatorInterface
{

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.mutator.mutators.MutatorInterface#doMutation(org.maloi.evolvo.expressiontree.ExpressionTree, double, java.util.Random)
    */
   public ExpressionTree doMutation(ExpressionTree old, double level, Random r)
   {
      ExpressionTree params[] = old.getParams();
      
      int numScalarParams = old.getNumberOfScalarParams();
      int numTripletParams = old.getNumberOfTripletParams();

      boolean returnsTriplet = old.returnsTriplet();

      if ((returnsTriplet && (numTripletParams == 0))
         || (!returnsTriplet && (numScalarParams == 0)))
      {
         // none of our child arguments are of the appropriate type, so just return
         return old;
      }

      int base = returnsTriplet ? numScalarParams : 0;
      int length = returnsTriplet ? numTripletParams : numScalarParams;

      if ((params != null) && (params.length > 0))
      {
         int which = (int) (r.nextDouble() * length);
         old = params[base + which];
      }

      return old;
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.mutator.mutators.MutatorInterface#getName()
    */
   public String getName()
   {
      return "become_argument"; //$NON-NLS-1$
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.mutator.mutators.MutatorInterface#getDisplayName()
    */
   public String getDisplayName()
   {
      return "Become Argument"; //$NON-NLS-1$
   }

}
