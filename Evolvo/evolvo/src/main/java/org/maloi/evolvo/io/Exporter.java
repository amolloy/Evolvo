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


package org.maloi.evolvo.io;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.ArrayList;

import org.maloi.evolvo.gui.SystemConsole;
import org.maloi.evolvo.io.exporters.v1.ExporterInterface;
import org.maloi.evolvo.localization.MessageStrings;
import org.maloi.evolvo.settings.GlobalSettings;

public class Exporter
{
   static String pluginList[] = { "org.maloi.evolvo.io.exporters.v1.ImageIOExporter", //$NON-NLS-1$
      "org.maloi.evolvo.io.exporters.v1.QuickTimeExporter" }; //$NON-NLS-1$

   static String exporterNames[];

   static Class<?> theExporter;
   static Object theExporterObject;
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
      ArrayList<String> pluginArrayList = new ArrayList<>();
      int i;

      for (i = 0; i < pluginList.length; i++)
      {
         pluginArrayList.add(pluginList[i]);
      }

      if (userPlugins != null)
      {
         StringTokenizer tokenizer = new StringTokenizer(userPlugins, ",", false); //$NON-NLS-1$

         for (; tokenizer.hasMoreTokens();)
         {
            StringBuilder nameBuffer = new StringBuilder(tokenizer.nextToken());

            nameBuffer.insert(0, "FileIO.ExporterPlugins.v1."); //$NON-NLS-1$

            pluginArrayList.add(nameBuffer.toString());
         }
      }

      pluginArrayList = checkPlugins(pluginArrayList);

      if (pluginArrayList.isEmpty())
      {
         available = false;
         console.println(MessageStrings.getString("Exporter.No_exporters_available")); //$NON-NLS-1$
         return;
      }

      Object tempArray[] = pluginArrayList.toArray();

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

         theExporterObject = theExporter.getDeclaredConstructor().newInstance();

         Method initialize = theExporter.getDeclaredMethod("initialize", new Class<?>[] { //$NON-NLS-1$
         });

         initialize.invoke(theExporterObject);

         getFormatDescriptions = theExporter.getDeclaredMethod("getFormatDescriptions", new Class<?>[] { //$NON-NLS-1$
         });

            getFormatExtensions = theExporter.getDeclaredMethod("getFormatExtensions", //$NON-NLS-1$
   new Class<?>[] { String.class });

            write = theExporter.getDeclaredMethod("write", //$NON-NLS-1$
   new Class<?>[] { RenderedImage.class, int.class, File.class });

         available = true;
      }
      catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e)
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
               getFormatDescriptions.invoke(theExporterObject);

            return (String[])retval;
         }
         catch (IllegalAccessException | InvocationTargetException e)
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
         catch (IllegalAccessException | InvocationTargetException e)
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
               new Object[] { i, which, f });
         }
         catch (IllegalAccessException | InvocationTargetException e)
         {
            console.printStackTrace(e);
         }
      }
   }

   static ArrayList<String> checkPlugins(ArrayList<String> pluginArrayList)
   {
      int i;
      int j;
      ArrayList<String> nameArrayList = new ArrayList<>(15);
      ArrayList<String> trimmed = new ArrayList<>(15);
      boolean flag;

      for (i = 0; i < pluginArrayList.size(); i++)
      {
         try
         {
            Class<?> pluginClass =
               Class.forName(pluginArrayList.get(i));

            Class<?> interfaces[] = pluginClass.getInterfaces();

            flag = false;
            for (j = 0; j < interfaces.length; j++)
            {
               if (interfaces[j] == ExporterInterface.class)
               {
                  flag = true;
                  break;
               }
            }

            if (flag)
            {
               Object pluginObject = pluginClass.getDeclaredConstructor().newInstance();

               Method isAvailable = pluginClass.getDeclaredMethod("isAvailable", new Class<?>[] { //$NON-NLS-1$
               });

               Boolean avail = (Boolean)isAvailable.invoke(pluginObject);

               if (avail)
               {
                  Method getName = pluginClass.getDeclaredMethod("getName", new Class<?>[] { //$NON-NLS-1$
                  });

                  nameArrayList.add((String)getName.invoke(pluginObject));
                  trimmed.add(pluginArrayList.get(i));
               }
            }
         }
         catch (NoClassDefFoundError | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassCastException e)
         {
         }
      }

      Object tempArray[] = nameArrayList.toArray();

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
