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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.RenderedImage;
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

		replaceImage(ri);
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

   public RenderedImage getImage()
   {
      return image;
   }

   public void flush()
   {
      // free the resources used by the image
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

   private void vline(int x, int y1, int y2)
   {
      int p[];
      
      if (x < 0 || x > width)
      {
         return;
      }
      
      if (y1 < 0)
      {
         y1 = 0;
      }
      
      if (y2 > (height))
      {
         y2 = height;
      }
      
      for (int y = y1; y < y2; y++)
      {
         p = image.getPixels(x, y, 1, 1);
         p[0] = p[0] ^ 0xFF;
         image.setPixel(x, y, p[0]);
      }
   }
   
   private void hline(int y, int x1, int x2)
   {
      int p[];
      
      if (y < 0 || y > height)
      {
         return;
      }
      
      if (x1 < 0)
      {
         x1 = 0;
      }
      
      if (x2 > (width))
      {
         x2 = width;
      }

      p = new int[0];
      
      try
      {
         p = image.getPixels(x1, y, x2 - x1, 1);
      }
      catch (NegativeArraySizeException nase)
      {
         System.err.println(x1 + " " + x2 + " " + y + " " + (x2 - x1) + " 1");
      }
      
      for (int x = 0; x < x2 - x1; x++)
      {
         p[x] = p[x] ^ 0xFF;
      }
      
      image.setPixels(x1, y, x2 - x1, 1, p);
   }
   
   synchronized public void xorRectangle(int x1, int y1, int x2, int y2)
   {
      int t;
      
      if (x1 > x2)
      {
         t = x1;
         x1 = x2;
         x2 = t;
      }
      
      if (y1 > y2)
      {
         t = y1;
         y1 = y2;
         y2 = t;
      }
      
      if (y1 != y2)
      {
         vline(x1, y1, y2);
         vline(x2, y1, y2);
      }
      
      if ((x2 - x1) > 1)
      {
         hline(y1, x1, x2);
         hline(y2, x1, x2);
      }
   }
   
   public void setProperties(Hashtable props)
   {
      //ignore
   }
   
   public void replaceImage(RendererInterface ri)
   {
   	if (image != null) 
   	{
			flush();
   	}
   	   	
		image = new TiledImage(width, height, ri);

		repaint();

		ri.addChangeListener(this);

		ri.startProduction(this);
   }
}
