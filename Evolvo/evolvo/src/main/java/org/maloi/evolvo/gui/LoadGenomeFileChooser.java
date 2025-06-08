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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.maloi.evolvo.expressiontree.ExpressionTree;
import org.maloi.evolvo.expressiontree.renderer.RendererInterface;
import org.maloi.evolvo.expressiontree.renderer.StandardRenderer;
import org.maloi.evolvo.io.GenotypeFileIO;
import org.maloi.evolvo.localization.MessageStrings;
import org.maloi.evolvo.settings.GlobalSettings;

public class LoadGenomeFileChooser extends JFileChooser
{
   private static final long serialVersionUID = -1696848139740214683L;

   static GlobalSettings settings = GlobalSettings.getInstance();

   static openChooserPreviewListener openChangeListener;

   static Dimension maxDimensions;

   static final JPanel previewPane = new JPanel();
   static final JPanel preview = new JPanel();

   public LoadGenomeFileChooser()
   {
      super();

      Border previewBorder = BorderFactory.createEtchedBorder();
      preview.setBorder(
         BorderFactory.createTitledBorder(previewBorder, MessageStrings.getString("CustomFileChooser.Preview_1"))); //$NON-NLS-1$
      preview.setPreferredSize(new Dimension(100, 85));

      previewPane.add(preview);

      openChangeListener = new openChooserPreviewListener();

      resetChoosableFileFilters();

      setAcceptAllFileFilterUsed(false);

      addChoosableFileFilter(
         new GenericFileFilter(
            new String[] { "evo" }, //$NON-NLS-1$
            MessageStrings.getString("CustomFileChooser.Evolvo_Genotype_Files_Type"), //$NON-NLS-1$
            0));

      setAccessory(previewPane);
      addPropertyChangeListener(openChangeListener);
   }
   
   class openChooserPreviewListener
      implements PropertyChangeListener, ChangeListener
   {
      public void stateChanged(ChangeEvent e)
      {
         preview.repaint();
      }

      public void propertyChange(PropertyChangeEvent ce)
      {
         if (ce == null)
         {
            return;
         }
         if (ce.getPropertyName().equals("SelectedFileChangedProperty")) //$NON-NLS-1$
         {
            File theFile =
               ((LoadGenomeFileChooser) (ce.getSource())).getSelectedFile();

            try
            {
               preview.removeAll();

               ExpressionTree expression = GenotypeFileIO.loadFile(theFile);

               if (expression != null)
               {
                  RendererInterface ri =
                     new StandardRenderer(expression, 80, 50);
                  ri.addChangeListener(this);

                  SwingImagePanel ip = new SwingImagePanel(ri);

                  preview.add(ip);
                  preview.setVisible(true);
                  preview.validate();
               }
            }
            catch (Exception e)
            {
               preview.removeAll();
               preview.validate();
               preview.repaint();
            }
         }
      }
   }
}
