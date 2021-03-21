package tools;

public class ASingleMaths {
    public static double remap_value(double value, double r1_low, double r1_high, double r2_low, double r2_high) {
        return r2_low + (value - r1_low) * (r2_high - r2_low) / (r1_high - r1_low);
    }

}
