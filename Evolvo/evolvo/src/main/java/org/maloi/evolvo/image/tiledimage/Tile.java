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
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.maloi.evolvo.gui.SystemConsole;
import org.maloi.evolvo.localization.MessageStrings;

public class Tile
{
   public static final int TILE_SIZE = 128;
   // All tiles are squares with this as their dimension
   static final int LOCATION_INVALID = 0x00;
   // the tile is invalid (not yet initialized)
   static final int LOCATION_MEMORY = 0x01; // the tile is in memory
   static final int LOCATION_DISK = 0x02; // the tile has been swapped to disk

   FileChannel file; // the file where the disk cache is kept
   int fposition; // this tile's location in the disk cache

   int xloc; // the tile's x location in the image
   int yloc; // the tile's y location in the image
   int data[]; // the tile's data - null when swapped to disk

   int location; // the tile's current location (invalid, disk, or memory)

   SinglePixelPackedSampleModel sm;

   int[] masks; // masks for where each color is within a pixel int
   int[] offsets; // how many bits to shift an int to get the color

   SystemConsole console = SystemConsole.getInstance();

   long lastused;
   
   public Tile(int tilex, int tiley, FileChannel file, int fposition)
   {
      this(
         tilex,
         tiley,
         file,
         fposition,
         new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_RGB)
            .getSampleModel());
   }

   public Tile(
      int tilex,
      int tiley,
      FileChannel file,
      int fposition,
      SampleModel sm)
   {
      this.file = file;
      this.fposition = fposition;

      xloc = TILE_SIZE * tilex;
      yloc = TILE_SIZE * tiley;

      location = LOCATION_INVALID;
      data = null;

      this.sm = (SinglePixelPackedSampleModel) sm;

      masks = this.sm.getBitMasks();
      offsets = this.sm.getBitOffsets();

      //validate(); // go ahead and validate this tile
      // on second thought, let's be lazy about it...
   
      lastused = System.currentTimeMillis();
   }

   public void validate(int[] mem)
   {
      	data = mem;
      	
      	switch (location)
      	{
      		case LOCATION_MEMORY: break;
      		case LOCATION_DISK: loadTile(); break;
      		case LOCATION_INVALID: 
               // now that we share data space with other
               // tiles, we need to reset new tiles to 0's 
               // if we're invalid, else we'll get some 
               // other tile's crap in our scratch space
               location = LOCATION_MEMORY; 
               
               for (int i = 0; i < data.length; i++)
               {
                  data[i] = 0;
               }
               
               break;
      	}
   
      	lastused = System.currentTimeMillis();
   }

   public int[] getData()
   {
      lastused = System.currentTimeMillis();
      return data;
   }

   public void setData(int[] data)
   {
      for (int i = 0; i < this.data.length; i++)
      {
         this.data[i] = data[i];
      }
      
      lastused = System.currentTimeMillis();
   }

   public void setPixel(int x, int y, int value)
   {
      data[(x - xloc) + ((y - yloc) * TILE_SIZE)] = fixPixel(value);
   
      lastused = System.currentTimeMillis();
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
            data[dstYOffset + x - xloc] =
               fixPixel(src[srcYOffset + (x - startX)]);
         }
      }
      else if (w == 1)
      {
         for (y = startY; y < endY; y++)
         {
            srcYOffset = (y - startY) * srcScansize + off;
            dstYOffset = (y - yloc) * TILE_SIZE + startX - xloc;

            data[dstYOffset] = fixPixel(src[srcYOffset]);
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
               data[dstYOffset + x - xloc] =
                  fixPixel(src[srcYOffset + (x - startX)]);
            }
         }
      }
      
      lastused = System.currentTimeMillis();
   }

   public int getPixel(int x, int y)
   {
      lastused = System.currentTimeMillis();
      return data[(x - xloc) + ((y - yloc) * TILE_SIZE)];
   }

   public void getPixels(
      int startX,
      int startY,
      int w,
      int h,
      int dest[],
      int off,
      int destScansize)
   {
      lastused = System.currentTimeMillis();
      
      if (dest == null)
      {
         dest = new int[w * h];
      }

      int x;
      int y;
      
      int endX = startX + w;
      int endY = startY + h;

      int destYOffset;
      int dataYOffset;

      if (h == 1)
      {
         y = startY;

         destYOffset = (y - startY) * destScansize + off;
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

            destYOffset = (y - startY) * destScansize + off;
            dataYOffset = (y - yloc) * TILE_SIZE;

            dest[destYOffset + (x - startX)] = data[dataYOffset + x - xloc];
         }
      }
      else
      {
         for (y = startY; y < endY; y++)
         {
            destYOffset = (y - startY) * destScansize + off;
            dataYOffset = (y - yloc) * TILE_SIZE;

            for (x = startX; x < endX; x++)
            {
               dest[destYOffset + (x - startX)] = data[dataYOffset + x - xloc];
            }
         }
      }
   }

   synchronized public void expire()
   {
      if (location != LOCATION_MEMORY)
      {
         return;
      }
      
      if (data == null)
      {
         return;
      }
         
      byte diskdata[] = new byte[TILE_SIZE * TILE_SIZE * 3];
      // 3 bytes per pixel, TILE_SIZExTILE_SIZE pixels
      int dataOffset = 0;

      for (int dataCount = 0; dataCount < data.length; dataCount++)
      {
         for (int sampleCount = 0; sampleCount < 3; sampleCount++)
         {
            diskdata[dataOffset + sampleCount] =
               (byte) 
                  ((
                        data[dataCount] & 
                        masks[sampleCount])
                  >> offsets[sampleCount]);
         }

         dataOffset += 3;
      }

      try
      {
         file.position(fposition);
         file.write(ByteBuffer.wrap(diskdata));
         file.force(true);
      }
      catch (IOException ioe)
      {
         console.println(MessageStrings.getString("Tile.Could_not_write_tile_to_disk.")); //$NON-NLS-1$
      }

      location = LOCATION_DISK;
      data = null;
      lastused = System.currentTimeMillis();
   }

   public void loadTile()
   {
      byte diskdata[] = new byte[TILE_SIZE * TILE_SIZE * 3];
      // 3 bytes per pixel, TILE_SIZExTILE_SIZE pixels

      try
      {
         file.position(fposition);
         file.read(ByteBuffer.wrap(diskdata));
      }
      catch (IOException ioe)
      {
         console.println(MessageStrings.getString("Tile.Could_not_read_tile_from_disk.")); //$NON-NLS-1$
         console.printStackTrace(ioe);
      }

      int dataOffset = 0;

      for (int dataCount = 0; dataCount < data.length; dataCount++)
      {
         data[dataCount] = 0;

         for (int sampleCount = 0; sampleCount < 3; sampleCount++)
         {
            data[dataCount] |= (diskdata[dataOffset++] << offsets[sampleCount])
               & masks[sampleCount];
         }
      }

      location = LOCATION_MEMORY;
      
      lastused = System.currentTimeMillis();
   }

   public boolean isValid()
   {
      lastused = System.currentTimeMillis();
      return location == LOCATION_MEMORY;
   }

   public int getXLocation()
   {
      lastused = System.currentTimeMillis();
      return xloc;
   }

   public int getYLocation()
   {
      lastused = System.currentTimeMillis();
      return yloc;
   }

   public Point getLocation()
   {
      lastused = System.currentTimeMillis();
      return new Point(xloc, yloc);
   }

   public SampleModel getSampleModel()
   {
      lastused = System.currentTimeMillis();
      return sm;
   }

   int fixPixel(int p)
   {
      int[] samples = new int[3];
      int sampleCount;
      int temp;

      for (sampleCount = 0; sampleCount < 3; sampleCount++)
      {
         samples[sampleCount] =
            (byte) ((p & masks[sampleCount]) >> offsets[sampleCount]);
      }

      p = 0;

      temp = samples[0];
      samples[0] = samples[2];
      samples[2] = temp;

      for (sampleCount = 0; sampleCount < 3; sampleCount++)
      {
         p |= (samples[sampleCount] << offsets[sampleCount])
            & masks[sampleCount];
      }

      return p;
   }
   
   public long lastUsedAt()
   {
      return lastused;
   }
}