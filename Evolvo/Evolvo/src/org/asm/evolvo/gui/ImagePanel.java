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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.util.Hashtable;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.asm.evolvo.expressiontree.renderer.RendererInterface;

public class ImagePanel extends JPanel implements ChangeListener
{
   Image scaledImage;
   Image finalImage;

   boolean finished = false;

   int width;
   int height;

   public ImagePanel(RendererInterface ri)
   {
      this(ri, null);
   }

   /**
    * Creates an image panel, drawing a scaled up instance of a thumbnail
    * of the RendererInterface's image to act as a placeholder until 
    * the RendererInterface is done drawing the full sized version.
    */
   public ImagePanel(RendererInterface ri, Image thumb)
   {
      width = ri.getWidth();
      height = ri.getHeight();

      if (thumb != null)
      {
         scaledImage = thumb;

         repaint();
      }

      finalImage = createImage(ri);

      ri.addChangeListener(this);

      prepareImage(finalImage, this);
   }

   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);

      if ((finished) && (finalImage != null))
      {
         g.drawImage(finalImage, 0, 0, this);
      }
      else if (scaledImage != null)
      {
         g.drawImage(scaledImage, 0, 0, width, height, this);
      }

      g.dispose();
   }

   public Dimension getPreferredSize()
   {
      return new Dimension(width, height);
   }

   public Image getImage()
   {
      return finalImage;
   }

   // Methods from ChangeListener
   public void stateChanged(ChangeEvent ce)
   {
      finished = true;

      repaint();
   }
}
