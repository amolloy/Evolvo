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
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

import javax.swing.event.ChangeEvent;

import org.maloi.evolvo.expressiontree.renderer.RendererInterface;

public class SwingImagePanel extends ImagePanel implements ImageConsumer
{
   BufferedImage image;
   Image thumb;

   boolean finished = false;

   int width;
   int height;

   public SwingImagePanel(RendererInterface ri)
   {
      this(ri, null);
   }

   /**
    * Creates an image panel, drawing a scaled up instance of a thumbnail
    * of the RendererInterface's image to act as a placeholder until 
    * the RendererInterface is done drawing the full sized version.
    */
   public SwingImagePanel(RendererInterface ri, Image thumb)
   {
      width = ri.getWidth();
      height = ri.getHeight();
		this.thumb = thumb;

		replaceImage(ri);
   }

   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);

      if (image != null)
      {
         g.drawImage(image, 0, 0, this);
      }

      g.dispose();
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
      int i = 0;
      int x;
      int y;
      int endx = startx + w;
      int endy = starty + h;

      if (image == null)
      {
         return;
      }

      int color[] = new int[3];

      WritableRaster raster = image.getRaster();

      for (y = starty; y < endy; y++)
      {
         for (x = startx; x < endx; x++)
         {
            color[0] = cm.getRed(pixels[i]);
            color[1] = cm.getGreen(pixels[i]);
            color[2] = cm.getBlue(pixels[i]);
                        
            raster.setPixel(x, y, color);
            
            i++;
         }
      }

      repaint();
   }

   public void setProperties(Hashtable props)
   {
      //ignore
   }
   
	public void replaceImage(RendererInterface ri)
	{
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2 = image.createGraphics();

		if (thumb != null)
		{
			g2.drawImage(thumb, 0, 0, width, height, this);
		}
		else
		{
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, width, height);
		}

		g2.dispose();
		repaint();

		ri.addChangeListener(this);

		ri.startProduction(this);
	}
   
}
