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

package org.maloi.evolvo.expressiontree.renderer;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.maloi.evolvo.expressiontree.ExpressionTree;
import org.maloi.evolvo.expressiontree.utilities.Tools;
import org.maloi.evolvo.expressiontree.vm.Machine;
import org.maloi.evolvo.expressiontree.vm.Stack;
import org.maloi.evolvo.gui.CustomProgressMonitor;
import org.maloi.evolvo.gui.SystemConsole;

/**
 * Rendered that breaks the image down to bite-sized tiles, rendering the
 * image tile by tile until finished. Note that these tiles are not related
 * to BufferedImage's tiles.
 */

public class RegionTiledRenderer implements RendererInterface, Runnable
{
   /** 
    * The size of the tiles to break the image into (in pixels)
    */
   final static int tileSize = 128;
   final static int pmIncrement = tileSize * tileSize;

   // We shouldn't ever have more than one consumer, but who knows
   // what happens deep in the depths of AWT & Swing, so we'll start
   // with room for 2.  Same thing for listeners
   Vector consumers = new Vector(2);
   Vector listeners = new Vector(2);

   Machine theMachine;
   ExpressionTree expression;
   int width;
   int height;
   double x1, y1, x2, y2; // the bounds of the region to render
   double rw, rh; // the width & height of the region to render
   boolean finished = false;
   boolean stopFlag = false;
   boolean started = false;
   CustomProgressMonitor pm = null;

   SystemConsole console = SystemConsole.getInstance();

   Thread theThread;

   public RegionTiledRenderer(
      ExpressionTree expression,
      int width,
      int height,
      double x1,
      double y1,
      double x2,
      double y2)
   {
      this.expression = expression;
      this.width = width;
      this.height = height;
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;

      rw = x2 - x1;
      rh = y2 - y1;
      
      theThread = new Thread(this);
   }

   public void addConsumer(ImageConsumer ic)
   {
      if (!isConsumer(ic) && (ic != null))
      {
         consumers.add(ic);
      }
   }

   public boolean isConsumer(ImageConsumer ic)
   {
      return consumers.contains(ic);
   }

   public void removeConsumer(ImageConsumer ic)
   {
      if (isConsumer(ic))
      {
         consumers.remove(ic);
      }
   }

   public void requestTopDownLeftRightResend(ImageConsumer ic)
   {
      // We ignore this call, because a) we're allowed, b)
      // it shouldn't ever occur
   }

   public void startProduction(ImageConsumer ic)
   {
      addConsumer(ic);

      synchronized (theThread)
      {
         if (!started)
         {
            theThread.start();
            started = true;
         }
      }
   }

   public boolean isFinished()
   {
      return finished;
   }

   public void stop()
   {
      stopFlag = true;
   }

   public void setProgressMonitor(CustomProgressMonitor pm)
   {
      this.pm = pm;
   }

   public void setSize(int w, int h)
   {
      width = w;
      height = h;
   }

   public void run()
   {
      int tileX;
      int tileY;
      int xoffset;
      int yoffset;
      int xcount;
      int ycount;
      int xleftover;
      int yleftover;

      theMachine = expression.getMachine();

      // calculate how many tiles there will be
      xcount = width / tileSize;
      ycount = height / tileSize;

      // calculate how much space is left over in the image
      // after it's been broken into tiles
      xleftover = width % tileSize;
      yleftover = height % tileSize;

      for (tileY = 0; tileY < ycount; tileY++)
      {
         yoffset = tileY * tileSize;

         for (tileX = 0; tileX < xcount; tileX++)
         {
            xoffset = tileX * tileSize;

            drawTile(xoffset, yoffset, tileSize, tileSize);

            if (pm != null)
            {
               pm.incrementProgress(pmIncrement);
            }

            if (((pm != null) && (pm.isCanceled())) || stopFlag == true)
            {
               finished();
               return;
            }
         }

         if (xleftover != 0)
         {
            xoffset = xcount * tileSize;

            drawTile(xoffset, yoffset, xleftover, tileSize);

            if (pm != null)
            {
               pm.incrementProgress(tileSize * xleftover);
            }

            if (((pm != null) && (pm.isCanceled())) || stopFlag == true)
            {
               finished();
               return;
            }
         }
      }

      if (yleftover != 0)
      {
         yoffset = ycount * tileSize;

         for (tileX = 0; tileX < xcount; tileX++)
         {
            xoffset = tileX * tileSize;

            drawTile(xoffset, yoffset, tileSize, yleftover);

            if (pm != null)
            {
               pm.incrementProgress(tileSize * yleftover);
            }

            if (((pm != null) && (pm.isCanceled())) || stopFlag == true)
            {
               finished();
               return;
            }
         }

         if (xleftover != 0)
         {
            xoffset = xcount * tileSize;

            drawTile(xoffset, yoffset, xleftover, yleftover);

            if (pm != null)
            {
               pm.incrementProgress(xleftover * yleftover);
            }

            if (((pm != null) && (pm.isCanceled())) || stopFlag == true)
            {
               finished();
               return;
            }
         }
      }

      finished();
   }

