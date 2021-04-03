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
        if(w<=70) {
            return w / 70;
        }
        return 1;
    }
    public static double f1(double w) {
        w = scale_w(w);
        w = 0 - ((1-0.25+0.001)/(1-w+(1-0.25+0.001))) + 1;
        return w;
    }
    public static double f2(double w) {
        return Math.max(0, Tuner.c1c * scale_w(w) * (1 - f1(w) - f4(w)));
    }
    public static double f3(double w) {
        return Math.max(0, Tuner.c2c * (1 - f1(w) - f2(w) - f4(w)));
    }
    public static double f4(double w) {
        w = scale_w(w);
        return Tuner.t2c * Math.pow(w, Tuner.t2p);
    }
    public static double f(double w, double alpha){
        if(Tuner.use_decreasing_alpha_asymptote){
            final double c = 1-Tuner.alpha_curve;
            double ascale = 1 - Math.min(1,alpha/Tuner.alpha_cap);
            double wscale = Math.min(1,w/Tuner.w_cap);
            return ascale * ((Tuner.fw * w)/(alpha + Tuner.falphab)) * ((c)/(wscale+c));
        }
        return (Tuner.fw * Math.pow(w,Tuner.fwp)) /
                (Tuner.falpha * (Math.pow(alpha, Tuner.falphap) + Tuner.falphab));
    }
    public static double sumf(double w, double[] alphas){
        double sum = 0.0;
        System.out.println();
        for(double alpha : alphas){
            double temp = f(w, alpha);
            Debug.RunVerboseL2DebugCode(()->{
                System.out.printf("     alpha_i: %.4f\nf(w,alpha_i): %.4f\n", alpha, temp);
            });
            sum += temp;
        }
        return sum;
    }
    public static double h(double t1, double t2, double w) {
        if(Tuner.use_mobility_heuristic || Tuner.use_territory_heuristic) {
            if (Tuner.use_winner_heuristic) {
                if(Tuner.use_amazongs_heuristic) {
                    return (t1 + w) * (w * t2 + w);
                }
                return (w * t2 + w);
            }
            if(Tuner.use_amazongs_heuristic) {
                return t1 * t2;
            }
        }
        if(Tuner.use_amazongs_heuristic) {
            if (Tuner.use_winner_heuristic) {
                return t1 + w;
            }
            return t1;
        }
        return 0;
    }
    public static double delta(double left, double right){
        if(Double.isNaN(left) && Double.isNaN(right)){
            return 0.0;
        } else if ((int)left == (int)right){
            return Tuner.move_first_advantage;
        } else if (left < right){
            return 1.0;
        } else {
            return -1.0;
        }

    }
}
