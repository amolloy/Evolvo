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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.io.Exporter;
import org.maloi.evolvo.resources.Constants;
import org.maloi.evolvo.settings.GlobalSettings;

/**
 * Generates a standard dialog box for adjusting the settings found in the 
 * globalSettings object, and updates the globalSettings object to reflect 
 * user input.
 *
 * This dialog box is really horrible, and I invite anyone to please, please
 * make it better.  Please.
 *
 */
public class SettingsDialog implements ActionListener
{
   static SettingsDialog _instance;

   JTabbedPane dialogTabbedPane;
   static GlobalSettings settings = GlobalSettings.getInstance();
   DoubleFieldSlider[] fields;
   DoubleFieldSlider x, y, r, theta;
   DoubleFieldSlider complexity, depreciation, variableProb;
   IntegerField widthField, heightField;

   DoubleFieldSlider mutate_change,
      mutate_new_expression,
      mutate_scalar_change_value,
      mutate_to_variable,
      mutate_to_scalar,
      mutate_change_function,
      mutate_new_expression_arg,
      mutate_become_arg,
      mutate_arg_to_child_arg;

   JComboBox preferredPlugin;
   JTextField plugins;

   SystemConsole console = SystemConsole.getInstance();

   public static SettingsDialog getInstance()
   {
      if (_instance == null)
      {
         _instance = new SettingsDialog();
      }

      return _instance;
   }

