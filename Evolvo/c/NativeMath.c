#include <jni.h>
#include <math.h>
#include "NativeMath.h"
#include <stdio.h>

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_acos
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return acos(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_asin
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return asin(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_atan
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return atan(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_atan2
  (JNIEnv * jnienv, jclass class, jdouble a, jdouble b)
{
	return atan2(a, b);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_ceil
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return ceil(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_cos
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return cos(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_cosh
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return cosh(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_exp
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return exp(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_fabs
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return fabs(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_floor
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return floor(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_fmod
  (JNIEnv * jnienv, jclass class, jdouble a, jdouble b)
{
	return fmod(a, b);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_log
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return log(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_log10
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return log10(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_pow
  (JNIEnv * jnienv, jclass class, jdouble a, jdouble b)
{
	return pow(a, b);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_sin
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return sin(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_sinh
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return sinh(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_sqrt
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return sqrt(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_tan
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return tan(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_tanh
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return tanh(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_erf
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return erf(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_erfc
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return erfc(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_gamma
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return gamma(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_hypot
  (JNIEnv * jnienv, jclass class, jdouble a, jdouble b)
{
	return hypot(a, b);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_j0
  (JNIEnv * jnienv, jclass class, jdouble a)  
{
	return j0(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_j1
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return j1(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_lgamma
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return lgamma(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_y0
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return y0(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_y1
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return y1(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_acosh
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return acosh(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_asinh
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return asinh(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_atanh
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return atanh(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_cbrt
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return cbrt(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_expm1
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return expm1(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_log1p
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return log1p(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_logb
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return logb(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_nextafter
  (JNIEnv * jnienv, jclass class, jdouble a, jdouble b)
{
	return nextafter(a, b);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_remainder
  (JNIEnv * jnienv, jclass class, jdouble a, jdouble b)
{
	return remainder(a, b);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_rint
  (JNIEnv * jnienv, jclass class, jdouble a)
{
	return rint(a);
}

JNIEXPORT jdouble JNICALL Java_org_maloi_evolvo_math_NativeMath_scalb
  (JNIEnv * jnienv, jclass class, jdouble a, jdouble b)
{
	return scalb(a, b);
}