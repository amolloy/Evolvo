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

package org.maloi.evolvo.test;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Random;

import org.maloi.evolvo.expressiontree.ExpressionTree;
import org.maloi.evolvo.expressiontree.mutator.Mutator;
import org.maloi.evolvo.expressiontree.mutator.MutatorList;
import org.maloi.evolvo.expressiontree.mutator.mutators.MutatorInterface;
import org.maloi.evolvo.expressiontree.utilities.ExpressionTreeGenerator;
import org.maloi.evolvo.expressiontree.utilities.ExpressionTreeParser;
import org.maloi.evolvo.expressiontree.utilities.SyntaxErrorException;
import org.maloi.evolvo.expressiontree.vm.Machine;

/**
 * @author Andy
 */
public class TestTreeFunctions extends Harness
{
   Random r = new Random();

   public boolean test0()
   {
      String test =
         "(mux3 (magnitude  (tripletSub  < y, (mandel x x 0.42280236786446224 y y x), 0.39181895639199005> (tripletAdd  < y, x, x> < y, y, x> ) ) ) (sin (ceil y)) (sub y (mux 0.475923575140631 (xor x y) (add y 0.6374955021356609) (mandel (not y) y y y 2.953070357227805E-4 0.8027840571248878) (ceil (beta y x)) y)) (ifs (ifs 0.27915636639743946 (add (not y) 0.7111752600490433) (sin y) (not (magnitude  < x, 0.314784367334743, 0.6250933160794659> )) (exp 0.31015252993712017) (mandel (not x) y (atan y) y x (mux3 x x x x)) 0.20717909891010633 y (mux3 (div 0.691187388766545 (arccos 0.4118630629517799)) x y (magnitude  < y, 0.19679439249476205, x> ))) (magnitude  (tripletAdd  < y, x, 0.43440377952834286> < x, y, y> ) ) (arcsin (cos 0.6716408319410063)) (ceil 0.135819838406531) (ifs y x (magnitude  < x, x, x> ) y (mandel y y y x (abs x) x) (div (floor y) y) y 0.7390436300379749 x) x (arcsin y) (not (and (abs x) y)) y))"; //$NON-NLS-1$

      try
      {
         ExpressionTree parsed =
            ExpressionTreeParser.parse(
               new StreamTokenizer(new StringReader(test)));
         parsed.buildMachine(new Machine());
      }
      catch (IOException ioe)
      {
         System.err.println("IOException error!  Should never happen!"); //$NON-NLS-1$
         return false;
      }
      catch (SyntaxErrorException see)
      {
         System.err.println("Syntax error"); //$NON-NLS-1$
         System.err.println(test);
         System.err.println(see.toString());

         return false;
      }

      return true;
   }

   public boolean test1()
   {
      int i;
      // we are going to generate 10000 expression trees which return a scalar
      // then pass them through the parser to validate them.
      // Of course, this relies on the parser being correct.
      for (i = 0; i < 10000; i++)
      {
         ExpressionTree exp = ExpressionTreeGenerator.generate(r, false);
         ExpressionTree parsed;

         try
         {
            parsed =
               ExpressionTreeParser.parse(
                  new StreamTokenizer(new StringReader(exp.toString())));
				parsed.buildMachine(new Machine());                 
         }
         catch (IOException ioe)
         {
            System.err.println(
               "IOException - this should never happen in this case."); //$NON-NLS-1$
            return false;
         }
         catch (SyntaxErrorException see)
         {
            System.err.println("Syntax error"); //$NON-NLS-1$
            System.err.println(see.toString());
            System.err.println(exp.toString());
            return false;
         }
      }

      return true;
   }

   public boolean test2()
   {
      int i;

      // we are going to generate 10000 expression trees which return a triplet
      // then pass them through the parser to validate them.
      // Of course, this relies on the parser being correct.
      for (i = 0; i < 10000; i++)
      {
         ExpressionTree exp = ExpressionTreeGenerator.generate(r, true);

         try
         {
            ExpressionTree parser =
               ExpressionTreeParser.parse(
                  new StreamTokenizer(new StringReader(exp.toString())));
				parser.buildMachine(new Machine());
         }
         catch (IOException ioe)
         {
            System.err.println(
               "IOException - this should never happen in this case."); //$NON-NLS-1$
            return false;
         }
         catch (SyntaxErrorException see)
         {
            System.err.println("Syntax error"); //$NON-NLS-1$
            System.err.println(see.toString());
            System.err.println(exp.toString());
            return false;
         }
      }

      return true;
   }

