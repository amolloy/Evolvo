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

package org.asm.evolvo.expressiontree.vm;

import org.asm.evolvo.expressiontree.operators.OperatorInterface;

/**
 * A single instruction for a simple stack machine.
 * Yes, this is made up almost entirely of public data members.
 * Yes, that's what I wanted.
 * Actually, I wanted a C-style struct.  But this is the closest I can get.
 */
public class Instruction
{
   public final static byte TYPE_VALUE = 0x00;
   public final static byte TYPE_REGISTER = 0x01;
   public final static byte TYPE_OPERATOR = 0x02;

   public byte type; // is this a value, variable, or operator?

   public double value;
   public int reg;
   public OperatorInterface op;

   public Instruction()
   {
      type = TYPE_VALUE;
      value = 0.0;
      reg = 0;
      op = null;
   }

   public Instruction(Instruction inst)
   {
      type = inst.type;
      value = inst.value;
      reg = inst.reg;
      op = inst.op;
   }
}
