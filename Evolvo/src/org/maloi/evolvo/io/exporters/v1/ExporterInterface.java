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

package org.maloi.evolvo.io.exporters.v1;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface ExporterInterface
{
   /**
    *  Returns the name displayed to the user in the exporter selection
    *  box in the settings dialog.
    */
   public String getName();

   /**
    *  Returns a list of format descriptions suitable for display in the
    *  File->Save dialog's "File Type" list.
    */
   public String[] getFormatDescriptions();

   /**
    *  Returns a list of file extensions the Save dialog should display
    *  for each format. The list must be in the same order as the list
    *  returned by getFormatDescriptions(). Also, the first extension
    *  in this list is considered the default extension for the file,
    *  and appended to filenames which do not have an extension
    */
   public String[] getFormatExtensions(String format);

   /**
    * Performs the export.
    *
    * i     - Image to write
    * which - Index of exporter to use (from getFormatDescriptions)
    * f     - The File to write to
    */
   public void write(Image i, int which, File f) throws IOException;

   /**
    * Checks to see if the exporter can be used on this system.
    *
    */
   public boolean isAvailable();

   /**
    * Initialize the exporter.
    *
    */
   public void initialize();
}
