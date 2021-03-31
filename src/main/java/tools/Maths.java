package tools;

public class Maths {
    public static double remap_value(double value, double r1_low, double r1_high, double r2_low, double r2_high) {
        return r2_low + (value - r1_low) * (r2_high - r2_low) / (r1_high - r1_low);
    }
    public static double lerp(double v0, double v1, double alpha){
        double min = Math.min(v0,v1);
        double max = Math.max(v0,v1);
        return Math.min(max,Math.max(min,min + (alpha * (max-min))));
    }
    public static double clamp(double v, double min, double max){
        return Math.min(max,Math.max(min,v));
    }

    public static double scale_w(double w){
        return w/80;
    }
    public static double f1(double w) {
        w = scale_w(w);
        return Tuner.t1c * (1 - Math.pow(w, Tuner.t1p));
    }
    public static double f2(double w){
        return Tuner.c1c * scale_w(w) * (1 - f1(w) - f4(w));
    }
    public static double f3(double w){
        return Tuner.c2c * (1 - f1(w) - f2(w) - f4(w));
    }
    public static double f4(double w) {
        w = scale_w(w);
        return Tuner.t2c * Math.pow(w, Tuner.t2p);
    }
    public static double f(double w, double alpha){
        return (Tuner.fw * Math.pow(w,Tuner.fwp)) /
                (Tuner.falpha * (Math.pow(alpha, Tuner.falphap) + Tuner.falphab));
    }
    public static double sumf(double w, double[] alphas){
        double sum = 0.0;
        for(double alpha : alphas){
            sum += f(w, alpha);
        }
        return sum;
    }
    public static double h(double t1, double t2, double w){
        return (t1 + w) * (w * t2 + w);
    }
}
