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

package org.maloi.evolvo;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.maloi.evolvo.expressiontree.ExpressionTree;
import org.maloi.evolvo.expressiontree.renderer.RendererInterface;
import org.maloi.evolvo.expressiontree.renderer.StandardRenderer;
import org.maloi.evolvo.expressiontree.utilities.ExpressionTreeGenerator;
import org.maloi.evolvo.expressiontree.utilities.ExpressionTreeMutator;
import org.maloi.evolvo.expressiontree.utilities.Tools;
import org.maloi.evolvo.expressiontree.utilities.VariablePackage;
import org.maloi.evolvo.gui.CustomFileChooser;
import org.maloi.evolvo.gui.ImageButtonPanel;
import org.maloi.evolvo.gui.RenderFrame;
import org.maloi.evolvo.gui.SettingsDialog;
import org.maloi.evolvo.gui.SplashWindow;
import org.maloi.evolvo.gui.TextDialog;
import org.maloi.evolvo.io.GenotypeFileIO;
import org.maloi.evolvo.resources.Constants;
import org.maloi.evolvo.resources.LicenseText;
import org.maloi.evolvo.settings.GlobalSettings;

public class Evolvo extends JFrame implements ActionListener
{
   static GlobalSettings settings;
   static SettingsDialog settingsDialogBox;
   CustomFileChooser genericFileChooser;
   Properties prop;
   VariablePackage variables;
   ImageButtonPanel imagePanel;
   RendererInterface[] ri = new RendererInterface[9];
   Object macHandler = null; // Only used when run on a mac

   public Evolvo()
   {
      super("Evolvo");

      SplashWindow Splash = new SplashWindow(false, this);

      if (!Constants.isMac)
      {
         Splash.setMessage("Setting Look and Feel...");
         // Don't change L&F on Mac
         try
         {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         }
         catch (Exception e)
         {
            //Do nothing-will automatically use default L&F if failure occurs  
         }
      }

      Splash.setMessage("Loading preferences...");
      settings = GlobalSettings.getInstance();

      Splash.setMessage("Creating preferences dialog...");
      settingsDialogBox = SettingsDialog.getInstance();

      Splash.setMessage("Creating file chooser dialog...");
      genericFileChooser = CustomFileChooser.getInstance();

      Splash.setMessage("Reading preferences...");
      prop = settings.getProperties();

      Splash.setMessage("Configuring variables...");
      variables = VariablePackage.getInstance();

      Splash.setMessage("Creating main window...");
      getContentPane().setLayout(new BorderLayout());

      JMenuBar menubar = makeMenuBar();
      setJMenuBar(menubar);

      JPanel mainPanel = makeMainPanel();
      getContentPane().add(mainPanel, BorderLayout.CENTER);

      JPanel auxPanel = makeAuxiliaryPanel();
      getContentPane().add(auxPanel, BorderLayout.SOUTH);

      Dimension menuBarSize = menubar.getPreferredSize();
      Dimension mainPanelSize = mainPanel.getPreferredSize();
      Dimension mainFrameSize = getPreferredSize();
      Dimension contentPaneSize = getContentPane().getPreferredSize();
      Dimension auxPanelSize = auxPanel.getPreferredSize();

      int width =
         mainPanelSize.width + (mainFrameSize.width - contentPaneSize.width);
      int height =
         (menuBarSize.height + mainPanelSize.height + auxPanelSize.height)
            + (mainFrameSize.height - contentPaneSize.height);

      setSize(width, height);
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      int x = (screen.width - width) / 2;
      int y = (screen.height - height) / 2;
      setBounds(x, y, width, height);
      pack();
      setResizable(false);
      setVisible(true);

      Splash.close();
   }

