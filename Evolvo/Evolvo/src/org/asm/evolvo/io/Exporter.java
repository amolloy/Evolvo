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

package org.asm.evolvo.io;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Vector;

import org.asm.evolvo.io.exporters.v1.ExporterInterface;
import org.asm.evolvo.settings.GlobalSettings;

public class Exporter
{
   static String pluginList[] =
      {
         "org.asm.evolvo.io.exporters.v1.QuickTimeExporter",
         "org.asm.evolvo.io.exporters.v1.ImageIOExporter" };

   static String exporterNames[];

   static Class theExporter;
   static Object theExporterObject;
   static Method getName;
   static Method getFormatDescriptions;
   static Method getFormatExtensions;
   static Method write;

   static int exporterID = 0;

   static boolean available = false;

   static {
      init();
   }

   public static void init()
   {
      GlobalSettings settings = GlobalSettings.getInstance();
      String preferredPlugin = settings.getStringProperty("usePlugin");

      if (preferredPlugin == null)
      {
         preferredPlugin = pluginList[0];
      }

      init(preferredPlugin);
   }

   public static void init(String preferredPlugin)
   {
      GlobalSettings settings = GlobalSettings.getInstance();
      String userPlugins = settings.getStringProperty("plugins");
      Vector pluginVector = new Vector();
      int i;

      for (i = 0; i < pluginList.length; i++)
      {
         pluginVector.add(pluginList[i]);
      }

      if (userPlugins != null)
      {
         StringTokenizer tokenizer =
            new StringTokenizer(userPlugins, ",", false);

         for (; tokenizer.hasMoreTokens();)
         {
            StringBuffer nameBuffer = new StringBuffer(tokenizer.nextToken());

            nameBuffer.insert(0, "FileIO.ExporterPlugins.v1.");

            pluginVector.add(nameBuffer);
         }
      }

      pluginVector = checkPlugins(pluginVector);

      if (pluginVector.size() == 0)
      {
         available = false;
         System.err.println("No exporters available!");
         return;
      }

      Object tempArray[] = pluginVector.toArray();

      pluginList = new String[tempArray.length];

      for (i = 0; i < tempArray.length; i++)
      {
         pluginList[i] = (String) tempArray[i];
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

         Method initialize =
            theExporter.getDeclaredMethod("initialize", new Class[] {
         });

         initialize.invoke(theExporterObject, null);

         getName = theExporter.getDeclaredMethod("getName", new Class[] {
         });

         getFormatDescriptions =
            theExporter
               .getDeclaredMethod("getFormatDescriptions", new Class[] {
         });

         getFormatExtensions =
            theExporter.getDeclaredMethod(
               "getFormatExtensions",
               new Class[] { String.class });

         write =
            theExporter.getDeclaredMethod(
               "write",
               new Class[] { Image.class, int.class, File.class });

         available = true;
      }
      catch (Exception e)
      {
         e.printStackTrace();
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

            return (String[]) retval;
         }
         catch (Exception e)
         {
            e.printStackTrace();
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

            return (String[]) retval;
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }

      return null;
   }

   public static void write(Image i, int which, File f) throws IOException
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
            //	    e.getCause().printStackTrace();
            e.printStackTrace();
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
               Class.forName((String) pluginVector.elementAt(i));

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

               Method isAvailable =
                  pluginClass.getDeclaredMethod("isAvailable", new Class[] {
               });

               Boolean avail = (Boolean) isAvailable.invoke(pluginObject, null);

               if (avail.booleanValue())
               {
                  Method getName =
                     pluginClass.getDeclaredMethod("getName", new Class[] {
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
         exporterNames[i] = (String) tempArray[i];
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
