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

package org.maloi.evolvo.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

/**
 * @author amolloy
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TiledImage extends Image
{

   /**
    * Constructor for TiledImage.
    */
   public TiledImage()
   {
      super();
   }

   /**
    * @see java.awt.Image#getWidth(ImageObserver)
    */
   public int getWidth(ImageObserver observer)
   {
      return 0;
   }

   /**
    * @see java.awt.Image#getHeight(ImageObserver)
    */
   public int getHeight(ImageObserver observer)
   {
      return 0;
   }

   /**
    * @see java.awt.Image#getSource()
    */
   public ImageProducer getSource()
   {
      return null;
   }

   /**
    * @see java.awt.Image#getGraphics()
    */
   public Graphics getGraphics()
   {
      return null;
   }

   /**
    * @see java.awt.Image#getProperty(String, ImageObserver)
    */
   public Object getProperty(String name, ImageObserver observer)
   {
      return null;
   }

   /**
    * @see java.awt.Image#flush()
    */
   public void flush()
   {
   }

}
