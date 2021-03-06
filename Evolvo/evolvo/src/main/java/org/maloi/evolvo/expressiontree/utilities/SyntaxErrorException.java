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

/*
 *
 * Exception to handle syntax errors in evolvo genotype files.
 *
 */
public class SyntaxErrorException extends Exception
{
   private static final long serialVersionUID = 8121473830264126280L;
   int lineno;
   String description;

   public SyntaxErrorException(int l, String d)
   {
      lineno = l;
      description = d;
   }

   public String getMessage()
   {
      String linenoString = Integer.valueOf(lineno).toString();

      return "Error on line " + linenoString + ": " + description; //$NON-NLS-1$ //$NON-NLS-2$
   }
}