   public boolean test3()
   {
      int i;
      int mutator = 0;
      MutatorInterface[] mutators = MutatorList.getMutatorList();

      ExpressionTree mutated = null;

      for (i = 0; i < 10000; i++)
      {
         ExpressionTree exp = ExpressionTreeGenerator.generate(r, true);

         try
         {
            for (mutator = 0; mutator < mutators.length; mutator++)
            {
               mutated = mutators[mutator].doMutation(exp.getClone(), 0.0, r);

               ExpressionTree parser =
                  ExpressionTreeParser.parse(
                     new StreamTokenizer(new StringReader(mutated.toString())));
					parser.buildMachine(new Machine());
            }
         }
         catch (IOException ioe)
         {
            System.err.println(
               "IOException - this should never happen in this case."); //$NON-NLS-1$
            return false;
         }
         catch (SyntaxErrorException see)
         {
            System.err.println(
               "Error with mutator " + mutators[mutator].getDisplayName()); //$NON-NLS-1$
            System.err.println(see.toString());
            System.err.println("Original:"); //$NON-NLS-1$
            System.err.println(exp.toString());
            System.err.println("Mutated:"); //$NON-NLS-1$
            System.err.println(mutated.toString());
            return false;
         }

      }
      return true;
   }

   public boolean test4()
   {
      int i;
      int mutator = 0;
      MutatorInterface[] mutators = MutatorList.getMutatorList();

      ExpressionTree mutated = null;

      for (i = 0; i < 10000; i++)
      {
         ExpressionTree exp = ExpressionTreeGenerator.generate(r, false);

         try
         {
            for (mutator = 0; mutator < mutators.length; mutator++)
            {
               mutated = mutators[mutator].doMutation(exp.getClone(), 0.0, r);

               ExpressionTree parser =
                  ExpressionTreeParser.parse(
                     new StreamTokenizer(new StringReader(mutated.toString())));
					parser.buildMachine(new Machine());
            }
         }
         catch (IOException ioe)
         {
            System.err.println(
               "IOException - this should never happen in this case."); //$NON-NLS-1$
            return false;
         }
         catch (SyntaxErrorException see)
         {
            System.err.println(
               "Error with mutator " + mutators[mutator].getDisplayName()); //$NON-NLS-1$
            System.err.println(see.toString());
            System.err.println("Original:"); //$NON-NLS-1$
            System.err.println(exp.toString());
            System.err.println("Mutated:"); //$NON-NLS-1$
            System.err.println(mutated.toString());
            return false;
         }

      }
      return true;
   }

   public boolean test5()
   {
      int i;

      ExpressionTree mutated = null;

      for (i = 0; i < 10000; i++)
      {
         ExpressionTree exp = ExpressionTreeGenerator.generate(r, false);

         try
         {
            mutated = Mutator.mutate(r, exp.getClone());

            ExpressionTree parser =
               ExpressionTreeParser.parse(
                  new StreamTokenizer(new StringReader(mutated.toString())));
				parser.buildMachine(new Machine());
         }
         catch (IOException ioe)
         {
            System.err.println(
               "IOException - this should never happen in this case."); //$NON-NLS-1$
            return false;
         }
         catch (SyntaxErrorException see)
         {
            System.err.println("Error with full mutator"); //$NON-NLS-1$
            System.err.println(see.toString());
            System.err.println("Original:"); //$NON-NLS-1$
            System.err.println(exp.toString());
            System.err.println("Mutated:"); //$NON-NLS-1$
            System.err.println(mutated.toString());
            return false;
         }

      }
      return true;
   }

   public void run()
   {
      doTest(0, test0());
      doTest(1, test1());
      doTest(2, test2());
      doTest(3, test3());
      doTest(4, test4());
      doTest(5, test5());
   }

   public static void main(String[] args)
   {
      TestTreeFunctions theTest = new TestTreeFunctions();

      theTest.run();
   }
}
