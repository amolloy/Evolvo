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

package org.asm.evolvo.expressiontree.utilities;

import java.util.Random;

import org.asm.evolvo.expressiontree.ExpressionTree;
import org.asm.evolvo.expressiontree.operators.OperatorInterface;
import org.asm.evolvo.expressiontree.operators.OperatorList;
import org.asm.evolvo.settings.GlobalSettings;

public class Tools
{
   static OperatorInterface[] list = OperatorList.getOperators();
   static GlobalSettings settings = GlobalSettings.getInstance();

   /** No one should construct a tools */
   private Tools()
   {
   }

   /** Returns a randomly chosen operatorInterface object. */
   static OperatorInterface pickRandomOp(Random whichOp)
   {
      boolean flag = false;
      OperatorInterface pick = list[0]; // keep the compiler happy
      double chance;

      // I don't really like this algorithm.
      // Probably should come up with something else in the near future.
      while (!flag)
      {
         pick = list[(int) (whichOp.nextDouble() * list.length)];
         chance = whichOp.nextDouble();
         if (chance < settings.getDoubleProperty(pick.getName()))
         {
            flag = true;
         }
      }

      return pick;
   }

   public static String toString(ExpressionTree[] expressions)
   {
      StringBuffer displayString = new StringBuffer();
      int i;

      String channelNames[] = new String[] { "Hue", "Saturation", "Value" };

      for (i = 0; i < 3; i++)
      {
         displayString.append("// " + channelNames[i] + "\n");
         displayString.append(expressions[i].toString());
         displayString.append("\n\n");
      }

      return displayString.toString();
   }
}
