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

import org.maloi.evolvo.expressiontree.utilities.Tools;

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
         System.arraycopy(program, 0, tempProgram, 0, program.length);
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
      
      for (int pc = 0; pc < size; pc++)
      {
         switch (program[pc].type)
         {
            case Instruction.TYPE_OPERATOR :
            program[pc].op.perform(programStack, registers);
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
   
   public int executeToPixel()
   {
      Stack stack = execute();
      double red, green, blue;
      int rInt, bInt, gInt, pixel;
      
      if (stack.canPopTriplet())
      {
         var colorTriplet = stack.popTriplet();
         colorTriplet = Tools.normalize(colorTriplet);
         
         //red = Tools.map(stack.pop());
         //green = Tools.map(stack.pop());
         //blue = Tools.map(stack.pop());
         
         red = (colorTriplet[0] + 1.0) * 0.5;
         green = (colorTriplet[1] + 1.0) * 0.5;
         blue = (colorTriplet[2] + 1.0) * 0.5;
      }
      else
      {
         var level = stack.pop();
         level = (level + 1.0) * 0.5;
         red = green = blue = level;
      }
      
      rInt = (int) (red * 255.0);
      gInt = (int) (green * 255.0);
      bInt = (int) (blue * 255.0);
      
      pixel = 0;
      
      pixel |= (rInt << Tools.offsets[0]) & Tools.masks[0];
      pixel |= (gInt << Tools.offsets[1]) & Tools.masks[1];
      pixel |= (bInt << Tools.offsets[2]) & Tools.masks[2];
      
      return pixel;
   }
   
   @Override
   public String toString()
   {
      StringBuilder theString = new StringBuilder();
      
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
            switch (reg) {
               case REGISTER_X:
                  regName = "REG_X"; //$NON-NLS-1$
                  break;
               case REGISTER_Y:
                  regName = "REG_Y"; //$NON-NLS-1$
                  break;
               case REGISTER_R:
                  regName = "REG_R"; //$NON-NLS-1$
                  break;
               case REGISTER_THETA:
                  regName = "REG_THETA"; //$NON-NLS-1$
                  break;
               default:
                  regName = "REG_" + Integer.toHexString(reg); //$NON-NLS-1$
                  break;
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