   Box createStandardOptions()
   {
      JPanel generationOptions = new JPanel(new GridLayout(3, 1));
      Border generationOptionsBorder = BorderFactory.createEtchedBorder();

      generationOptions.setBorder(
         BorderFactory.createTitledBorder(
            generationOptionsBorder,
            GUIMessages.getString("SettingsDialog.Generation_Options"))); //$NON-NLS-1$

      complexity =
         new DoubleFieldSlider(
            settings.getDoubleProperty("complexity"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            GUIMessages.getString("SettingsDialog.Complexity")); //$NON-NLS-1$
      depreciation =
         new DoubleFieldSlider(
            settings.getDoubleProperty("depreciation"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            GUIMessages.getString("SettingsDialog.Depreciation")); //$NON-NLS-1$
      variableProb =
         new DoubleFieldSlider(
            settings.getDoubleProperty("variable.probability"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            GUIMessages.getString("SettingsDialog.Variable_Probability")); //$NON-NLS-1$

      generationOptions.add(complexity);
      generationOptions.add(depreciation);
      generationOptions.add(variableProb);

      // create the variable probabilities panel
      JPanel variableProbs = new JPanel(new GridLayout(4, 1));
      Border variableProbsBorder = BorderFactory.createEtchedBorder();

      variableProbs.setBorder(
         BorderFactory.createTitledBorder(
            variableProbsBorder,
            GUIMessages.getString("SettingsDialog.Variable_Probabilities"))); //$NON-NLS-1$

      x =
         new DoubleFieldSlider(
            settings.getDoubleProperty("variable.x"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            "x"); //$NON-NLS-1$
      y =
         new DoubleFieldSlider(
            settings.getDoubleProperty("variable.y"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            "y"); //$NON-NLS-1$
      r =
         new DoubleFieldSlider(
            settings.getDoubleProperty("variable.r"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            "r"); //$NON-NLS-1$
      theta =
         new DoubleFieldSlider(
            settings.getDoubleProperty("variable.theta"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            "theta"); //$NON-NLS-1$

      variableProbs.add(x);
      variableProbs.add(y);
      variableProbs.add(r);
      variableProbs.add(theta);

      // create the standardOptions panel
      Box standardOptions = new Box(BoxLayout.Y_AXIS);
      standardOptions.add(generationOptions);
      standardOptions.add(variableProbs);

      return standardOptions;
   }

   JComponent createOperatorOptions()
   {
      // create the operatorControlPanel panel
      Box operatorControlPanel = new Box(BoxLayout.Y_AXIS);

      fields = getOperatorControls();
      for (int index = 0; index < fields.length; index++)
      {
         operatorControlPanel.add(fields[index]);
      }

      // create the scrollableControlPanel panel
      JScrollPane scrollableControlPanel =
         new JScrollPane(
            operatorControlPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

      return scrollableControlPanel;
   }

   JComponent createMutateOptions()
   {
      // create the mutation options panel      
      JPanel mutateOptions = new JPanel(new GridLayout(10, 1));
      Border mutateOptionsBorder = BorderFactory.createEtchedBorder();

      mutateOptions.setBorder(
         BorderFactory.createTitledBorder(
            mutateOptionsBorder,
            GUIMessages.getString("SettingsDialog.Mutation_Options"))); //$NON-NLS-1$

      mutate_change =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.change"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            GUIMessages.getString("SettingsDialog.Change_Probability")); //$NON-NLS-1$
      mutate_new_expression =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.new_expression"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            GUIMessages.getString("SettingsDialog.New_Expression")); //$NON-NLS-1$
      mutate_scalar_change_value =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.scalar_change_value"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            GUIMessages.getString("SettingsDialog.Change_Scalar_Value")); //$NON-NLS-1$
      mutate_to_variable =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.to_variable"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            GUIMessages.getString("SettingsDialog.Change_to_a_Variable")); //$NON-NLS-1$
      mutate_to_scalar =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.to_scalar"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            GUIMessages.getString("SettingsDialog.Change_to_a_Scalar")); //$NON-NLS-1$
      mutate_change_function =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.change_function"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            GUIMessages.getString("SettingsDialog.Change_Functions")); //$NON-NLS-1$
      mutate_new_expression_arg =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.new_expression_arg"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            GUIMessages.getString("SettingsDialog.Generate_A_New_Argument")); //$NON-NLS-1$
      mutate_become_arg =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.become_arg"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            GUIMessages.getString("SettingsDialog.Become_A_Child_Argument")); //$NON-NLS-1$
      mutate_arg_to_child_arg =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.arg_to_child_arg"), //$NON-NLS-1$
            0.0,
            0.0,
            1.0,
            4,
            GUIMessages.getString("SettingsDialog.Change_Argument_To_Child_Argument")); //$NON-NLS-1$

      mutateOptions.add(mutate_change);
      mutateOptions.add(mutate_new_expression);
      mutateOptions.add(mutate_scalar_change_value);
      mutateOptions.add(mutate_to_variable);
      mutateOptions.add(mutate_to_scalar);
      mutateOptions.add(mutate_change_function);
      mutateOptions.add(mutate_new_expression_arg);
      mutateOptions.add(mutate_become_arg);
      mutateOptions.add(mutate_arg_to_child_arg);

      // Put a little space between the lines...
      JPanel fillerPanel = new JPanel();
      fillerPanel.setPreferredSize(new Dimension(300, 10));
      mutateOptions.add(fillerPanel, BorderLayout.CENTER);

      return mutateOptions;
   }

   Box createAdvancedOptions()
   {
      // Create the Advanced tab
      Box advancedOptions = new Box(BoxLayout.Y_AXIS);
      JPanel exporterOptions = new JPanel(new BorderLayout());
      Border advancedOptionsBorder = BorderFactory.createEtchedBorder();

      exporterOptions.setBorder(
         BorderFactory.createTitledBorder(advancedOptionsBorder, GUIMessages.getString("SettingsDialog.Exporter"))); //$NON-NLS-1$

      // Create the preferred exporter drop down box
      String preferredPluginName = settings.getStringProperty("usePlugin"); //$NON-NLS-1$

      preferredPlugin = new JComboBox(Exporter.getExporterNames());
      preferredPlugin.setEditable(false);
      preferredPlugin.setEnabled(Exporter.isAvailable());
      preferredPlugin.setSelectedItem(preferredPluginName);

      JButton rescanButton = new JButton(GUIMessages.getString("SettingsDialog.Rescan")); //$NON-NLS-1$
      rescanButton.addActionListener(this);

      Dimension fillerSize = new Dimension(5, 0);

      JPanel pluginSelectAndLabel = new JPanel();
      Box pluginTextAndRescan = new Box(BoxLayout.X_AXIS);

      pluginSelectAndLabel.add(preferredPlugin);

      exporterOptions.add(pluginSelectAndLabel, BorderLayout.NORTH);

      plugins = new JTextField(settings.getStringProperty("plugins")); //$NON-NLS-1$

      pluginTextAndRescan.add(plugins);
      pluginTextAndRescan.add(
         new Box.Filler(fillerSize, fillerSize, fillerSize));
      pluginTextAndRescan.add(rescanButton);

      exporterOptions.add(pluginTextAndRescan, BorderLayout.SOUTH);

      // Put a little space between the lines...
      JPanel fillerPanel = new JPanel();
      fillerPanel.setPreferredSize(new Dimension(300, 10));
      exporterOptions.add(fillerPanel, BorderLayout.CENTER);

      // And finally add it to the advanced Panel
      advancedOptions.add(exporterOptions);

      // Fill up the extra space...
      fillerPanel = new JPanel();
      Dimension filledSize = advancedOptions.getPreferredSize();
      fillerPanel.setPreferredSize(new Dimension(300, 400 - filledSize.height));

      advancedOptions.add(fillerPanel);

      return advancedOptions;
   }

   protected SettingsDialog()
   {
      dialogTabbedPane = new JTabbedPane();
      dialogTabbedPane.setPreferredSize(new Dimension(550, 400));
      dialogTabbedPane.add(GUIMessages.getString("SettingsDialog.Settings"), createStandardOptions()); //$NON-NLS-1$
      dialogTabbedPane.add(GUIMessages.getString("SettingsDialog.Operators"), createOperatorOptions()); //$NON-NLS-1$
      dialogTabbedPane.add(GUIMessages.getString("SettingsDialog.Mutate"), createMutateOptions()); //$NON-NLS-1$
      dialogTabbedPane.add(GUIMessages.getString("SettingsDialog.Advanced"), createAdvancedOptions()); //$NON-NLS-1$
   }

   /** Creates the settings dialog box.
     * Returns the updated globalSettings object.  If the user presses 
     * cancel, the globalSettings object is returned unchanged. */
   public void showDialog(JFrame f)
   {
      boolean exitFlag;

      do
      {
         exitFlag = true;

         int result =
            JOptionPane.showOptionDialog(
               f,
               new Object[] { dialogTabbedPane },
               GUIMessages.getString("SettingsDialog.Settings"), //$NON-NLS-1$
               JOptionPane.OK_CANCEL_OPTION,
               JOptionPane.PLAIN_MESSAGE,
               null,
               null,
               null);

         if (result == JOptionPane.OK_OPTION)
         {
            if (depreciation.getValue() < 0.001)
            {
               JOptionPane.showMessageDialog(
                  f,
                  GUIMessages.getString("SettingsDialog.Depreciation_must_not_be_less_than_0.001.")); //$NON-NLS-1$
               exitFlag = false;
               continue;
            }

            // We don't want them to be able to set ALL of the operators to 
            // >0.1 probability, so we walk through them
            // and test that they have a high enough value...
            boolean flag = false;
            for (int i = 0; i < fields.length; i++)
            {
               if (fields[i].getValue() >= 0.1)
               {
                  flag = true;
               }
            }

            if (flag == false)
            {
               JOptionPane.showMessageDialog(
                  f,
                  GUIMessages.getString("SettingsDialog.At_least_one_operator_must_have_probability_greater_than_or_equal_to_0.1.")); //$NON-NLS-1$
               exitFlag = false;
               continue;
            }
            flag = false;
            if (x.getValue() > 0.1)
            {
               flag = true;
            }
            if (y.getValue() > 0.1)
            {
               flag = true;
            }
            if (r.getValue() > 0.1)
            {
               flag = true;
            }
            if (theta.getValue() > 0.1)
            {
               flag = true;
            }

            if (flag == false)
            {
               JOptionPane.showMessageDialog(
                  f,
                  GUIMessages.getString("SettingsDialog.At_least_one_variable_must_have_probability_greater_than_or_equal_to_0.1.")); //$NON-NLS-1$
               exitFlag = false;
            }
            else
            {
               settings.setDoubleProperty("complexity", complexity.getValue()); //$NON-NLS-1$
               settings.setDoubleProperty(
                  "depreciation", //$NON-NLS-1$
                  depreciation.getValue());
               settings.setDoubleProperty(
                  "variable.probability", //$NON-NLS-1$
                  variableProb.getValue());
               settings.setDoubleProperty("variable.x", x.getValue()); //$NON-NLS-1$
               settings.setDoubleProperty("variable.y", y.getValue()); //$NON-NLS-1$
               settings.setDoubleProperty("variable.r", r.getValue()); //$NON-NLS-1$
               settings.setDoubleProperty("variable.theta", theta.getValue()); //$NON-NLS-1$
               OperatorInterface ops[] = settings.getOperators();
               for (int i = 0; i < fields.length; i++)
               {
                  settings.setDoubleProperty(
                     Constants.operatorPrefix + ops[i].getName(),
                     fields[i].getValue());
               }

               settings.setDoubleProperty(
                  "mutate.change", //$NON-NLS-1$
                  mutate_change.getValue());
               settings.setDoubleProperty(
                  "mutate.new_expression", //$NON-NLS-1$
                  mutate_new_expression.getValue());
               settings.setDoubleProperty(
                  "mutate.scalar_change_value", //$NON-NLS-1$
                  mutate_scalar_change_value.getValue());
               settings.setDoubleProperty(
                  "mutate.to_variable", //$NON-NLS-1$
                  mutate_to_variable.getValue());
               settings.setDoubleProperty(
                  "mutate.to_scalar", //$NON-NLS-1$
                  mutate_to_scalar.getValue());
               settings.setDoubleProperty(
                  "mutate.change_function", //$NON-NLS-1$
                  mutate_change_function.getValue());
               settings.setDoubleProperty(
                  "mutate.new_expression_arg", //$NON-NLS-1$
                  mutate_new_expression_arg.getValue());
               settings.setDoubleProperty(
                  "mutate.become_arg", //$NON-NLS-1$
                  mutate_become_arg.getValue());
               settings.setDoubleProperty(
                  "mutate.arg_to_child_arg", //$NON-NLS-1$
                  mutate_arg_to_child_arg.getValue());

               // The exporter needs to be told if a new exporter has been
               // chosen, as well as the new exporter being stored in the
               // properties file.
               Exporter.setPlugin((String)preferredPlugin.getSelectedItem());
               settings.setProperty(
                  "usePlugin", //$NON-NLS-1$
                  (String)preferredPlugin.getSelectedItem());

               settings.setProperty("plugins", plugins.getText()); //$NON-NLS-1$

               try
               {
                  settings.storeProperties();
               }
               catch (Exception e)
               {
                  console.println(GUIMessages.getString("SettingsDialog.Error_storing_new_settings")); //$NON-NLS-1$
               }
            }
         }
      }
      while (exitFlag == false);
   }

   /** Utility method to generate a set of JDoubleFieldSlider controls for all
     * the operators listed in the operators list stored in globalSettings. 
     */
   static DoubleFieldSlider[] getOperatorControls()
   {
      OperatorInterface ops[] = settings.getOperators();

      DoubleFieldSlider fields[] = new DoubleFieldSlider[ops.length];

      for (int index = 0; index < ops.length; index++)
      {
         fields[index] =
            new DoubleFieldSlider(
               settings.getDoubleProperty(
                  Constants.operatorPrefix + ops[index].getName()),
               0.0,
               0.0,
               1.0,
               4,
               ops[index].getName());
      }

      return fields;
   }

   public void actionPerformed(ActionEvent e)
   {
      if (e.getSource().equals(GUIMessages.getString("SettingsDialog.Rescan"))) //$NON-NLS-1$
      {
         // Go ahead and add the plugins field to the settings...
         settings.setProperty("plugins", plugins.getText()); //$NON-NLS-1$

         // Check for new exporter plugins...
         Exporter.rescan();

         // Then make sure our combo box is up to date
         preferredPlugin.removeAllItems();

         String temp[] = Exporter.getExporterNames();

         for (int i = 0; i < temp.length; i++)
         {
            preferredPlugin.addItem(temp[i]);
         }

         preferredPlugin.setEnabled(Exporter.isAvailable());
      }
   }
}
