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
package org.maloi.evolvo.gui;

public class SystemConsole extends TextDialog
{
   static SystemConsole _instance;

   public SystemConsole()
   {
      super("Initializing system console.");
   }

   static public SystemConsole getInstance()
   {
      if (_instance == null)
      {
         _instance = new SystemConsole();
      }

      return _instance;
   }
   
   public void print(String s)
   {
       textArea.append(s);
   }
   
   public void println(String s)
   {
       print(s);
       print("\n");
   }
   
   public void printStackTrace(Exception e)
   {
      StackTraceElement[] elements = e.getStackTrace();
      int length = elements.length;
      
      println(e.toString());
      
      for (int i = 0; i < length; i++)
      {
         println("\t" + elements[i].toString());
      }
   }
}
