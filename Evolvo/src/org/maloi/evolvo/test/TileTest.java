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

package org.maloi.evolvo.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.maloi.evolvo.image.tiledimage.Tile;

public class TileTest extends Harness
{
   static Tile testTile;

   static final int TEST_WIDTH = 10;
   static final int TEST_HEIGHT = 10;

   static final int DATA_SIZE = Tile.TILE_SIZE * Tile.TILE_SIZE;

   static int foffset_inc = 3 * DATA_SIZE;

   static FileChannel cacheFile = getCacheFile().getChannel();

   public static void main(String[] args)
   {
      doTest(1, test1());
      doTest(2, test2());
      doTest(3, test3());
      doTest(4, test4());
      doTest(5, test5());
      doTest(6, test6());
      doTest(7, test7());
   }

   public static boolean test1()
   {
      int[] data = new int[DATA_SIZE];

      for (int i = 0; i < DATA_SIZE; i++)
      {
         data[i] = i;
      }

      // test case 1:
      // Tile at (0, 0) located at the beginning of the file

      // Creation
      testTile = new Tile(0, 0, cacheFile, 0);

      // Validation
      testTile.validate();

      // Setting data
      testTile.setData(data);

      // Expiring tile
      testTile.expire();

      // Re-validate tile
      testTile.validate();

      // check data
      int[] testData = testTile.getData();
      boolean failFlag = false;

      //showImages(1, data, testData);

      for (int i = 0; i < DATA_SIZE; i++)
      {
         if (testData[i] != data[i])
         {
            error =
               "Tile data does not match at "
                  + i
                  + "\n      from Tile:        "
                  + testData[i] + " (" + Integer.toHexString(testData[i]) + " )"
                  + "\n      from local cache: "
                  + data[i] + " (" + Integer.toHexString(data[i]) + " )";
            failFlag = true;
            break;
         }
      }

      return !failFlag;
   }

   public static boolean test2()
   {
      // test case 2
      // Tile at (5, 2) located at the appropriate location in the file

      int tileX = 5;
      int tileY = 2;

      int[] data = new int[DATA_SIZE];

      for (int i = 0; i < DATA_SIZE; i++)
      {
         data[i] = i;
      }

      // Creation
      testTile =
         new Tile(
            tileX,
            tileY,
            cacheFile,
            foffset_inc * ((Tile.TILE_SIZE * tileY) + tileX));

      // Validation
      testTile.validate();

      // Setting data
      testTile.setData(data);

      // Expiring tile
      testTile.expire();

      // Re-validate tile
      testTile.validate();

      // check data
      int[] testData = testTile.getData();
      boolean failFlag = false;

      for (int i = 0; i < DATA_SIZE; i++)
      {
         if (testData[i] != data[i])
         {
            error =
               "Tile data does not match at "
                  + i
                  + "\n      from Tile:        "
                  + testData[i]
                  + "\n      from local cache: "
                  + data[i];
            failFlag = true;
            break;
         }
      }

      return !failFlag;
   }

