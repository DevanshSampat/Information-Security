package com.devansh;

public class DiffieHellman {

    public static void main(String[] args) {
        int xa, xb, alpha, q, ya, yb, k1, k2;
        q = 23;
        alpha = 11;
        xa = 6;
        xb = 5;
        ya = power(alpha, xa, q);
        yb = power(alpha, xb, q);
        k1 = power(ya, xb, q);
        k2 = power(yb, xa, q);
        System.out.println("Key 1 = "+k1);
        System.out.println("Key 2 = "+k2);
    }

    static int power(int alpha, int pow, int mod) {
        return ((int)Math.pow(alpha,pow) % mod);
    }

}
