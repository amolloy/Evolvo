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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.MouseInputAdapter;

import org.maloi.evolvo.expressiontree.renderer.RegionTiledRenderer;
import org.maloi.evolvo.expressiontree.renderer.RendererInterface;
import org.maloi.evolvo.io.Exporter;
import org.maloi.evolvo.io.GenotypeFileIO;
import org.maloi.evolvo.localization.MessageStrings;
import org.maloi.evolvo.resources.Constants;
import org.maloi.evolvo.settings.GlobalSettings;

public class ExplorerFrame extends JFrame
{
   ImagePanel panel = null;
   RendererInterface ri;
   GlobalSettings settings = GlobalSettings.getInstance();
   explorerActionListener aL;
   explorerMouseListener mL;

   final JMenuItem exportMenuItem = new JMenuItem(MessageStrings.getString("RenderFrame.Export_Menu")); //$NON-NLS-1$

   private int height;
   private int width;
   double x1, y1, x2, y2;

   public ExplorerFrame(RendererInterface renderer)
   {
      aL = new explorerActionListener(this);
		mL = new explorerMouseListener(this);

      width = 800;
      height = 600;

      ri = renderer;

      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      addWindowListener(new explorerWindowListener());

      makeMenubar();

      x1 = -1.0;
      y1 = -1.0;
      x2 = 1.0;
      y2 = 1.0;

      redraw();

      JPanel centerPanel = new JPanel();
      centerPanel.add(panel);
      centerPanel.add(makeAuxiliaryPanel());

      getContentPane().add(centerPanel);
      pack();

      setVisible(true);
   }

   private void makeMenubar()
   {
      JMenuItem menuitem;

      JMenu fileMenu = new JMenu(MessageStrings.getString("RenderFrame.File_Menu")); //$NON-NLS-1$
      fileMenu.setMnemonic(KeyEvent.VK_F);

      fileMenu.addMenuListener(new explorerMenuListener());

      // Export item
      exportMenuItem.setEnabled(false);
      exportMenuItem.setMnemonic(KeyEvent.VK_E);
      exportMenuItem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_E, Constants.KEY_MASK));
      exportMenuItem.addActionListener(new exportPerformer());
      fileMenu.add(exportMenuItem);

      menuitem = new JMenuItem(MessageStrings.getString("RenderFrame.Save_Genotype_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_S);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_S, Constants.KEY_MASK));
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
         KeyStroke.getKeyStroke(KeyEvent.VK_C, Constants.KEY_MASK));
      menuitem.addActionListener(aL);

      fileMenu.add(menuitem);

      JMenuBar menubar = new JMenuBar();
      menubar.add(fileMenu);
      setJMenuBar(menubar);
   }

   JPanel makeAuxiliaryPanel()
   {
      JPanel auxPanel = new JPanel();
      JButton auxButton;

      auxButton = new JButton("Zoom Out"); //$NON-NLS-1$
      auxButton.setMnemonic(KeyEvent.VK_O);
      auxButton.addActionListener(aL);

      auxPanel.add(auxButton);

      return auxPanel;
   }

   void redraw()
   {
      ri =
         new RegionTiledRenderer(
            ri.getExpression(),
            width,
            height,
            x1,
            y1,
            x2,
            y2);

      CustomProgressMonitor pm =
         new CustomProgressMonitor(
            this,
            MessageStrings.getString("RenderFrame.Generating_Image..."),
            "",
            0,
            width * height);

      pm.setProgress(0);
      pm.setMillisToDecideToPopup(750);

      ri.setProgressMonitor(pm);

      if (panel == null)
      {
         if (Constants.USE_TILEDIMAGEPANEL)
         {
            panel = new TiledImagePanel(ri, null);
         }
         else
         {
            panel = new SwingImagePanel(ri, null);
         }
      }
      else
      {
         panel.replaceImage(ri);
      }
      
      panel.addMouseListener(mL);
      panel.addMouseMotionListener(mL);
   }

   void saveGenotype()
   {
      GenotypeFileIO.putGenotypeToFile(this, ri.getExpression());
   }

   protected void zoomOut()
   {
      double cx, cy; // center of the window
      double w, h; // width and height of window

      // we'll just double the width and height of the window
      // that means we can find the new boundaries by adding
      // the original width & height to the center point 

      w = x2 - x1;
      h = y2 - y1;

      cx = w / 2.0 + x1;
      cy = h / 2.0 + y1;

      x1 = cx - w;
      y1 = cy - h;
      x2 = cx + w;
      y2 = cy + h;

      redraw();
   }

	protected void doZoomRect(int x1, int y1, int x2, int y2)
	{
		// draw zoom rectangle      
	}

	protected void zoomTo(int px1, int py1, int px2, int py2)
	{
		double w, h; // width and height of window
		double nx1, ny1, nx2, ny2;
		
		w = x2 - x1;
		h = y2 - y1;

		nx1 = px1 / width * w + x1;
		ny1 = py1 / height * h + y1;
		nx2 = px2 / width * w + x1;
		ny2 = py2 / height * h + y1;
		
		x1 = nx1;
		y1 = ny1;
		x2 = nx2;
		y2 = ny2;
		
		redraw();
	}

   class exportPerformer implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         CustomFileChooser fileChooser = CustomFileChooser.getInstance();

         int result = fileChooser.showExportDialog(ExplorerFrame.this);

         if (result == CustomFileChooser.CANCEL_OPTION)
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
            (GenericFileFilter)fileChooser.getFileFilter();

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
            switch (JOptionPane.showConfirmDialog(null, MessageStrings.getString("RenderFrame.File_exists_prompt"))) //$NON-NLS-1$
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
   
   class explorerMouseListener extends MouseInputAdapter
   {
   	ExplorerFrame dad;
   	int x1, y1, x2, y2;
   	
   	public explorerMouseListener(ExplorerFrame dad)
   	{
   		this.dad = dad;
   	}
   	
		public void mousePressed(MouseEvent e)
		{
			x1 = e.getX();
			y1 = e.getY();
		}
		
		public void mouseReleased(MouseEvent e)
		{
			x2 = e.getX();
			y2 = e.getY();
			
			dad.zoomTo(x1, y1, x2, y2);
		}
		
		public void mouseDragged(MouseEvent e)
		{
			x2 = e.getX();
			y2 = e.getY();

			dad.doZoomRect(x1, y1, x2, y2);
		}
   }

   class explorerWindowListener extends WindowAdapter
   {
      public void windowClosing(WindowEvent we)
      {
         ri.stop();

         panel.flush();
      }
   }

   class explorerMenuListener implements MenuListener
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

   class explorerActionListener implements ActionListener
   {
      ExplorerFrame dad;

      public explorerActionListener(ExplorerFrame dad)
      {
         this.dad = dad;
      }

      public void actionPerformed(ActionEvent e)
      {
         String cmd = e.getActionCommand();

         if (cmd.equals("Close"))
         {
            ri.stop();
            processWindowEvent(
               new WindowEvent(ExplorerFrame.this, WindowEvent.WINDOW_CLOSING));
         }
         else if (cmd.equals("Zoom Out"))
         {
            dad.zoomOut();
         }
      }
   }
}
