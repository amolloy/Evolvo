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
import java.awt.image.DataBuffer;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.maloi.evolvo.debugtools.CallLogger;
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

   public TiledRaster(int width, int height)
   {
      super(
         new SinglePixelPackedSampleModel(
            DataBuffer.TYPE_INT,
            width,
            height,
            new int[] { 0x000000FF, 0x0000FF00, 0x00FF0000 }),
         new Point(0, 0));

      minX = 0;
      minY = 0;
      this.width = width;
      this.height = height;
      numBands = 3;
      dataBuffer = null;
      parent = null;
      sampleModelTranslateX = 0;
      sampleModelTranslateY = 0;

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
         raFile.setLength((tileWidth * tileHeight) * (TILE_SIZE * TILE_SIZE) * 3);
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
            tiles[yoffset + tilex] = new Tile(tilex, tiley, file, foffset);
            foffset += foffset_inc;
         }
      }
   }

   Tile validateTile(int tilex, int tiley)
   {
      int whichTile = (tiley * tileWidth) + tilex;

      if (tiles[whichTile].isValid())
      {
         return tiles[whichTile];
      }

      // The tile isn't in memory, so we need to validate it, then make sure we haven't loaded too many tiles
      tiles[whichTile].validate();

      numResidentTiles++;

      if (numResidentTiles > MAX_RESIDENT_TILES)
      {
         // we've loaded too many tiles, we need to expire one
         expireLRUTile();
      }

      // okay, the tile is valid, we can return it now

      return tiles[whichTile];
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

      // when we exit this loop, whichTile contains the index of the least recently used resident tile
      // so we just expire it

      tiles[whichTile].expire();
   }

   public void flush()
   {
      tiles = null;

      //System.gc(); //  go ahead and force a garbage collection
   }

   public void setPixels(int startX, int startY, int w, int h, int[] src)
   {
      System.err.println(
         "Unimplemented: TiledRaster.setPixels("
            + startX
            + ", "
            + startY
            + ", "
            + width
            + ",  "
            + height
            + ", "
            + src
            + ");");

      int endX = startX + w;
      int endY = startY + h;

      int startTileX = startX / TILE_SIZE;
      int startTileY = startY / TILE_SIZE;
      int endTileX = endX / TILE_SIZE;
      int endTileY = endY / TILE_SIZE;

      int tileX;
      int tileY;

      int tileOffset;

      System.err.println(
         "Drawing to tiles: ("
            + startTileX
            + ", "
            + startTileY
            + ") through ("
            + endTileX
            + ", "
            + endTileY
            + ")");

      if (startTileY == endTileY)
      {
         System.err.println("Horizontal line.");

         tileOffset = startTileY * tileWidth;
         tileY = startTileY;

         for (tileX = startTileX; tileX < endTileX; tileX++)
         {
            validateTile(tileX, tileY);
         }
      }
      else if (startTileX == endTileX)
      {
         System.err.println("Vertical line");

         tileX = startTileX;

         for (tileY = startTileY; tileY < endTileY; tileY++)
         {
            tileOffset = tileY * tileWidth;

            validateTile(tileX, tileY);
         }
      }
      else
      {
         System.err.println("Rectangle");

         for (tileY = startTileY; tileY < endTileY; tileY++)
         {
            tileOffset = tileY * tileWidth;

            for (tileX = startTileX; tileX < endTileX; tileX++)
            {
               validateTile(tileX, tileY);
            }
         }
      }
   }

   public int[] getPixels(int startX, int startY, int w, int h, int[] dest)
   {
      System.err.println(
         "Unimplemented: TiledRaster.getPixels("
            + startX
            + ", "
            + startY
            + ", "
            + w
            + ",  "
            + h
            + ", "
            + dest
            + ");");

      if (dest == null)
      {
         dest = new int[w * h];
      }

      int endX = startX + w;
      int endY = startY + h;

      int startTileX = startX / TILE_SIZE;
      int startTileY = startY / TILE_SIZE;
      int endTileX = endX / TILE_SIZE;
      int endTileY = endY / TILE_SIZE;

      System.err.println(
         "Getting data from tiles: ("
            + startTileX
            + ", "
            + startTileY
            + ") through ("
            + endTileX
            + ", "
            + endTileY
            + ")");

      if (startTileY == endTileY)
      {
         System.err.println("Horizontal line");
      }
      else if (startTileX == endTileX)
      {
         System.err.println("Vertical line");
      }
      else
      {
         System.err.println("Rectangle");
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

      System.err.println(
         "TiledRaster.createChild("
            + parentX
            + ", "
            + parentY
            + ", "
            + width
            + ", "
            + height
            + ", "
            + childMinX
            + ", "
            + childMinY
            + ", "
            + bandlist
            + ");");

      return this;
   }

   public Raster getTile(int tileX, int tileY)
   {
      Tile theTile = validateTile(tileX, tileY);

      Point location = theTile.getLocation();

      WritableRaster tr =
         Raster.createWritableRaster(theTile.getSampleModel(), location);

      System.err.println(tr);

      tr.setDataElements(
         location.x,
         location.y,
         TILE_SIZE,
         TILE_SIZE,
         new int[TILE_SIZE * TILE_SIZE]);
//         theTile.getData());

      System.err.println(tr);

      return tr;
   }
}

