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

import java.awt.Component;

import javax.swing.ProgressMonitor;

public class CustomProgressMonitor
{
   ProgressMonitor pm;
   int progress;

   public CustomProgressMonitor(
      Component parentComponent,
      Object message,
      String note,
      int min,
      int max)
   {
      pm = new ProgressMonitor(parentComponent, message, note, min, max);
      progress = 0;
   }

   public void close()
   {
      synchronized (pm)
      {
         pm.close();
      }
   }

   public int getMaximum()
   {
      synchronized (pm)
      {
         return pm.getMaximum();
      }
   }

   public int getMillisToDecideToPopup()
   {
      synchronized (pm)
      {
         return pm.getMillisToDecideToPopup();
      }
   }

   public int getMillisToPopup()
   {
      synchronized (pm)
      {
         return pm.getMillisToPopup();
      }
   }

   public int getMinimum()
   {
      synchronized (pm)
      {
         return pm.getMinimum();
      }
   }

   public String getNote()
   {
      synchronized (pm)
      {
         return pm.getNote();
      }
   }

   public boolean isCanceled()
   {
      synchronized (pm)
      {
         return pm.isCanceled();
      }
   }

   public void setMaximum(int m)
   {
      synchronized (pm)
      {
         pm.setMaximum(m);
      }
   }

   public void setMillisToDecideToPopup(int millisToDecideToPopup)
   {
      synchronized (pm)
      {
         pm.setMillisToDecideToPopup(millisToDecideToPopup);
      }
   }

   public void setMillisToPopup(int millisToPopup)
   {
      synchronized (pm)
      {
         pm.setMillisToPopup(millisToPopup);
      }
   }

   public void setMinimum(int m)
   {
      synchronized (pm)
      {
         pm.setMinimum(m);
      }
   }

   public void setNote(String note)
   {
      synchronized (pm)
      {
         pm.setNote(note);
      }
   }

   public void setProgress(int nv)
   {
      synchronized (pm)
      {
         progress = nv;
         pm.setProgress(nv);
      }
   }

   public ProgressMonitor getProgressMonitor()
   {
      synchronized (pm)
      {
         return pm;
      }
   }

   public void incrementProgress(int i)
   {
      synchronized (pm)
      {
         progress += i;
         pm.setProgress(progress);
      }
   }
}
