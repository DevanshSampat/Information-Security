package com.devansh;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class RSA {

    private static int f1, f2, pi;
    private static int e, n;
    private static int d;
    private static int[][][] encryptedMatrix;


    public static void main(String[] args) {
        f1 = 7;
        f2 = 37;
        n = f1*f2;
        e = 7;
        pi = (f1-1)*(f2-1);
        d = (pi+1)/e;
        String file ="C:\\Users\\DEVANSH\\IdeaProjects\\InformationSecurity\\src\\com\\devansh\\images\\jethalal.jpg";
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(file));
            System.out.println(img.getWidth()+" "+img.getHeight());
            encrypt(img, e, n);
            decrypt(img, d, n);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void encrypt(BufferedImage image, int e, int n) {
        encryptedMatrix = new int[image.getWidth()][image.getHeight()][3];
        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                int pixel = image.getRGB(i,j);
                Color color = new Color(pixel, true);
                int[] data = new int[3];
                data[0] = color.getRed();
                data[1] = color.getGreen();
                data[2] = color.getBlue();
                for(int k = 0; k < 3; k++) {
                    data[k] = encrypt(data[k], e, n);
                    encryptedMatrix[i][j][k] = data[k];
                    data[k] = data[k] % 256;
                }
                color = new Color(data[0],data[1],data[2]);
                image.setRGB(i,j,color.getRGB());
            }
        }
        try {
            ImageIO.write(image, "jpg", new File("C:\\Users\\DEVANSH\\IdeaProjects\\InformationSecurity\\src\\com\\devansh\\images\\encrypted.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int encrypt(int data, int e, int n) {
        BigInteger b = BigInteger.valueOf(data);
        b = b.pow(e);
        b = b.mod(BigInteger.valueOf(n));
        return b.intValue();
    }

    public static void decrypt(BufferedImage image, int d, int n) {
        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                int[] data = new int[3];
                for(int k = 0; k < 3; k++) data[k] = encryptedMatrix[i][j][k];
                for(int k = 0; k < 3; k++) {
                    data[k] = decrypt(data[k], d, n);
                }
                Color color = new Color(data[0],data[1],data[2]);
                image.setRGB(i,j,color.getRGB());
            }
        }
        try {
            ImageIO.write(image, "jpg", new File("C:\\Users\\DEVANSH\\IdeaProjects\\InformationSecurity\\src\\com\\devansh\\images\\decrypted.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int decrypt(int data, int d, int n) {
        BigInteger b = BigInteger.valueOf(data);
        b = b.pow(d);
        b = b.mod(BigInteger.valueOf(n));
        return b.intValue();
    }
}
