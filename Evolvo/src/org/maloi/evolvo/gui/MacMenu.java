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

package org.maloi.evolvo.gui;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MacMenu extends ApplicationAdapter
{
   ActionListener javaApp;
   Application macApp = new Application();

   public MacMenu(ActionListener theApplication)
   {
      javaApp = theApplication;

      macApp.addApplicationListener(this);
      macApp.setEnabledPreferencesMenu(true);
   }

   public void handleQuit(ApplicationEvent e)
   {
      javaApp.actionPerformed(new ActionEvent(this, 0, "Exit"));
      e.setHandled(true);
   }

   public void handlePreferences(ApplicationEvent e)
   {
      javaApp.actionPerformed(new ActionEvent(this, 0, "Preferences"));
      e.setHandled(true);
   }

   public void handleAbout(ApplicationEvent e)
   {
      javaApp.actionPerformed(new ActionEvent(this, 0, "About Evolvo"));
      e.setHandled(true);
   }
}
