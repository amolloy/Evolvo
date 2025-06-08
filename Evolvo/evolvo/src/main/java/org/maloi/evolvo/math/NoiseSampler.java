package org.maloi.evolvo.math;

public class NoiseSampler {

    public static double sampleNoise(double scale, long seed, double x, double y) {
        double ns = scale / 25.0; // This just gets us something similar to Sims original
        double nx = x / ns;
        double ny = y / ns;

        return (double)OpenSimplex2S.noise2(seed, nx, ny);
    }
}
