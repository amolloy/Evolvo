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
 * $Id$
 */

package org.maloi.evolvo.expressiontree.utilities;

import java.io.IOException;
import java.io.StreamTokenizer;

import org.maloi.evolvo.expressiontree.ExpressionTree;
import org.maloi.evolvo.expressiontree.Value;
import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.operators.OperatorList;

public class ExpressionTreeParser
{
   static VariablePackage vp = VariablePackage.getInstance();

   /*
    * Constructs an expressionTree from a StreamTokenizer
    * Expects the string to be in a lisp-like notation, ie:
    *
    * (+ 1 (- 4 2))
    */
   public static ExpressionTree parse(StreamTokenizer st)
      throws SyntaxErrorException, IOException
   {
      // (op parm1 parm2 ...)
      //     - OR -
      // x
      //     - OR -
      // 0.0

      int token;
      String opName;
      int numParms;
      int parmIndex = 0;

      ExpressionTree root = null;
      OperatorInterface operator;
      ExpressionTree params[];

      token = st.nextToken();

      if ((char) token == '(')
      {
         // (

         // op

         root = new ExpressionTree();

         token = st.nextToken();
         if (token != StreamTokenizer.TT_WORD)
         {
            throw new SyntaxErrorException(st.lineno(), "Missing operator");
         }

         opName = st.sval;
         operator = OperatorList.byName(opName);

         if (operator == null)
         {
            throw new SyntaxErrorException(
               st.lineno(),
               "Invalid operator: " + opName);
         }

         root.setOperator(operator);

         // parm1 parm2 ...
         numParms = operator.getNumberOfParameters();
         params = new ExpressionTree[numParms];

         while (((token = st.nextToken()) != StreamTokenizer.TT_EOF)
            && ((char) token != ')')
            && (parmIndex < numParms))
         {
            if ((char) token == '(')
            {
               st.pushBack();

               params[parmIndex] = new ExpressionTree();
               params[parmIndex] = ExpressionTreeParser.parse(st);
            }
            else if (token == StreamTokenizer.TT_WORD)
            {
               params[parmIndex] = vp.getVariable(st.sval);

               if (params[parmIndex] == null)
               {
                  throw new SyntaxErrorException(
                     st.lineno(),
                     "Unknown Identifier: " + st.sval);
               }
            }
            else if (token == StreamTokenizer.TT_NUMBER)
            {
               params[parmIndex] = new Value(st.nval);
            }

            parmIndex++;
         }

         if ((parmIndex != numParms) || ((char) token != ')'))
         {
            throw new SyntaxErrorException(
               st.lineno(),
               "Wrong number of parameters.");
         }

         root.setParams(params);
      }
      else if (token == StreamTokenizer.TT_WORD)
      {
         // x

         ExpressionTree var = vp.getVariable(st.sval);

         if (var == null)
         {
            throw new SyntaxErrorException(
               st.lineno(),
               "Unknown Identifier: " + st.sval);
         }

         root = var;
      }
      else if (token == StreamTokenizer.TT_NUMBER)
      {
         // 0.0

         ExpressionTree value = new Value(st.nval);

         root = value;
      }
      else if (token == StreamTokenizer.TT_EOF)
      {
         // End of file - too soon!

         throw new SyntaxErrorException(st.lineno(), "Unexpected end of file.");
      }
      else
      {
         throw new SyntaxErrorException(st.lineno(), "Unexpected token.");
      }

      return root;
   }
}