   public static boolean test3()
   {
      // test case 3
      // Tile at (3, 3) located at the appropriate location in the file
      // write to the tile using setPixel
      // read tile using getPixel

      int tileX = 3;
      int tileY = 3;

      int tileXOffset = tileX * Tile.TILE_SIZE;
      int tileYOffset = tileY * Tile.TILE_SIZE;

      int x;
      int y;
      int i = 0;

      int[] data = new int[DATA_SIZE];

      for (y = 0; y < Tile.TILE_SIZE; y++)
      {
         for (x = 0; x < Tile.TILE_SIZE; x++)
         {
            data[y * Tile.TILE_SIZE + x] = i;
            i++;
         }
      }

      // Creation
      testTile =
         new Tile(
            tileX,
            tileY,
            cacheFile,
            foffset_inc * ((Tile.TILE_SIZE * tileY) + tileX));

      // Validation
      testTile.validate();

      // Setting data
      for (y = 0; y < Tile.TILE_SIZE; y++)
      {
         for (x = 0; x < Tile.TILE_SIZE; x++)
         {
            testTile.setPixel(
               x + tileXOffset,
               y + tileYOffset,
               data[y * Tile.TILE_SIZE + x]);
         }
      }

      // Expiring tile
      testTile.expire();

      // Re-validate tile
      testTile.validate();

      // check data
      int[] testData = testTile.getData();
      boolean failFlag = false;

      for (i = 0; i < DATA_SIZE; i++)
      {
         if (testData[i] != data[i])
         {
            error =
               "From retrieved data array - tile data does not match at "
                  + i
                  + "\n      from Tile:        "
                  + testData[i]
                  + "\n      from local cache: "
                  + data[i];
            failFlag = true;
            break;
         }
      }

      // check data using getPixel
      for (y = 0; y < Tile.TILE_SIZE; y++)
      {
         for (x = 0; x < Tile.TILE_SIZE; x++)
         {
            if (testTile.getPixel(x + tileXOffset, y + tileYOffset)
               != data[y * Tile.TILE_SIZE + x])
            {
               error =
                  "From getPixel - tile data does not match at ("
                     + x
                     + ", "
                     + y
                     + ")"
                     + "\n      from Tile:        "
                     + testData[i]
                     + "\n      from local cache: "
                     + data[i];
               failFlag = true;
               break;
            }
            i++;
         }
      }

      return !failFlag;
   }

   public static boolean test4()
   {
      // test case 4
      // Tile at (2, 1) located at the appropriate location in the file
      // write to the tile using setPixels
      // read tile using getPixels

      int tileX = 2;
      int tileY = 1;

      int tileXOffset = tileX * Tile.TILE_SIZE;
      int tileYOffset = tileY * Tile.TILE_SIZE;

      int i = 0;

      int[] data = new int[DATA_SIZE];

      for (i = 0; i < DATA_SIZE; i++)
      {
         data[i] = i;
      }

      // Creation
      testTile =
         new Tile(
            tileX,
            tileY,
            cacheFile,
            foffset_inc * ((Tile.TILE_SIZE * tileY) + tileX));

      // Validation
      testTile.validate();

      // Setting data
      testTile.setPixels(
         tileXOffset,
         tileYOffset,
         Tile.TILE_SIZE,
         Tile.TILE_SIZE,
         data,
         0,
         Tile.TILE_SIZE);

      // Expiring tile
      testTile.expire();

      // Re-validate tile
      testTile.validate();

      // check data
      int[] testData = testTile.getData();
      boolean failFlag = false;

      for (i = 0; i < DATA_SIZE; i++)
      {
         if (testData[i] != data[i])
         {
            error =
               "From retrieved data array - tile data does not match at "
                  + i
                  + "\n      from Tile:        "
                  + testData[i]
                  + "\n      from local cache: "
                  + data[i];
            failFlag = true;
            break;
         }
      }

      // check data using getPixels
      testTile.getPixels(
         tileXOffset,
         tileYOffset,
         Tile.TILE_SIZE,
         Tile.TILE_SIZE,
         testData,
         0,
         Tile.TILE_SIZE);

      for (i = 0; i < DATA_SIZE; i++)
      {
         if (data[i] != testData[i])
         {
            error =
               "From getPixels - tile data does not match at "
                  + i
                  + "\n      from Tile:        "
                  + testData[i]
                  + "\n      from local cache: "
                  + data[i];
            failFlag = true;
            break;
         }
      }

      return !failFlag;
   }

