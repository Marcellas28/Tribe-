package com.dayworks_ltd.loyalty_engine.utility;

public class LoyaltyUtil {

    public static String generateFourDigitOtp()
    {
        int first = 0, second = 0, third = 0, fourth = 0;

        first = (int) (0 + (Math.random() * 10));
        second = (int) (0 + (Math.random() * 10));
        third = (int) (0 + (Math.random() * 10));
        fourth = (int) (0 + (Math.random() * 10));

        return new StringBuilder().append(first).append(second).append(third).append(fourth).toString();
    }
}
