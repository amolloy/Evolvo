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
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.maloi.evolvo.gui.SystemConsole;
import org.maloi.evolvo.localization.MessageStrings;
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

   int numResidentTiles = 0; // how many tiles currently in memory (valid)

   FileChannel file; // file containing the disk cache
   File tempFile; // abstract file pointing to the temp file

   int width; // the image's width
   int height; // the image's height
   int tileWidth; // the image's width in tiles
   int tileHeight; // the image's height in tiles
   
   int tileCache[];
   int cacheCursor;
   
   // pool of arrays for the tiles
   int[][] cachePool;
   
   Tile tiles[]; // the actual tiles

   Raster parent = null;

   static GlobalSettings settings = GlobalSettings.getInstance();
   static SampleModel tileSampleModel;
   static int[] masks;
   static SystemConsole console = SystemConsole.getInstance();
   static int MAX_RESIDENT_TILES; // total number of tiles to allow in memory

   static {
      BufferedImage image;

      image =
         new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_RGB);

      tileSampleModel = image.getSampleModel();

      masks = ((SinglePixelPackedSampleModel)tileSampleModel).getBitMasks();
   }

   public TiledRaster(int width, int height)
   {
      this(width, height, 0, 0);
   }

   public TiledRaster(int width, int height, int startX, int startY)
   {
      super(
         new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
            .getSampleModel()
            .createCompatibleSampleModel(1, 1),
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

      MAX_RESIDENT_TILES = settings.getIntegerProperty("tilecache.maxtiles"); //$NON-NLS-1$

      tileCache = new int[MAX_RESIDENT_TILES];
      cachePool = new int[MAX_RESIDENT_TILES][];
      for (int i = 0; i < MAX_RESIDENT_TILES; i++)
      {
         tileCache[i] = -1;
         cachePool[i] = new int[TILE_SIZE * TILE_SIZE * 3];
      }
      
      cacheCursor = 0;
      
      try
      {
            tempFile = File.createTempFile("evo", //$NON-NLS-1$
                        null);
      }
      catch (IOException ioe)
      {
         console.println(MessageStrings.getString("TiledRaster.Could_not_create_temporary_file")); //$NON-NLS-1$
         console.printStackTrace(ioe);
      }

      // this doesn't appear to actually work...
      tempFile.deleteOnExit();

      RandomAccessFile raFile = null;

      try
      {
         raFile = new RandomAccessFile(tempFile, "rwd"); //$NON-NLS-1$
      }
      catch (FileNotFoundException fnfe)
      {
         console.println(MessageStrings.getString("TiledRaster.Cache_file_not_found.")); //$NON-NLS-1$
         console.printStackTrace(fnfe);
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
         console.println(MessageStrings.getString("TiledRaster.Could_not_set_file_length")); //$NON-NLS-1$
         console.printStackTrace(ioe);
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

   /**
    * Creates a single-tile TiledRaster. Note that it does not copy the given
    * tile, but simply references it directly.
    * 
    * @param tile
    */
   protected TiledRaster(Tile tile)
   {
      super(
         new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_RGB)
            .getSampleModel(),
         new Point(tile.xloc, tile.yloc));

      minX = 0;
      minY = 0;
      width = TILE_SIZE;
      height = TILE_SIZE;
      numBands = 3;
      dataBuffer = null;
      parent = null;
      sampleModelTranslateX = tile.xloc;
      sampleModelTranslateY = tile.yloc;

      tileWidth = 1;
      tileHeight = 1;

      tiles = new Tile[1];

      tiles[0] = tile;

      tileCache = new int[0];
      tileCache[0] = 1;
      cacheCursor = 0;
   }

   synchronized void validateTile(int tilex, int tiley)
   {
      int whichTile = (tiley * tileWidth) + tilex;

      if (tiles[whichTile].isValid())
      {
         return;
      }

		// first expire any tile that might already be in this spot...
		if (tileCache[cacheCursor] != -1)
		{
			tiles[tileCache[cacheCursor]].expire();
		}

      // The tile isn't in memory, so we need to validate it, then make sure we 
      // haven't loaded too many tiles
      tiles[whichTile].validate(cachePool[cacheCursor]);
      
      tileCache[cacheCursor] = whichTile;

      cacheCursor++;
      if (cacheCursor == MAX_RESIDENT_TILES)
      {
         cacheCursor = 0;
      }
   }

   public void flush()
   {
      // release all of the tiles...      
      tiles = null;

      // and delete the file
      tempFile.delete();

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
      BufferedImage bi =
         new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

      bi.setRGB(
         childMinX,
         childMinY,
         width,
         height,
         getData(parentX, parentY, width, height, null),
         0,
         0);

      DataBuffer db = bi.getRaster().getDataBuffer();

      Raster r =
         Raster.createRaster(
            bi.getSampleModel(),
            db,
            new Point(childMinX, childMinY));

      return r;
   }

   public Raster getTile(int tileX, int tileY)
   {
      Tile theTile = tiles[tileY * tileWidth + tileX];

      validateTile(tileX, tileY);

      Point location = theTile.getLocation();

      WritableRaster returnRaster =
         Raster.createPackedRaster(
            DataBuffer.TYPE_INT,
            TILE_SIZE,
            TILE_SIZE,
            masks,
            location);

      returnRaster.setDataElements(
         location.x,
         location.y,
         TILE_SIZE,
         TILE_SIZE,
         theTile.getData());

      return returnRaster;
   }

   public Raster getParent()
   {
      return parent;
   }
}
