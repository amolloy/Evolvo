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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

import org.maloi.evolvo.resources.Constants;

public class SplashWindow extends JWindow
{
   boolean clickable; // true if clicking makes splash go away
   Thread splashThread;
   Frame owner;
   Container content;
   Graphics2D g2;
   int width = 394;
   int height = 278;
   Area eraser = new Area(new Rectangle2D.Double(11.0, 260.0, 380.0, 14.0));
   SystemConsole console = SystemConsole.getInstance();

   final static String copyright =
      ("Copyright (C) 2000 Andrew Molloy. "
         + "Evolvo comes with ABSOLUTELY NO WARRANY; for details, choose "
         + "Warrany Information from the Help menu. This is free software, "
         + "and you are welcome to redistribute it under certain conditions; "
         + "select Redistribution Information from the Help menu.");

   public SplashWindow(boolean clickable, Frame owner)
   {
      super(owner);

      this.clickable = clickable;
      this.owner = owner;

      content = getContentPane();

      makeSplash();

      if (clickable)
      {
         addMouseListener(new MouseAdapter()
         {
            public void mousePressed(MouseEvent e)
            {
               close();
            }
         });
      }

      setVisible(true);
   }

   public void close()
   {
      setVisible(false);
      dispose();
   }

   void makeSplash()
   {
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      int x = (screen.width - width) / 2;
      int y = (screen.height - height) / 2;

      JLabel label;
      setBounds(x, y, width, height);

      ImageIcon splashImage = null;

      // First look for it in the jar file
      URL url = SplashWindow.class.getResource("/splash.png");

      if (url != null)
      {
         // Okay, it's in the jar file, load it up
         splashImage = new ImageIcon(url);
      }
      else
      {
         // Not in the jar file, look for it in the regular file system
         splashImage = new ImageIcon("Resources/splash.png");
      }

      // If the image's width is -1, it didn't load
      if (splashImage.getIconWidth() == -1)
      {
         // so get rid of our ImageIcon
         splashImage = null;
      }

      if (splashImage == null)
      {
         splashImage = new ImageIcon();

         splashImage.setImage(
            new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
      }

      // Now we need to add the text to it.
      double pny = 203.0;
      Image im = splashImage.getImage();
      BufferedImage bi =
         new BufferedImage(
            splashImage.getIconWidth(),
            splashImage.getIconHeight(),
            BufferedImage.TYPE_INT_RGB);

      g2 = bi.createGraphics();

      FontRenderContext frc = g2.getFontRenderContext();
      Font f = new Font("sansserif", Font.BOLD, 12);
      AttributedString as = new AttributedString(copyright);
      AffineTransform af = new AffineTransform();
      Stroke s =
         new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);

      af.translate(12.0, 188.0);

      g2.drawImage(im, null, null);
      g2.setRenderingHint(
         RenderingHints.KEY_ANTIALIASING,
         RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(
         RenderingHints.KEY_ALPHA_INTERPOLATION,
         RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      g2.setRenderingHint(
         RenderingHints.KEY_RENDERING,
         RenderingHints.VALUE_RENDER_QUALITY);
      g2.setRenderingHint(
         RenderingHints.KEY_FRACTIONALMETRICS,
         RenderingHints.VALUE_FRACTIONALMETRICS_ON);

      as.addAttribute(TextAttribute.FONT, f);

      AttributedCharacterIterator aci = as.getIterator();

      LineBreakMeasurer measurer = new LineBreakMeasurer(aci, frc);
      float wrappingWidth = width - 22;

      TextLayout tl;
      float sw, sh;
      Shape sha;

      // First, draw the Copyright info text
      while (measurer.getPosition() < copyright.length())
      {
         tl = measurer.nextLayout(wrappingWidth);

         pny = (tl.getAscent());
         af.translate(0, pny);

         sw = (float) tl.getBounds().getWidth();
         sh = (float) tl.getBounds().getHeight();

         sha = tl.getOutline(af);

         g2.setColor(Color.black);
         g2.setStroke(s);
         g2.draw(sha);
         g2.setColor(Color.white);
         g2.fill(sha);
      }

      // Now the version info
      tl = new TextLayout("Version " + Constants.VERSION, f, frc);

      Rectangle2D bounds = tl.getBounds();

      af.setToTranslation(width - bounds.getWidth() - 13.0, 67.0);

      sha = tl.getOutline(af);
      g2.setColor(Color.black);
      g2.setStroke(s);
      g2.draw(sha);
      g2.setColor(Color.white);
      g2.fill(sha);

      // We got the splash image, make it our label
      label = new JLabel(new ImageIcon(bi));

      content.add(label);

      toFront();
      setVisible(true);
   }

   public void setMessage(String s)
   {
      // Erase old text
      g2.setColor(Color.white);
      g2.fill(eraser);

      // Add new text
      g2.setColor(Color.black);
      g2.drawString(s, 12.0f, 272.0f);

      // Repaint
      repaint();
      
      // Then add message to system console
      console.println("Splash: " + s);
   }
}
