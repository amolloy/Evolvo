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

package org.maloi.evolvo.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.util.Hashtable;

import javax.swing.event.ChangeEvent;

import org.maloi.evolvo.expressiontree.renderer.RendererInterface;
import org.maloi.evolvo.image.TiledImage;

public class TiledImagePanel extends ImagePanel implements ImageConsumer
{
   TiledImage image;

   boolean finished = false;

   int width;
   int height;

   AffineTransform identityTransform;

   public TiledImagePanel(RendererInterface ri)
   {
      this(ri, null);
   }

   /**
    * Creates an image panel, drawing a scaled up instance of a thumbnail
    * of the RendererInterface's image to act as a placeholder until 
    * the RendererInterface is done drawing the full sized version.
    * 
    */
   public TiledImagePanel(RendererInterface ri, Image thumb)
   {
      width = ri.getWidth();
      height = ri.getHeight();

      identityTransform =  AffineTransform.getTranslateInstance(0.0, 0.0);
      identityTransform.setToIdentity(); // it should already be identity, but whatever

      image = new TiledImage(width, height, ri);

      Graphics g = image.getGraphics();

      if (thumb != null)
      {
         g.drawImage(thumb, 0, 0, width, height, this);
      }
      else
      {
         g.setColor(Color.BLACK);
         g.fillRect(0, 0, width, height);
      }

      g.dispose();
      repaint();

      ri.addChangeListener(this);

      ri.startProduction(this);
   }

   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);

      Graphics2D g2 = (Graphics2D)g;

      if (image != null)
      {
         g2.drawRenderedImage(image, identityTransform);
      }

      g2.dispose();
   }

   public Dimension getPreferredSize()
   {
      return new Dimension(width, height);
   }

   public Image getImage()
   {
      return image;
   }

   public void flush()
   {
      image.flush();
      
      image = null;
   }

   /*
    * ChangeListener
    */

   public void stateChanged(ChangeEvent ce)
   {
      finished = true;

      repaint();
   }

   /*
    * Image Consumer
    */

   /* 
    * We actually only implement setPixels(int,int,int,int,ColorModel,int[],int,int)
    * and imageComplete(int)
    */

   public void imageComplete(int status)
   {
      // Schedule a repaint
      stateChanged(null);
   }

   public void setColorModel(ColorModel model)
   {
      //ignore
   }

   public void setDimensions(int width, int height)
   {
      //ignore
   }

   public void setHints(int hintflags)
   {
      //ignore
   }

   public void setPixels(
      int x,
      int y,
      int w,
      int h,
      ColorModel model,
      byte[] pixels,
      int off,
      int scansize)
   {
      //ignore
   }

   public void setPixels(
      int startx,
      int starty,
      int w,
      int h,
      ColorModel cm,
      int[] pixels,
      int off,
      int scansize)
   {
      if (image == null)
      {
         return;
      }

      image.setPixels(startx, starty, w, h, pixels);

      repaint();
   }

   public void setProperties(Hashtable props)
   {
      //ignore
   }
   
   
}
