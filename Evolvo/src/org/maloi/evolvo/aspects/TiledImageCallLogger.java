/*
 * Created on May 28, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.maloi.evolvo.aspects;

import org.maloi.evolvo.image.tiledimage.*;

/**
 * @author amolloy
 *
 */
public aspect TiledImageCallLogger
{
	pointcut tracedCall():
	  call(void TiledRaster.*());

	before(): tracedCall() {
	  System.out.println("Entering: " + thisJoinPoint);//$NON-NLS-1$
	}
}