   JMenuBar makeMenuBar()
   {
      JMenuBar menubar;
      JMenuItem menuitem;
      JMenu fileMenu;
      JMenu renderMenu;
      JMenu helpMenu;

      // Make the File Menu
      fileMenu = new JMenu("File");
      fileMenu.setMnemonic(KeyEvent.VK_F);

      // Save menu item
      menuitem = new JMenuItem("Save Genotype");
      menuitem.setMnemonic(KeyEvent.VK_S);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_S, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      fileMenu.add(menuitem);

      // Load menu item
      menuitem = new JMenuItem("Load Genotype");
      menuitem.setMnemonic(KeyEvent.VK_L);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_L, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      fileMenu.add(menuitem);
      fileMenu.addSeparator();

      // Display
      menuitem = new JMenuItem("Display Genotype");
      menuitem.setMnemonic(KeyEvent.VK_D);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_D, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      fileMenu.add(menuitem);

      // render saved genotype menu item
      menuitem = new JMenuItem("Render Saved Genotype");
      menuitem.setMnemonic(KeyEvent.VK_E);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_E, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      fileMenu.add(menuitem);

      if (!Constants.isMac)
      {
         // On Mac, these are handled elsewhere

         fileMenu.addSeparator();

         // Preferences menu item
         menuitem = new JMenuItem("Preferences");
         menuitem.setMnemonic(KeyEvent.VK_P);
         menuitem.addActionListener(this);
         fileMenu.add(menuitem);

         // Exit menu item
         menuitem = new JMenuItem("Exit");
         menuitem.setMnemonic(KeyEvent.VK_X);
         menuitem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_X, Constants.KEY_MASK));
         menuitem.addActionListener(this);
         fileMenu.add(menuitem);
      }
      else
      {
         try
         {
            Class macHandlerClass = Class.forName("CustomComponents.MacMenu");
            Constructor macHandlerConstructor =
               macHandlerClass.getConstructor(
                  new Class[] { ActionListener.class });
            macHandler =
               macHandlerConstructor.newInstance(new Object[] { this });
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }

      // Make Render Menu
      renderMenu = new JMenu("Render");
      renderMenu.setMnemonic(KeyEvent.VK_R);

      // New Generation menu item
      menuitem = new JMenuItem("New Generation");
      menuitem.setMnemonic(KeyEvent.VK_N);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_N, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      renderMenu.add(menuitem);
      renderMenu.addSeparator();

      // Render Image menu item
      menuitem = new JMenuItem("Render Image");
      menuitem.setMnemonic(KeyEvent.VK_R);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_R, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      renderMenu.add(menuitem);

      // Make stop menu item
      menuitem = new JMenuItem("Stop");
      menuitem.setMnemonic(KeyEvent.VK_T);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_T, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      renderMenu.add(menuitem);

      // Make Render Menu
      helpMenu = new JMenu("Help");
      helpMenu.setMnemonic(KeyEvent.VK_H);

      if (!Constants.isMac)
      {
         // On Macs, this is handled elsewhere...

         // About Evolvo menu item
         menuitem = new JMenuItem("About Evolvo");
         menuitem.setMnemonic(KeyEvent.VK_A);
         menuitem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_A, Constants.KEY_MASK));
         menuitem.addActionListener(this);
         helpMenu.add(menuitem);
         helpMenu.addSeparator();
      }

      // GPL License menu item
      menuitem = new JMenuItem("GPL License");
      menuitem.setMnemonic(KeyEvent.VK_G);
      menuitem.addActionListener(this);
      helpMenu.add(menuitem);

      // Warranty Info menu item
      menuitem = new JMenuItem("Warranty Information");
      menuitem.setMnemonic(KeyEvent.VK_W);
      menuitem.addActionListener(this);
      helpMenu.add(menuitem);

      // Redistribution Info menu item
      menuitem = new JMenuItem("Redistribution Information");
      menuitem.setMnemonic(KeyEvent.VK_R);
      menuitem.addActionListener(this);
      helpMenu.add(menuitem);

      menubar = new JMenuBar();
      menubar.add(fileMenu);
      menubar.add(renderMenu);
      menubar.add(helpMenu);

      return menubar;
   }

   JPanel makeMainPanel()
   {
      JPanel mainPanel = new JPanel();

      imagePanel = new ImageButtonPanel(makeFirstGeneration());

      mainPanel.add(imagePanel);
      mainPanel.setVisible(true);

      Dimension imageButtonPanelSize = imagePanel.getPreferredSize();

      return mainPanel;
   }

   JPanel makeAuxiliaryPanel()
   {
      JPanel auxPanel = new JPanel();
      JButton auxButton;

      auxButton = new JButton("New Generation");
      auxButton.setMnemonic(KeyEvent.VK_N);
      auxButton.addActionListener(this);

      auxPanel.add(auxButton);

      auxButton = new JButton("Render Image");
      auxButton.setMnemonic(KeyEvent.VK_R);
      auxButton.addActionListener(this);

      auxPanel.add(auxButton);

      return auxPanel;
   }

   private RendererInterface[] makeFirstGeneration()
   {
      int riIndex;
      int exprIndex;
      ExpressionTree[] expressions;
      Random r = new Random();

      for (riIndex = 0; riIndex < 9; riIndex++)
      {
         expressions = new ExpressionTree[3];

         for (exprIndex = 0; exprIndex < 3; exprIndex++)
         {
            expressions[exprIndex] =
               ExpressionTreeGenerator.generate(new Random(r.nextLong()));
         }

         ri[riIndex] =
            new StandardRenderer(
               expressions,
               Constants.THUMBNAIL_WIDTH,
               Constants.THUMBNAIL_HEIGHT);
      }

      return ri;
   }

   void makeNewGeneration()
   {
      int selected = imagePanel.getSelectedButton();

      Random randomNumber = new Random();

      if (selected != -1)
      {
         ExpressionTree[] originalExpressions = ri[selected].getExpressions();

         for (int i = 0; i < 9; i++)
         {
            // Don't do anything to the selected button
            if (i != selected)
            {
               ExpressionTree[] expressions = new ExpressionTree[3];

               for (int k = 0; k < 3; k++)
               {
                  Random newRandom = new Random(randomNumber.nextLong());

                  expressions[k] =
                     ExpressionTreeMutator.mutate(
                        newRandom,
                        originalExpressions[k].getClone());
               }

               ri[i] =
                  new StandardRenderer(
                     expressions,
                     Constants.THUMBNAIL_WIDTH,
                     Constants.THUMBNAIL_HEIGHT);
            }
         }
      }
      else
      {
         ri = makeFirstGeneration();
      }

      imagePanel.setRenderers(ri, selected);
   }

   void doPreferencesDialog()
   {
      settingsDialogBox.showDialog(this);
   }

   void saveGenotype()
   {
      int selection = imagePanel.getSelectedButton();

      if (selection == -1)
      {
         JOptionPane.showMessageDialog(
            this,
            "Please select a genotype to save.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
      else
      {
         GenotypeFileIO.putGenotypeToFile(this, ri[selection].getExpressions());
      }
   }

   void displayGenotype()
   {
      int selection = imagePanel.getSelectedButton();

      if (selection == -1)
      {
         JOptionPane.showMessageDialog(
            this,
            "Please select a genotype to display.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
      else
      {
         String displayString = Tools.toString(ri[selection].getExpressions());

         TextDialog displayDialog = new TextDialog(displayString);
         displayDialog.scrollToRow(0);
      }
   }

   void renderLoadedImage()
   {
      ExpressionTree[] expressions = GenotypeFileIO.getGenotypeFromFile(this);

      if (expressions != null)
      {
         RenderFrame theRenderFrame =
            new RenderFrame(
               new StandardRenderer(
                  expressions,
                  settings.getIntegerProperty("render.width"),
                  settings.getIntegerProperty("render.height")),
               null);
      }
   }

   void loadGenotype()
   {
      int result =
         JOptionPane.showConfirmDialog(
            this,
            "Loading a genotype will replace the image "
               + "in the top left corner, are you sure you wish to do this?",
            "Warning",
            JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.NO_OPTION)
      {
         return;
      }

      ExpressionTree[] expressions = GenotypeFileIO.getGenotypeFromFile(this);

      if (expressions != null)
      {
         ri[0] =
            new StandardRenderer(
               expressions,
               Constants.THUMBNAIL_WIDTH,
               Constants.THUMBNAIL_HEIGHT);
         imagePanel.setRendererAtIndex(ri[0], 0);
      }
   }

   void renderImage()
   {
      int selection = imagePanel.getSelectedButton();

      if (selection == -1)
      {
         JOptionPane.showMessageDialog(
            this,
            "Please select an image to render.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
      else
      {
         RenderFrame theRenderFrame = new RenderFrame(ri[selection], 
            imagePanel.getImageForButton(selection));
      }
   }

   public void actionPerformed(ActionEvent e)
   {
      String cmd = e.getActionCommand();

      if (cmd.equals("Save Genotype"))
      {
         saveGenotype();
      }
      else if (cmd.equals("Load Genotype"))
      {
         loadGenotype();
      }
      else if (cmd.equals("Display Genotype"))
      {
         displayGenotype();
      }
      else if (cmd.equals("Render Saved Genotype"))
      {
         renderLoadedImage();
      }
      else if (cmd.equals("Preferences"))
      {
         doPreferencesDialog();
      }
      else if (cmd.equals("Exit"))
      {
         System.exit(0);
      }
      else if (cmd.equals("New Generation"))
      {
         makeNewGeneration();
      }
      else if (cmd.equals("Render Image"))
      {
         renderImage();
      }
      else if (cmd.equals("Stop"))
      {
         imagePanel.stop();
      }
      else if (cmd.equals("About Evolvo"))
      {
         SplashWindow Splash = new SplashWindow(true, this);
      }
      else if (cmd.equals("GPL License"))
      {
         showLicenseDialog(0);
      }
      else if (cmd.equals("Warranty Information"))
      {
         showLicenseDialog(LicenseText.warrantyInfoStart);
      }
      else if (cmd.equals("Redistribution Information"))
      {
         showLicenseDialog(LicenseText.redistributeInfoStart);
      }
      else
      {
         System.err.println("Unknown ActionCommand: " + cmd);
      }
   }

   void showLicenseDialog(int row)
   {
      TextDialog licenseDialog = new TextDialog(LicenseText.license);
      licenseDialog.scrollToRow(row);
   }

   public static void main(String[] args)
   {
      JFrame f = new Evolvo();
      f.addWindowListener(new WindowAdapter()
      {
         public void windowClosing(WindowEvent we)
         {
            System.exit(0);
         }
      });
   }
}
