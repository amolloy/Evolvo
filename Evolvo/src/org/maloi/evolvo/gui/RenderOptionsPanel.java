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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.maloi.evolvo.settings.GlobalSettings;

public class RenderOptionsPanel
{
   // Properties:
   // 
   // render.width.units  =
   // render.height.units = pixels, inches, or cm
   // render.width
   // render.height
   // render.width.pixels
   // render.height.pixels
   // render.resolution.units = pixels/in or pixels/cm
   // render.resolution

   GlobalSettings settings = GlobalSettings.getInstance();
   DoubleField widthField;
   DoubleField heightField;
   JComboBox widthUnits;
   JComboBox heightUnits;

   RenderOptionsActionListener listener;

   public RenderOptionsPanel()
   {
      listener = new RenderOptionsActionListener();
   }

   JComboBox createUnitsComboBox()
   {
      String units[] = { "pixels", "inches", "cm" };

      JComboBox unitsComboBox = new JComboBox(units);

      unitsComboBox.setEditable(false);
      unitsComboBox.setEnabled(true);
      unitsComboBox.addActionListener(listener);

      return unitsComboBox;
   }

   JComponent createRenderOptionsPanel()
   {
      // create the render options panel
      JPanel renderSizeOptions = new JPanel(new GridLayout(2, 2));
      Border renderSizeBorder = BorderFactory.createEtchedBorder();
      renderSizeOptions.setBorder(
         BorderFactory.createTitledBorder(renderSizeBorder, "Render"));

      // width
      JLabel widthLabel = new JLabel("Width");
      renderSizeOptions.add(widthLabel);
      widthField =
         new DoubleField(
            settings.getDoubleProperty("render.width"),
            0.0,
            0.1,
            Double.MAX_VALUE,
            4);
      renderSizeOptions.add(widthField);

      // The width units combo box
      widthUnits = createUnitsComboBox();
      widthUnits.setSelectedItem(settings.getProperty("render.width.units"));
      renderSizeOptions.add(widthUnits);

      // height
      JLabel heightLabel = new JLabel("Height");
      renderSizeOptions.add(heightLabel);
      heightField =
         new DoubleField(
            settings.getDoubleProperty("render.height"),
            0.0,
            0.1,
            Double.MAX_VALUE,
            4);
      renderSizeOptions.add(heightField);

      // The width units combo box
      heightUnits = createUnitsComboBox();
      heightUnits.setSelectedItem(settings.getProperty("render.height.units"));
      renderSizeOptions.add(heightUnits);

      // the actual render panel
      Box renderOptionsPanel = new Box(BoxLayout.Y_AXIS);
      renderOptionsPanel.add(renderSizeOptions);

      return renderOptionsPanel;
   }

   /**
    * @return true if user chooses "OK," false if "CANCEL"
    */
   boolean showOptions()
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

      settings.setDoubleProperty("render.width", widthField.getValue());
      settings.setDoubleProperty("render.height", heightField.getValue());

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

   class RenderOptionsActionListener implements ActionListener
   {
      public void actionPerformed(ActionEvent ae)
      {
         Object source = ae.getSource();
         String selected;

         if (source == widthUnits)
         {
            selected = (String)widthUnits.getSelectedItem();
            if (selected.equals("pixels"))
            {
               // If the field is in pixels, don't display any decimal points
               widthField.setPrecision(0);
            }
            else
            {
               // Otherwise, give four points of precision
               widthField.setPrecision(4);
            }
         }

         if (source == heightUnits)
         {
            selected = (String)heightUnits.getSelectedItem();
            if (selected.equals("pixels"))
            {
               // If the field is in pixels, don't display any decimal points
               heightField.setPrecision(0);
            }
            else
            {
               // Otherwise, give four points of precision
               heightField.setPrecision(4);
            }
         }
      }
   }
}
