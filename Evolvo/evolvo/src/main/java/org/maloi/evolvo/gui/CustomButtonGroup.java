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

import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

/**
 *  Drop in replacement for ButtonGroup, which allows the user to
 *  de-select a button by clicking it a second time.  If no buttons
 *  are selected, the button group returns null when asked what
 *  button is selected.
 */

public class CustomButtonGroup extends ButtonGroup
{

   private static final long serialVersionUID = 7400115433305841491L;


   /**
    * The current choice.
    */
   transient ButtonModel selection = null;

   /**
    * Creates a new ButtonGroup.
    */
   public CustomButtonGroup()
   {
   }

   /**
    * Adds the button to the group.
    */
   @Override
   public void add(AbstractButton b)
   {
      if (b == null)
      {
         return;
      }
      buttons.add(b);
      if (selection == null && b.isSelected())
      {
         selection = b.getModel();
      }
      b.getModel().setGroup(this);
   }

   /**
    * Removes the button from the group.
    */
   @Override
   public void remove(AbstractButton b)
   {
      if (b == null)
      {
         return;
      }
      buttons.remove(b);
      if (b.getModel() == selection)
      {
         selection = null;
      }
      b.getModel().setGroup(null);
   }

   /**
    * Return all the buttons that are participating in
    * this group.
    */
   @Override
   public Enumeration<AbstractButton> getElements()
   {
      return buttons.elements();
   }

   /**
    * Return the selected button model.
    */
   @Override
   public ButtonModel getSelection()
   {
      return selection;
   }

   /**
    * Sets the selected value for the button.
    */
   @Override
   public void setSelected(ButtonModel m, boolean b)
   {
      if (b && m != selection)
      {
         ButtonModel oldSelection = selection;
         selection = m;
         if (oldSelection != null)
         {
            oldSelection.setSelected(false);
         }
      }
      if (!b && m == selection)
      {
         ButtonModel oldSelection = selection;
         selection = null;
         if (oldSelection != null)
         {
            oldSelection.setSelected(false);
         }
      }
   }

   /**
    * Returns the selected value for the button.
    */
   @Override
   public boolean isSelected(ButtonModel m)
   {
      return (m == selection);
   }

   /**
    * Returns the number of buttons in the group.
    */
   @Override
   public int getButtonCount()
   {
      if (buttons == null)
      {
         return 0;
      }
      else
      {
         return buttons.size();
      }
   }

    public void setSelection(ButtonModel selection) {
        this.selection = selection;
    }

}
