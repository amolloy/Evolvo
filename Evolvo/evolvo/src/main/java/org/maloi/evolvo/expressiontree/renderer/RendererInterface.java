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

package org.maloi.evolvo.expressiontree.renderer;

import java.awt.image.ImageProducer;

import javax.swing.event.ChangeListener;

import org.maloi.evolvo.expressiontree.ExpressionTree;
import org.maloi.evolvo.gui.MultiProgressMonitor;

public interface RendererInterface extends ImageProducer
{
   public boolean isFinished();
   public void stop();
   public void setProgressMonitor(MultiProgressMonitor pm);
   public void setSize(int w, int h);
   public int getWidth();
   public int getHeight();
   public ExpressionTree getExpression();
   public void addChangeListener(ChangeListener cl);
}