   public static boolean test5()
   {
      // test case 5
      // Tile at (9, 9) located at the appropriate location in the file
      // write to the tile using setPixels, not full-tile
      // read tile using getPixels, not full-tile

      int tileX = 2;
      int tileY = 1;

      int tileXOffset = tileX * Tile.TILE_SIZE;
      int tileYOffset = tileY * Tile.TILE_SIZE;

      int anchorX = 5;
      int anchorY = 10;

      int i = 0;

      // Creation
      testTile =
         new Tile(
            tileX,
            tileY,
            cacheFile,
            foffset_inc * ((Tile.TILE_SIZE * tileY) + tileX));

      int[] data = new int[DATA_SIZE];
      int[] smallData = new int[50 * 50];

      for (i = 0; i < DATA_SIZE; i++)
      {
         data[i] = 0;
      }

      // Validation
      testTile.validate();

      // clear out the tile
      testTile.setData(data);

      for (i = 0; i < (50 * 50); i++)
      {
         smallData[i] = i;
      }

      for (int x = anchorX; x < (50 + anchorX); x++)
      {
         for (int y = anchorY; y < (50 + anchorY); y++)
         {
            data[y * Tile.TILE_SIZE + x] =
               smallData[(y - anchorY) * 50 + (x - anchorX)];
         }
      }

      // Setting data
      testTile.setPixels(
         tileXOffset + anchorX,
         tileYOffset + anchorY,
         50,
         50,
         smallData,
         0,
         50);

      // Expiring tile
      testTile.expire();

      // Re-validate tile
      testTile.validate();

      // check data
      int[] testData = testTile.getData();
      boolean failFlag = false;

      for (i = 0; i < DATA_SIZE; i++)
      {
         if (testData[i] != data[i])
         {
            error =
               "From retrieved data array - tile data does not match at "
                  + i
                  + "\n      from Tile:        "
                  + testData[i]
                  + "\n      from local cache: "
                  + data[i];
            return false;
         }
      }

      // check data using getPixels
      testTile.getPixels(
         tileXOffset + anchorX,
         tileYOffset + anchorY,
         50,
         50,
         testData,
         0,
         50);

      for (i = 0; i < (50 * 50); i++)
      {
         if (smallData[i] != testData[i])
         {
            error =
               "From getPixels - tile data does not match at "
                  + i
                  + "\n      from Tile:        "
                  + testData[i]
                  + "\n      from local cache: "
                  + data[i];
            return false;
         }
      }

      return true;
   }

   public static boolean test6()
   {
      // test case 5
      // Tile at (4, 3) located at the appropriate location in the file
      // write to the tile using setPixels, in horizontal strip
      // read tile using getPixels, in horizontal strip

      int tileX = 4;
      int tileY = 3;

      int tileXOffset = tileX * Tile.TILE_SIZE;
      int tileYOffset = tileY * Tile.TILE_SIZE;

      int anchorX = 2;
      int anchorY = 4;

      int i = 0;

      // Creation
      testTile =
         new Tile(
            tileX,
            tileY,
            cacheFile,
            foffset_inc * ((Tile.TILE_SIZE * tileY) + tileX));

      int[] data = new int[DATA_SIZE];
      int[] smallData = new int[50];

      for (i = 0; i < DATA_SIZE; i++)
      {
         data[i] = 0;
      }

      // Validation
      testTile.validate();

      // clear out the tile
      testTile.setData(data);

      for (i = 0; i < 50; i++)
      {
         smallData[i] = i;
      }

      for (int x = anchorX; x < (50 + anchorX); x++)
      {
         data[anchorY * Tile.TILE_SIZE + x] = smallData[x - anchorX];
      }

      // Setting data
      testTile.setPixels(
         tileXOffset + anchorX,
         tileYOffset + anchorY,
         50,
         1,
         smallData,
         0,
         50);

      // Expiring tile
      testTile.expire();

      // Re-validate tile
      testTile.validate();

      // check data
      int[] testData = testTile.getData();
      boolean failFlag = false;

      for (i = 0; i < DATA_SIZE; i++)
      {
         if (testData[i] != data[i])
         {
            error =
               "From retrieved data array - tile data does not match at "
                  + i
                  + "\n      from Tile:        "
                  + testData[i]
                  + "\n      from local cache: "
                  + data[i];
            return false;
         }
      }

      // check data using getPixels
      testTile.getPixels(
         tileXOffset + anchorX,
         tileYOffset + anchorY,
         50,
         1,
         testData,
         0,
         50);

      for (i = 0; i < 50; i++)
      {
         if (smallData[i] != testData[i])
         {
            error =
               "From getPixels - tile data does not match at "
                  + i
                  + "\n      from Tile:        "
                  + testData[i]
                  + "\n      from local cache: "
                  + data[i];
            return false;
         }
      }

      return true;
   }

