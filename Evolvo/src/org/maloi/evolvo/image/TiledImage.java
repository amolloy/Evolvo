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

package org.maloi.evolvo.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Enumeration;
import java.util.Vector;

import org.maloi.evolvo.image.tiledimage.Tile;
import org.maloi.evolvo.image.tiledimage.TiledImageGraphics;
import org.maloi.evolvo.image.tiledimage.TiledRaster;

public class TiledImage extends Image implements RenderedImage, ImageProducer, Cloneable
{
   public final static int TILE_SIZE = Tile.TILE_SIZE;

   int width;
   int height;

   TiledRaster raster;
   ColorModel cm;

   Vector consumers;
   Vector observers;

   public TiledImage(int width, int height, ImageProducer source)
   {
      this.width = width;
      this.height = height;

      raster = new TiledRaster(width, height);

      cm =
         (new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getColorModel());

      consumers = new Vector(2);
      observers = new Vector(2);
   }

   // ***
   // Image
   // ***

   public void flush()
   {
   	raster = null;
   	
      System.err.println("TiledImage.flush();");
   }

   public Graphics getGraphics()
   {
      // need to implement something here, I think, so that drawImage(...) can get data from us
      System.err.println("TiledImage.getGraphics();");

      return new TiledImageGraphics(this);
   }

   public int getHeight(ImageObserver observer)
   {
      System.err.println("TiledImage.getHeight(observer=" + observer + ");");

		addObserver(observer);

      return height;
   }

   public Object getProperty(String name, ImageObserver observer)
   {
   	System.err.println("TiledImage.getProperty("+name+", "+observer+");");
   	
   	addObserver(observer);
   	
      return null;
   }

   public Image getScaledInstance(int width, int height, int hints)
   {
      System.err.println("TiledImage.getScaledInstance();");

      return null;
   }

   public ImageProducer getSource()
   {
      System.err.println("TiledImage.getSource();");

      return this;
   }
   
   public int getWidth(ImageObserver observer)
   {
      System.err.println("TiledImage.getWidth(observer=" + observer +");");

		addObserver(observer);

      return width;
   }

	boolean isObserver(ImageObserver observer)
	{
		return observers.contains(observer);
	}
	
	void addObserver(ImageObserver observer)
	{
		if ((observer != null) && (!isObserver(observer)))
		{
			observers.add(observer);
			
			sendImageDone();
		}
	}

	void sendImageDone()
	{
		System.err.println("Observers:");
		
	   for (Enumeration e = observers.elements(); e.hasMoreElements();)
	   {
	      System.err.println("\t" + e.nextElement());
	   }
	}

   // ***
   // RenderedImage
   // ***

   public WritableRaster copyData(WritableRaster raster)
   {
      return null;
   }

   public ColorModel getColorModel()
   {
      return cm;
   }

   public Raster getData()
   {
      return raster;
   }

   public Raster getData(Rectangle rect)
   {
      SampleModel sm =
         new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB)
            .getSampleModel();

      DataBufferInt dataBuffer =
         new DataBufferInt(
            raster.getData(rect.x, rect.y, rect.width, rect.height, null),
            rect.width * rect.height);

      Point location = new Point(rect.x, rect.y);

      return Raster.createRaster(sm, dataBuffer, location);
   }

   public int getHeight()
   {
      return height;
   }

   public int getWidth()
   {
      return width;
   }

   public int getMinTileX()
   {
      return 0;
   }

   public int getMinTileY()
   {
      return 0;
   }

   public int getMinX()
   {
      return 0;
   }

   public int getMinY()
   {
      return 0;
   }

   public int getNumXTiles()
   {
      return raster.getNumXTiles();
   }

   public int getNumYTiles()
   {
      return raster.getNumYTiles();
   }

   public Object getProperty(String s)
   {
      return null;
   }

   public String[] getPropertyNames()
   {
      return null;
   }

   public SampleModel getSampleModel()
   {
      return raster.getSampleModel();
   }

   public Vector getSources()
   {
      return null;
   }

   public Raster getTile(int tileX, int tileY)
   {
      Raster returnRaster = raster.getTile(tileX, tileY);

      return returnRaster;
   }

   public int getTileGridXOffset()
   {
      return 0;
   }

   public int getTileGridYOffset()
   {
      return 0;
   }

   public int getTileHeight()
   {
      return TILE_SIZE;
   }

   public int getTileWidth()
   {
      return TILE_SIZE;
   }

   public void setPixels(
      int startX,
      int startY,
      int width,
      int height,
      int[] pixels)
   {
      raster.setPixels(startX, startY, width, height, pixels);
   }

   public void setPixel(int x, int y, int pixel)
   {
      raster.setPixel(x, y, pixel);
   }

   // ***
   // ImageProducer
   // ***

   public boolean isConsumer(ImageConsumer ic)
   {
   	System.err.println("TiledImage.isConsumer("+ic+");");
   	
      return consumers.contains(ic);
   }

   public void addConsumer(ImageConsumer ic)
   {
      System.err.println("TiledImage.addConsumer("+ic+");");
   	
      if ((ic != null) && (!isConsumer(ic)))
      {
         consumers.add(ic);
      }
   }

   public void removeConsumer(ImageConsumer ic)
   {
      System.err.println("TiledImage.removeConsumer("+ic+");");
   	
      if (isConsumer(ic))
      {
         consumers.remove(ic);
      }
   }

   public void requestTopDownLeftRightResend(ImageConsumer ic)
   {
      System.err.println("TiledImage.requestTopDownLeftRightResend("+ic+");");
   	
      addConsumer(ic);

      sendData();
   }

   public void startProduction(ImageConsumer ic)
   {
      System.err.println("TiledImage.startProduction("+ic+");");
   	
      addConsumer(ic);

      sendData();
   }

   void sendData()
   {
      System.err.println("Recieved send message. Consumers:");

      for (Enumeration e = consumers.elements(); e.hasMoreElements();)
      {
         System.err.println("\t" + e.nextElement());
      }
   }
}
