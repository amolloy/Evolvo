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

package org.maloi.evolvo.image.tiledimage;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

import org.maloi.evolvo.gui.SystemConsole;
import org.maloi.evolvo.image.TiledImage;

public class TiledImageGraphics extends Graphics
{
   TiledImage image;
   SystemConsole console = SystemConsole.getInstance();

   public TiledImageGraphics(TiledImage image)
   {
      this.image = image;

      console.println("TiledImageGraphics.constructor(" + image + ");"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * @see java.awt.Graphics#create()
    */
   public Graphics create()
   {
      console.println("TiledImageGraphics.create();"); //$NON-NLS-1$

      return this;
   }

   /**
    * @see java.awt.Graphics#translate(int, int)
    */
   public void translate(int x, int y)
   {
      console.println("TiledImageGraphics.translate(" + x + ", " + y + ");"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
   }

   /**
    * @see java.awt.Graphics#getColor()
    */
   public Color getColor()
   {
      console.println("TiledImageGraphics.getColor();"); //$NON-NLS-1$

      return null;
   }

   /**
    * @see java.awt.Graphics#setColor(Color)
    */
   public void setColor(Color c)
   {
      console.println("TiledImageGraphics.setColor(" + c + ");"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * @see java.awt.Graphics#setPaintMode()
    */
   public void setPaintMode()
   {
      console.println("TiledImageGraphics.setPaintMode();"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#setXORMode(Color)
    */
   public void setXORMode(Color c1)
   {
      console.println("TiledImageGraphics.setXORMode(" + c1 + ");"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * @see java.awt.Graphics#getFont()
    */
   public Font getFont()
   {
      console.println("TiledImageGraphics.getFont();"); //$NON-NLS-1$

      return null;
   }

   /**
    * @see java.awt.Graphics#setFont(Font)
    */
   public void setFont(Font font)
   {
      console.println("TiledImageGraphics.setFont(" + font + ");"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * @see java.awt.Graphics#getFontMetrics(Font)
    */
   public FontMetrics getFontMetrics(Font f)
   {
      console.println("TiledImageGraphics.getFontMetrics(" + f + ");"); //$NON-NLS-1$ //$NON-NLS-2$

      return null;
   }

   /**
    * @see java.awt.Graphics#getClipBounds()
    */
   public Rectangle getClipBounds()
   {
      console.println("TiledImageGraphics.getClipBounds();"); //$NON-NLS-1$

      return null;
   }

   /**
    * @see java.awt.Graphics#clipRect(int, int, int, int)
    */
   public void clipRect(int x, int y, int width, int height)
   {
      console.println(
         "TiledImageGraphics.clipRect(" //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + width
            + ", " //$NON-NLS-1$
            + height
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#setClip(int, int, int, int)
    */
   public void setClip(int x, int y, int width, int height)
   {
      console.println(
         "TiledImageGraphics.setClip(" //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + width
            + ", " //$NON-NLS-1$
            + height
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#getClip()
    */
   public Shape getClip()
   {
      console.println("TiledImageGraphics.getClip();"); //$NON-NLS-1$

      return null;
   }

   /**
    * @see java.awt.Graphics#setClip(Shape)
    */
   public void setClip(Shape clip)
   {
      console.println("TiledImageGraphics.setClip(" + clip + ");"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * @see java.awt.Graphics#copyArea(int, int, int, int, int, int)
    */
   public void copyArea(int x, int y, int width, int height, int dx, int dy)
   {
      console.println(
         "TiledImageGraphics.copyArea(" //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + width
            + ", " //$NON-NLS-1$
            + height
            + ", " //$NON-NLS-1$
            + dx
            + ", " //$NON-NLS-1$
            + dy
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#drawLine(int, int, int, int)
    */
   public void drawLine(int x1, int y1, int x2, int y2)
   {
      console.println(
         "TiledImageGraphics.drawLine(" //$NON-NLS-1$
            + x1
            + ", " //$NON-NLS-1$
            + y1
            + ", " //$NON-NLS-1$
            + x2
            + ", " //$NON-NLS-1$
            + y2
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#fillRect(int, int, int, int)
    */
   public void fillRect(int x, int y, int width, int height)
   {
      console.println(
         "TiledImageGraphics.fillRect(" //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + width
            + ", " //$NON-NLS-1$
            + height
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#clearRect(int, int, int, int)
    */
   public void clearRect(int x, int y, int width, int height)
   {
      console.println(
         "TiledImageGraphics.clearRect(" //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + width
            + ", " //$NON-NLS-1$
            + height
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#drawRoundRect(int, int, int, int, int, int)
    */
   public void drawRoundRect(
      int x,
      int y,
      int width,
      int height,
      int arcWidth,
      int arcHeight)
   {
      console.println(
         "TiledImageGraphics.drawRoundRect(" //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + width
            + ", " //$NON-NLS-1$
            + height
            + ", " //$NON-NLS-1$
            + arcWidth
            + ", " //$NON-NLS-1$
            + arcHeight
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#fillRoundRect(int, int, int, int, int, int)
    */
   public void fillRoundRect(
      int x,
      int y,
      int width,
      int height,
      int arcWidth,
      int arcHeight)
   {
      console.println(
         "TiledImageGraphics.fillRoundRect(" //$NON-NLS-1$
            + x
            + ",. " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + width
            + ", " //$NON-NLS-1$
            + height
            + ", " //$NON-NLS-1$
            + arcWidth
            + ", " //$NON-NLS-1$
            + arcHeight
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#drawOval(int, int, int, int)
    */
   public void drawOval(int x, int y, int width, int height)
   {
      console.println(
         "TiledImageGraphics.drawOval(" //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + width
            + ", " //$NON-NLS-1$
            + height
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#fillOval(int, int, int, int)
    */
   public void fillOval(int x, int y, int width, int height)
   {
      console.println(
         "TiledImageGraphics.fillOval(" //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + width
            + ", " //$NON-NLS-1$
            + height
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#drawArc(int, int, int, int, int, int)
    */
   public void drawArc(
      int x,
      int y,
      int width,
      int height,
      int startAngle,
      int arcAngle)
   {
      console.println(
         "TiledImageGraphics.drawArc(" //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + width
            + ", " //$NON-NLS-1$
            + height
            + ", " //$NON-NLS-1$
            + startAngle
            + ", " //$NON-NLS-1$
            + arcAngle
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#fillArc(int, int, int, int, int, int)
    */
   public void fillArc(
      int x,
      int y,
      int width,
      int height,
      int startAngle,
      int arcAngle)
   {
      console.println(
         "TiledImageGraphics.fillArc(" //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + width
            + ", " //$NON-NLS-1$
            + height
            + ", " //$NON-NLS-1$
            + startAngle
            + ", " //$NON-NLS-1$
            + arcAngle
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#drawPolyline(int[], int[], int)
    */
   public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints)
   {
      console.println("TiledImageGraphics.drawPolyline(int[], int[], int);"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#drawPolygon(int[], int[], int)
    */
   public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints)
   {
      console.println("TiledImageGraphics.drawPolygon(int[], int[], int);"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#fillPolygon(int[], int[], int)
    */
   public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints)
   {
      console.println("TiledImageGraphics.fillPolygon(int[], int[], int);"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#drawString(String, int, int)
    */
   public void drawString(String str, int x, int y)
   {
      console.println(
         "TiledImageGraphics.drawString(\"" //$NON-NLS-1$
            + str
            + "\", " //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#drawString(AttributedCharacterIterator, int, int)
    */
   public void drawString(AttributedCharacterIterator iterator, int x, int y)
   {
      console.println(
         "TiledImageGraphics.drawString(" //$NON-NLS-1$
            + iterator
            + ", " //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ");"); //$NON-NLS-1$
   }

   /**
    * @see java.awt.Graphics#drawImage(Image, int, int, ImageObserver)
    */
   public boolean drawImage(Image img, int x, int y, ImageObserver observer)
   {
      console.println(
         "TiledImageGraphics.drawImage(" //$NON-NLS-1$
            + img
            + ", " //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + observer
            + ");"); //$NON-NLS-1$

      return false;
   }

   /**
    * @see java.awt.Graphics#drawImage(Image, int, int, int, int, ImageObserver)
    */
   public boolean drawImage(
      Image img,
      int startX,
      int startY,
      int newWidth,
      int newHeight,
      ImageObserver observer)
   {
      console.println(
         "TiledImageGraphics.drawImage(" //$NON-NLS-1$
            + img
            + ", " //$NON-NLS-1$
            + startX
            + ", " //$NON-NLS-1$
            + startY
            + ", " //$NON-NLS-1$
            + newWidth
            + ", " //$NON-NLS-1$
            + newHeight
            + ", " //$NON-NLS-1$
            + observer
            + ");"); //$NON-NLS-1$

      return false;
   }

   /**
    * @see java.awt.Graphics#drawImage(Image, int, int, Color, ImageObserver)
    */
   public boolean drawImage(
      Image img,
      int x,
      int y,
      Color bgcolor,
      ImageObserver observer)
   {
      console.println(
         "TileImageGraphics.drawImage(" //$NON-NLS-1$
            + img
            + ", " //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + bgcolor
            + ", " //$NON-NLS-1$
            + observer
            + ");"); //$NON-NLS-1$

      return false;
   }

   /**
    * @see java.awt.Graphics#drawImage(Image, int, int, int, int, Color, ImageObserver)
    */
   public boolean drawImage(
      Image img,
      int x,
      int y,
      int width,
      int height,
      Color bgcolor,
      ImageObserver observer)
   {
      console.println(
         "TileImageGraphics.drawImage(" //$NON-NLS-1$
            + img
            + ", " //$NON-NLS-1$
            + x
            + ", " //$NON-NLS-1$
            + y
            + ", " //$NON-NLS-1$
            + width
            + ", " //$NON-NLS-1$
            + height
            + ", " //$NON-NLS-1$
            + bgcolor
            + ", " //$NON-NLS-1$
            + observer
            + ");"); //$NON-NLS-1$

      return false;
   }

   /**
    * @see java.awt.Graphics#drawImage(Image, int, int, int, int, int, int, int, int, ImageObserver)
    */
   public boolean drawImage(
      Image img,
      int dx1,
      int dy1,
      int dx2,
      int dy2,
      int sx1,
      int sy1,
      int sx2,
      int sy2,
      ImageObserver observer)
   {
      console.println(
         "TiledImageGraphics.drawImage(" //$NON-NLS-1$
            + dx1
            + ", " //$NON-NLS-1$
            + dy1
            + ", " //$NON-NLS-1$
            + dx2
            + ", " //$NON-NLS-1$
            + dy2
            + ", " //$NON-NLS-1$
            + sx1
            + ", " //$NON-NLS-1$
            + sy1
            + ", " //$NON-NLS-1$
            + sx2
            + ", " //$NON-NLS-1$
            + sy2
            + ", " //$NON-NLS-1$
            + observer
            + ");"); //$NON-NLS-1$

      return false;
   }

   /**
    * @see java.awt.Graphics#drawImage(Image, int, int, int, int, int, int, int, int, Color, ImageObserver)
    */
   public boolean drawImage(
      Image img,
      int dx1,
      int dy1,
      int dx2,
      int dy2,
      int sx1,
      int sy1,
      int sx2,
      int sy2,
      Color bgcolor,
      ImageObserver observer)
   {
      console.println(
         "TiledImageGraphics.drawImage(" //$NON-NLS-1$
            + dx1
            + ", " //$NON-NLS-1$
            + dy1
            + ", " //$NON-NLS-1$
            + dx2
            + ", " //$NON-NLS-1$
            + dy2
            + ", " //$NON-NLS-1$
            + sx1
            + ", " //$NON-NLS-1$
            + sy1
            + ", " //$NON-NLS-1$
            + sx2
            + ", " //$NON-NLS-1$
            + sy2
            + ", " //$NON-NLS-1$
            + bgcolor
            + ", " //$NON-NLS-1$
            + observer
            + ");"); //$NON-NLS-1$

      return false;
   }

   /**
    * @see java.awt.Graphics#dispose()
    */
   public void dispose()
   {
      console.println("TiledImageGraphics.dispose();"); //$NON-NLS-1$
   }

}
