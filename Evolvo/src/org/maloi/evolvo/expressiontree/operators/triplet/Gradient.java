/*
 * Created on Mar 7, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.maloi.evolvo.expressiontree.operators.triplet;

import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.vm.Stack;

/**
 * @author amolloy
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Gradient implements OperatorInterface
{
   final static double epsilon = 1e-4;
   final static double two_epsilon = epsilon * 2.0; 
   
   /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#getName()
    */
   public String getName()
   {
      return "Gradient";
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
      return 2;
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
      double in[] = theStack.popTriplet();
      double a[] = new double[3];
      double b[] = new double[3];
      
      a[0] = in[0] - epsilon;
      a[1] = in[1] - epsilon;
      a[2] = in[2] - epsilon;
      
      b[0] = in[0] + epsilon;
      b[1] = in[1] + epsilon;
      b[2] = in[2] + epsilon;
      
      b[0] -= a[0];
      b[1] -= a[1];
      b[2] -= a[2];
      
      b[0] /= two_epsilon;
      b[1] /= two_epsilon;
      b[2] /= two_epsilon;
      
      theStack.pushTriplet(b);      
   }

}
