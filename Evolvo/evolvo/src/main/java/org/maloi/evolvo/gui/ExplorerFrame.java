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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
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
import org.maloi.evolvo.settings.GlobalSettings;

public class ExplorerFrame extends JFrame
{
   private static final long serialVersionUID = 3506051221530473010L;
   TiledImagePanel panel = null;
   RendererInterface ri;
   GlobalSettings settings = GlobalSettings.getInstance();
   explorerActionListener aL;
   explorerMouseListener mL;
   boolean canClick = false;

   final JMenuItem exportMenuItem = new JMenuItem(MessageStrings.getString("RenderFrame.Export_Menu")); //$NON-NLS-1$

   private int height;
   private int width;
   double x1, y1, x2, y2;
   int lx1, ly1, lx2, ly2;
   boolean dirty;
      
   public ExplorerFrame(RendererInterface renderer)
   {
      aL = new explorerActionListener(this);
		mL = new explorerMouseListener(this);

      width = 500;
      height = 500;

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

      resetLastRect();
      
      setVisible(true);
   }
      
   synchronized private void resetLastRect()
   {
      lx1 = 0;
      ly1 = 0;
      lx2 = 0;
      ly2 = 0;
      dirty = false;
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
      menuitem.addActionListener(aL);

      fileMenu.add(menuitem);

      JMenuBar menubar = new JMenuBar();
      menubar.add(fileMenu);
      setJMenuBar(menubar);
   }

   Box makeAuxiliaryPanel()
   {
      Box auxPanel = Box.createVerticalBox();
      JButton auxButton;

      auxButton = new JButton("Zoom Out"); //$NON-NLS-1$
      auxButton.setMnemonic(KeyEvent.VK_O);
      auxButton.addActionListener(aL);
      auxPanel.add(auxButton);

      auxButton = new JButton("Zoom Way Out"); //$NON-NLS-1$
      auxButton.setMnemonic(KeyEvent.VK_W);
      auxButton.addActionListener(aL);      
      auxPanel.add(auxButton);
      
      return auxPanel;
   }

   void redraw()
   {
      canClick = false;
      
      ri =
         new RegionTiledRenderer(
            ri.getExpression(),
            width,
            height,
            x1,
            y1,
            x2,
            y2);

      MultiProgressMonitor pm =
         new MultiProgressMonitor(
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
         panel = new TiledImagePanel(ri, null);
         panel.addMouseListener(mL);
         panel.addMouseMotionListener(mL);
      }
      else
      {
         panel.replaceImage(ri);
      }
      
      resetLastRect();
      canClick = true;
   }

   void saveGenotype()
   {
      GenotypeFileIO.putGenotypeToFile(this, ri.getExpression());
   }

   protected void zoomOut(double factor)
   {
      if (!canClick || ri == null || !ri.isFinished())
      {
         return;
      }
      
      double cx, cy; // center of the window
      double w, h; // width and height of window

      w = (x2 - x1) / 2.0;
      h = (y2 - y1) / 2.0;

      // find the center point...
      cx = w + x1;
      cy = h + y1;

      // now figure the new width and height,
      // each divided by two
      w *= factor;
      h *= factor;
      
      x1 = cx - w;
      y1 = cy - h;
      x2 = cx + w;
      y2 = cy + h;

      redraw();
   }

	synchronized protected void doZoomRect(int x1, int y1, int x2, int y2)
	{
      if (!canClick || ri == null || !ri.isFinished())
      {
         return;
      }
      
      if (x2 < x1)
      {
         int x = x2;
         x2 = x1;
         x1 = x;
      }
      if (y2 < y1)
      {
         int y = y2;
         y2 = y1;
         y1 = y;
      }
      
      if (dirty)
      {
         System.err.println("Erasing: (" + lx1 + ", " + ly1 + ", " + lx2 + ", " + ly2 + ")");
         panel.xorRectangle(lx1, ly1, lx2, ly2);
      }

      System.err.println();
      System.err.println("Drawing: (" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ")");
      panel.xorRectangle(x1, y1, x2, y2);
      
      lx1 = x1;
      ly1 = y1;
      lx2 = x2;
      ly2 = y2;
      dirty = true;
      
      repaint();
   }

	private int bound(int x, int l, int h)
	{
		if (x < l) x = l;
		if (x > h) x = h;
		
		return x;
	}

	protected void zoomTo(int px1, int py1, int px2, int py2)
	{
      if (!canClick || ri == null || !ri.isFinished())
      {
         return;
      }
      
      double w, h; // width and height of window
		double nx1, ny1, nx2, ny2;
	
		bound(px1, 0, width);
		bound(py1, 0, height);
		bound(px2, 0, width);
		bound(py2, 0, height);
		
		w = x2 - x1;
		h = y2 - y1;

		nx1 = (double)px1 / (double)width * w + x1;
		ny1 = (double)py1 / (double)height * h + y1;
		nx2 = (double)px2 / (double)width * w + x1;
		ny2 = (double)py2 / (double)height * h + y1;
		
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
         SaveImageFileChooser fileChooser = new SaveImageFileChooser(); 

         int result = fileChooser.showSaveDialog(ExplorerFrame.this);

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
            dad.zoomOut(2.0);
         }
         else if (cmd.equals("Zoom Way Out"))
         {
            dad.zoomOut(10.0);
         }
      }
   }
   
   class explorerImagePanel extends JPanel
   {
      private static final long serialVersionUID = -6892087835460309718L;
      BufferedImage image;
      int width, height;
      
      public explorerImagePanel(BufferedImage image)
      {
         this.image = image;
         width = image.getWidth();
         height = image.getHeight();
      }
      
      public void paintComponent(Graphics g)
      {
         super.paintComponent(g);

         if (image != null)
         {
            g.drawImage(image, 0, 0, this);
         }

         g.dispose();
      }

      public Dimension getPreferredSize()
      {
         return new Dimension(width, height);
      }
   }
}
