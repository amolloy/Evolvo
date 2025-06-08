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

import java.awt.Component;
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
import org.maloi.evolvo.io.Exporter;
import org.maloi.evolvo.io.GenotypeFileIO;
import org.maloi.evolvo.localization.MessageStrings;
import org.maloi.evolvo.settings.GlobalSettings;

public class ExportFileChooser extends JFileChooser
{
   private static final long serialVersionUID = 3037872316787290168L;

   static GlobalSettings settings = GlobalSettings.getInstance();

   static openChooserPreviewListener openChangeListener;

   static Dimension maxDimensions;

   static final JPanel previewPane = new JPanel();
   static final JPanel preview = new JPanel();

   static ExportFileChooser _instance = null;

   public static ExportFileChooser getInstance()
   {
      if (_instance == null)
      {
         _instance = new ExportFileChooser();
      }

      return _instance;
   }

   ExportFileChooser()
   {
      super();

      Border previewBorder = BorderFactory.createEtchedBorder();
      preview.setBorder(
         BorderFactory.createTitledBorder(previewBorder, MessageStrings.getString("CustomFileChooser.Preview_1"))); //$NON-NLS-1$
      preview.setPreferredSize(new Dimension(100, 85));

      previewPane.add(preview);

      openChangeListener = new openChooserPreviewListener();
   }

   public int showExportDialog(Component f)
   {
      resetChoosableFileFilters();
      setAcceptAllFileFilterUsed(false);
      setAccessory(null);

      String[] formats = Exporter.getFormatDescriptions();

      int i;

      for (i = 0; i < formats.length; i++)
      {
         String[] extensions = Exporter.getFormatExtensions(formats[i]);

         addChoosableFileFilter(
            new GenericFileFilter(extensions, formats[i], i));
      }

      return showSaveDialog(f);
   }

   public int showSaveGeneratorDialog(Component f)
   {
      resetChoosableFileFilters();

      setAcceptAllFileFilterUsed(false);

      addChoosableFileFilter(
         new GenericFileFilter(
            new String[] { "evo" }, //$NON-NLS-1$
            MessageStrings.getString("CustomFileChooser.Evolvo_Genotype_Files_Type"), //$NON-NLS-1$
            0));

      setAccessory(null);

      return showSaveDialog(f);
   }

   public int showOpenGeneratorDialog(Component f)
   {
      resetChoosableFileFilters();

      setAcceptAllFileFilterUsed(false);

      addChoosableFileFilter(
         new GenericFileFilter(
            new String[] { "evo" }, //$NON-NLS-1$
            MessageStrings.getString("CustomFileChooser.Evolvo_Genotype_Files_Type"), //$NON-NLS-1$
            0));

      setAccessory(previewPane);

      addPropertyChangeListener(openChangeListener);

      int result = showOpenDialog(f);

      removePropertyChangeListener(openChangeListener);

      try
      {
         settings.storeProperties();
      }
      catch (Exception e)
      {
      }

      return result;
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
               ((ExportFileChooser) (ce.getSource())).getSelectedFile();

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
