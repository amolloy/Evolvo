package org.maloi.evolvo.gui;

import java.awt.Dimension;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.maloi.evolvo.expressiontree.renderer.RendererInterface;

/**
 * @author Andy
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class ImagePanel extends JPanel implements ChangeListener
{
   public Dimension getPreferredSize()
   {
      return null;
   }
   
   public Image getImage()
   {
      return null;
   }
}
