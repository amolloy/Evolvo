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


package org.maloi.evolvo.expressiontree.operators.triplet;

import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.utilities.Tools;
import org.maloi.evolvo.expressiontree.vm.Stack;

/**
 * @author amolloy
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class HSVtoRGB implements OperatorInterface
{

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#getName()
    */
   public String getName()
   {
      return "HSVtoRGB";
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
   public void perform(Stack theStack, final double registers[])
   {
      int i;
      double f;
      double r, g, b, h, s, v;
      double p, q, t;
      double in[] = theStack.popTriplet();
      double out[] = new double[3];
      
      in = Tools.normalize(in);
      h = in[0] * 180.0 + 180.0; // between 0 and 360
      s = in[1] * 0.5 + 0.5; // 0..1
      v = in[2] * 0.5 + 0.5; // 0..1
      
      if (s == 0.0)
      {
         // gray
      
         out[0] = out[1] = out[2] = v;
         theStack.pushTriplet(out);
         return;
      }
      
      h /= 60.0;
      i = (int)Math.floor(h);
      f = h - i; // factorial part
      p = v * (1.0 - s);
      q = v * (1.0 - s * f);
      t = v * (1.0 - s * (1 - f));
      
      switch (i)
      {
         case 0:
            r = v;
            g = t;
            b = p;
            break;
            
         case 1:
            r = q;
            g = v;
            b = p;
            break;
         
         case 2:
            r = p;
            g = v;
            b = t;
            break;
            
         case 3:
            r = p;
            g = q;
            b = v;
            break;
            
         case 4:
            r = t;
            g = p;
            b = v;
            break;
            
         default:
            r = v;
            g = p;
            b = q;
            break;
      }
            
     
      out[0] = r;
      out[1] = g;
      out[2] = b;
      
      theStack.pushTriplet(out);
   }

}
