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

package org.maloi.evolvo.expressiontree.utilities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.maloi.evolvo.expressiontree.Variable;

/**
 *
 * Keeps a list of variables available for use in an expressionTree.
 *
 */
public class VariablePackage implements Serializable
{
   /** Stores the variables. */
   static HashMap vars = new HashMap(4);

   static VariablePackage _instance = null;

   /**
    * Creates instances for each variable in the package.
    */
   private VariablePackage()
   {
      vars.put("x", new Variable(0.0, "x")); //$NON-NLS-1$ //$NON-NLS-2$
      vars.put("y", new Variable(0.0, "y")); //$NON-NLS-1$ //$NON-NLS-2$
      vars.put("r", new Variable(0.0, "r")); //$NON-NLS-1$ //$NON-NLS-2$
      vars.put("theta", new Variable(0.0, "theta")); //$NON-NLS-1$ //$NON-NLS-2$
   }

   public static VariablePackage getInstance()
   {
      if (_instance == null)
      {
         _instance = new VariablePackage();
      }

      return _instance;
   }

   /**
    * Returns a variable by its name.
    */
   public Variable getVariable(String which)
   {
      return (Variable) vars.get(which);
   }

   /**
    *  This ought to be removed, and all code that uses it rewritten.
    *  Seriously.
    */
   public Variable[] getVariables()
   {
      Variable temp[] = new Variable[vars.size()];
      Set keys = vars.keySet();
      int count = 0;

      for (Iterator i = keys.iterator(); i.hasNext();)
      {
         temp[count] = (Variable) vars.get(i.next());
         count++;
      }

      return temp;
   }
}
