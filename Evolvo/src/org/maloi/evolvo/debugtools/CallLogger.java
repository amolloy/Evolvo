/* Evolvo - Image Generator
 * Copyright (C) 2000 Andrew Molloy
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

/*
 * $Id$
 */
package org.maloi.evolvo.debugtools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.maloi.evolvo.gui.SystemConsole;

public class CallLogger implements InvocationHandler
{
   private Object target = null;

   SystemConsole console = SystemConsole.getInstance();

   public static Object newInstance(Object target)
   {
      Class targetClass = target.getClass();
      Class[] interfaces = targetClass.getInterfaces();
      return Proxy.newProxyInstance(
         targetClass.getClassLoader(),
         interfaces,
         new CallLogger(target));
   }

   CallLogger(Object target)
   {
      this.target = target;
   }

   public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable
   {
      Object invocationResult = null;

      try
      {
         System.out.println("Before method " + method.getName()); //$NON-NLS-1$

         invocationResult = method.invoke(this.target, args);
      }

      catch (InvocationTargetException ite)
      {
         //this is the exception thrown by the method being invoked

         //we just rethrow the wrapped exception to conform to the

         //interface
         throw ite.getTargetException();
      }

      catch (Exception e)
      {
         console.println("Invocation of " + method.getName() + " failed"); //$NON-NLS-1$ //$NON-NLS-2$
         console.println(e.getMessage());
      }

      return invocationResult;
   }
}
