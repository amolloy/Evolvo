/*
 * Evolvo - Image Generator Copyright (C) 2004 Andrew Molloy
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
/**
 * $Id$
 */
/**
 * Renders a portion of an evolvo image space, sending the results to a buffered
 * output stream
 */
package org.maloi.evolvo.expressiontree.renderer;
import java.io.BufferedWriter;
import java.io.IOException;

import org.maloi.evolvo.expressiontree.ExpressionTree;
import org.maloi.evolvo.expressiontree.vm.Machine;
/**
 * @author amolloy
 * 
 */
public class StreamRenderer
{
   BufferedWriter out;
   double         x1, y1, x2, y2;
   int            w, h;
   ExpressionTree et;
   double rw, rh;
   
   public StreamRenderer(ExpressionTree et, BufferedWriter out, double x1, double y1, double x2,
         double y2, int w, int h)
   {
      this.et = et;
      this.out = out;
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y1 = y2;
      this.w = w;
      this.h = h;
      
      rw = x2 - x1;
      rh = y2 - y1;
   }
   
   /**
    * start rendering and sending the results to the BufferedWriter
    *
    */
   public void start()
   {
      int x, y;
      double tx, ty;
      
      Machine machine = et.getMachine();
      
      for (y = 0; y < h; y++)
      {
         System.err.println(y);
         ty = ((double) y / (double)h) * rh + y1;

         machine.setRegister(Machine.REGISTER_Y, ty);

         for (x = 0; x < w; x++)
         {
            tx = ((double) x / (double)w) * rw + x1;

            machine.setRegister(Machine.REGISTER_X, tx);
            machine.setRegister(
               Machine.REGISTER_R,
               Math.sqrt((tx * tx) + (ty * ty)));
            machine.setRegister(Machine.REGISTER_THETA, Math.atan2(tx, ty));

            try
            {
               out.write(machine.executeToPixel());
            }
            catch (IOException ioe)
            {
               System.err.println("IO Exception:");
               ioe.printStackTrace(System.err);
               System.exit(5);
            }
         }
      }
   }
}