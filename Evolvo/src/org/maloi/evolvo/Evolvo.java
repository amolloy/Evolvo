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
import java.util.Enumeration;
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
import org.maloi.evolvo.expressiontree.mutator.Mutator;
import org.maloi.evolvo.expressiontree.renderer.RendererInterface;
import org.maloi.evolvo.expressiontree.renderer.StandardRenderer;
import org.maloi.evolvo.expressiontree.utilities.ExpressionTreeGenerator;
import org.maloi.evolvo.expressiontree.utilities.VariablePackage;
import org.maloi.evolvo.gui.CustomFileChooser;
import org.maloi.evolvo.gui.ExplorerFrame;
import org.maloi.evolvo.gui.ImageButtonPanel;
import org.maloi.evolvo.gui.RenderFrame;
import org.maloi.evolvo.gui.SettingsDialog;
import org.maloi.evolvo.gui.SplashWindow;
import org.maloi.evolvo.gui.SystemConsole;
import org.maloi.evolvo.gui.TextDialog;
import org.maloi.evolvo.io.GenotypeFileIO;
import org.maloi.evolvo.localization.MessageStrings;
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
   ImageButtonPanel buttonPanel;
   RendererInterface[] ri = new RendererInterface[9];
   Object macHandler = null; // Only used when run on a mac
   SystemConsole console;

   static {
      // Set some OS X specific settings (has no affect on other platforms)
      System.setProperty("apple.laf.useScreenMenuBar", "true"); //$NON-NLS-1$ //$NON-NLS-2$
      //System.setProperty("com.apple.mrj.application.apple.menu.about.name",
      //   "Evolvo");
      //System.setProperty("apple.awt.brushMetalLook", "true");
      System.setProperty("apple.awt.textantialising", "true"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   public Evolvo()
   {
      super(MessageStrings.getString("Evolvo.Program_Title")); //$NON-NLS-1$

      if (!Constants.isMac)
      {
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

      console = SystemConsole.getInstance();

      logSystemInfo();

		console.println("Setting up system logger..."); //$NON-NLS-1$

      console.println(MessageStrings.getString("Evolvo.Creating_splash_screen_6")); //$NON-NLS-1$

      SplashWindow Splash = new SplashWindow(false, this);

      Splash.setMessage(MessageStrings.getString("Evolvo.Loading_preferences..._7")); //$NON-NLS-1$
      settings = GlobalSettings.getInstance();

      Splash.setMessage(MessageStrings.getString("Evolvo.Creating_preferences_dialog..._8")); //$NON-NLS-1$
      settingsDialogBox = SettingsDialog.getInstance();

      Splash.setMessage(MessageStrings.getString("Evolvo.Creating_file_chooser_dialog..._9")); //$NON-NLS-1$
      genericFileChooser = CustomFileChooser.getInstance();

      Splash.setMessage(MessageStrings.getString("Evolvo.Reading_preferences..._10")); //$NON-NLS-1$
      prop = settings.getProperties();

      Splash.setMessage(MessageStrings.getString("Evolvo.Configuring_variables..._11")); //$NON-NLS-1$
      variables = VariablePackage.getInstance();

      Splash.setMessage(MessageStrings.getString("Evolvo.Creating_main_window..._12")); //$NON-NLS-1$
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

   /**
    * 
    */
   private void logSystemInfo()
   {
      Properties sysProps = System.getProperties();

      console.println(MessageStrings.getString("Evolvo.System_Properties__13")); //$NON-NLS-1$

      for (Enumeration e = sysProps.propertyNames(); e.hasMoreElements();)
      {
         String key = (String)e.nextElement();

         console.println(key + "=" + sysProps.getProperty(key)); //$NON-NLS-1$
      }
   }

   JMenuBar makeMenuBar()
   {
      JMenuBar menubar;
      JMenuItem menuitem;
      JMenu fileMenu;
      JMenu renderMenu;
      JMenu helpMenu;

      // Make the File Menu
      fileMenu = new JMenu(MessageStrings.getString("Evolvo.File_15")); //$NON-NLS-1$
      fileMenu.setMnemonic(KeyEvent.VK_F);

      // Save menu item
      menuitem = new JMenuItem(MessageStrings.getString("Evolvo.Save_Genotype_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_S);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_S, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      fileMenu.add(menuitem);

      // Load menu item
      menuitem = new JMenuItem(MessageStrings.getString("Evolvo.Load_Genotype_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_L);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_L, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      fileMenu.add(menuitem);
      fileMenu.addSeparator();

      // Display
      menuitem = new JMenuItem(MessageStrings.getString("Evolvo.Display_Genotype_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_D);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_D, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      fileMenu.add(menuitem);

      // render saved genotype menu item
      menuitem = new JMenuItem(MessageStrings.getString("Evolvo.Render_Saved_Genotype_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_E);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_E, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      fileMenu.add(menuitem);

      fileMenu.addSeparator();

      // Display system console
      menuitem = new JMenuItem(MessageStrings.getString("Evolvo.Display_System_Console_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_C);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_C, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      fileMenu.add(menuitem);

      if (!Constants.isMac || true) // disable mac menu for now
      {
         // On Mac, these are handled elsewhere

         fileMenu.addSeparator();

         // Preferences menu item
         menuitem = new JMenuItem(MessageStrings.getString("Evolvo.Preferences_Menu")); //$NON-NLS-1$
         menuitem.setMnemonic(KeyEvent.VK_P);
         menuitem.addActionListener(this);
         fileMenu.add(menuitem);

         // Exit menu item
         menuitem = new JMenuItem(MessageStrings.getString("Evolvo.Exit_Menu")); //$NON-NLS-1$
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
            Class macHandlerClass =
               Class.forName("org.maloi.evolvo.gui.MacMenu"); //$NON-NLS-1$
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
      renderMenu = new JMenu(MessageStrings.getString("Evolvo.Render_Menu")); //$NON-NLS-1$
      renderMenu.setMnemonic(KeyEvent.VK_R);

      // New Generation menu item
      menuitem = new JMenuItem(MessageStrings.getString("Evolvo.New_Generation_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_N);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_N, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      renderMenu.add(menuitem);
      renderMenu.addSeparator();

      // Render Image menu item
      menuitem = new JMenuItem(MessageStrings.getString("Evolvo.Render_Image_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_R);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_R, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      renderMenu.add(menuitem);

      // Make stop menu item
      menuitem = new JMenuItem(MessageStrings.getString("Evolvo.Stop_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_T);
      menuitem.setAccelerator(
         KeyStroke.getKeyStroke(KeyEvent.VK_T, Constants.KEY_MASK));
      menuitem.addActionListener(this);
      renderMenu.add(menuitem);

      // Make Render Menu
      helpMenu = new JMenu(MessageStrings.getString("Evolvo.Help_Menu")); //$NON-NLS-1$
      helpMenu.setMnemonic(KeyEvent.VK_H);

      if (!Constants.isMac)
      {
         // On Macs, this is handled elsewhere...

         // About Evolvo menu item
         menuitem = new JMenuItem(MessageStrings.getString("Evolvo.About_Evolvo_Menu")); //$NON-NLS-1$
         menuitem.setMnemonic(KeyEvent.VK_A);
         menuitem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_A, Constants.KEY_MASK));
         menuitem.addActionListener(this);
         helpMenu.add(menuitem);
         helpMenu.addSeparator();
      }

      // GPL License menu item
      menuitem = new JMenuItem(MessageStrings.getString("Evolvo.GPL_License_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_G);
      menuitem.addActionListener(this);
      helpMenu.add(menuitem);

      // Warranty Info menu item
      menuitem = new JMenuItem(MessageStrings.getString("Evolvo.Warranty_Information_Menu")); //$NON-NLS-1$
      menuitem.setMnemonic(KeyEvent.VK_W);
      menuitem.addActionListener(this);
      helpMenu.add(menuitem);

      // Redistribution Info menu item
      menuitem = new JMenuItem(MessageStrings.getString("Evolvo.Redistribution_Information_Menu")); //$NON-NLS-1$
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

      buttonPanel = new ImageButtonPanel(makeFirstGeneration());

      mainPanel.add(buttonPanel);
      mainPanel.setVisible(true);

      return mainPanel;
   }

   JPanel makeAuxiliaryPanel()
   {
      JPanel auxPanel = new JPanel();
      JButton auxButton;

      auxButton = new JButton(MessageStrings.getString("Evolvo.New_Generation_33")); //$NON-NLS-1$
      auxButton.setMnemonic(KeyEvent.VK_N);
      auxButton.addActionListener(this);

      auxPanel.add(auxButton);

      auxButton = new JButton(MessageStrings.getString("Evolvo.Render_Image_34")); //$NON-NLS-1$
      auxButton.setMnemonic(KeyEvent.VK_R);
      auxButton.addActionListener(this);

      auxPanel.add(auxButton);
      
      auxButton = new JButton("Explore Image"); //$NON-NLS-1$
      auxButton.setMnemonic(KeyEvent.VK_E);
      auxButton.addActionListener(this);
      
      auxPanel.add(auxButton);

      return auxPanel;
   }

   private RendererInterface[] makeFirstGeneration()
   {
      int riIndex;
      ExpressionTree expression;
      Random r = new Random();

      for (riIndex = 0; riIndex < 9; riIndex++)
      {
         expression =
            ExpressionTreeGenerator.generate(new Random(r.nextLong()), true);

         ri[riIndex] =
            new StandardRenderer(
               expression,
               Constants.THUMBNAIL_WIDTH,
               Constants.THUMBNAIL_HEIGHT);
      }

      return ri;
   }

   void makeNewGeneration()
   {
      int selected = buttonPanel.getSelectedButton();

      Random randomNumber = new Random();

      if (selected != -1)
      {
         ExpressionTree originalExpression = ri[selected].getExpression();

         for (int i = 0; i < 9; i++)
         {
            // Don't do anything to the selected button
            if (i != selected)
            {
               Random newRandom = new Random(randomNumber.nextLong());

               ExpressionTree expression =
                  Mutator.mutate(newRandom, originalExpression.getClone());

               ri[i] =
                  new StandardRenderer(
                     expression,
                     Constants.THUMBNAIL_WIDTH,
                     Constants.THUMBNAIL_HEIGHT);
            }
         }
      }
      else
      {
         ri = makeFirstGeneration();
      }

      buttonPanel.setRenderers(ri, selected);
   }

   void doPreferencesDialog()
   {
      settingsDialogBox.showDialog(this);
   }

   void saveGenotype()
   {
      int selection = buttonPanel.getSelectedButton();

      if (selection == -1)
      {
         JOptionPane.showMessageDialog(
            this,
            MessageStrings.getString("Evolvo.Select_Genotype"), //$NON-NLS-1$
            MessageStrings.getString("Evolvo.Error_Title"), //$NON-NLS-1$
            JOptionPane.ERROR_MESSAGE);
      }
      else
      {
         GenotypeFileIO.putGenotypeToFile(this, ri[selection].getExpression());
      }
   }

   void displayGenotype()
   {
      int selection = buttonPanel.getSelectedButton();

      if (selection == -1)
      {
         JOptionPane.showMessageDialog(
            this,
            MessageStrings.getString("Evolvo.Select_Genotype"), //$NON-NLS-1$
            MessageStrings.getString("Evolvo.Error_Title"), //$NON-NLS-1$
            JOptionPane.ERROR_MESSAGE);
      }
      else
      {
         String displayString = ri[selection].getExpression().toString();

         TextDialog displayDialog = new TextDialog(displayString);
         displayDialog.scrollToRow(0);
         displayDialog.setVisible(true);
      }
   }

   void renderLoadedImage()
   {
      ExpressionTree expression = GenotypeFileIO.getGenotypeFromFile(this);
		RenderFrame theRenderFrame;

      if (expression != null)
      {
          theRenderFrame =
            new RenderFrame(
               new StandardRenderer(
                  expression,
                  settings.getIntegerProperty("render.width"), //$NON-NLS-1$
                  settings.getIntegerProperty("render.height")), //$NON-NLS-1$
               null);
         
			theRenderFrame.toFront();
      }
   }

   void loadGenotype()
   {
      int result =
         JOptionPane.showConfirmDialog(
            this,
            MessageStrings.getString("Evolvo.Loading_Genotype_41"), //$NON-NLS-1$
            MessageStrings.getString("Evolvo.Warning_43"), //$NON-NLS-1$
            JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.NO_OPTION)
      {
         return;
      }

      ExpressionTree expression = GenotypeFileIO.getGenotypeFromFile(this);

      if (expression != null)
      {
         ri[0] =
            new StandardRenderer(
               expression,
               Constants.THUMBNAIL_WIDTH,
               Constants.THUMBNAIL_HEIGHT);
         buttonPanel.setRendererAtIndex(ri[0], 0);
      }
   }

   void renderImage()
   {
      int selection = buttonPanel.getSelectedButton();
		RenderFrame theRenderFrame;

      if (selection == -1)
      {
         JOptionPane.showMessageDialog(
            this,
            MessageStrings.getString("Evolvo.Select_Image_For_Render_44"), //$NON-NLS-1$
            MessageStrings.getString("Evolvo.Error_Title"), //$NON-NLS-1$
            JOptionPane.ERROR_MESSAGE);
      }
      else
      {
         theRenderFrame =
            new RenderFrame(
               ri[selection],
               buttonPanel.getImageForButton(selection));
               
         theRenderFrame.toFront();
      }
   }

	void exploreImage()
	{
		int selection = buttonPanel.getSelectedButton();
		ExplorerFrame theExplorerFrame;

		if (selection == -1)
		{
			JOptionPane.showMessageDialog(
				this,
				MessageStrings.getString("Evolvo.Select_Image_For_Render_44"), //$NON-NLS-1$
				MessageStrings.getString("Evolvo.Error_Title"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			theExplorerFrame =
				new ExplorerFrame(
					ri[selection]);
               
			theExplorerFrame.toFront();
		}
	}

   public void actionPerformed(ActionEvent e)
   {
      String cmd = e.getActionCommand();

      if (cmd.equals(MessageStrings.getString("Evolvo.Save_Genotype_Menu"))) //$NON-NLS-1$
      {
         saveGenotype();
      }
      else if (cmd.equals(MessageStrings.getString("Evolvo.Load_Genotype_Menu"))) //$NON-NLS-1$
      {
         loadGenotype();
      }
      else if (cmd.equals(MessageStrings.getString("Evolvo.Display_Genotype_Menu"))) //$NON-NLS-1$
      {
         displayGenotype();
      }
      else if (cmd.equals(MessageStrings.getString("Evolvo.Display_System_Console_Menu"))) //$NON-NLS-1$
      {
         console.setVisible(true);
      }
      else if (cmd.equals(MessageStrings.getString("Evolvo.Render_Saved_Genotype_Menu"))) //$NON-NLS-1$
      {
         renderLoadedImage();
      }
      else if (cmd.equals(MessageStrings.getString("Evolvo.Preferences_Menu"))) //$NON-NLS-1$
      {
         doPreferencesDialog();
      }
      else if (cmd.equals(MessageStrings.getString("Evolvo.Exit_Menu"))) //$NON-NLS-1$
      {
         System.exit(0);
      }
      else if (cmd.equals(MessageStrings.getString("Evolvo.New_Generation_Menu"))) //$NON-NLS-1$
      {
         makeNewGeneration();
      }
      else if (cmd.equals(MessageStrings.getString("Evolvo.Render_Image_Menu"))) //$NON-NLS-1$
      {
         renderImage();
      }
		else if (cmd.equals("Explore Image")) //$NON-NLS-1$
		{
			exploreImage();
		}
      else if (cmd.equals(MessageStrings.getString("Evolvo.Stop_Menu"))) //$NON-NLS-1$
      {
         buttonPanel.stop();
      }
      else if (cmd.equals(MessageStrings.getString("Evolvo.About_Evolvo_Menu"))) //$NON-NLS-1$
      {
         SplashWindow splash = new SplashWindow(true, this);
         
         splash.toFront();
      }
      else if (cmd.equals(MessageStrings.getString("Evolvo.GPL_License_Menu"))) //$NON-NLS-1$
      {
         showLicenseDialog(0);
      }
      else if (cmd.equals(MessageStrings.getString("Evolvo.Warranty_Information_Menu"))) //$NON-NLS-1$
      {
         showLicenseDialog(LicenseText.warrantyInfoStart);
      }
      else if (cmd.equals(MessageStrings.getString("Evolvo.Redistribution_Information_Menu"))) //$NON-NLS-1$
      {
         showLicenseDialog(LicenseText.redistributeInfoStart);
      }
      else
      {
         console.println(MessageStrings.getString("Evolvo.Unknown_ActionCommand___60") + cmd); //$NON-NLS-1$
      }
   }

   void showLicenseDialog(int row)
   {
      TextDialog licenseDialog = new TextDialog(LicenseText.license);
      licenseDialog.scrollToRow(row);
      licenseDialog.setVisible(true);
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