//   public void setPixels(
//      int startX,
//      int startY,
//      int width,
//      int height,
//      int[] src)
//   {
//      // Sets the pixels in the rectangle originating at (x,y) with width w and height h
//
//      int endX = startX + width;
//      int endY = startY + height;
//
//      int startTileX = (int) Math.floor(startX / TILE_SIZE);
//      int endTileX = (int) Math.floor(endX / TILE_SIZE);
//
//      int startTileY = (int) Math.floor(startY / TILE_SIZE);
//      int endTileY = (int) Math.floor(endY / TILE_SIZE);
//
//      Tile tile;
//
//      int tileX;
//      int tileY;
//
//      int xoffset;
//      int yoffset;
//      int totalOffset;
//
//      int x;
//      int y;
//      int w;
//      int h;
//      int i;
//
//      int srcYOffset;
//      int dstYOffset;
//
//      int roiWidth;
//      int roiHeight;
//
//      if (startTileY == endTileY)
//      {
//         dstYOffset = startY * tileWidth;
//         
//         for (tileX = startTileX, i = 0; tileX < endTileX; tileX++, i++)
//         {
//            tile = validateTile(tileX, startTileY);
//
//            if (tileX == startX)
//            {
//               roiWidth = ((tileX + 1) * TILE_SIZE) - startX;
//            }
//            else if (tileX == endX)
//            {
//               roiWidth = endX  - (tileX * TILE_SIZE);
//            }
//            else
//            {
//               roiWidth = TILE_SIZE;
//            }
//
//            xoffset = (tileX * TILE_SIZE) - startX;
//            
//            if (xoffset < 0)
//            {
//               xoffset = 0;
//            }
//
//            tile.setPixels(tileX * TILE_SIZE, 
//                                 startY, 
//                                 roiWidth, 
//                                 1, 
//                                 src,
//                                  xoffset, 
//                                  width);
//         }
//      }
//      else if (startTileX == endTileX)
//      {
//      }
//      else
//      {
//
//         for (tileY = startTileY; tileY < endTileY; tileY++)
//         {
//            for (tileX = startTileX; tileX < endTileX; tileX++)
//            {
//               tile = validateTile(tileX, tileY);
//
//               xoffset = tile.getXLocation() - startX;
//               if (xoffset < 0)
//               {
//                  xoffset = 0;
//               }
//
//               yoffset = tile.getYLocation() - startY;
//               if (yoffset < 0)
//               {
//                  yoffset = 0;
//               }
//
//               totalOffset = yoffset * width + xoffset;
//
//               x = startX + xoffset;
//               y = startY + yoffset;
//
//               w = tile.getXLocation() + TILE_SIZE - x;
//               if ((x + w) > endX)
//               {
//                  w = endX - x;
//               }
//
//               h = tile.getYLocation() + TILE_SIZE - y;
//               if ((y + h) > endY)
//               {
//                  h = endY - y;
//               }
//
//               tile.setPixels(x, y, w, h, src, totalOffset, w);
//            }
//         }
//      }
//   }
