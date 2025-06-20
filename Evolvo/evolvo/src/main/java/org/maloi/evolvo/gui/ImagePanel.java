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

import java.awt.Dimension;
import java.awt.image.RenderedImage;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import org.maloi.evolvo.expressiontree.renderer.RendererInterface;

public abstract class ImagePanel extends JPanel implements ChangeListener
{
   private static final long serialVersionUID = -7172562889022425835L;

   @Override
   public Dimension getPreferredSize()
   {
      return null;
   }
   
   public RenderedImage getImage()
   {
      return null;
   }
   
   public void flush()
   {
   }   
   
	public void replaceImage(RendererInterface ri)
	{
	}
}
