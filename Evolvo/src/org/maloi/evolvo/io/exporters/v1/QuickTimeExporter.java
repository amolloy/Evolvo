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
 * 
 * NOTE: To use this, you must have ALL THREE of the following installed:
 * 
 * - Quicktime for Java
 * - Quicktime Still Images
 * - Quicktime Authoring
 * 
 * The annoying part is, if you have the first two, but not that last
 * (Authoring), QT will act like it's going to export the image just fine - 
 * until it actually gets to the part where it physically writes the image to
 * disk. Then it throws an exception complaining it can't find the handler or
 * some such stuff. 
 * 
 * Note: See if the above condition can be tested for when discovering this
 * plugin's "availability."
 */

package org.maloi.evolvo.io.exporters.v1;

import java.awt.Frame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.maloi.evolvo.gui.SystemConsole;
import org.maloi.evolvo.image.tiledimage.TiledImageInterface;

import quicktime.Errors;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.display.QTCanvas;
import quicktime.app.image.QTImageDrawer;
import quicktime.io.IOConstants;
import quicktime.io.QTFile;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.comp.ComponentDescription;
import quicktime.std.comp.ComponentIdentifier;
import quicktime.std.image.GraphicsExporter;

public class QuickTimeExporter
   implements ExporterInterface, StdQTConstants, IOConstants, Errors
{
   final String name = "QuickTime Exporter"; //$NON-NLS-1$

   int subTypes[];
   String descriptions[];
   boolean available = true;
   SystemConsole console = SystemConsole.getInstance();

   public QuickTimeExporter()
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
      if (format.equals("Photoshop")) //$NON-NLS-1$
      {
         return new String[] { "PSD" }; //$NON-NLS-1$
      }
      if (format.equals("JPEG")) //$NON-NLS-1$
      {
         return new String[] { "JPG", "JPEG" }; //$NON-NLS-1$ //$NON-NLS-2$
      }
      if (format.equals("MacPaint")) //$NON-NLS-1$
      {
         return new String[] { "MAC" }; //$NON-NLS-1$
      }
      if (format.equals("TIFF")) //$NON-NLS-1$
      {
         return new String[] { "TIF", "TIFF" }; //$NON-NLS-1$ //$NON-NLS-2$
      }
      if (format.equals("QuickTime Image")) //$NON-NLS-1$
      {
         return new String[] { "QIF" }; //$NON-NLS-1$
      }

      return new String[] { format };
   }

   public void write(RenderedImage inImage, int which, File f)
      throws IOException
   {
      if (!available)
      {
         return;
      }

      Image image;

      // see what sort of Image image is...
      // is it a TiledImage?
      if (inImage instanceof TiledImageInterface)
      {
         // why yes, it is... Use its getImage() method to get an Image from it
         image = ((TiledImageInterface)inImage).getImage();

      }
      // is it a BufferedImage?
      else if (inImage instanceof BufferedImage)
      {
         // yes - it can just be cast to Image
         image = (Image)inImage;
      }
      // is it just a plain old standard Image?
      else if (inImage instanceof Image)
      {
         // yes - again, just cast it to Image
         image = (Image)inImage;
      }
      // if it's anything else, we don't know what to do...
      else
      {
         console.println(
            "QuickTimeExporter: Don't know what to do with this: " + inImage); //$NON-NLS-1$
         return;
      }

      try
      {
         QTSession.open();

         int width = image.getWidth(null);
         int height = image.getHeight(null);

         Frame frame = new Frame();
         QTCanvas canvas = new QTCanvas();

         canvas.setVisible(false);
         frame.add(canvas);
         frame.addNotify();

         QDGraphics offscreen = new QDGraphics(new QDRect(0, 0, width, height));

         QTImageDrawer imageDrawer = new QTImageDrawer(image);

         imageDrawer.setRedrawing(true);
         canvas.setClient(imageDrawer, true);
         imageDrawer.setGWorld(offscreen);
         imageDrawer.redraw(null);

         QTFile exportFile = new QTFile(f.toString());
         GraphicsExporter ge = new GraphicsExporter(subTypes[which]);

         ge.setInputPixmap(offscreen);

         ge.requestSettings();

         ge.setOutputFile(exportFile);
         ge.doExport();

         QTSession.close();
      }
      catch (QTException e)
      {
         console.println("QTException"); //$NON-NLS-1$

         console.printStackTrace(e);
      }
      catch (Exception e)
      {
         console.printStackTrace(e);
      }
   }

   public boolean isAvailable()
   {
      try
      {
         Class testClass = Class.forName("quicktime.QTSession"); //$NON-NLS-1$
         testClass.getName(); // no real reason
      }
      catch (ClassNotFoundException e)
      {
      	console.println("Quicktime Exporter not available:"); //$NON-NLS-1$
      	console.printStackTrace(e);
      	
      	return false;
      }

      return true;
   }

   public void initialize()
   {
      Vector subTypeVector = new Vector(15);
      Vector descriptionVector = new Vector(15);

      try
      {
         QTSession.open();

         ComponentIdentifier ci = null;
         ComponentDescription cd = new ComponentDescription();
         ComponentDescription tempCD;

         cd.setType(graphicsExporterComponentType);
         cd.setSubType(0);
         cd.setManufacturer(0);
         cd.setFlags(0);
         cd.setMask(graphicsExporterIsBaseExporter);

         do
         {
            ci = ComponentIdentifier.find(ci, cd);

            if (ci != null)
            {
               tempCD = ci.getInfo();

               String name = tempCD.getName();
               int subType = tempCD.getSubType();

               if ((!name.equals("SGI")) && (!name.equals("MacPaint"))) //$NON-NLS-1$ //$NON-NLS-2$
               {
                  descriptionVector.add(name);
                  subTypeVector.add(new Integer(subType));
               }
            }
         }
         while (ci != null);

         Object temp[];

         temp = descriptionVector.toArray();
         descriptions = new String[temp.length];
         for (int i = 0; i < temp.length; i++)
         {
            descriptions[i] = (String)temp[i];
         }

         temp = subTypeVector.toArray();
         subTypes = new int[temp.length];
         for (int i = 0; i < temp.length; i++)
         {
            subTypes[i] = ((Integer)temp[i]).intValue();
         }

         QTSession.close();
      }
      catch (QTException e)
      {
         available = false;
         e.printStackTrace();
      }

   }
}
