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
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.maloi.evolvo.image.tiledimage.Tile;
import org.maloi.evolvo.settings.GlobalSettings;

public class TiledImage extends Image
{
   public final static int TILE_SIZE = Tile.TILE_SIZE;

   int MAX_RESIDENT_TILES;
   // total number of tiles to allow in memory at a time

   int numResidentTiles = 0; // how many tiles currently in memory (valid)

   RandomAccessFile file; // file containing the disk cache
   File tempFile; // abstract file pointing to the temp file

   int width; // the image's width
   int height; // the image's height
   int tileWidth; // the image's width in tiles
   int tileHeight; // the image's height in tiles

   Tile tiles[]; // the actual tiles

   ImageProducer source;

   GlobalSettings settings = GlobalSettings.getInstance();

   public TiledImage(int width, int height, ImageProducer source)
   {
      this.width = width;
      this.height = height;

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

      try
      {
         file = new RandomAccessFile(tempFile, "rw");
      }
      catch (FileNotFoundException fnfe)
      {
         System.err.println("Cache file not found.");
      }

      // grow the file large enough to hold the entire image
      // the image is 3 bytes per pixel, made up of tileWidth * tileHeight tiles, each of which contains TILE_SIZE x TILE_SIZE pixels
      try
      {
         file.setLength((tileWidth * tileHeight) * (TILE_SIZE * TILE_SIZE) * 3);
      }
      catch (IOException ioe)
      {
         System.err.println("Could not set file length: ");
         ioe.printStackTrace();
      }

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

      this.source = source;
   }

   Tile getTile(int tilex, int tiley)
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
      return source;
   }

   public Graphics getGraphics()
   {
      // need to implement something here, I think, so that drawImage(...) can get data from us
      return null;
   }

   public Object getProperty(String name, ImageObserver observer)
   {
      return null;
   }

   public void flush()
   {
      tiles = null;

      System.gc();
      // we'll go ahead and force a gc here to get rid of all the tiles right away.
   }

   public void setPixels(
      int startX,
      int startY,
      int width,
      int height,
      int[] pixels)
   {
      // Sets the pixels in the rectangle originating at (x,y) with width w and height h

      int endX = startX + width;
      int endY = startY + height;

      int startTileX = (int) Math.floor(startX / TILE_SIZE);
      int endTileX = (int) Math.floor(endX / TILE_SIZE);

      int startTileY = (int) Math.floor(startY / TILE_SIZE);
      int endTileY = (int) Math.floor(endY / TILE_SIZE);

      Tile tile;

      int tileX;
      int tileY;

      int xoffset;
      int yoffset;
      int totalOffset;

      int x;
      int y;
      int w;
      int h;

      for (tileY = startTileY; tileY < endTileY; tileY++)
      {
         for (tileX = startTileX; tileX < endTileX; tileX++)
         {
            tile = getTile(tileX, tileY);

            xoffset = tile.getXLocation() - startX;
            if (xoffset < 0)
            {
               xoffset = 0;
            }

            yoffset = tile.getYLocation() - startY;
            if (yoffset < 0)
            {
               yoffset = 0;
            }

            totalOffset = yoffset * width + xoffset;

            x = startX + xoffset;
            y = startY + yoffset;

            w = tile.getXLocation() + TILE_SIZE - x;
            if ((x + w) > endX)
            {
               w = endX - x;
            }

            h = tile.getYLocation() + TILE_SIZE - y;
            if ((y + h) > endY)
            {
               h = endY - y;
            }

            tile.setPixels(x, y, w, h, pixels, totalOffset, w);
         }
      }
   }
}
