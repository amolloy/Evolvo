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

package org.maloi.evolvo.io.exporters.v1;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageIOExporter implements ExporterInterface, ImageObserver
{
   final String name = "Java ImageIO Exporter";

   String descriptions[];

   public ImageIOExporter()
   {
   }

   public String getName()
   {
      return name;
   }

   public String[] getFormatDescriptions()
   {
      return descriptions;
   }

   public String[] getFormatExtensions(String format)
   {
      if (format.equals("jpg"))
      {
         return new String[] { "JPG", "JPEG" };
      }
      if (format.equals("png"))
      {
         return new String[] { "PNG" };
      }

      return new String[] { format };
   }

   public void write(Image i, int which, File f) throws IOException
   {
      BufferedImage bi =
         new BufferedImage(
            i.getWidth(this),
            i.getHeight(this),
            BufferedImage.TYPE_INT_RGB);

      Graphics g = bi.getGraphics();

      g.drawImage(i, 0, 0, this);

      ImageIO.write(bi, descriptions[which], f);
   }

   private String[] getWriterFormatNames()
   {
      ImageIO.scanForPlugins();

      String formatNames[] = ImageIO.getWriterFormatNames();

      String[] filtered = new String[formatNames.length];
      int i;
      int j = 0;
      int k;
      int newLen;
      boolean flag;

      // Eliminate duplicates
      for (i = 0; i < formatNames.length; i++)
      {
         flag = false; // No match
         for (k = 0; k < j; k++)
         {
            if (formatNames[i].compareToIgnoreCase(filtered[k]) == 0)
            {
               flag = true;
            }
         }
         if (!flag) // If there is no match
         {
            filtered[j++] = formatNames[i].toLowerCase(); // add the format
         }
      }

      formatNames = filtered;

      newLen = j;
      j = 0;

      // Now get rid of duplicate jpeg (same as jpg)
      for (i = 0; i < newLen; i++)
      {
         if (!formatNames[i].equals("jpeg"))
         {
            filtered[j++] = formatNames[i];
         }
      }

      // Finally, trim the array
      formatNames = new String[j];

      for (i = 0; i < j; i++)
      {
         formatNames[i] = filtered[i];
      }

      return formatNames;
   }

   public boolean isAvailable()
   {
      try
      {
         Class testClass = Class.forName("javax.imageio.ImageIO");
      }
      catch (ClassNotFoundException e)
      {
         return false;
      }

      return true;
   }

   public void initialize()
   {
      descriptions = getWriterFormatNames();

      for (int i = 0; i < descriptions.length; i++)
      {
         String exts[] = getFormatExtensions(descriptions[i]);
         Integer I = new Integer(i);
      }
   }

   public boolean imageUpdate(
      Image img,
      int infoflags,
      int x,
      int y,
      int width,
      int height)
   {
      return false;
   }
}
