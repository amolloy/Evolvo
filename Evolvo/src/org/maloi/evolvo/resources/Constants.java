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

package org.maloi.evolvo.resources;

import java.awt.Dimension;
import java.awt.Toolkit;

public class Constants
{
   public static final String VERSION = "0.5";

   public static final int KEY_MASK =
      (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

   public static final boolean isMac =
      (System.getProperty("mrj.version") != null);

   public static final int THUMBNAIL_WIDTH = 160;
   public static final int THUMBNAIL_HEIGHT = 120;
   public static final Dimension ThumbnailSize =
      new Dimension(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
   
   public static final boolean USE_TILEDIMAGEPANEL = (!isMac);
}
