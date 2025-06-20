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


package org.maloi.evolvo.expressiontree.operators;

import java.util.HashMap;

import org.maloi.evolvo.expressiontree.operators.scalar.*;
import org.maloi.evolvo.expressiontree.operators.triplet.*;

/**
 * Creates a list of the currently available operators.
 * 
 * SimpleTriplet is not included in the list, because it is not really
 * an operator, and is intended only for use as a placeholder in 
 * ExpressionTree's.
 */
public class OperatorList
{
   static HashMap<String, OperatorInterface> operatorHash;
   static OperatorInterface scalarList[] = new OperatorInterface[32];
   static OperatorInterface tripletList[] = new OperatorInterface[13];
   static OperatorInterface completeList[];

   static {
      operatorHash = new HashMap<String, OperatorInterface>();
      completeList =
         new OperatorInterface[scalarList.length + tripletList.length];

      scalarList[0] = new Addition();
      scalarList[1] = new Subtraction();
      scalarList[2] = new Multiplication();
      scalarList[3] = new Division();
      scalarList[4] = new Absolute();
      scalarList[5] = new Invert();
      scalarList[6] = new Maximum();
      scalarList[7] = new Minimum();
      scalarList[8] = new Exponent();
      scalarList[9] = new Sine();
      scalarList[10] = new Cosine();
      scalarList[11] = new Arccos();
      scalarList[12] = new Arcsin();
      scalarList[13] = new Arctan();
      scalarList[14] = new Atan2();
      scalarList[15] = new Xor();
      scalarList[16] = new And();
      scalarList[17] = new Or();
      scalarList[18] = new Not();
      scalarList[19] = new Mandel();
      scalarList[20] = new Ifs();
      scalarList[21] = new Beta();
      scalarList[22] = new Ceil();
      scalarList[23] = new Floor();
      scalarList[24] = new Rint();
      scalarList[25] = new Remainder();
      scalarList[26] = new Mux5();
      scalarList[27] = new Mux3();
      scalarList[28] = new Magnitude();
      scalarList[29] = new DotProduct();
      scalarList[30] = new BWNoise();
      scalarList[31] = new WarpedBWNoise();
   
      tripletList[0] = new TripletAdd();
      tripletList[1] = new TripletSubtract();
      tripletList[2] = new Scale();
      tripletList[3] = new CrossProduct();
      tripletList[4] = new Normalize();
      tripletList[5] = new CartesianToSpherical();
      tripletList[6] = new SphericalToCartesian();
      tripletList[7] = new HSVtoRGB();
      tripletList[8] = new RGBtoHSV();
      tripletList[9] = new SphereMux();
      tripletList[10] = new GreyToRGB();
      tripletList[11] = new ColorNoise();
      tripletList[12] = new WarperColorNoise();
      
      int count = 0;

      // Perform any initialization code the operator might need,
      // as well as construct the HashMap of operators for the byName()
      // method and put together the complete list of operators  
      for (int i = 0; i < scalarList.length; i++)
      {
         scalarList[i].init();
         operatorHash.put(scalarList[i].getName().toLowerCase(), scalarList[i]);
         completeList[count++] = scalarList[i];
      }

      for (int i = 0; i < tripletList.length; i++)
      {
         tripletList[i].init();
         operatorHash.put(tripletList[i].getName().toLowerCase(), tripletList[i]);
         completeList[count++] = tripletList[i];
      }
   }

   /**
    *  Don't allow anyone to construct us.
    */
   private OperatorList()
   {
      throw new RuntimeException("operatorList: Should not have gotten here!"); //$NON-NLS-1$
   }

   /** Return the list of scalar operators available. */
   public static OperatorInterface[] getScalarOperators()
   {
      return scalarList;
   }

   /** Return the list of vector operators available. */
   public static OperatorInterface[] getTripletOperators()
   {
      return tripletList;
   }

   /** Return a complete list of operators available. */
   public static OperatorInterface[] getAllOperators()
   {
      return completeList;
   }

   /**
    *  Return an operator by its name.  Null if the named operator
    *  does not exist.
    */
   public static OperatorInterface byName(String name)
   {
      return operatorHash.get(name.toLowerCase());
   }
}
