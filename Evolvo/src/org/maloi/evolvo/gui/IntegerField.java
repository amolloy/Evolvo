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

import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Extension of JTextField that only accepts positive Integer values.
 */
public class IntegerField extends JTextField implements ActionListener
{
   /** The value of the text field. */
   Integer value;

   /** Default constructor. */
   public IntegerField()
   {
      this(6, 0);
   }

   /** Class constructor.  Creates a TextField made up of cols columns, whose value is v. */
   public IntegerField(int cols, int v)
   {
      super(cols);
      value = new Integer(v);
      addActionListener(this);
      // We listen to our own actionEvents so we can make sure the values being entered
      // are actually positive integers.
      setText(value.toString());
   }

   /** Creates a IntegerDocument model for the text field. */
   protected Document createDefaultModel()
   {
      return new IntegerDocument();
   }

   /** Handles ActionEvents from this. */
   public void actionPerformed(ActionEvent e)
   {
      try
      {
         setValue(new Integer(Integer.parseInt(getText())));
         // Set our value to what's been entered in the text field
      }
      catch (NumberFormatException ex)
      {
         select(0, getText().length());
         // If the value of the text field is not a valid integer, refuse to accept it and select
         // the entire field.
      }
   }

   /** Handles ChangeEvents. */
   public void stateChanged(ChangeEvent e)
   {
      setValue(value);
      setText(value.toString());
   }

   /** Returns the value of the text field as an int. */
   public int getValue()
   {
      return value.intValue();
   }

   /** Sets value from the text field, selects all text in the field, and generates a ChangeEvent. */
   public void setValue(Integer v)
   {
      Integer oldValue = value;
      value = v;
      setText(value.toString());
      select(0, getText().length());
      firePropertyChange("value", oldValue, v);
   }

   /** Extends PlainDocument.  Restricts user input to numbers. */
   static class IntegerDocument extends PlainDocument
   {
      /** Characters that are acceptable in input. */
      byte[] validChars = new String("0123456789").getBytes();
      /** The number of acceptable characters. */
      int vcLength = validChars.length;

      /** Checks the validity of a String, and inserts it if deemed valid. */
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
