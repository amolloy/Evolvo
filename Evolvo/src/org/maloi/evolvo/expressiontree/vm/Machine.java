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

package org.maloi.evolvo.expressiontree.vm;

/**
 * Very simple stack machine, and code for it to execute.
 */
public class Machine
{
   public final static int REGISTER_X = 0x00;
   public final static int REGISTER_Y = 0x01;
   public final static int REGISTER_R = 0x02;
   public final static int REGISTER_THETA = 0x03;
   public final static int FREE_REGISTER_BASE = 0x04;

   Instruction program[];
   int size = 0;
   double registers[];

   public Machine()
   {
      program = new Instruction[100];
      registers = new double[FREE_REGISTER_BASE];
   }

   public Machine(int initialCapacity)
   {
      program = new Instruction[initialCapacity];
      registers = new double[FREE_REGISTER_BASE];
   }

   public Machine(int initialCapacity, int numRegisters)
   {
      program = new Instruction[initialCapacity];
      registers = new double[FREE_REGISTER_BASE + numRegisters];
	}

   public void addInstruction(Instruction inst)
   {
      if (size >= program.length)
      {
         // grow the array.  Slow.  Avoid if possible.

         Instruction tempProgram[] = new Instruction[program.length * 2];
         for (int index = 0; index < program.length; index++)
         {
            tempProgram[index] = program[index];
         }
         program = tempProgram;
      }

      program[size++] = new Instruction(inst);
   }

   public void setRegister(int reg, double value)
   {
      if (reg > registers.length)
      {
         return;
      }
      registers[reg] = value;
   }

   // returns the program stack, which should contain all return values
   public Stack execute()
   {
      Stack programStack = new Stack();
      int pc = 0;

      for (pc = 0; pc < size; pc++)
      {
         switch (program[pc].type)
         {
            case Instruction.TYPE_OPERATOR :
               program[pc].op.perform(programStack);
               break;
            case Instruction.TYPE_REGISTER :
               programStack.push(registers[program[pc].reg]);
               break;
            case Instruction.TYPE_VALUE :
               programStack.push(program[pc].value);
               break;
         }
      }

      return programStack;
   }

   public String toString()
   {
      StringBuffer theString = new StringBuffer();

      for (int i = 0; i < size; i++)
      {
         switch (program[i].type)
         {
            case Instruction.TYPE_OPERATOR :
               theString.append(program[i].op.getName()).append("\n"); //$NON-NLS-1$
               break;
            case Instruction.TYPE_REGISTER :
               int reg = program[i].reg;
               String regName;
               if (reg == REGISTER_X)
               {
                  regName = "REG_X"; //$NON-NLS-1$
               }
               else if (reg == REGISTER_Y)
               {
                  regName = "REG_Y"; //$NON-NLS-1$
               }
               else if (reg == REGISTER_R)
               {
                  regName = "REG_R"; //$NON-NLS-1$
               }
               else if (reg == REGISTER_THETA)
               {
                  regName = "REG_THETA"; //$NON-NLS-1$
               }
               else
               {
                  regName = "REG_" + Integer.toHexString(reg); //$NON-NLS-1$
               }
               theString.append(regName).append("\n"); //$NON-NLS-1$
               break;
            case Instruction.TYPE_VALUE :
               theString.append(program[i].value).append("\n"); //$NON-NLS-1$
               break;
         }
      }

      return theString.toString();
   }
   
   public int getSize()
   {
   	return size;
   }
   
   public Instruction[] getProgram()
   {
   	return program;
   }
}
