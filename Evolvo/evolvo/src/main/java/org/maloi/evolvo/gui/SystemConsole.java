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

import org.maloi.evolvo.localization.MessageStrings;

public class SystemConsole extends TextDialog
{
   private static final long serialVersionUID = -8024335516260738480L;

   static SystemConsole _instance;
   
   static int lastRow = 0;

   private SystemConsole()
   {
      super(MessageStrings.getString("SystemConsole.Initializing_system_console.")); //$NON-NLS-1$
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
       print("\n"); //$NON-NLS-1$
       scrollToRow(++lastRow);
   }
   
   public void printStackTrace(Exception e)
   {
      StackTraceElement[] elements = e.getStackTrace();
     
      println(e.toString());

      if (elements == null)
      {
      	println("No stack trace."); //$NON-NLS-1$
      	return;
      }
      
		int length = elements.length;
      
      for (int i = 0; i < length; i++)
      {
         println(MessageStrings.getString("SystemConsole._t_3") + elements[i].toString()); //$NON-NLS-1$
      }
   }
}
