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

package org.maloi.evolvo.io;

import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StreamTokenizer;

import javax.swing.JOptionPane;

import org.maloi.evolvo.expressiontree.ExpressionTree;
import org.maloi.evolvo.expressiontree.utilities.ExpressionTreeParser;
import org.maloi.evolvo.expressiontree.utilities.SyntaxErrorException;
import org.maloi.evolvo.expressiontree.utilities.Tools;
import org.maloi.evolvo.gui.CustomFileChooser;
import org.maloi.evolvo.gui.GenericFileFilter;
import org.maloi.evolvo.settings.GlobalSettings;

public class GenotypeFileIO
{
   static GlobalSettings settings = GlobalSettings.getInstance();
   static CustomFileChooser fileChooser = CustomFileChooser.getInstance();

   public static ExpressionTree[] getGenotypeFromFile(Component parent)
   {
      int result;

      result = fileChooser.showOpenGeneratorDialog(parent);

      if (result == CustomFileChooser.CANCEL_OPTION)
      {
         return null;
      }

      try
      {
         return loadFile(fileChooser.getSelectedFile());
      }
      catch (SyntaxErrorException see)
      {
         JOptionPane.showMessageDialog(
            null,
            see.getMessage(),
            "Syntax Error",
            JOptionPane.ERROR_MESSAGE);
         return null;
      }
      catch (IOException ioe)
      {
         JOptionPane.showMessageDialog(
            null,
            "File Read Error",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return null;
      }
   }

   public static ExpressionTree[] loadFile(File theFile)
      throws SyntaxErrorException, IOException
   {
      ExpressionTree[] expressions = new ExpressionTree[3];

      FileReader reader = new FileReader(theFile);
      StreamTokenizer st = new StreamTokenizer(reader);

      // We wish to allow both C and C++ style comments
      st.slashStarComments(true);
      st.slashSlashComments(true);

      // This is a simple, forgiving language, so ignore case
      st.lowerCaseMode(true);

      // Parses three expressionTrees
      // They occur in this order:
      // Hue, Saturation, Value

      int i;

      for (i = 0; i < 3; i++)
      {
         expressions[i] = new ExpressionTree();
         expressions[i] = ExpressionTreeParser.parse(st);
      }

      reader.close();

      return expressions;
   }

   public static void putGenotypeToFile(
      Component parent,
      ExpressionTree[] expressions)
   {
      int result = fileChooser.showSaveGeneratorDialog(parent);

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
         (GenericFileFilter) fileChooser.getFileFilter();

      int id = fileFilter.getID();

      if (i > 0 && i < length - 1)
      {
         ext = filename.substring(i + 1).toLowerCase();
      }
      else
      {
         ext = fileFilter.getExtensions()[0].toLowerCase();
         filename = theFile.getPath().concat(".").concat(ext);
         theFile = new File(filename);
      }

      if (theFile.exists())
      {
         switch (JOptionPane
            .showConfirmDialog(parent, "File exists, overwrite?"))
         {
            case JOptionPane.NO_OPTION :
            case JOptionPane.CANCEL_OPTION :
            case JOptionPane.CLOSED_OPTION :
               return;
         }
      }

      try
      {
         FileWriter writer = new FileWriter(theFile);

         writer.write("// Evolvo Saved Genotype\n\n");

         writer.write(Tools.toString(expressions));

         writer.close();
      }
      catch (Exception e)
      {
         System.out.println(e);
         JOptionPane.showMessageDialog(
            parent,
            "Could not save file.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
   }
}
