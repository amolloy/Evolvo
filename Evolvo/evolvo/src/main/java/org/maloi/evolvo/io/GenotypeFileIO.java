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
import org.maloi.evolvo.gui.LoadGenomeFileChooser;
import org.maloi.evolvo.gui.SaveGenomeFileChooser;
import org.maloi.evolvo.gui.GenericFileFilter;

public class GenotypeFileIO
{
   public static ExpressionTree getGenotypeFromFile(Component parent)
   {
      int result;
      LoadGenomeFileChooser fileChooser = new LoadGenomeFileChooser();
      
      result = fileChooser.showOpenDialog(parent);

      if (result == SaveGenomeFileChooser.CANCEL_OPTION)
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
            "Syntax Error", //$NON-NLS-1$
            JOptionPane.ERROR_MESSAGE);
         return null;
      }
      catch (IOException ioe)
      {
         JOptionPane.showMessageDialog(
            null,
            "File Read Error", //$NON-NLS-1$
            "Error", //$NON-NLS-1$
            JOptionPane.ERROR_MESSAGE);
         return null;
      }
   }

   public static ExpressionTree loadFile(File theFile)
      throws SyntaxErrorException, IOException
   {
      ExpressionTree expression;

       try (FileReader reader = new FileReader(theFile)) {
           StreamTokenizer st = new StreamTokenizer(reader);
           
           // We wish to allow both C and C++ style comments
           st.slashStarComments(true);
           st.slashSlashComments(true);
           
           // This is a simple, forgiving language, so ignore case
           st.lowerCaseMode(true);
           
           // Parses three expressionTrees
           // They occur in this order:
           // Hue, Saturation, Value
           
           expression = ExpressionTreeParser.parse(st);
       }

      return expression;
   }

   public static void putGenotypeToFile(
      Component parent,
      ExpressionTree expression)
   {
      SaveGenomeFileChooser fileChooser = new SaveGenomeFileChooser();
      int result = fileChooser.showSaveDialog(parent);

      if (result == SaveGenomeFileChooser.CANCEL_OPTION)
      {
         return;
      }
      File theFile = fileChooser.getSelectedFile();

      if (theFile == null)
      {
         return;
      }

      String filename = theFile.getName();
      int length = filename.length();
      int i = filename.lastIndexOf('.');
      GenericFileFilter fileFilter =
         (GenericFileFilter) fileChooser.getFileFilter();

      if (!(i > 0 && i < length - 1))
      {
         String ext = fileFilter.getExtensions()[0].toLowerCase();
         filename = theFile.getPath().concat(".").concat(ext); //$NON-NLS-1$
         theFile = new File(filename);
      }

      if (theFile.exists())
      {
         switch (JOptionPane
            .showConfirmDialog(parent, "File exists, overwrite?")) //$NON-NLS-1$
         {
            case JOptionPane.NO_OPTION :
            case JOptionPane.CANCEL_OPTION :
            case JOptionPane.CLOSED_OPTION :
               return;
         }
      }

      try
      {
          try (FileWriter writer = new FileWriter(theFile)) {
              writer.write("// Evolvo Saved Genotype\n\n"); //$NON-NLS-1$
              
              writer.write(expression.toString());
          } //$NON-NLS-1$
      }
      catch (IOException e)
      {
         System.out.println(e);
         JOptionPane.showMessageDialog(
            parent,
            "Could not save file.", //$NON-NLS-1$
            "Error", //$NON-NLS-1$
            JOptionPane.ERROR_MESSAGE);
      }
   }
}
