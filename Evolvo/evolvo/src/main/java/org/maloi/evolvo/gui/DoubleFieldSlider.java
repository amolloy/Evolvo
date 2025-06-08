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

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Combines a JDoubleSlider and JDoubleField into a single component, binding 
 * each component's data together.
 */
public class DoubleFieldSlider extends JPanel
{
   private static final long serialVersionUID = -4421961671089975461L;
   /** The range model for the slider and text field. */
   DoubleBoundedRangeModel dbrm;
   /** The slider itself. */
   DoubleSlider slider;
   /** A label for the component. */
   JLabel label;
   /** The field itself. */
   DoubleField field;

   /** Default constructor. */
   public DoubleFieldSlider()
   {
      this(new DoubleBoundedRangeModel(0.0, 0.0, 0.0, 1.0, 4), ""); //$NON-NLS-1$
   }

   /** Constructor that takes individual parameters. */
   public DoubleFieldSlider(
      double v,
      double e,
      double min,
      double max,
      int p,
      String str)
   {
      this(new DoubleBoundedRangeModel(v, e, min, max, p), str);
   }

   /** Class constructor which takes an already built DoubleBoundedRangeModel. */
   public DoubleFieldSlider(DoubleBoundedRangeModel rm, String str)
   {
      dbrm = rm;

      // Add the label...

      label = new JLabel(str, JLabel.LEFT);
      label.setPreferredSize(new Dimension(175, 20));
      label.setHorizontalAlignment(JLabel.RIGHT);
      this.add(label);

      // And the slider...

      slider = new DoubleSlider(dbrm);
      slider.setPreferredSize(new Dimension(200, 20));
      this.add(slider);

      // And the field...

      field = new DoubleField(dbrm);
      field.setPreferredSize(new Dimension(75, 20));
      this.add(field);

      setVisible(true);
   }

   /** Sets the major tick spacing of the slider. */
   public void setMajorTickSpacing(double ts)
   {
      slider.setMajorTickSpacing(ts);
   }

   /** Sets the minor tick spacing of the slider. */
   public void setMinorTickSpacing(double ts)
   {
      slider.setMinorTickSpacing(ts);
   }

   /** Determines if the slider's ticks should be drawn. */
   public void setPaintTicks(boolean pt)
   {
      slider.setPaintTicks(pt);
   }

   /** Returns the current value of the component. */
   public double getValue()
   {
      return dbrm.getDoubleValue();
   }

   /** Sets the current value of the component. */
   public void setValue(double v)
   {
      dbrm.setDoubleValue(v);
   }

   /** Returns the text of the label for the component. */
   public String getLabel()
   {
      return label.getText();
   }

   /** Sets the text of the label for the component. */
   public void setLabel(String str)
   {
      label.setText(str);
   }

   /** Sets the key to use as a mnemonic in the label.  
    *  Also activates the mnemonic key to select the text field when pressed. 
    */
   public void setDisplayedMnemonic(char c)
   {
      label.setDisplayedMnemonic(c);
      label.setLabelFor(field);
   }

   public DoubleSlider getSlider()
   {
      return slider;
   }
}
