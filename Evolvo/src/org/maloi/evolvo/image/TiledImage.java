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
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Vector;

import org.maloi.evolvo.image.tiledimage.Tile;
import org.maloi.evolvo.image.tiledimage.TiledImageGraphics;
import org.maloi.evolvo.image.tiledimage.TiledRaster;

public class TiledImage extends Image implements RenderedImage
{
   public final static int TILE_SIZE = Tile.TILE_SIZE;

   int width;
   int height;

   TiledRaster raster;
   ColorModel cm;

   public TiledImage(int width, int height, ImageProducer source)
   {
      this.width = width;
      this.height = height;
      
      raster = new TiledRaster(width, height);
      cm = new DirectColorModel(24, 0xFF0000, 0xFF00, 0xFF, 0x0);
   }

   public int getWidth(ImageObserver observer)
   {
      return width;
   }

   public int getHeight(ImageObserver observer)
   {
      return height;
   }

   public ImageProducer getSource()
   {
      return null;
   }

   public Graphics getGraphics()
   {
      // need to implement something here, I think, so that drawImage(...) can get data from us
      return new TiledImageGraphics(this);
   }

   public Object getProperty(String name, ImageObserver observer)
   {
      System.err.println("getProperty(" + name + ", " + observer + ");");
      return null;
   }

   public void flush()
   {
      
   }
   
   // ***
   // RenderedImage
   // ***

   public WritableRaster copyData(WritableRaster raster)
   {
      System.err.println("copyData(" + raster + ");");

      return null;
   }

   public ColorModel getColorModel()
   {
      System.err.println("getColorModel();");

      return cm;
   }

   public Raster getData()
   {
      System.err.println("getData();");

      return raster;
   }

   public Raster getData(Rectangle rect)
   {
      System.err.println("getData(" + rect + ");");

      return null;
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
      System.err.println("getMinTileX();");

      return 0;
   }

   public int getMinTileY()
   {
      System.err.println("getMinTileY();");

      return 0;
   }

   public int getMinX()
   {
      System.err.println("getMinX();");
      return 0;
   }

   public int getMinY()
   {
      System.err.println("getMinY();");
      return 0;
   }

   public int getNumXTiles()
   {
      System.err.println("getNumXTiles();");

      return raster.getNumXTiles();
   }

   public int getNumYTiles()
   {
      System.err.println("getNumYTiles();");

      return raster.getNumYTiles();
   }

   public Object getProperty(String s)
   {
      System.err.println("getProperty(" + s + ");");

      return null;
   }

   public String[] getPropertyNames()
   {
      System.err.println("getPropertyNames();");

      return null;
   }

   public SampleModel getSampleModel()
   {
      System.err.println("getSampleModel();");

      return raster.getSampleModel();
   }

   public Vector getSources()
   {
      System.err.println("getSources();");

      return null;
   }

   public Raster getTile(int tileX, int tileY)
   {
      System.err.println("getTile(" + tileX + ", " + tileY + "); ");

      return null;
   }

   public int getTileGridXOffset()
   {
      System.err.println("getTileGridXOffset();");

      return 0;
   }

   public int getTileGridYOffset()
   {
      System.err.println("getTileGridYOffset();");

      return 0;
   }

   public int getTileHeight()
   {
      System.err.println("getTileHeight();");

      return TILE_SIZE;
   }

   public int getTileWidth()
   {
      System.err.println("getTileWidth();");

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
}
