/*
 * Created on Jan 16, 2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.maloi.evolvo.math;

/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NativeMath {
    static {
        System.loadLibrary("NativeMath");
    }

    public static native double acos(double a);

    public static native double asin(double a);

    public static native double atan(double a);

    public static native double atan2(double a, double b);

    public static native double ceil(double a);

    public static native double cos(double a);

    public static native double cosh(double a);

    public static native double exp(double a);

    public static native double fabs(double a);

    public static native double floor(double a);

    public static native double fmod(double a, double b);

    public static native double log(double a);

    public static native double log10(double a);

    public static native double pow(double a, double b);

    public static native double sin(double a);

    public static native double sinh(double a);

    public static native double sqrt(double a);

    public static native double tan(double a);

    public static native double tanh(double a);

    public static native double erf(double a);

    public static native double erfc(double a);

    public static native double gamma(double a);

    public static native double hypot(double a, double b);

    public static native double j0(double a);

    public static native double j1(double a);

    public static native double lgamma(double a);

    public static native double y0(double a);

    public static native double y1(double a);

    public static native double acosh(double a);

    public static native double asinh(double a);

    public static native double atanh(double a);

    public static native double cbrt(double a);

    public static native double expm1(double a);

    public static native double log1p(double a);

    public static native double logb(double a);

    public static native double nextafter(double a, double b);

    public static native double remainder(double a, double b);

    public static native double rint(double a);

    public static native double scalb(double a, double b);
}
