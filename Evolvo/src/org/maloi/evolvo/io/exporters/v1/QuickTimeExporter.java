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
 * TODO: See if the above condition can be tested for when discovering this
 * plugin's "availability."
 */

package org.maloi.evolvo.io.exporters.v1;

import java.awt.Frame;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

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
   final String name = "QuickTime Exporter";

   int subTypes[];
   String descriptions[];
   boolean available = true;

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
      if (format.equals("Photoshop"))
      {
         return new String[] { "PSD" };
      }
      if (format.equals("JPEG"))
      {
         return new String[] { "JPG", "JPEG" };
      }
      if (format.equals("MacPaint"))
      {
         return new String[] { "MAC" };
      }
      if (format.equals("TIFF"))
      {
         return new String[] { "TIF", "TIFF" };
      }
      if (format.equals("QuickTime Image"))
      {
         return new String[] { "QIF" };
      }

      return new String[] { format };
   }

   public void write(Image i, int which, File f) throws IOException
   {
      if (!available)
      {
         return;
      }

      try
      {
         QTSession.open();

         int width = i.getWidth(null);
         int height = i.getHeight(null);

         Frame frame = new Frame();
         QTCanvas canvas = new QTCanvas();

         canvas.setVisible(false);
         frame.add(canvas);
         frame.addNotify();

         QDGraphics offscreen = new QDGraphics(new QDRect(0, 0, width, height));

         QTImageDrawer imageDrawer = new QTImageDrawer(i);

         imageDrawer.setRedrawing(true);
         canvas.setClient(imageDrawer, true);
         imageDrawer.setGWorld(offscreen);
         imageDrawer.redraw(null);

         QTFile exportFile = new QTFile(f.toString());
         GraphicsExporter ge = new GraphicsExporter(subTypes[which]);

         ge.setInputPixmap(offscreen);

         ge.requestSettings();

         ge.setOutputFile(exportFile);
         int size = ge.doExport();

         QTSession.close();
      }
      catch (QTException e)
      {
         System.err.println("QTException");

         e.printStackTrace();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public boolean isAvailable()
   {
      try
      {
         Class testClass = Class.forName("quicktime.QTSession");
      }
      catch (ClassNotFoundException e)
      {
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

               if ((!name.equals("SGI")) && (!name.equals("MacPaint")))
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
            descriptions[i] = (String) temp[i];
         }

         temp = subTypeVector.toArray();
         subTypes = new int[temp.length];
         for (int i = 0; i < temp.length; i++)
         {
            subTypes[i] = ((Integer) temp[i]).intValue();
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
