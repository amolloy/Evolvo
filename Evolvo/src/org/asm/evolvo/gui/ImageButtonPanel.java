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
import java.awt.Image;
import java.util.Random;

import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.asm.evolvo.expressiontree.renderer.RendererInterface;
import org.asm.evolvo.expressiontree.utilities.VariablePackage;
import org.asm.evolvo.resources.Constants;
import org.asm.evolvo.settings.GlobalSettings;

public class ImageButtonPanel extends JPanel implements ChangeListener
{
   GlobalSettings settings = GlobalSettings.getInstance();
   VariablePackage variables = VariablePackage.getInstance();
   JToggleButton buttons[] = new JToggleButton[9];
   Image buttonImages[] = new Image[9];
   CustomButtonGroup imageButtonGroup;
   RendererInterface ri[];
   Dimension size;
   Random theRandom;
   final Dimension buttonSize =
      new Dimension(
         Constants.THUMBNAIL_WIDTH + 20,
         Constants.THUMBNAIL_HEIGHT + 20);

   public ImageButtonPanel(RendererInterface[] ri)
   {
      super(new GridLayout(3, 3));

      this.ri = ri;

      setRenderers(this.ri, -1);
   }

   public int getSelectedButton()
   {
      ButtonModel selected = imageButtonGroup.getSelection();

      if (selected == null)
      {
         return -1;
      }

      return Integer.parseInt(selected.getActionCommand());
   }

   public void stop()
   {
      for (int i = 0; i < 9; i++)
      {
         ri[i].stop();
      }
   }

   public void setRendererAtIndex(RendererInterface renderer, int i)
   {
      ri[i] = renderer;

      remove(i);

      imageButtonGroup.remove(buttons[i]);

      CustomProgressMonitor pm =
         new CustomProgressMonitor(
            this,
            "Generating Image...",
            "",
            0,
            Constants.THUMBNAIL_HEIGHT);

      pm.setMillisToPopup(750);

      // Attach our progress monitor
      ri[i].setProgressMonitor(pm);

      // Listen for the ri to be done drawing
      ri[i].addChangeListener(this);

      // Start up the image producer
      buttonImages[i] = createImage(ri[i]);

      // Put it in something JToggleButton can understand...
      AsyncImageIcon ic =
         new AsyncImageIcon(
            buttonImages[i],
            Constants.THUMBNAIL_WIDTH,
            Constants.THUMBNAIL_HEIGHT);

      // And make the new button
      buttons[i] = new JToggleButton(ic);

      // Make each button's action command be its index into the array of
      // buttons
      buttons[i].setActionCommand(Integer.toString(i));

      buttons[i].setPreferredSize(buttonSize);

      imageButtonGroup.add(buttons[i]);

      add(buttons[i], i);

      validate();
   }

   public void setRenderers(RendererInterface[] renderers, int skip)
   {
      int totalScanLines =
         ((skip == -1)
            ? Constants.THUMBNAIL_HEIGHT * 9
            : Constants.THUMBNAIL_HEIGHT * 8);

      ri = renderers;

      imageButtonGroup = new CustomButtonGroup();

      CustomProgressMonitor pm =
         new CustomProgressMonitor(
            this,
            "Generating Images...",
            "",
            0,
            totalScanLines);

      for (int i = 0; i < 9; i++)
      {
         // don't do anything with the button specified by "skip"
         // (the parent image when creating a new generation)
         if (i != skip)
         {
            if (buttons[i] != null)
            {
               remove(buttons[i]);
            }

            // Attach our progress monitor
            ri[i].setProgressMonitor(pm);

            // Listen for the ri to be done drawing
            ri[i].addChangeListener(this);

            // Start up the image producer
            buttonImages[i] = createImage(ri[i]);

            // Put it in something JToggleButton can understand...
            AsyncImageIcon ic =
               new AsyncImageIcon(
                  buttonImages[i],
                  Constants.THUMBNAIL_WIDTH,
                  Constants.THUMBNAIL_HEIGHT);

            // And make the new button
            buttons[i] = new JToggleButton(ic);

            // Make each button's action command be its index into the array of
            // buttons
            buttons[i].setActionCommand(Integer.toString(i));

            buttons[i].setPreferredSize(buttonSize);

            add(buttons[i], i);
         }

         // All buttons need to be added to the new button group, including
         // the one that was skipped.
         imageButtonGroup.add(buttons[i]);
      }

      if (skip != -1)
      {
         // If we skipped a button, it's because it's selected.  Make that 
         // remain the case with the new button group instance
         imageButtonGroup.setSelected(buttons[skip].getModel(), true);
      }

      validate();
   }

   public void stateChanged(ChangeEvent ce)
   {
      repaint();
   }
   
   public Image getImageForButton(int i)
   {
      return buttonImages[i];
   }
}
