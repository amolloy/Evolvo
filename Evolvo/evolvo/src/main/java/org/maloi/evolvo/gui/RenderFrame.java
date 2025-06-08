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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.maloi.evolvo.expressiontree.renderer.RendererInterface;
import org.maloi.evolvo.expressiontree.renderer.TiledRenderer;
import org.maloi.evolvo.io.Exporter;
import org.maloi.evolvo.io.GenotypeFileIO;
import org.maloi.evolvo.localization.MessageStrings;
import org.maloi.evolvo.settings.GlobalSettings;

public class RenderFrame extends JFrame
{
   private static final long serialVersionUID = 7242012625951307347L;
   ImagePanel panel;
   RendererInterface ri;
   GlobalSettings settings = GlobalSettings.getInstance();

   final JMenuItem exportMenuItem = new JMenuItem(MessageStrings.getString("RenderFrame.Export_Menu")); //$NON-NLS-1$

   public RenderFrame(RendererInterface renderer, Image thumb)
   {
      RenderOptionsPanel optionsPanel = new RenderOptionsPanel();

      if (!optionsPanel.showOptions())
      {
         // If showOptions() returns false, the user has canceled.
         // so return
         return;
      }

      int imageWidth = settings.getIntegerProperty("render.width.pixels"); //$NON-NLS-1$
      int imageHeight = settings.getIntegerProperty("render.height.pixels"); //$NON-NLS-1$

      ri =
         new TiledRenderer(renderer.getExpression(), imageWidth, imageHeight);

      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      addWindowListener(new renderWindowListener());

      JMenuItem menuitem;

      JMenu fileMenu = new JMenu(MessageStrings.getString("RenderFrame.File_Menu")); //$NON-NLS-1$
      fileMenu.setMnemonic(KeyEvent.VK_F);

      fileMenu.addMenuListener(new renderMenuListener());

      // Export item
      exportMenuItem.setEnabled(false);
      exportMenuItem.setMnemonic(KeyEvent.VK_E);
      exportMenuItem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
      exportMenuItem.addActionListener(new exportPerformer());
      fileMenu.add(exportMenuItem);

      menuitem = new JMenuItem(MessageStrings.getString("RenderFrame.Save_Genotype_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_S);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
      menuitem.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            saveGenotype();
         }
      });
      fileMenu.add(menuitem);

      fileMenu.addSeparator();

      // Close item
      menuitem = new JMenuItem(MessageStrings.getString("RenderFrame.Close_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_C);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
      menuitem.addActionListener(new renderActionListener());

      fileMenu.add(menuitem);

      JMenuBar menubar = new JMenuBar();
      menubar.add(fileMenu);
      setJMenuBar(menubar);

      MultiProgressMonitor pm =
         new MultiProgressMonitor(
            this,
            MessageStrings.getString("RenderFrame.Generating_Image..."), //$NON-NLS-1$
            "", //$NON-NLS-1$
            0,
            settings.getIntegerProperty("render.height.pixels") //$NON-NLS-1$
               * settings.getIntegerProperty("render.width.pixels")); //$NON-NLS-1$
      pm.setProgress(0);
      pm.setMillisToDecideToPopup(750);

      ri.setProgressMonitor(pm);

      panel = new SwingImagePanel(ri, thumb);

      JScrollPane scrollPane =
         new JScrollPane(
            panel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      getContentPane().add(scrollPane);

      if (imageWidth > 1024)
      {
         imageWidth = 1024;
      }
      if (imageHeight > 768)
      {
         imageHeight = 768;
      }

      setSize(imageWidth, imageHeight);

      setVisible(true);
   }

   void saveGenotype()
   {
      GenotypeFileIO.putGenotypeToFile(this, ri.getExpression());
   }

   class exportPerformer implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         SaveImageFileChooser fileChooser = new SaveImageFileChooser();

         int result = fileChooser.showSaveDialog(RenderFrame.this);

         if (result == SaveGenomeFileChooser.CANCEL_OPTION)
         {
            return;
         }

         File theFile = fileChooser.getSelectedFile();

         if (theFile == null)
         {
            return;
         }

         String ext;

         String filename = theFile.getName();
         int length = filename.length();
         int i = filename.lastIndexOf('.');
         GenericFileFilter fileFilter =
            (GenericFileFilter) fileChooser.getFileFilter();

         int id = fileFilter.getID();

         if (i > 0 && i < length - 1)
         {
            ext = filename.substring(i + 1).toLowerCase();
         }
         else
         {
            ext = fileFilter.getExtensions()[0].toLowerCase();
            filename = theFile.getPath().concat(".").concat(ext); //$NON-NLS-1$
            theFile = new File(filename);
         }

         if (theFile.exists())
         {
            switch (JOptionPane
               .showConfirmDialog(null, MessageStrings.getString("RenderFrame.File_exists_prompt"))) //$NON-NLS-1$
            {
               case JOptionPane.NO_OPTION :
               case JOptionPane.CANCEL_OPTION :
               case JOptionPane.CLOSED_OPTION :
                  return;
            }
         }

         try
         {
            Exporter.write(panel.getImage(), id, theFile);
         }
         catch (IOException ioe)
         {
            JOptionPane.showMessageDialog(null, MessageStrings.getString("RenderFrame.Error_saving_file.")); //$NON-NLS-1$
         }
      }
   }

   class renderWindowListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent we)
      {
         ri.stop();
         
         panel.flush();
      }
   }

   class renderMenuListener implements MenuListener
   {
      public void menuSelected(MenuEvent e)
      {
         if (ri.isFinished() && Exporter.isAvailable())
         {
            exportMenuItem.setEnabled(true);
         }
      }

      public void menuDeselected(MenuEvent e)
      {
      }

      public void menuCanceled(MenuEvent e)
      {
      }
   }

   class renderActionListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         ri.stop();
         processWindowEvent(
            new WindowEvent(RenderFrame.this, WindowEvent.WINDOW_CLOSING));
      }
   }
}
