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

package org.maloi.evolvo.image.tiledimage;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.maloi.evolvo.settings.GlobalSettings;

/**
 * @author amolloy
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TiledRaster extends WritableRaster
{
   public final static int TILE_SIZE = Tile.TILE_SIZE;

   int MAX_RESIDENT_TILES;
   // total number of tiles to allow in memory at a time

   int numResidentTiles = 0; // how many tiles currently in memory (valid)

   FileChannel file; // file containing the disk cache
   File tempFile; // abstract file pointing to the temp file

   int width; // the image's width
   int height; // the image's height
   int tileWidth; // the image's width in tiles
   int tileHeight; // the image's height in tiles

   Tile tiles[]; // the actual tiles

   GlobalSettings settings = GlobalSettings.getInstance();

   static SampleModel tileSampleModel;

   static int[] masks;

   static {
      BufferedImage image;

      image =
         new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_RGB);

      tileSampleModel = image.getSampleModel();

      masks = ((SinglePixelPackedSampleModel) tileSampleModel).getBitMasks();
   }

   public TiledRaster(int width, int height)
   {
      this(width, height, 0, 0);
   }
   
   public TiledRaster(int width, int height, int startX, int startY)
   {
      super(
         new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            .getSampleModel(),
         new Point(startX, startY));

      minX = 0;
      minY = 0;
      this.width = width;
      this.height = height;
      numBands = 3;
      dataBuffer = null;
      parent = null;
      sampleModelTranslateX = startX;
      sampleModelTranslateY = startY;

      tileWidth = Math.round((width / TILE_SIZE) + 0.5f);
      tileHeight = Math.round((height / TILE_SIZE) + 0.5f);

      tiles = new Tile[tileWidth * tileHeight];

      MAX_RESIDENT_TILES = settings.getIntegerProperty("tilecache.maxtiles");
      String pathToCache = settings.getStringProperty("tilecache.location");

      try
      {
         tempFile =
            File.createTempFile(
               "evolvoCache" + this.hashCode(),
               null,
               new File(pathToCache));
      }
      catch (IOException ioe)
      {
         System.err.println("Could not create temporary file:");
         ioe.printStackTrace();
      }

      tempFile.deleteOnExit();

      RandomAccessFile raFile = null;

      try
      {
         raFile = new RandomAccessFile(tempFile, "rwd");
      }
      catch (FileNotFoundException fnfe)
      {
         System.err.println("Cache file not found.");
      }

      // grow the file large enough to hold the entire image
      // the image is 3 bytes per pixel, made up of tileWidth * tileHeight tiles, each of which contains TILE_SIZE x TILE_SIZE pixels
      try
      {
         raFile.setLength(
            (tileWidth * tileHeight) * (TILE_SIZE * TILE_SIZE) * 3);
      }
      catch (IOException ioe)
      {
         System.err.println("Could not set file length: ");
         ioe.printStackTrace();
      }

      file = raFile.getChannel();

      int foffset = 0;
      int foffset_inc = 3 * TILE_SIZE * TILE_SIZE;
      // each tile takes 3 * TILE_SIZExTILE_SIZE bytes
      int yoffset;

      for (int tiley = 0; tiley < tileHeight; tiley++)
      {
         yoffset = tiley * tileWidth;

         for (int tilex = 0; tilex < tileWidth; tilex++)
         {
            tiles[yoffset + tilex] =
               new Tile(tilex, tiley, file, foffset, tileSampleModel);
            foffset += foffset_inc;
         }
      }
   }

   void validateTile(int tilex, int tiley)
   {
      int whichTile = (tiley * tileWidth) + tilex;

      if (tiles[whichTile].isValid())
      {
         return;
      }

      // The tile isn't in memory, so we need to validate it, then make sure we 
      // haven't loaded too many tiles
      tiles[whichTile].validate();

      numResidentTiles++;

      if (numResidentTiles > MAX_RESIDENT_TILES)
      {
         // we've loaded too many tiles, we need to expire one
         expireLRUTile();
      }
   }

   void expireLRUTile()
   {
      // get rid of the least recently used tile.

      long minLastUsed = Long.MAX_VALUE;
      // set it higher than any other possible value
      int whichTile = 0;

      for (int i = 0; i < tiles.length; i++)
      {
         if (tiles[i].isValid() && (tiles[i].getLastUsedTime() < minLastUsed))
         {
            whichTile = i;
            minLastUsed = tiles[i].getLastUsedTime();
         }
      }

      // when we exit this loop, whichTile contains the index of the least 
      // recently used resident tile so we just expire it

      tiles[whichTile].expire();
   }

   public void flush()
   {
      tiles = null;

      //System.gc(); //  go ahead and force a garbage collection
   }

   public void setPixel(int x, int y, int pix)
   {
      int tileX;
      int tileY;

      tileX = x / TILE_SIZE;
      tileY = y / TILE_SIZE;

      validateTile(tileX, tileY);

      tiles[tileY * tileWidth + tileX].setPixel(x, y, pix);
   }

   public void setPixels(int startX, int startY, int w, int h, int[] src)
   {
      int x;
      int y;

      int endX = startX + w;
      int endY = startY + h;

      int tileX;
      int tileY;
      int lastTileX = -1;
      int lastTileY = -1;

      int tileYOffset;
      int srcYOffset;

      for (y = startY; y < endY; y++)
      {
         tileY = y / TILE_SIZE;

         tileYOffset = tileY * tileWidth;

         srcYOffset = (y - startY) * w;

         for (x = startX; x < endX; x++)
         {
            tileX = x / TILE_SIZE;

            if ((tileX != lastTileX) || (tileY != lastTileY))
            {
               lastTileX = tileX;
               lastTileY = tileY;

               validateTile(tileX, tileY);
            }

            tiles[tileYOffset
               + tileX].setPixel(x, y, src[srcYOffset + (x - startX)]);
         }
      }
   }

   public int[] getData(int startX, int startY, int w, int h, int[] dest)
   {
      if (dest == null)
      {
         dest = new int[w * h];
      }

      int x;
      int y;

      int endX = startX + w;
      int endY = startY + h;

      int tileX;
      int tileY;
      int lastTileX = -1;
      int lastTileY = -1;

      int tileYOffset;
      int srcYOffset;

      for (y = startY; y < endY; y++)
      {
         tileY = y / TILE_SIZE;

         tileYOffset = tileY * tileWidth;

         srcYOffset = (y - startY) * w;

         for (x = startX; x < endX; x++)
         {
            tileX = x / TILE_SIZE;

            if ((tileX != lastTileX) || (tileY != lastTileY))
            {
               lastTileX = tileX;
               lastTileY = tileY;

               validateTile(tileX, tileY);
            }

            dest[srcYOffset + (x - startX)] =
               tiles[tileYOffset + tileX].getPixel(x, y);
         }
      }

      return dest;
   }

   public int getNumXTiles()
   {
      return tileWidth;
   }

   public int getNumYTiles()
   {
      return tileHeight;
   }

   public Raster createChild(
      int parentX,
      int parentY,
      int width,
      int height,
      int childMinX,
      int childMinY,
      int[] bandlist)
   {
      return this;
   }

   public Raster getTile(int tileX, int tileY)
   {
      Tile theTile = tiles[tileY * tileWidth + tileX];

      validateTile(tileX, tileY);

      Point location = theTile.getLocation();

      DataBufferInt dataBuffer =
         new DataBufferInt(theTile.getData(), TILE_SIZE * TILE_SIZE);

      return Raster.createRaster(tileSampleModel, dataBuffer, location);
   }
}
