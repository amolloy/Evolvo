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

package org.maloi.evolvo.test;

public class Harness
{
   static String error = ""; //$NON-NLS-1$

   public static void doTest(int test, boolean flag)
   {
      System.out.print("Test " + test + ": "); //$NON-NLS-1$ //$NON-NLS-2$
      if (flag)
      {
         System.out.println("Success"); //$NON-NLS-1$

      }
      else
      {
         System.out.println("Failure:\n" + error); //$NON-NLS-1$
      }
   }
   
   public static long getFreeMemory()
   {
      return Runtime.getRuntime().totalMemory()
         - Runtime.getRuntime().freeMemory();
   }
}
