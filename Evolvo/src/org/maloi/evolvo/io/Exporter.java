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

package org.maloi.evolvo.io;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Vector;

import org.maloi.evolvo.gui.SystemConsole;
import org.maloi.evolvo.io.exporters.v1.ExporterInterface;
import org.maloi.evolvo.localization.MessageStrings;
import org.maloi.evolvo.settings.GlobalSettings;

public class Exporter
{
   static String pluginList[] = { "org.maloi.evolvo.io.exporters.v1.ImageIOExporter", //$NON-NLS-1$
      "org.maloi.evolvo.io.exporters.v1.QuickTimeExporter" }; //$NON-NLS-1$

   static String exporterNames[];

   static Class theExporter;
   static Object theExporterObject;
   static Method getName;
   static Method getFormatDescriptions;
   static Method getFormatExtensions;
   static Method write;

   static int exporterID = 0;

   static SystemConsole console = SystemConsole.getInstance();

   static boolean available = false;

   static {
      init();
   }

   public static void init()
   {
      GlobalSettings settings = GlobalSettings.getInstance();
      String preferredPlugin = settings.getStringProperty("usePlugin"); //$NON-NLS-1$

      if (preferredPlugin == null)
      {
         preferredPlugin = pluginList[0];
      }

      init(preferredPlugin);
   }

   public static void init(String preferredPlugin)
   {
      GlobalSettings settings = GlobalSettings.getInstance();
      String userPlugins = settings.getStringProperty("plugins"); //$NON-NLS-1$
      Vector pluginVector = new Vector();
      int i;

      for (i = 0; i < pluginList.length; i++)
      {
         pluginVector.add(pluginList[i]);
      }

      if (userPlugins != null)
      {
         StringTokenizer tokenizer = new StringTokenizer(userPlugins, ",", false); //$NON-NLS-1$

         for (; tokenizer.hasMoreTokens();)
         {
            StringBuffer nameBuffer = new StringBuffer(tokenizer.nextToken());

            nameBuffer.insert(0, "FileIO.ExporterPlugins.v1."); //$NON-NLS-1$

            pluginVector.add(nameBuffer);
         }
      }

      pluginVector = checkPlugins(pluginVector);

      if (pluginVector.size() == 0)
      {
         available = false;
         console.println(MessageStrings.getString("Exporter.No_exporters_available")); //$NON-NLS-1$
         return;
      }

      Object tempArray[] = pluginVector.toArray();

      pluginList = new String[tempArray.length];

      for (i = 0; i < tempArray.length; i++)
      {
         pluginList[i] = (String)tempArray[i];
      }

      for (i = 0; i < exporterNames.length; i++)
      {
         if (exporterNames[i].compareTo(preferredPlugin) == 0)
         {
            exporterID = i;
            break;
         }
      }

      try
      {
         theExporter = Class.forName(pluginList[exporterID]);

         theExporterObject = theExporter.newInstance();

         Method initialize = theExporter.getDeclaredMethod("initialize", new Class[] { //$NON-NLS-1$
         });

         initialize.invoke(theExporterObject, null);

         getName = theExporter.getDeclaredMethod("getName", new Class[] { //$NON-NLS-1$
         });

         getFormatDescriptions = theExporter.getDeclaredMethod("getFormatDescriptions", new Class[] { //$NON-NLS-1$
         });

            getFormatExtensions = theExporter.getDeclaredMethod("getFormatExtensions", //$NON-NLS-1$
   new Class[] { String.class });

            write = theExporter.getDeclaredMethod("write", //$NON-NLS-1$
   new Class[] { RenderedImage.class, int.class, File.class });

         available = true;
      }
      catch (Exception e)
      {
         console.printStackTrace(e);
      }
   }

   public static String[] getFormatDescriptions()
   {
      if (available)
      {
         try
         {
            Object retval =
               getFormatDescriptions.invoke(theExporterObject, null);

            return (String[])retval;
         }
         catch (Exception e)
         {
            console.printStackTrace(e);
         }
      }

      return null;
   }

   public static String[] getFormatExtensions(String writer)
   {
      if (available)
      {
         try
         {
            Object retval =
               getFormatExtensions.invoke(
                  theExporterObject,
                  new Object[] { writer });

            return (String[])retval;
         }
         catch (Exception e)
         {
            console.printStackTrace(e);
         }
      }

      return null;
   }

   public static void write(RenderedImage i, int which, File f)
      throws IOException
   {
      if (available)
      {
         try
         {
            write.invoke(
               theExporterObject,
               new Object[] { i, new Integer(which), f });
         }
         catch (Exception e)
         {
            console.printStackTrace(e);
         }
      }
   }

   static Vector checkPlugins(Vector pluginVector)
   {
      int i;
      int j;
      Vector nameVector = new Vector(15);
      Vector trimmed = new Vector(15);
      boolean flag;

      for (i = 0; i < pluginVector.size(); i++)
      {
         try
         {
            Class pluginClass =
               Class.forName((String)pluginVector.elementAt(i));

            Class interfaces[] = pluginClass.getInterfaces();

            flag = false;
            for (j = 0; j < interfaces.length; j++)
            {
               if (interfaces[j] == ExporterInterface.class)
               {
                  flag = true;
                  continue;
               }
            }

            if (flag)
            {
               Object pluginObject = pluginClass.newInstance();

               Method isAvailable = pluginClass.getDeclaredMethod("isAvailable", new Class[] { //$NON-NLS-1$
               });

               Boolean avail = (Boolean)isAvailable.invoke(pluginObject, null);

               if (avail.booleanValue())
               {
                  Method getName = pluginClass.getDeclaredMethod("getName", new Class[] { //$NON-NLS-1$
                  });

                  nameVector.add(getName.invoke(pluginObject, null));
                  trimmed.add(pluginVector.elementAt(i));
               }
            }
         }
         catch (NoClassDefFoundError e)
         {
         }
         catch (ClassNotFoundException e)
         {
         }
         catch (InstantiationException e)
         {
         }
         catch (IllegalAccessException e)
         {
         }
         catch (NoSuchMethodException e)
         {
         }
         catch (InvocationTargetException e)
         {
         }
         catch (ClassCastException e)
         {
         }
      }

      Object tempArray[] = nameVector.toArray();

      exporterNames = new String[tempArray.length];

      for (i = 0; i < exporterNames.length; i++)
      {
         exporterNames[i] = (String)tempArray[i];
      }

      return trimmed;
   }

   public static String[] getExporterNames()
   {
      return exporterNames;
   }

   public static void rescan()
   {
      init();
   }

   public static boolean isAvailable()
   {
      return available;
   }

   public static void setPlugin(int i)
   {
      if ((i > 0) && (i < exporterNames.length))
      {
         init(exporterNames[i]);
      }
   }

   public static void setPlugin(String p)
   {
      init(p);
   }
}
