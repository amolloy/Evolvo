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

package org.asm.evolvo.expressiontree.operators;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Creates a list of the currently available operators.
 */
public class OperatorList implements Serializable
{
   static HashMap operatorHash;
   static OperatorInterface list[] = new OperatorInterface[28];

   static {
      operatorHash = new HashMap(28);

      list[0] = new Addition();
      list[1] = new Subtraction();
      list[2] = new Multiplication();
      list[3] = new Division();
      list[4] = new Absolute();
      list[5] = new Invert();
      list[6] = new Maximum();
      list[7] = new Minimum();
      list[8] = new Exponent();
      list[9] = new Sine();
      list[10] = new Cosine();
      list[11] = new Arccos();
      list[12] = new Arcsin();
      list[13] = new Arctan();
      list[14] = new Atan2();
      list[15] = new Xor();
      list[16] = new And();
      list[17] = new Or();
      list[18] = new Not();
      list[19] = new Mandel();
      list[20] = new Ifs();
      list[21] = new Beta();
      list[22] = new Ceil();
      list[23] = new Floor();
      list[24] = new Rint();
      list[25] = new Remainder();
      list[26] = new Mux5();
      list[27] = new Mux3();

      // Perform any initialization code the operator might need,
      // as well as construct the HashMap of operators for the byName()
      // method.      
      for (int i = 0; i < list.length; i++)
      {
         list[i].init();
         operatorHash.put(list[i].getName().toLowerCase(), list[i]);
      }
   }

   /**
    *  Don't allow anyone to construct us.
    */
   private OperatorList()
   {
      throw new RuntimeException("operatorList: Should not have gotten here!");
   }

   /** Return the list of operators available. */
   public static OperatorInterface[] getOperators()
   {
      return list;
   }

   /**
    *  Return an operator by its name.  Null if the named operator
    *  does not exist.
    */
   public static OperatorInterface byName(String name)
   {
      Object operatorObject = operatorHash.get(name);

      if (operatorObject == null)
      {
         return null;
      }

      return (OperatorInterface) operatorObject;
   }
}
