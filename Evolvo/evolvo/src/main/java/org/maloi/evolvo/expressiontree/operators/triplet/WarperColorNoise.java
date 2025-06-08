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


package org.maloi.evolvo.expressiontree.operators.triplet;

import java.io.Serializable;
import java.util.Random;

import org.maloi.evolvo.expressiontree.operators.OperatorInterface;
import org.maloi.evolvo.expressiontree.vm.Stack;
import org.maloi.evolvo.math.NoiseSampler;

/**
* Returns some noise in grayscale space
*/
public class WarperColorNoise implements OperatorInterface, Serializable
{
    private static final long serialVersionUID = 1L;
    
    /** Perform the operation. */
    @Override
    public void perform(Stack theStack, final double registers[])
    {
        long seed = (long)theStack.pop();
        double scale = theStack.pop();
        double y = theStack.pop();
        double x = theStack.pop();

        // This is arbitrary, but gets us closer to Sims' supposed original
        scale /= 1;

        Random r = new Random(seed);

        double noise[] = {
            NoiseSampler.sampleNoise(scale, r.nextLong(), x, y),
            NoiseSampler.sampleNoise(scale, r.nextLong(), x, y),
            NoiseSampler.sampleNoise(scale, r.nextLong(), x, y)
        };

        theStack.pushTriplet(noise);
    }
    
    /** Returns the operator's name. */
    @Override
    public String getName()
    {
        return "warped-color-noise"; //$NON-NLS-1$
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
        return 4;
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
        return true;
    }
}
