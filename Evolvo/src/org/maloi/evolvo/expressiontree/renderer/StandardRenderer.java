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
import org.maloi.evolvo.expressiontree.vm.Machine;
import org.maloi.evolvo.gui.CustomProgressMonitor;

public class StandardRenderer implements RendererInterface, Runnable
{
   // We shouldn't ever have more than one consumer, but who knows
   // what happens deep in the depths of AWT & Swing, so we'll start
   // with room for 2.  Same thing for listeners
   Vector consumers = new Vector(2);
   Vector listeners = new Vector(2);

   ExpressionTree expression;
   int width;
   int height;
   boolean finished = false;
   boolean stopFlag = false;
   boolean started = false;
   CustomProgressMonitor pm = null;

   Thread theThread;

   public StandardRenderer(ExpressionTree expression, int width, int height)
   {
      this.expression = expression;
      this.width = width;
      this.height = height;

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
      int y;
      int x;
      int offset;
      double ty;
      double tx;

      Machine theMachine = expression.getMachine();

      int data[] = new int[height * width];

      for (y = 0; y < height; y++)
      {
         offset = y * width;

         ty = ((double)y / (double)height) * 2.0 - 1.0;

         theMachine.setRegister(Machine.REGISTER_Y, ty);

         for (x = 0; x < width; x++)
         {
            tx = ((double)x / (double)width) * 2.0 - 1.0;

            theMachine.setRegister(Machine.REGISTER_X, tx);
            theMachine.setRegister(
               Machine.REGISTER_R,
               Math.sqrt((tx * tx) + (ty * ty)));
            theMachine.setRegister(Machine.REGISTER_THETA, Math.atan2(tx, ty));

            data[offset + x] = theMachine.executeToPixel();
         }

         if (pm != null)
         {
            pm.incrementProgress(1);
         }

         if (((pm != null) && (pm.isCanceled())) || stopFlag == true)
         {
            feedConsumers(data);
            finished = true;
            return;
         }

         Thread.yield(); // let other threads do their thing
      }

      feedConsumers(data);

      finished = true;
   }

   void feedConsumers(int[] data)
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
         ic.setHints(ImageConsumer.TOPDOWNLEFTRIGHT);
         ic.setPixels(0, 0, width, height, cm, data, 0, width);
         ic.imageComplete(ImageConsumer.STATICIMAGEDONE);
      }

      fireStateChange();
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
