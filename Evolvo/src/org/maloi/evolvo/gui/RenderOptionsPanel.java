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

public class RenderOptionsPanel implements ActionListener
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

   final static String sizeUnits[] = { "pixels", "inches", "cm" };
   final static String resolutionUnits[] = { "pixels/in", "pixels/cm" };

   final static int S_UNITS_PIXELS = 0;
   final static int S_UNITS_INCHES = 1;
   final static int S_UNITS_CENTS = 2;

   final static int R_UNITS_PIXELS_PER_INCH = 0;
   final static int R_UNITS_PIXELS_PER_CENT = 1;

   final static double CENTS_TO_INCHES = 0.3937;
   final static double INCHES_TO_CENTS = 1 / CENTS_TO_INCHES;

   int lastWidthUnit;
   int lastHieghtUnit;

   GlobalSettings settings = GlobalSettings.getInstance();

   UnboundPositiveDoubleField widthField;
   UnboundPositiveDoubleField heightField;
   UnboundPositiveDoubleField resolutionField;

   JComboBox widthUnits;
   JComboBox heightUnits;
   JComboBox resolutionUnitsBox;

   String widthUnitsDescription;
   String heightUnitsDescription;
   String resolutionUnitsDescription;

   double width;
   double height;
   double resolution;

   boolean settingUp = true;

   public RenderOptionsPanel()
   {
   }

   JComboBox createUnitsComboBox()
   {
      JComboBox unitsComboBox = new JComboBox(sizeUnits);

      unitsComboBox.setEditable(false);
      unitsComboBox.setEnabled(true);
      unitsComboBox.addActionListener(this);

      return unitsComboBox;
   }

   JComboBox createResolutionComboBox()
   {
      JComboBox resolutionComboBox = new JComboBox(resolutionUnits);

      resolutionComboBox.setEditable(false);
      resolutionComboBox.setEnabled(true);
      resolutionComboBox.addActionListener(this);

      return resolutionComboBox;
   }

   JComponent createRenderOptionsPanel()
   {
      // create the render options panel
      JPanel renderSizeOptions = new JPanel(new GridLayout(3, 2));
      Border renderSizeBorder = BorderFactory.createEtchedBorder();
      renderSizeOptions.setBorder(
         BorderFactory.createTitledBorder(renderSizeBorder, "Render"));

      // width
      width = settings.getDoubleProperty("render.width");
      JLabel widthLabel = new JLabel("Width");
      renderSizeOptions.add(widthLabel);
      widthField = new UnboundPositiveDoubleField(width, 4);
      renderSizeOptions.add(widthField);

      // The width units combo box
      widthUnitsDescription = settings.getStringProperty("render.width.units");

      if (widthUnitsDescription == null)
      {
         widthUnitsDescription = "pixels";
      }

      widthUnits = createUnitsComboBox();
      widthUnits.setSelectedItem(widthUnitsDescription);

      lastWidthUnit = widthUnits.getSelectedIndex();

      if (widthUnitsDescription.equals("pixels"))
      {
         widthField.setPrecision(0);
      }
      else
      {
         widthField.setPrecision(4);
      }

      renderSizeOptions.add(widthUnits);

      // height
      height = settings.getDoubleProperty("render.height");
      JLabel heightLabel = new JLabel("Height");
      renderSizeOptions.add(heightLabel);
      heightField = new UnboundPositiveDoubleField(height, 4);
      renderSizeOptions.add(heightField);

      // The height units combo box
      heightUnitsDescription =
         settings.getStringProperty("render.height.units");

      if (heightUnitsDescription == null)
      {
         heightUnitsDescription = "pixels";
      }

      heightUnits = createUnitsComboBox();
      heightUnits.setSelectedItem(heightUnitsDescription);

      lastHieghtUnit = heightUnits.getSelectedIndex();

      if (heightUnitsDescription.equals("pixels"))
      {
         heightField.setPrecision(0);
      }
      else
      {
         heightField.setPrecision(4);
      }

      renderSizeOptions.add(heightUnits);

      // resolution
      resolution = settings.getDoubleProperty("render.resolution");
      JLabel resolutionLabel = new JLabel("Resolution");
      renderSizeOptions.add(resolutionLabel);
      resolutionField = new UnboundPositiveDoubleField(resolution, 4);
      renderSizeOptions.add(resolutionField);

      // The resolution combo box
      resolutionUnitsDescription =
         settings.getStringProperty("render.resolution.units");
      resolutionUnitsBox = createResolutionComboBox();
      resolutionUnitsBox.setSelectedItem(resolutionUnitsDescription);

      renderSizeOptions.add(resolutionUnitsBox);

      // the actual render panel
      Box renderOptionsPanel = new Box(BoxLayout.Y_AXIS);
      renderOptionsPanel.add(renderSizeOptions);

      settingUp = false;

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

      //      settings.setDoubleProperty("render.width", widthField.getValue());
      //      settings.setDoubleProperty("render.height", heightField.getValue());

      settings.setIntegerProperty(
         "render.width.pixels",
         (int) widthField.getValue());
      settings.setIntegerProperty(
         "render.height.pixels",
         (int) heightField.getValue());

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

   double getConversionFactor(
      int units_from,
      int units_to,
      double resolution,
      int units_resolution)
   {
      System.err.print(
         "Converting from "
            + sizeUnits[units_from]
            + " to "
            + sizeUnits[units_to]
            + " at "
            + resolution
            + " "
            + resolutionUnits[units_resolution]
            + "... Factor: ");

      // There has to be a better way...

      double factor = 0.0;

      switch (units_from)
      {
         case S_UNITS_PIXELS :
            switch (units_to)
            {
               case S_UNITS_PIXELS :
                  factor = 1.0;
                  break;
               case S_UNITS_INCHES :
                  switch (units_resolution)
                  {
                     case R_UNITS_PIXELS_PER_INCH :
                        factor = 1 / resolution;
                        break;
                     case R_UNITS_PIXELS_PER_CENT :
                        factor = 1 / (resolution * CENTS_TO_INCHES);
                        break;
                  }
                  break;
               case S_UNITS_CENTS :
                  switch (units_resolution)
                  {
                     case R_UNITS_PIXELS_PER_INCH :
                        factor = 1 / (resolution * INCHES_TO_CENTS);
                        break;
                     case R_UNITS_PIXELS_PER_CENT :
                        factor = 1 / resolution;
                        break;
                  }
                  break;
            }
            break;
         case S_UNITS_INCHES :
            switch (units_to)
            {
               case S_UNITS_PIXELS :
                  switch (units_resolution)
                  {
                     case R_UNITS_PIXELS_PER_INCH :
                        factor = resolution;
                        break;
                     case R_UNITS_PIXELS_PER_CENT :
                        factor = resolution * CENTS_TO_INCHES;
                        break;
                  }
                  break;
               case S_UNITS_INCHES :
                  factor = 1.0;
                  break;
               case S_UNITS_CENTS :
                  factor = INCHES_TO_CENTS;
                  break;
            }
            break;
         case S_UNITS_CENTS :
            switch (units_to)
            {
               case S_UNITS_PIXELS :
                  switch (units_resolution)
                  {
                     case R_UNITS_PIXELS_PER_INCH :
                        factor = resolution * INCHES_TO_CENTS;
                        break;
                     case R_UNITS_PIXELS_PER_CENT :
                        factor = resolution;
                        break;
                  }
                  break;
               case S_UNITS_INCHES :
                  factor = CENTS_TO_INCHES;
                  break;
               case S_UNITS_CENTS :
                  factor = 1.0;
                  break;
            }
            break;
      }

      System.err.println(factor);

      if (factor == 0.0)
      {
         System.err.println("Factor was not set.");
      }

      return factor;
   }

   public void actionPerformed(ActionEvent ae)
   {
      if (settingUp)
      {
         return;
      }

      Object source = ae.getSource();
      String selected;

      if (source == widthUnits)
      {
         selected = (String) widthUnits.getSelectedItem();

         int units_from = lastWidthUnit;
         int units_to = widthUnits.getSelectedIndex();
         resolution = resolutionField.getValue();
         int resolution_units = resolutionUnitsBox.getSelectedIndex();
         double factor =
            getConversionFactor(
               units_from,
               units_to,
               resolution,
               resolution_units);

         width = widthField.getValue() * factor;

         lastWidthUnit = units_to;

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

         widthField.setValue(width);
      }

      if (source == heightUnits)
      {
         selected = (String) heightUnits.getSelectedItem();
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