   public void drawTile(
      int xoffset,
      int yoffset,
      int tileWidth,
      int tileHeight)
   {
      int x;
      int y;
      int offset;
      double tx;
      double ty;
      double red;
      double green;
      double blue;
      Stack stack;

      int data[] = new int[tileWidth * tileHeight];

      for (y = 0; y < tileHeight; y++)
      {
         offset = y * tileWidth;

         ty = ((double) (y + yoffset) / (double)height) * rh + y1;

         theMachine.setRegister(Machine.REGISTER_Y, ty);

         for (x = 0; x < tileWidth; x++)
         {
            tx = ((double) (x + xoffset) / (double)width) * rw + x1;

            theMachine.setRegister(Machine.REGISTER_X, tx);
            theMachine.setRegister(
               Machine.REGISTER_R,
               Math.sqrt((tx * tx) + (ty * ty)));
            theMachine.setRegister(Machine.REGISTER_THETA, Math.atan2(tx, ty));

            stack = theMachine.execute();

            red = Tools.map(stack.pop());
            green = Tools.map(stack.pop());
            blue = Tools.map(stack.pop());

            int rInt = (int) (red * 255.0);
            int gInt = (int) (green * 255.0);
            int bInt = (int) (blue * 255.0);

            int pixel = 0;

            pixel |= (rInt << Tools.offsets[0]) & Tools.masks[0];
            pixel |= (gInt << Tools.offsets[1]) & Tools.masks[1];
            pixel |= (bInt << Tools.offsets[2]) & Tools.masks[2];

            data[offset + x] = pixel;

         }

         if (((pm != null) && (pm.isCanceled())) || stopFlag == true)
         {
            feedConsumers(xoffset, yoffset, tileWidth, tileHeight, data);
            finished = true;
            return;
         }

         Thread.yield(); // let other threads do their thing
      }

      feedConsumers(xoffset, yoffset, tileWidth, tileHeight, data);
   }

   void feedConsumers(
      int xoffset,
      int yoffset,
      int tileWidth,
      int tileHeight,
      int[] data)
   {
      ImageConsumer ic;

      Hashtable ht = new Hashtable();

      ColorModel cm = new DirectColorModel(24, 0x0000FF, 0x00FF00, 0xFF0000);

      Vector v = (Vector)consumers.clone();

      Enumeration e = v.elements();

      while (e.hasMoreElements())
      {
         ic = (ImageConsumer)e.nextElement();

         // Send the data
         ic.setColorModel(cm);
         ic.setDimensions(width, height);
         ic.setProperties(ht); // is this necessary?
         ic.setHints(ImageConsumer.RANDOMPIXELORDER);
         ic.setPixels(
            xoffset,
            yoffset,
            tileWidth,
            tileHeight,
            cm,
            data,
            0,
            tileWidth);

      }
   }

   void finished()
   {
      // Let everyone know we're done.

      ImageConsumer ic;

      Vector v = (Vector)consumers.clone();

      Enumeration e = v.elements();

      while (e.hasMoreElements())
      {
         ic = (ImageConsumer)e.nextElement();

         ic.imageComplete(ImageConsumer.STATICIMAGEDONE);
      }

      fireStateChange();

      finished = true;
   }

   public int getWidth()
   {
      return width;
   }

   public int getHeight()
   {
      return height;
   }

   public ExpressionTree getExpression()
   {
      return expression;
   }

   public void addChangeListener(ChangeListener cl)
   {
      if (!listeners.contains(cl))
      {
         listeners.add(cl);
      }
   }

   void fireStateChange()
   {
      ChangeListener cl;
      ChangeEvent ce = new ChangeEvent(this);

      Vector v = (Vector)listeners.clone();

      Enumeration e = v.elements();

      while (e.hasMoreElements())
      {
         cl = (ChangeListener)e.nextElement();

         cl.stateChanged(ce);
      }
   }
}
