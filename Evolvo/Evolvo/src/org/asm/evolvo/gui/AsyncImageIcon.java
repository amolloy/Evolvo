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

package org.asm.evolvo.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;

/**
 *  Implements an Icon, similar to ImageIcon, except that it does not block
 *  while waiting for the image to be drawn.
 */

public class AsyncImageIcon implements Icon
{
   Image i;
   int width;
   int height;

   public AsyncImageIcon(Image i, int width, int height)
   {
      this.i = i;
      this.width = width;
      this.height = height;
   }

   public int getIconWidth()
   {
      return width;
   }

   public int getIconHeight()
   {
      return height;
   }

   public void paintIcon(Component c, Graphics g, int x, int y)
   {
      if (i != null)
      {
         g.drawImage(i, x, y, c);
      }

      g.dispose();
   }
}
