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
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.asm.evolvo.settings.GlobalSettings;

public class RenderOptionsPanel
{
   // Properties:
   // 
   // render.width.units  =
   // render.height.units = pixels, inches, or cm
   // render.width
   // render.height
   // render.resolution.units = pixels/in or pixels/cm
   // render.resolution
   
   static GlobalSettings settings = GlobalSettings.getInstance();
   static IntegerField widthField;
   static IntegerField heightField;

   private RenderOptionsPanel()
   {
   }

   static JComponent createRenderOptionsPanel()
   {
      // create the render options panel
      JPanel renderSizeOptions = new JPanel(new GridLayout(2, 1));
      Border renderSizeBorder = BorderFactory.createEtchedBorder();
      renderSizeOptions.setBorder(
         BorderFactory.createTitledBorder(renderSizeBorder, "Render Size"));

      // width
      JLabel widthLabel = new JLabel("Width");
      renderSizeOptions.add(widthLabel);
      widthField =
         new IntegerField(6, settings.getIntegerProperty("render.width"));
      renderSizeOptions.add(widthField);

      // height
      JLabel heightLabel = new JLabel("Height");
      renderSizeOptions.add(heightLabel);
      heightField =
         new IntegerField(6, settings.getIntegerProperty("render.height"));
      renderSizeOptions.add(heightField);

      // the actual render panel
      Box renderOptionsPanel = new Box(BoxLayout.Y_AXIS);
      renderOptionsPanel.add(renderSizeOptions);

      return renderOptionsPanel;
   }

   /**
    * @return true if user chooses "OK," false if "CANCEL"
    */
   static boolean showOptions()
   {
      int result =
         JOptionPane.showOptionDialog(
            null,
            new Object[] { createRenderOptionsPanel()},
            "Settings",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            null);

      if (result == JOptionPane.CANCEL_OPTION)
      {
         return false;
      }

      settings.setIntegerProperty("render.width", widthField.getValue());
      settings.setIntegerProperty("render.height", heightField.getValue());

      try
      {
         settings.storeProperties();
      }
      catch (Exception e)
      {
         System.err.println("Error storing new settings");
      }

      return true;
   }
}
