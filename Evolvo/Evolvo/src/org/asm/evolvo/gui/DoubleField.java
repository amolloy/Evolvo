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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Extends JTextField, allowing only numbers to be entered.
 * These numbers may be floating point (double, specifically), but are 
 * restricted to a specified range.
 */
public class DoubleField
   extends JTextField
   implements ActionListener, ChangeListener
{
   /** Range model used to determine valid values for the text field. */
   DoubleBoundedRangeModel dbrm;

   /** Default constructor. */
   public DoubleField()
   {
      this(0.0, 0.0, 0.0, 1.0, 4, 6);
   }

   /** Class constuctor that takes DoubleBoundedRandeModel's parameters 
    *  individually, and a column width. 
    */
   public DoubleField(
      double v,
      double e,
      double min,
      double max,
      int p,
      int cols)
   {
      this(new DoubleBoundedRangeModel(v, e, min, max, p), cols);
   }

   /** Class constuctor that takes DoubleBoundedRandeModel's parameters 
    *  individually, setting a default column width. 
    */
   public DoubleField(double v, double e, double min, double max, int p)
   {
      this(new DoubleBoundedRangeModel(v, e, min, max, p), 6);
   }

   /** Class constructor that takes a DoubleBoundedRangeModel and sets the 
    *  column with to a default value. 
    */
   public DoubleField(DoubleBoundedRangeModel dm)
   {
      this(dm, 6);
   }

   /** Class constructor which takes a DoubleBoundedRangeModel, and a column 
    *  width. 
    */
   public DoubleField(DoubleBoundedRangeModel dm, int cols)
   {
      super(cols);
      dbrm = dm;
      addActionListener(this);
      addChangeListener(this);
      setText("" + dbrm.getDoubleValue());
   }

   /** Creates a new DoubleDocument document model. */
   protected Document createDefaultModel()
   {
      return new DoubleDocument();
   }

   /** Handles all recieved ActionEvents. */
   public void actionPerformed(ActionEvent e)
   {
      try
      {
         setValue(Double.parseDouble(getText()));
         // Set the value to number in the text field.
      }
      catch (NumberFormatException ex)
      {
         select(0, getText().length());
         // If the text field does not contain a valid number, reject the input and select all the text.
      }
   }

   /** Handles all recieved ChangeEvents. */
   public void stateChanged(ChangeEvent e)
   {
      setValue(dbrm.getDoubleValue());
      setText("" + dbrm.getDoubleValue());
   }

   /** Returns the current value of the text field as a double. */
   public double getValue()
   {
      return dbrm.getDoubleValue();
   }

   /** Sets the value of the text field from a double. */
   public void setValue(double newValue)
   {
      double oldValue = dbrm.getDoubleValue();
      dbrm.setDoubleValue(newValue);
      newValue = dbrm.getDoubleValue();
      setText("" + dbrm.getDoubleValue());
      select(0, getText().length()); // Select the entire text field.
      firePropertyChange("value", oldValue, dbrm.getDoubleValue());
   }

   /** Adds a ChangeListener. */
   public void addChangeListener(ChangeListener l)
   {
      dbrm.addChangeListener(l);
   }

   /** Removes a ChangeListener. */
   public void removeChangeListener(ChangeListener l)
   {
      dbrm.removeChangeListener(l);
   }

   /** Defines a document model that restricts input to characters which may 
    *  be used to define a double. 
    */
   static class DoubleDocument extends PlainDocument
   {
      /** The acceptable characters. */
      byte[] validChars = new String("eE-+.0123456789").getBytes();
      /** The number of acceptable characters. */
      int vcLength = validChars.length;

      /** Insert the given String into the document iff it is valid.
        * @throws BadLocationException
        */
      public void insertString(int offset, String str, AttributeSet a)
         throws BadLocationException
      {
         byte[] strArray = str.getBytes();
         boolean valid = true;

         for (int strIndex = 0; strIndex < strArray.length; strIndex++)
         {
            boolean internalValid = false;
            for (int vcIndex = 0; vcIndex < vcLength; vcIndex++)
            {
               if (strArray[strIndex] == validChars[vcIndex])
               {
                  internalValid = true;
                  vcIndex = vcLength;
               }
            }
            if (internalValid == false)
            {
               valid = false;
               strIndex = strArray.length;
            }
         }
         if (!valid)
         {
            return;
         }
         super.insertString(offset, str, a);
      }
   };
}
