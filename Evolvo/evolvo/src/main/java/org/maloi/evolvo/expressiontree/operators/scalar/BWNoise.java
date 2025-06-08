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


package org.maloi.evolvo.expressiontree.operators.scalar;

import java.io.Serializable;

import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.vm.Machine;
import org.maloi.evolvo.expressiontree.vm.Stack;
import org.maloi.evolvo.math.NoiseSampler;

/**
* Returns some noise in grayscale space
*/
public class BWNoise implements OperatorInterface, Serializable
{
    private static final long serialVersionUID = 1L;
    
    /** Perform the operation. */
    @Override
    public void perform(Stack theStack, final double registers[])
    {
        double scale = theStack.pop();
        long seed = (long)theStack.pop();
        double x = registers[Machine.REGISTER_X];
        double y = registers[Machine.REGISTER_Y];

        double noise = NoiseSampler.sampleNoise(scale, seed, x, y);

        theStack.push(noise);
    }
    
    /** Returns the operator's name. */
    @Override
    public String getName()
    {
        return "bwnoise"; //$NON-NLS-1$
    }
    
    /** Performs any initialization the operator requires. */
    @Override
    public void init()
    {
    }
    
    /** Returns the number of parameters expected by the operator. */
    @Override
    public int getNumberOfScalarParameters()
    {
        return 2;
    }
    
    /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#getNumberOfTripletParameters()
    */
    @Override
    public int getNumberOfTripletParameters()
    {
        return 0;
    }
    
    /* (non-Javadoc)
    * @see org.maloi.evolvo.expressiontree.operators.OperatorInterface#returnsTriplet()
    */
    @Override
    public boolean returnsTriplet()
    {
        return false;
    }
}
