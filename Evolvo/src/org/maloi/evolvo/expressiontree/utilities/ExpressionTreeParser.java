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
import org.maloi.evolvo.expressiontree.operators.pseudo.SimpleTriplet;

public class ExpressionTreeParser
{
   static VariablePackage vp = VariablePackage.getInstance();
   static SimpleTriplet simpleTriplet = new SimpleTriplet();

   public static ExpressionTree parse(StreamTokenizer st)
      throws SyntaxErrorException, IOException
   {
      st.lowerCaseMode(true);
      
      return doParse(st);
   }

   /*
    * Constructs an expressionTree from a StreamTokenizer
    * Expects the string to be in a lisp-like notation, ie:
    *
    * (+ 1 (- 4 2))
    */
   private static ExpressionTree doParse(StreamTokenizer st)
      throws SyntaxErrorException, IOException
   {
      // (op exptree exptree ...)
      //     - OR -
      // x
      //     - OR -
      // 0.0
      //     - OR -
      // <exptree, exptree, exptree>

      int token;
      String opName;
      int numScalarParams;
      int numTripletParams;
      int foundScalarParams;
      int foundTripletParams;
      int paramIndex = 0;

      ExpressionTree root = null;
      OperatorInterface operator;
      ExpressionTree params[];

      token = st.nextToken();

      if ((char)token == '(')
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
         numScalarParams = operator.getNumberOfScalarParameters();
         numTripletParams = operator.getNumberOfTripletParameters();

         params = new ExpressionTree[numScalarParams + numTripletParams];

         paramIndex = 0;

         while (((token = st.nextToken()) != StreamTokenizer.TT_EOF)
            && ((char)token != ')')
            && (paramIndex < numScalarParams))
         {
            if ((char)token == '(')
            {
               st.pushBack();
               int lineNumber = st.lineno();

               // params[parmIndex] = new ExpressionTree();
               params[paramIndex] = ExpressionTreeParser.parse(st);

               // make sure the parameter returns a scalar
               if (params[paramIndex].returnsTriplet())
               {
                  throw new SyntaxErrorException(
                     lineNumber,
                     "Type mismatch. Expected scalar.");
               }
            }
            else if (token == StreamTokenizer.TT_WORD)
            {
               params[paramIndex] = vp.getVariable(st.sval);

               if (params[paramIndex] == null)
               {
                  throw new SyntaxErrorException(
                     st.lineno(),
                     "Unknown Identifier: " + st.sval);
               }
            }
            else if (token == StreamTokenizer.TT_NUMBER)
            {
               params[paramIndex] = new Value(parseDouble(st));
            }

            paramIndex++;
         }

         foundScalarParams = paramIndex;

         st.pushBack();

         while (((token = st.nextToken()) != StreamTokenizer.TT_EOF)
            && ((char)token != ')')
            && ((paramIndex - numScalarParams) < numTripletParams))
         {
            if ((char)token == '(')
            {
               st.pushBack();
               int lineNumber = st.lineno();

               // params[parmIndex] = new ExpressionTree();
               params[paramIndex] = ExpressionTreeParser.parse(st);

               // make sure the parameter returns a triplet
               if (!params[paramIndex].returnsTriplet())
               {
                  throw new SyntaxErrorException(
                     lineNumber,
                     "Type mismatch. Expected triplet.");
               }
            }
            else if ((char)token == '<')
            {
               params[paramIndex] = parseTriplet(st, token);
            }
            else
            {
               throw new SyntaxErrorException(
                  st.lineno(),
                  "Type mismatch.  Expected triplet, found "
                     + currentTokenString(st, token));
            }

            paramIndex++;
         }

         foundTripletParams = paramIndex - foundScalarParams;

         if ((paramIndex != (numScalarParams + numTripletParams))
            || ((char)token != ')'))
         {
            throw new SyntaxErrorException(
               st.lineno(),
               "Wrong number of parameters "
                  + operator.getName()
                  + "\n"
                  + " expected "
                  + numScalarParams
                  + " scalar (found "
                  + foundScalarParams
                  + ") and "
                  + numTripletParams
                  + " triplet (found "
                  + foundTripletParams
                  + ")");
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
         ExpressionTree value = new Value(parseDouble(st));

         root = value;
      }
      else if ((char)token == '<')
      {
         // triplet
         root = parseTriplet(st, token);
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

   private static ExpressionTree parseTriplet(StreamTokenizer st, int token)
      throws SyntaxErrorException, IOException
   {
      ExpressionTree params[];

      if ((char)token != '<')
      {
         throw new SyntaxErrorException(st.lineno(), "Missing '<'.");
      }

      // triplet
      params = new ExpressionTree[3];

      for (int i = 0; i < 2; i++)
      {
         params[i] = ExpressionTreeParser.parse(st);

         token = st.nextToken();

         if ((char)token != ',')
         {
            throw new SyntaxErrorException(st.lineno(), "Missing ','.");
         }
      }

      params[2] = ExpressionTreeParser.parse(st);

      token = st.nextToken();

      if ((char)token != '>')
      {
         throw new SyntaxErrorException(
            st.lineno(),
            "Missing '>', found " + currentTokenString(st, token));
      }

      return new ExpressionTree(params, simpleTriplet);
   }

   private static String currentTokenString(StreamTokenizer st, int token)
      throws IOException
   {
      if (token == StreamTokenizer.TT_NUMBER)
      {
         return Double.toString(st.nval);
      }

      if (token == StreamTokenizer.TT_WORD)
      {
         return "(word) " + st.sval;
      }

      if (token == StreamTokenizer.TT_EOF)
      {
         return "EOF";
      }

      if (token == StreamTokenizer.TT_EOL)
      {
         return "EOL";
      }

      char tokenChar[] = new char[1];
      tokenChar[0] = (char)token;

      String tokenCharStr = new String(tokenChar);
      return "(char) " + tokenCharStr;
   }

   private static double parseDouble(StreamTokenizer st)
      throws IOException, SyntaxErrorException
   {
      double val = st.nval;
      double exp = 0.0;

      int token = st.nextToken();

      // check for an exponent
      // there are two possibilities
      // if it's in the form 'e<num>' or 'e-<num>', StreamTokenizer will
      // the whole thing as a word.  If, on the other hand, it's in the form
      // 'e+<num>', StreamTokenizer will break it down into three tokens:
      // <char>e <char>+ <num>
      
      // First, handle the more difficult case...  e+<num>      
      if ((char)token == 'e')
      {
         token = st.nextToken();

         if ((char)token == '+')
         {
            // skip over a plus
            token = st.nextToken();
         }

         // now parse the exponent
         if (token == StreamTokenizer.TT_NUMBER)
         {
            exp = st.nval;
         }
         else
         {
            st.pushBack();

            // if there is an 'e' followed by anything but a number, 
            // it is an error
            throw new SyntaxErrorException(
               st.lineno(),
               "Unexpected token.  Needed a number, found "
                  + currentTokenString(st, token));
         }
      }
      else if ( (token == StreamTokenizer.TT_WORD) && (st.sval.charAt(0) == 'e'))
      {
         exp = Integer.parseInt(st.sval.substring(1));
      }
      else
      {
         st.pushBack();
      }

      // make sure we have an integer for our exponent
      if (!(Double.compare(exp, Math.floor(exp)) == 0))
      {
         throw new SyntaxErrorException(
            st.lineno(),
            "Invalid exponent.  Exponent must be an integer.");
      }

      val = val * Math.pow(10, exp);

      return val;
   }
}
