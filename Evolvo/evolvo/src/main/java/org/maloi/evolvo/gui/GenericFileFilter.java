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


package org.maloi.evolvo.gui;

import java.io.File;

public class GenericFileFilter extends javax.swing.filechooser.FileFilter
{
   private String[] Extensions;
   private String description;
   private String descriptionWithExtensions;
   private int id;

   public GenericFileFilter(String[] exts, String desc, int id)
   {
      this.id = id;

      StringBuffer tempString = new StringBuffer();

      Extensions = exts;
      description = desc;

      tempString.append(description);
      tempString.append("  ("); //$NON-NLS-1$
      for (int i = 0; i < Extensions.length - 1; i++)
      {
         tempString.append("*."); //$NON-NLS-1$
         tempString.append(Extensions[i]);
         tempString.append(", "); //$NON-NLS-1$
      }
      tempString.append("*."); //$NON-NLS-1$
      tempString.append(Extensions[Extensions.length - 1]);
      tempString.append(")"); //$NON-NLS-1$

      descriptionWithExtensions = tempString.toString();
   }

   public boolean accept(File f)
   {
      String filename;
      String extension;
      int length;
      int i;
      int j;

      if (f.isDirectory())
      {
         return true;
      }

      filename = f.getName();
      length = filename.length();
      i = filename.lastIndexOf('.');

      if (i > 0 && i < length - 1)
      {
         extension = filename.substring(i + 1).toLowerCase();

         for (j = 0; j < Extensions.length; j++)
         {
            if (extension.equalsIgnoreCase(Extensions[j]))
            {
               return true;
            }
         }
      }

      return false;
   }

   public String getDescription()
   {
      return descriptionWithExtensions;
   }

   public String getSimpleDescription()
   {
      return description;
   }

   public String[] getExtensions()
   {
      return Extensions;
   }

   public int getID()
   {
      return id;
   }
}
