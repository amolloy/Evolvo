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


package org.maloi.evolvo.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;

import org.maloi.evolvo.localization.MessageStrings;

public class TextDialog extends JFrame implements ActionListener
{
   private static final long serialVersionUID = -4936317377279141487L;
   protected JTextArea textArea;
   JScrollPane scrollPane;

   public TextDialog()
   {
      this(""); //$NON-NLS-1$
   }

   public TextDialog(String text)
   {
      int width = 600;
      int height = 300;

      Container content = getContentPane();

      JPanel mainPanel = new JPanel();

      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      textArea = new JTextArea(text);
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);
      textArea.setEditable(false);

      scrollPane = new JScrollPane();
      scrollPane.getViewport().add(textArea);
      scrollPane.setVerticalScrollBarPolicy(
         JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      scrollPane.setPreferredSize(new Dimension(width, height));

      mainPanel.add(scrollPane);

      JPanel buttonPanel = new JPanel();

      JButton closeButton = new JButton(MessageStrings.getString("TextDialog.Close")); //$NON-NLS-1$
      closeButton.setMnemonic(KeyEvent.VK_C);
      closeButton.addActionListener(this);

      buttonPanel.add(closeButton);

      content.add(mainPanel, BorderLayout.CENTER);
      content.add(buttonPanel, BorderLayout.SOUTH);

      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      int x = (screen.width - width) / 2;
      int y = (screen.height - height) / 2;
      setBounds(x, y, width, height);

      pack();
      setResizable(false);
   }

   public void forcePaint()
   {
      Dimension size = getSize();
      ((JComponent) (getContentPane())).paintImmediately(
         0,
         0,
         size.width,
         size.height);
   }

   public void scrollToRow(int row)
   {
      int offset;
      JViewport viewport;
      Rectangle2D startLocation;

      viewport = scrollPane.getViewport();

      try
      {
         offset = textArea.getLineStartOffset(row);
         startLocation = textArea.modelToView2D(offset);

         viewport.setViewPosition(new Point((int)startLocation.getMinX(), (int)startLocation.getMinY()));

         textArea.setCaretPosition(textArea.getLineStartOffset(row));

      }
      catch (BadLocationException ble)
      {
      }
   }

   public void actionPerformed(ActionEvent e)
   {
      String cmd = e.getActionCommand();

      if (cmd.equals(MessageStrings.getString("TextDialog.Close"))) //$NON-NLS-1$
      {
         setVisible(false);
         dispose();
      }
   }

	public void setVisible(boolean visible)
	{
        super.setVisible(visible);
	}
}
