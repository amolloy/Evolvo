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

package org.maloi.evolvo.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.operators.OperatorList;
import org.maloi.evolvo.localization.MessageStrings;
import org.maloi.evolvo.resources.Constants;

/**
 * Stores all settings information for this application.  Most of the 
 * information is stored in a Properties object, but there is also an array 
 * of operatorInterfaces.
 *
 * This class is a singleton.
 */
public class GlobalSettings
{
   Properties props;
   OperatorInterface ops[];
   static GlobalSettings _instance;

   /**
    *  Static method to retrieve the singleton's instance
    */
   public static GlobalSettings getInstance()
   {
      if (_instance == null)
      {
         _instance = new GlobalSettings();
      }
      return _instance;
   }

   /**
    *  globalSettings' constructor is protected to ensure clients can only
    *  get an instance of it via the getInstance() method.
    */
   protected GlobalSettings()
   {
      ops = OperatorList.getAllOperators();
      props = new Properties();
      try
      {
         FileInputStream fin = new FileInputStream("evolution.ini"); //$NON-NLS-1$
         props.load(fin);
         fin.close();
      }
      catch (Exception e)
      {
         int index;
         for (index = 0; index < ops.length; index++)
         {
            props.put(Constants.operatorPrefix + ops[index].getName(), "1.0"); //$NON-NLS-1$
         }
         props.put("complexity", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("depreciation", "0.25"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("variable.probability", "0.75"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("variable.x", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("variable.y", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("variable.r", "0.0"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("variable.theta", "0.0"); //$NON-NLS-1$ //$NON-NLS-2$

         props.put("render.width", "640"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("render.height", "480"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("render.width.units", "pixels"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("render.height.units", "pixels"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("render.width.pixels", "640"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("render.height.pixels", "480"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("render.resolution.units", "pixels/in"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("render.resolution", "72"); //$NON-NLS-1$ //$NON-NLS-2$

         props.put("export.bmp.compressed", "true"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("export.png.interlaced", "false"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("export.pnm.mode", "raw"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("export.tiff.compression", "uncompressed"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("mutate.new_expression", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("mutate.scalar_change_value", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("mutate.to_variable", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("mutate.to_scalar", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("mutate.change_function", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("mutate.new_expression_arg", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("mutate.become_arg", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("mutate.arg_to_child_arg", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("mutate.change", "0.2"); //$NON-NLS-1$ //$NON-NLS-2$

         props.put("tilecache.maxtiles", "48"); //$NON-NLS-1$ //$NON-NLS-2$
         props.put("tilecache.location", System.getProperty("java.io.tmpdir")); //$NON-NLS-1$ //$NON-NLS-2$

         try
         {
            FileOutputStream fout = new FileOutputStream("evolution.ini"); //$NON-NLS-1$
            props.store(fout, "Evolution Properties"); //$NON-NLS-1$
            fout.close();
         }
         catch (IOException ioe)
         {
            JOptionPane.showMessageDialog(null, MessageStrings.getString("GlobalSettings.Cannot_create_settings_file.")); //$NON-NLS-1$
            System.exit(1);
         }
      }
   }

   /** Returns the list of operators available. */
   public OperatorInterface[] getOperators()
   {
      return ops;
   }

   /** Returns the Properties object. */
   public Properties getProperties()
   {
      return props;
   }

   /** Sets the Properties object to that which was passed in. */
   public void setProperties(Properties p)
   {
      props = p;
   }

   /** Returns a property from the Properties object as an Object, with a 
    *  default value of "0". 
    */
   public Object getProperty(String s)
   {
      return props.getProperty(s, "0"); //$NON-NLS-1$
   }

   /** Returns a propery from the Properties object as a String, with default 
    *  value of "". 
    */
   public String getStringProperty(String s)
   {
      return props.getProperty(s, ""); //$NON-NLS-1$
   }

   /** Returns a property from the Properties object as a double. */
   public double getDoubleProperty(String s)
   {
      return Double.parseDouble((String)getProperty(s));
   }

   /** Returns a property from the Properties object as an int. */
   public int getIntegerProperty(String s)
   {
      return Integer.parseInt((String)getProperty(s));
   }

   /** Sets a property in the Properties object. */
   public void setProperty(String k, String v)
   {
      props.put(k, v);
   }

   /** Sets a double property in the Properties object */
   public void setDoubleProperty(String k, double v)
   {
      props.put(k, new Double(v).toString());
   }

   /** Sets an integer property in the Properties object */
   public void setIntegerProperty(String k, int v)
   {
      props.put(k, new Integer(v).toString());
   }

   public void setBooleanProperty(String k, boolean b)
   {
      Boolean B = new Boolean(b);
      props.put(k, B.toString());
   }

   public boolean getBooleanProperty(String k)
   {
      Boolean b = new Boolean((String)props.get(k));

      return b.booleanValue();
   }

   /** Stores the properties object in a file called evolution.ini.
     * @throws IOException, FileNotFoundException */
   public void storeProperties() throws IOException, FileNotFoundException
   {
      storeProperties("evolution.ini"); //$NON-NLS-1$
   }

   /** Stores the Properties object in fn.
     * @throws IOException, FileNotFoundException */
   public void storeProperties(String fn)
      throws IOException, FileNotFoundException
   {
      FileOutputStream fout = new FileOutputStream(fn);
      props.store(fout, MessageStrings.getString("GlobalSettings.Evolution_Properties_69")); //$NON-NLS-1$
      fout.close();
   }

   /** Loads a Properties object from a file called evolution.ini.
     * @throws IOException, FileNotFoundException */
   public void loadProperties() throws IOException, FileNotFoundException
   {
      loadProperties("evolution.ini"); //$NON-NLS-1$
   }

   /** Loads a Properties object from a file named in fn.
     * @throws IOException, FileNotFoundException */
   public void loadProperties(String fn)
      throws IOException, FileNotFoundException
   {
      FileInputStream fin = new FileInputStream(fn);
      props.load(fin);
      fin.close();
   }
}
