/*
 * Created on May 28, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.maloi.evolvo;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author amolloy
 *
 */
public class Messages
{

   private static final String BUNDLE_NAME = "org.maloi.evolvo.Evolvo"; //$NON-NLS-1$

   private static final ResourceBundle RESOURCE_BUNDLE =
      ResourceBundle.getBundle(BUNDLE_NAME); //$NON-NLS-1$ //$NON-NLS-2$

   /**
    * 
    */
   private Messages()
   {

      // TODO Auto-generated constructor stub
   }
   /**
    * @param key
    * @return
    */
   public static String getString(String key)
   {
      // TODO Auto-generated method stub
      try
      {
         return RESOURCE_BUNDLE.getString(key);
      }
      catch (MissingResourceException e)
      {
         return '!' + key + '!';
      }
   }
}
