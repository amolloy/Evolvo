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

import org.maloi.evolvo.expressiontree.mutator.mutators.*;
import org.maloi.evolvo.expressiontree.mutator.mutators.MutatorInterface;

/**
 * @author Andy
 */
public class MutatorList
{
   static MutatorInterface list[] = new MutatorInterface[8];
   
   static
   {
      list[0] = new NewExpression();
      list[1] = new ScalarChangeValue();
      list[2] = new ChangeToVariable();
      list[3] = new ChangeToValue();
      list[4] = new ChangeFunction();
      list[5] = new NewArgument();
      list[6] = new BecomeArgument();
      list[7] = new ArgumentToChildArgument();
   }
   
   public static MutatorInterface[] getMutatorList()
   {
      return list;     
   }
}
