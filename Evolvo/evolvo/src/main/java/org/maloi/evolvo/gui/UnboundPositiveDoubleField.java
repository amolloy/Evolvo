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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Extends JTextField, allowing only numbers to be entered.
 * These numbers may be floating point (double, specifically), but are 
 * restricted to a specified range.
 */
public class UnboundPositiveDoubleField
   extends JTextField
   implements ActionListener, ChangeListener, FocusListener
{
   private static final long serialVersionUID = 6458364767439960824L;

   /**
    * precision determines how many decimals are displayed - the value is stored as
    * a double, at it's full precision
    */
   int precision;

   /**
    * Stores the current value of the field
    */
   double value;

   /** Default constructor. */
   public UnboundPositiveDoubleField()
   {
      this(0.0);
   }

   public UnboundPositiveDoubleField(double value)
   {
      this(0.0, 4);
   }

   public UnboundPositiveDoubleField(double value, int precision)
   {
      this.precision = precision;

      setValue(value);
      
      addFocusListener(this);
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

   public void setFieldText(String s)
   {
      int end = s.indexOf("."); //$NON-NLS-1$

      if (end == -1)
      {
         end = s.length();
      }
      else
      {
         end += precision;

         if (precision != 0)
         {
            end++;
         }

         if (end > s.length())
         {
            end = s.length();
         }
      }

      s = s.substring(0, end);

      setText(s);
   }

   /** Handles all recieved ChangeEvents. */
   public void stateChanged(ChangeEvent e)
   {
      setValue(value);

      setFieldText(Double.toString(value));
   }

   /** Returns the current value of the text field as a double. */
   public double getValue()
   {
      return value;
   }

   /** Sets the value of the text field from a double. */
   public void setValue(double newValue)
   {
      double oldValue = value;
      value = newValue;

      setFieldText(Double.toString(value));

      select(0, getText().length()); // Select the entire text field.

      firePropertyChange("value", oldValue, value); //$NON-NLS-1$
   }

   /** Defines a document model that restricts input to characters which may 
    *  be used to define a double. 
    */
   static class DoubleDocument extends PlainDocument
   {
      private static final long serialVersionUID = -4378998464548670265L;
      
      /** The acceptable characters. */
      byte[] validChars = new String(".0123456789").getBytes(); //$NON-NLS-1$
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
   }

   public void setPrecision(int p)
   {
      precision = p;

      setValue(value); // force it to redraw with the new precision
   }

   public void focusGained(FocusEvent e)
   {
   }

   public void focusLost(FocusEvent e)
   {
      // Set our value when we lose focus
      setValue(Double.parseDouble(getText()));
   }
}
