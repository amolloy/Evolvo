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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.asm.evolvo.expressiontree.operators.OperatorInterface;
import org.asm.evolvo.io.Exporter;
import org.asm.evolvo.settings.GlobalSettings;

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

   public static SettingsDialog getInstance()
   {
      if (_instance == null)
      {
         _instance = new SettingsDialog();
      }

      return _instance;
   }

   protected SettingsDialog()
   {
      // create the generation options panel      
      JPanel generationOptions = new JPanel(new GridLayout(3, 1));
      Border generationOptionsBorder = BorderFactory.createEtchedBorder();

      generationOptions.setBorder(
         BorderFactory.createTitledBorder(
            generationOptionsBorder,
            "Generation Options"));

      complexity =
         new DoubleFieldSlider(
            settings.getDoubleProperty("complexity"),
            0.0,
            0.0,
            1.0,
            4,
            "Complexity");
      depreciation =
         new DoubleFieldSlider(
            settings.getDoubleProperty("depreciation"),
            0.0,
            0.0,
            1.0,
            4,
            "Depreciation");
      variableProb =
         new DoubleFieldSlider(
            settings.getDoubleProperty("variable.probability"),
            0.0,
            0.0,
            1.0,
            4,
            "Variable Probability");

      generationOptions.add(complexity);
      generationOptions.add(depreciation);
      generationOptions.add(variableProb);

      // create the variable probabilities panel
      JPanel variableProbs = new JPanel(new GridLayout(4, 1));
      Border variableProbsBorder = BorderFactory.createEtchedBorder();

      variableProbs.setBorder(
         BorderFactory.createTitledBorder(
            variableProbsBorder,
            "Variable Probabilities"));

      x =
         new DoubleFieldSlider(
            settings.getDoubleProperty("variable.x"),
            0.0,
            0.0,
            1.0,
            4,
            "x");
      y =
         new DoubleFieldSlider(
            settings.getDoubleProperty("variable.y"),
            0.0,
            0.0,
            1.0,
            4,
            "y");
      r =
         new DoubleFieldSlider(
            settings.getDoubleProperty("variable.r"),
            0.0,
            0.0,
            1.0,
            4,
            "r");
      theta =
         new DoubleFieldSlider(
            settings.getDoubleProperty("variable.theta"),
            0.0,
            0.0,
            1.0,
            4,
            "theta");

      variableProbs.add(x);
      variableProbs.add(y);
      variableProbs.add(r);
      variableProbs.add(theta);

      // create the standardOptions panel
      Box standardOptions = new Box(BoxLayout.Y_AXIS);
      standardOptions.add(generationOptions);
      standardOptions.add(variableProbs);

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

      // create the render options panel
      JPanel renderSizeOptions = new JPanel(new GridLayout(2, 1));
      Border renderSizeBorder = BorderFactory.createEtchedBorder();
      renderSizeOptions.setBorder(
         BorderFactory.createTitledBorder(renderSizeBorder, "Render Size"));

      // width
      JLabel widthLabel = new JLabel("Width");
      renderSizeOptions.add(widthLabel);
      widthField =
         new IntegerField(6, settings.getIntegerProperty("render.width"));
      renderSizeOptions.add(widthField);

      // height
      JLabel heightLabel = new JLabel("Height");
      renderSizeOptions.add(heightLabel);
      heightField =
         new IntegerField(6, settings.getIntegerProperty("render.height"));
      renderSizeOptions.add(heightField);

      // Fill up the extra space...
      JPanel fillerPanel = new JPanel();
      Dimension filledSize = renderSizeOptions.getPreferredSize();
      fillerPanel.setPreferredSize(new Dimension(300, 400 - filledSize.height));

      // the actual render panel
      Box renderOptionsPanel = new Box(BoxLayout.Y_AXIS);
      renderOptionsPanel.add(renderSizeOptions);
      renderOptionsPanel.add(fillerPanel);

      // create the mutation options panel      
      JPanel mutateOptions = new JPanel(new GridLayout(9, 1));
      Border mutateOptionsBorder = BorderFactory.createEtchedBorder();

      mutateOptions.setBorder(
         BorderFactory.createTitledBorder(
            mutateOptionsBorder,
            "Mutation Options"));

      mutate_change =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.change"),
            0.0,
            0.0,
            1.0,
            4,
            "Change Probability");
      mutate_new_expression =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.new_expression"),
            0.0,
            0.0,
            1.0,
            4,
            "New Expression");
      mutate_scalar_change_value =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.scalar_change_value"),
            0.0,
            0.0,
            1.0,
            4,
            "Change Scalar Value");
      mutate_to_variable =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.to_variable"),
            0.0,
            0.0,
            1.0,
            4,
            "Change to a Variable");
      mutate_to_scalar =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.to_scalar"),
            0.0,
            0.0,
            1.0,
            4,
            "Change to a Scalar");
      mutate_change_function =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.change_function"),
            0.0,
            0.0,
            1.0,
            4,
            "Change Functions");
      mutate_new_expression_arg =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.new_expression_arg"),
            0.0,
            0.0,
            1.0,
            4,
            "Generate A New Argument");
      mutate_become_arg =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.become_arg"),
            0.0,
            0.0,
            1.0,
            4,
            "Become A Child Argument");
      mutate_arg_to_child_arg =
         new DoubleFieldSlider(
            settings.getDoubleProperty("mutate.arg_to_child_arg"),
            0.0,
            0.0,
            1.0,
            4,
            "Change Argument To Child Argument");

      mutateOptions.add(mutate_change);
      mutateOptions.add(mutate_new_expression);
      mutateOptions.add(mutate_scalar_change_value);
      mutateOptions.add(mutate_to_variable);
      mutateOptions.add(mutate_to_scalar);
      mutateOptions.add(mutate_change_function);
      mutateOptions.add(mutate_new_expression_arg);
      mutateOptions.add(mutate_become_arg);
      mutateOptions.add(mutate_arg_to_child_arg);

      // Create the Advanced tab
      Box advancedOptions = new Box(BoxLayout.Y_AXIS);
      JPanel exporterOptions = new JPanel(new BorderLayout());
      Border advancedOptionsBorder = BorderFactory.createEtchedBorder();

      exporterOptions.setBorder(
         BorderFactory.createTitledBorder(advancedOptionsBorder, "Exporter"));

      // Create the preferred exporter drop down box
      String preferredPluginName = settings.getStringProperty("usePlugin");

      preferredPlugin = new JComboBox(Exporter.getExporterNames());
      preferredPlugin.setEditable(false);
      preferredPlugin.setEnabled(Exporter.isAvailable());
      preferredPlugin.setSelectedItem(preferredPluginName);

      JButton rescanButton = new JButton("Rescan");
      rescanButton.addActionListener(this);

      Dimension fillerSize = new Dimension(5, 0);

      JPanel pluginSelectAndLabel = new JPanel();
      Box pluginTextAndRescan = new Box(BoxLayout.X_AXIS);

      pluginSelectAndLabel.add(preferredPlugin);

      exporterOptions.add(pluginSelectAndLabel, BorderLayout.NORTH);

      plugins = new JTextField(settings.getStringProperty("plugins"));

      pluginTextAndRescan.add(plugins);
      pluginTextAndRescan.add(
         new Box.Filler(fillerSize, fillerSize, fillerSize));
      pluginTextAndRescan.add(rescanButton);

      exporterOptions.add(pluginTextAndRescan, BorderLayout.SOUTH);

      // Put a little space between the lines...
      fillerPanel = new JPanel();
      fillerPanel.setPreferredSize(new Dimension(300, 10));
      exporterOptions.add(fillerPanel, BorderLayout.CENTER);

      // And finally add it to the advanced Panel
      advancedOptions.add(exporterOptions);

      // Fill up the extra space...
      fillerPanel = new JPanel();
      filledSize = advancedOptions.getPreferredSize();
      fillerPanel.setPreferredSize(new Dimension(300, 400 - filledSize.height));

      advancedOptions.add(fillerPanel);

      // create the tabbed pane
      dialogTabbedPane = new JTabbedPane();
      dialogTabbedPane.setPreferredSize(new Dimension(550, 400));
      dialogTabbedPane.add("Settings", standardOptions);
      dialogTabbedPane.add("Operators", scrollableControlPanel);
      dialogTabbedPane.add("Render", renderOptionsPanel);
      dialogTabbedPane.add("Mutate", mutateOptions);
      dialogTabbedPane.add("Advanced", advancedOptions);
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
               "Settings",
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
                  "Depreciation must not be less than 0.001.");
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
                  "At least one operator must have probability greater "
                     + "than or equal to 0.1.");
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
                  "At least one variable must have probability greater "
                     + "than or equal to 0.1.");
               exitFlag = false;
            }
            else
            {
               settings.setDoubleProperty("complexity", complexity.getValue());
               settings.setDoubleProperty(
                  "depreciation",
                  depreciation.getValue());
               settings.setDoubleProperty(
                  "variable.probability",
                  variableProb.getValue());
               settings.setDoubleProperty("variable.x", x.getValue());
               settings.setDoubleProperty("variable.y", y.getValue());
               settings.setDoubleProperty("variable.r", r.getValue());
               settings.setDoubleProperty("variable.theta", theta.getValue());
               OperatorInterface ops[] = settings.getOperators();
               for (int i = 0; i < fields.length; i++)
               {
                  settings.setDoubleProperty(
                     ops[i].getName(),
                     fields[i].getValue());
               }
               settings.setIntegerProperty(
                  "render.width",
                  widthField.getValue());
               settings.setIntegerProperty(
                  "render.height",
                  heightField.getValue());
               settings.setDoubleProperty(
                  "mutate.change",
                  mutate_change.getValue());
               settings.setDoubleProperty(
                  "mutate.new_expression",
                  mutate_new_expression.getValue());
               settings.setDoubleProperty(
                  "mutate.scalar_change_value",
                  mutate_scalar_change_value.getValue());
               settings.setDoubleProperty(
                  "mutate.to_variable",
                  mutate_to_variable.getValue());
               settings.setDoubleProperty(
                  "mutate.to_scalar",
                  mutate_to_scalar.getValue());
               settings.setDoubleProperty(
                  "mutate.change_function",
                  mutate_change_function.getValue());
               settings.setDoubleProperty(
                  "mutate.new_expression_arg",
                  mutate_new_expression_arg.getValue());
               settings.setDoubleProperty(
                  "mutate.become_arg",
                  mutate_become_arg.getValue());
               settings.setDoubleProperty(
                  "mutate.arg_to_child_arg",
                  mutate_arg_to_child_arg.getValue());

               // The exporter needs to be told if a new exporter has been
               // chosen, as well as the new exporter being stored in the
               // properties file.
               Exporter.setPlugin((String) preferredPlugin.getSelectedItem());
               settings.setProperty(
                  "usePlugin",
                  (String) preferredPlugin.getSelectedItem());

               settings.setProperty("plugins", plugins.getText());
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
               settings.getDoubleProperty(ops[index].getName()),
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
      if (e.getSource().equals("Rescan"))
      {
         // Go ahead and add the plugins field to the settings...
         settings.setProperty("plugins", plugins.getText());

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
