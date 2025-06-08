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


package org.maloi.evolvo.expressiontree.vm;

/**
 * Simple stack of doubles
 */
public class Stack
{
   double theStack[];
   int stackPointer = 0;

   public Stack()
   {
      theStack = new double[50];
   }

   public Stack(int initialSize)
   {
      theStack = new double[initialSize];
   }

   public void push(double value)
   {
      if (stackPointer >= theStack.length)
      {
         // Automatically grow the stack array if the stack gets
         // too big.  This is SLOW, so avoid it at all costs.

         double tempStack[] = new double[theStack.length * 2];
         for (int index = 0; index < theStack.length; index++)
         {
            tempStack[index] = theStack[index];
         }
         theStack = tempStack;
      }
      theStack[stackPointer++] = value;
   }

   public Boolean canPopTriplet()
   {
      return stackPointer >= 3;
   }

   public double pop()
   {
      if (stackPointer == 0)
      {
         return Double.NaN;
      }

      return theStack[--stackPointer];
   }
   
   public void pushTriplet(double[] values)
   {
      push(values[0]);
      push(values[1]);
      push(values[2]);
   }
   
   public double[] popTriplet()
   {
      double[] values = new double[3];
      
      values[0] = pop();
      values[1] = pop();
      values[2] = pop();
      
      return values;
   }
}