   public static boolean test7()
   {
      // test case 7
      // Tile at (4, 3) located at the appropriate location in the file
      // write to the tile using setPixels, in vertical strip
      // read tile using getPixels, in vertical strip

      int tileX = 4;
      int tileY = 3;

      int tileXOffset = tileX * Tile.TILE_SIZE;
      int tileYOffset = tileY * Tile.TILE_SIZE;

      int anchorX = 10;
      int anchorY = 4;

      int i = 0;

      // Creation
      testTile =
         new Tile(
            tileX,
            tileY,
            cacheFile,
            foffset_inc * ((Tile.TILE_SIZE * tileY) + tileX));

      int[] data = new int[DATA_SIZE];
      int[] smallData = new int[50];

      for (i = 0; i < DATA_SIZE; i++)
      {
         data[i] = 0;
      }

      // Validation
      testTile.validate();

      // clear out the tile
      testTile.setData(data);

      for (i = 0; i < 50; i++)
      {
         smallData[i] = i;
      }

      for (int y = anchorY; y < (50 + anchorY); y++)
      {
         data[y * Tile.TILE_SIZE + anchorX] = smallData[y - anchorY];
      }

      // Setting data
      testTile.setPixels(
         tileXOffset + anchorX,
         tileYOffset + anchorY,
         1,
         50,
         smallData,
         0,
         1);

      // Expiring tile
      testTile.expire();

      // Re-validate tile
      testTile.validate();

      // check data
      int[] testData = testTile.getData();

      //showImages(7, data, testData);

      for (i = 0; i < DATA_SIZE; i++)
      {
         if (testData[i] != data[i])
         {
            error =
               "From retrieved data array - tile data does not match at "
                  + i
                  + "\n      from Tile:        "
                  + testData[i]
                  + "\n      from local cache: "
                  + data[i];
            return false;
         }
      }

      // check data using getPixels
      testTile.getPixels(
         tileXOffset + anchorX,
         tileYOffset + anchorY,
         1,
         50,
         testData,
         0,
         1);

      for (i = 0; i < 50; i++)
      {
         if (smallData[i] != testData[i])
         {
            error =
               "From getPixels - tile data does not match at "
                  + i
                  + "\n      from Tile:        "
                  + testData[i]
                  + "\n      from local cache: "
                  + data[i];
            return false;
         }
      }

      return true;
   }

   public static RandomAccessFile getCacheFile()
   {
      File tempFile;
      RandomAccessFile file;

      try
      {
         tempFile =
            File.createTempFile(
               "testCache",
               null,
               new File(System.getProperty("java.io.tmpdir")));
      }
      catch (IOException ioe)
      {
         System.err.println("Could not create temporary file:");
         ioe.printStackTrace();
         return null;
      }

      System.out.println("Cache file: " + tempFile);

      tempFile.deleteOnExit();

      try
      {
         file = new RandomAccessFile(tempFile, "rws");
      }
      catch (FileNotFoundException fnfe)
      {
         System.err.println("Cache file not found.");
         return null;
      }

      try
      {
         file.setLength(
            (TEST_WIDTH * TEST_WIDTH) * (Tile.TILE_SIZE * Tile.TILE_SIZE) * 3);
      }
      catch (IOException ioe)
      {
         System.err.println("Could not set file length: ");
         ioe.printStackTrace();
      }

      return file;
   }
}
