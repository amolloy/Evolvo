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
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Tile
{
   public static final int TILE_SIZE = 128;
   // All tiles are squares with this as their dimension
   static final int LOCATION_INVALID = 0x00;
   // the tile is invalid (not yet initialized)
   static final int LOCATION_MEMORY = 0x01; // the tile is in memory
   static final int LOCATION_DISK = 0x02; // the tile has been swapped to disk

   RandomAccessFile file; // the file where the disk cache is kept
   int fposition; // this tile's location in the disk cache

   int xloc; // the tile's x location in the image
   int yloc; // the tile's y location in the image
   int data[]; // the tile's data - null when swapped to disk
   long lastused; // timestamp of the last time this tile was accessed

   int location; // the tile's current location (invalid, disk, or memory)

   SampleModel sm;

   public Tile(int tilex, int tiley, RandomAccessFile file, int fposition)
   {
      this.file = file;
      this.fposition = fposition;

      xloc = TILE_SIZE * tilex;
      yloc = TILE_SIZE * tiley;

      location = LOCATION_INVALID;
      data = null;
      lastused = System.currentTimeMillis();

      sm =
         new SinglePixelPackedSampleModel(
            DataBuffer.TYPE_INT,
            TILE_SIZE,
            TILE_SIZE,
            new int[] { 0x000000FF, 0x0000FF00, 0x00FF0000 });
   }

   public void validate()
   {
      lastused = System.currentTimeMillis();

      if (location == LOCATION_MEMORY)
      {
         return;
      }
      else if (location == LOCATION_DISK)
      {
         loadTile();
         return;
      }
      else // location == LOCATION_INVALID
         {
         data = new int[TILE_SIZE * TILE_SIZE];
         for (int i = 0; i < data.length; i++)
         {
            data[i] = 0;
         }
         location = LOCATION_MEMORY;
      }
   }

   public int[] getData()
   {
      return data;
   }

   public void setData(int[] data)
   {
      for (int i = 0; i < this.data.length; i++)
      {
         this.data[i] = data[i];
      }
   }

   public void setPixel(int x, int y, int value)
   {
      data[(x - xloc) + ((y - yloc) * TILE_SIZE)] = value;
   }

   public void setPixels(
      int startX,
      int startY,
      int w,
      int h,
      int src[],
      int off,
      int srcScansize)
   {
      int x;
      int y;
      int i;

      int endX = startX + w;
      int endY = startY + h;

      int srcYOffset;
      int dstYOffset;

      if (h == 1)
      {
         y = startY;

         srcYOffset = (y - startY) * srcScansize + off;
         dstYOffset = (y - yloc) * TILE_SIZE;

         for (x = startX; x < endX; x++)
         {
            data[dstYOffset + x - xloc] = src[srcYOffset + (x - startX)];
         }
      }
      else if (w == 1)
      {
         for (y = startY; y < endY; y++)
         {
            x = startX;

            srcYOffset = (y - startY) * srcScansize + off;
            dstYOffset = (y - yloc) * TILE_SIZE;

            data[dstYOffset + x - xloc] = src[srcYOffset + (x - startX)];
         }
      }
      else
      {
         for (y = startY; y < endY; y++)
         {
            srcYOffset = (y - startY) * srcScansize + off;
            dstYOffset = (y - yloc) * TILE_SIZE;

            for (x = startX; x < endX; x++)
            {
               data[dstYOffset + x - xloc] = src[srcYOffset + (x - startX)];
            }
         }
      }
   }

   public int getPixel(int x, int y)
   {
      return data[(x - xloc) + ((y - yloc) * TILE_SIZE)];
   }

   public void getPixels(
      int startX,
      int startY,
      int w,
      int h,
      int dest[],
      int off,
      int srcScansize)
   {
      if (dest == null)
      {
         dest = new int[w * h];
      }

      int x;
      int y;
      int i;

      int endX = startX + w;
      int endY = startY + h;

      int destYOffset;
      int dataYOffset;

      if (h == 1)
      {
         y = startY;

         destYOffset = (y - startY) * srcScansize + off;
         dataYOffset = (y - yloc) * TILE_SIZE;

         for (x = startX; x < endX; x++)
         {
            dest[destYOffset + (x - startX)] = data[dataYOffset + x - xloc];
         }
      }
      else if (w == 1)
      {
         for (y = startY; y < endY; y++)
         {
            x = startX;

            destYOffset = (y - startY) * srcScansize + off;
            dataYOffset = (y - yloc) * TILE_SIZE;

            dest[destYOffset + (x - startX)] = data[dataYOffset + x - xloc];
         }
      }
      else
      {
         for (y = startY; y < endY; y++)
         {
            destYOffset = (y - startY) * srcScansize + off;
            dataYOffset = (y - yloc) * TILE_SIZE;

            for (x = startX; x < endX; x++)
            {
               dest[destYOffset + (x - startX)] = data[dataYOffset + x - xloc];
            }
         }
      }
   }

   public void expire()
   {
      byte diskdata[] = new byte[TILE_SIZE * TILE_SIZE * 3];
      // 3 bytes per pixel, TILE_SIZExTILE_SIZE pixels
      int dataOffset = 0;

      for (int i = 0; i < data.length; i++)
      {
         diskdata[dataOffset++] = (byte) (data[i] | 0x000000FF);
         diskdata[dataOffset++] = (byte) ((data[i] | 0x0000FF00) >> 8);
         diskdata[dataOffset++] = (byte) ((data[i] | 0x00FF0000) >> 16);
      }

      try
      {
         file.write(diskdata, fposition, diskdata.length);
      }
      catch (IOException ioe)
      {
         System.err.println("Could not write tile to disk.");
      }
   }

   public void loadTile()
   {
      byte diskdata[] = new byte[TILE_SIZE * TILE_SIZE * 3];
      // 3 bytes per pixel, TILE_SIZExTILE_SIZE pixels

      try
      {
         file.read(diskdata, fposition, diskdata.length);
      }
      catch (IOException ioe)
      {
         System.err.println("Could not read tile from disk.");
      }

      int dataOffset = 0;

      for (int i = 0; i < data.length; i++)
      {
         data[i] = diskdata[dataOffset++] | 0x000000FF;
         data[i] += diskdata[dataOffset++] << 8;
         data[i] += diskdata[dataOffset++] << 16;
      }

      location = LOCATION_MEMORY;
   }

   public boolean isValid()
   {
      return location == LOCATION_MEMORY;
   }

   public long getLastUsedTime()
   {
      return lastused;
   }

   public int getXLocation()
   {
      return xloc;
   }

   public int getYLocation()
   {
      return yloc;
   }

   public Point getLocation()
   {
      return new Point(xloc, yloc);
   }
   
   public SampleModel getSampleModel()
   {
      return sm;
   }

}