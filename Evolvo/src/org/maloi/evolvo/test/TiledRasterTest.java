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

import java.awt.image.Raster;

import org.maloi.evolvo.image.tiledimage.Tile;
import org.maloi.evolvo.image.tiledimage.TiledRaster;

public class TiledRasterTest extends Harness
{
   public void run()
   {
      doTest(1, test1());
   }
   
   public boolean test1()
   {
      // create a TiledRaster made up of 1 tile (TILE_SIZE x TILE_SIZE)
      TiledRaster testRaster = new TiledRaster(Tile.TILE_SIZE, Tile.TILE_SIZE);
   
      Raster r = testRaster.getTile(0, 1);
      
      return true;
   }


   
   public static void main(String[] args)
   {
      TiledRasterTest theTest = new TiledRasterTest();
      
      theTest.run();
   }
}
