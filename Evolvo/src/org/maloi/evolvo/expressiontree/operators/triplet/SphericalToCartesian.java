/*
 * Created on Mar 7, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.maloi.evolvo.expressiontree.operators.triplet;

import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.utilities.Tools;
import org.maloi.evolvo.expressiontree.vm.Stack;

/**
 * @author amolloy
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SphericalToCartesian implements OperatorInterface
{

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#getName()
    */
   public String getName()
   {
      return "SphericalToCartesian";
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#init()
    */
   public void init()
   {
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#getNumberOfScalarParameters()
    */
   public int getNumberOfScalarParameters()
   {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#getNumberOfTripletParameters()
    */
   public int getNumberOfTripletParameters()
   {
      return 1;
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#returnsTriplet()
    */
   public boolean returnsTriplet()
   {
      return true;
   }

   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#perform(org.maloi.evolvo.expressiontree.vm.Stack)
    */
   public void perform(Stack theStack)
   {
      double a[] = theStack.popTriplet();
      
      double m;
      double theta;
      double phi;
      
      m = a[0];
      theta = a[1] * Math.PI;
      phi = a[2] * Tools.PI_OVER_TWO;
      
      theStack.push(m * Math.cos(theta) * Math.sin(phi));
      theStack.push(m * Math.sin(theta) * Math.sin(phi));
      theStack.push(m * Math.cos(phi));
   }

}
