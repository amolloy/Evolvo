/* Evolvo - Image Generator
 * Copyright (C) 2004 Andrew Molloy
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
 
/*
 * Created on Mar 9, 2004
 */

package org.maloi.evolvo.expressiontree.operators.triplet;

import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.vm.Stack;

/**
 * @author amolloy
 */
public class RGBtoHSV implements OperatorInterface
{

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#getName()
    */
   public String getName()
   {
      return "RGBtoHSV";
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#init()
    */
   public void init()
   {
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#getNumberOfScalarParameters()
    */
   public int getNumberOfScalarParameters()
   {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#getNumberOfTripletParameters()
    */
   public int getNumberOfTripletParameters()
   {
      return 1;
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#returnsTriplet()
    */
   public boolean returnsTriplet()
   {
      return true;
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#perform(org.maloi.evolvo.expressiontree.vm.Stack)
    */
   public void perform(Stack theStack)
   {
      double r, g, b, h, s, v;
      double min, max, delta;
      
      r = theStack.pop();
      g = theStack.pop();
      b = theStack.pop();
      
      min = Math.min(r, Math.min(g, b));
      max = Math.max(r, Math.max(g, b));
      
      v = max;
      
      delta = max - min;
      
      if ( max != 0 )
      {
         s = delta / max;
      }
      else
      {
         s = 0;
         h = -1;
      
         theStack.push(h);
         theStack.push(s);
         theStack.push(v);
         return;
      }
      
      if (r == max)
      {
         h = (g - b) / delta; // between yellow and magenta
      }
      else if (g == max)
      {
         h = 2 + (b - r) / delta; // between cyan and yellow
      }
      else
      {
         h = 4 + (r - g) / delta; // between magenta and cyan
      }
      
      h = h * 60.0 / 360.0;
      
      theStack.push(h);
      theStack.push(s);
      theStack.push(v);
   }

}
