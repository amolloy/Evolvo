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

      cm =
         (new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getColorModel());
   }

   public int getWidth(ImageObserver observer)
   {
//      System.err.println("TiledImage.getWidth(" + observer + ");");

      return width;
   }

   public int getHeight(ImageObserver observer)
   {
//      System.err.println("TiledImage.getHeight(" + observer + ");");

      return height;
   }

   public ImageProducer getSource()
   {
//      System.err.println("TiledImage.getSource();");

      return null;
   }

   public Graphics getGraphics()
   {
//      System.err.println("TiledImage.getGraphics();");

      // need to implement something here, I think, so that drawImage(...) can get data from us
      return new TiledImageGraphics(this);
   }

   public Object getProperty(String name, ImageObserver observer)
   {
//      System.err.println(
//         "TiledImage.getProperty(" + name + ", " + observer + ");");

      return null;
   }

   public void flush()
   {
//      System.err.println("TiledImage.flush();");
   }

   // ***
   // RenderedImage
   // ***

   public WritableRaster copyData(WritableRaster raster)
   {
//      System.err.println("TiledImage.copyData(" + raster + ");");

      return null;
   }

   public ColorModel getColorModel()
   {
//      System.err.println("TiledImage.getColorModel();");

      return cm;
   }

   public Raster getData()
   {
//      System.err.println("TiledImage.getData();");

      return raster;
   }

   public Raster getData(Rectangle rect)
   {
//      System.err.println("TiledImage.getData(" + rect + ");");

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
//      System.err.println("TiledImage.getHeight();");

      return height;
   }

   public int getWidth()
   {
//      System.err.println("TiledImage.getWidth();");

      return width;
   }

   public int getMinTileX()
   {
//      System.err.println("TiledImage.getMinTileX();");

      return 0;
   }

   public int getMinTileY()
   {
//      System.err.println("TiledImage.getMinTileY();");

      return 0;
   }

   public int getMinX()
   {
//      System.err.println("TiledImage.getMinX();");

      return 0;
   }

   public int getMinY()
   {
//      System.err.println("TiledImage.getMinY();");

      return 0;
   }

   public int getNumXTiles()
   {
//      System.err.println("TiledImage.getNumXTiles();");

      return raster.getNumXTiles();
   }

   public int getNumYTiles()
   {
//      System.err.println("TiledImage.getNumYTiles();");

      return raster.getNumYTiles();
   }

   public Object getProperty(String s)
   {
//      System.err.println("TiledImage.getProperty(" + s + ");");

      return null;
   }

   public String[] getPropertyNames()
   {
//      System.err.println("TiledImage.getPropertyNames();");

      return null;
   }

   public SampleModel getSampleModel()
   {
//      System.err.println("TiledImage.getSampleModel();");

      return raster.getSampleModel();
   }

   public Vector getSources()
   {
//      System.err.println("TiledImage.getSources();");

      return null;
   }

   public Raster getTile(int tileX, int tileY)
   {
//      System.err.println("TiledImage.getTile(" + tileX + ", " + tileY + ");");

      Raster returnRaster = raster.getTile(tileX, tileY);
      
//      System.err.println(returnRaster);
      
      return returnRaster;
   }

   public int getTileGridXOffset()
   {
//      System.err.println("TiledImage.getTileGridXOffset();");

      return 0;
   }

   public int getTileGridYOffset()
   {
//      System.err.println("TiledImage.getTileGridXOffset();");

      return 0;
   }

   public int getTileHeight()
   {
//      System.err.println("TiledImage.getTileHeight();");

      return TILE_SIZE;
   }

   public int getTileWidth()
   {
//      System.err.println("TiledImage.getTileWidth();");

      return TILE_SIZE;
   }

   public void setPixels(
      int startX,
      int startY,
      int width,
      int height,
      int[] pixels)
   {
//      System.err.println(
//         "TiledImage.setPixels("
//            + startX
//            + ", "
//            + startY
//            + ", "
//            + width
//            + ", "
//            + height
//            + ", "
//            + pixels
//            + ");");

      raster.setPixels(startX, startY, width, height, pixels);
   }

   public void setPixel(int x, int y, int pixel)
   {
//      System.err.println(
//         "TiledImage.setPixel(" + x + ", " + y + ", " + pixel + ");");

      raster.setPixel(x, y, pixel);
   }
}
