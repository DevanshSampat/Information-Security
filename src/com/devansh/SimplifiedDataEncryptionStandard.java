package com.devansh;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class SimplifiedDataEncryptionStandard {

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    
    private static int[] key1;
    private static int[] key2;
    private static int[] key;
    private static int[] plainText;
    private static int[][] s0;
    private static int[][] s1;
    private static int[] cipherText;
    private static int[] decryptedText;
    private static long time;

    public static void main(String[] args) {
        //Instantiating the Imagecodecs class
        //Reading the Image from the file
        String file ="C:\\Users\\DEVANSH\\IdeaProjects\\InformationSecurity\\src\\com\\devansh\\images\\jethalal.jpg";
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(file));
            System.out.println(img.getWidth()+" "+img.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mat matrix = Imgcodecs.imread(file);
        s0 = new int[][]{{1,0,3,2},
                {3,2,1,0},
                {0,2,1,3},
                {3,1,3,2}};
        s1 = new int[][]{{0,1,2,3},
                {2,0,1,3},
                {3,0,1,0},
                {2,1,0,3}};
        Scanner sc = new Scanner(System.in);
        String input;
        /* System.out.print("Enter 8 bit plain text ");
        input = sc.nextLine();
        plainText = new int[8];
        for(int i=0;i<8;i++) {
            try{
                plainText[i] = Integer.parseInt(input.substring(i,i+1));
                if(plainText[i]!=1&&plainText[i]!=0) {
                    System.out.println("invalid binary text");
                    return;
                }
            } catch (Exception e) {
                System.out.println("invalid text "+ e.toString());
                return;
            }
        }*/
        System.out.print("Enter your 10 digit key : ");
        input = sc.nextLine();
        key = new int[10];
        for(int i=0;i<10;i++) {
            try{
                key[i] = Integer.parseInt(input.substring(i,i+1));
                if(key[i]!=1&&key[i]!=0) {
                    System.out.println("invalid binary key");
                    return;
                }
            } catch (Exception e) {
                System.out.println("invalid key "+ e.toString());
                return;
            }
        }
        time = System.currentTimeMillis();
        encryptImage(img);
//        encryption(plainText);
//        decryption(cipherText);
    }

    private static void encryptImage(BufferedImage img) {
        for(int i=0;i<img.getWidth();i++) {
            for(int j=0;j<img.getHeight();j++) {
                int[] data = new int[3];
                int pixel;
                try{
                    pixel = img.getRGB(i,j);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(i+"\t"+j);
                    return;
                }
                Color color = new Color(pixel, true);
                //Retrieving the R G B values
                data[0] = color.getRed();
                data[1] = color.getGreen();
                data[2] = color.getBlue();

                for(int k=0;k<3;k++) {
                    int mod = 128;
                    int[] plainText = new int[8];
                    for(int index=0;index<8;index++){
                        plainText[index] = data[k]/mod;
                        if(plainText[index]==1) data[k] = data[k]-mod;
                        mod = mod/2;
                    }
                    int[] cipherText = encryption(plainText);
                    int result = 0;
                    mod = 128;
                    for(int index=0;index<8;index++){
                        int temp = cipherText[index];
                        if(temp==1) result = result + mod;
                        mod = mod/2;
                    }
                    data[k] = result;
                }
                color = new Color(data[0],data[1],data[2]);
                img.setRGB(i,j,color.getRGB());
            }
        }
        try {
            ImageIO.write(img, "jpg", new File("C:\\Users\\DEVANSH\\IdeaProjects\\InformationSecurity\\src\\com\\devansh\\images\\encrypted.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        long diff = System.currentTimeMillis() - time;
        System.out.println("Successfully encrypted in "+diff/1000+"s "+diff%1000+"ms");
        time = System.currentTimeMillis();
        decryptImage(img);
    }

    private static void decryptImage(BufferedImage img) {
        for(int i=0;i<img.getWidth();i++) {
            for(int j=0;j<img.getHeight();j++) {
                int[] data = new int[3];
                int pixel = img.getRGB(i,j);
                Color color = new Color(pixel, true);
                //Retrieving the R G B values
                data[0] = color.getRed();
                data[1] = color.getGreen();
                data[2] = color.getBlue();

                for(int k=0;k<3;k++) {
                    int mod = 128;
                    int[] cipherText = new int[8];
                    for(int index=0;index<8;index++){
                        cipherText[index] = data[k]/mod;
                        if(cipherText[index]==1) data[k] = data[k]-mod;
                        mod = mod/2;
                    }
                    int[] decryptedText = decryption(cipherText);
                    int result = 0;
                    mod = 128;
                    for(int index=0;index<8;index++){
                        int temp = decryptedText[index];
                        if(temp==1) result = result + mod;
                        mod = mod/2;
                    }
                    data[k] = result;
                }
                color = new Color(data[0],data[1],data[2]);
                img.setRGB(i,j,color.getRGB());
            }
        }
        try {
            ImageIO.write(img, "jpg", new File("C:\\Users\\DEVANSH\\IdeaProjects\\InformationSecurity\\src\\com\\devansh\\images\\decrypted.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        long diff = System.currentTimeMillis() - time;
        System.out.println("Successfully decrypted in "+diff/1000+"s "+diff%1000+"ms");
    }


    static int[] encryption(int[] plainText) {

        //generating keys

        int[] tempKey = convertToP10(key);
        int[] leftHalf = new int[5];
        int[] rightHalf = new int[5];
        System.arraycopy(tempKey, 0, leftHalf, 0, 5);
        System.arraycopy(tempKey, 5, rightHalf, 0, 5);
        leftShift(leftHalf);
        leftShift(rightHalf);
        key1 = convertToP8(join(leftHalf, rightHalf));
        leftShift(leftHalf);
        leftShift(leftHalf);
        leftShift(rightHalf);
        leftShift(rightHalf);
        key2 = convertToP8(join(leftHalf, rightHalf));

        //permuting plain text

        int[] permuted_input = initialPermutation(plainText);
        leftHalf = new int[4];
        rightHalf = new int[4];
        System.arraycopy(permuted_input, 0, leftHalf, 0, 4);
        System.arraycopy(permuted_input, 4, rightHalf, 0, 4);

        //expanding right half

        int[] permuted_right_half = expandedPermutation(rightHalf);

        //taking exor with key1

        int[] permuted_exor = performExorOnArray(permuted_right_half, key1);
        int[] left_half_for_permuted = new int[4];
        int[] right_half_for_permuted = new int[4];
        System.arraycopy(permuted_exor, 0, left_half_for_permuted, 0, 4);
        System.arraycopy(permuted_exor, 4, right_half_for_permuted, 0, 4);

        //getting elements from s0 and s1

        int row = 2*left_half_for_permuted[0]+left_half_for_permuted[3];
        int column = 2*left_half_for_permuted[1]+left_half_for_permuted[2];
        int left_part = s0[row][column];
        row = 2*right_half_for_permuted[0]+right_half_for_permuted[3];
        column = 2*right_half_for_permuted[1]+right_half_for_permuted[2];
        int right_part = s1[row][column];

        //generating 4 bit array of elements

        int[] four_bits = new int[4];
        four_bits[0] = left_part/2;
        four_bits[1] = left_part%2;
        four_bits[2] = right_part/2;
        four_bits[3] = right_part%2;
        four_bits = convertToP4(four_bits);

        //taking exor with left side four bits

        int[] second_time_right_part = performExorOnArray(four_bits, leftHalf);

        //expanding left half

        int[] permuted_left_half = expandedPermutation(second_time_right_part);

        //taking exor with key2

        permuted_exor = performExorOnArray(permuted_left_half, key2);
        left_half_for_permuted = new int[4];
        right_half_for_permuted = new int[4];
        System.arraycopy(permuted_exor, 0, left_half_for_permuted, 0, 4);
        System.arraycopy(permuted_exor, 4, right_half_for_permuted, 0, 4);

        //getting elements from s0 and s1

        row = 2*left_half_for_permuted[0]+left_half_for_permuted[3];
        column = 2*left_half_for_permuted[1]+left_half_for_permuted[2];
        left_part = s0[row][column];
        row = 2*right_half_for_permuted[0]+right_half_for_permuted[3];
        column = 2*right_half_for_permuted[1]+right_half_for_permuted[2];
        right_part = s1[row][column];

        //generating 4 bit array of elements

        four_bits = new int[4];
        four_bits[0] = left_part/2;
        four_bits[1] = left_part%2;
        four_bits[2] = right_part/2;
        four_bits[3] = right_part%2;
        four_bits = convertToP4(four_bits);

        //taking exor with right side four bits

        int[] second_time_left_part = performExorOnArray(four_bits, rightHalf);

        //merging the two arrays

        int[] final_merge = join(second_time_left_part, second_time_right_part);
        cipherText = inverseOfInitialPermutation(final_merge);
        return cipherText;
    }

    static int[] decryption(int[] cipherText) {

        //generating keys

        int[] tempKey = convertToP10(key);
        int[] leftHalf = new int[5];
        int[] rightHalf = new int[5];
        System.arraycopy(tempKey, 0, leftHalf, 0, 5);
        System.arraycopy(tempKey, 5, rightHalf, 0, 5);
        leftShift(leftHalf);
        leftShift(rightHalf);
        key1 = convertToP8(join(leftHalf, rightHalf));
        leftShift(leftHalf);
        leftShift(leftHalf);
        leftShift(rightHalf);
        leftShift(rightHalf);
        key2 = convertToP8(join(leftHalf, rightHalf));

        //permuting cipher text

        int[] permuted_input = initialPermutation(cipherText);
        leftHalf = new int[4];
        rightHalf = new int[4];
        System.arraycopy(permuted_input, 0, leftHalf, 0, 4);
        System.arraycopy(permuted_input, 4, rightHalf, 0, 4);

        //expanding right half

        int[] permuted_right_half = expandedPermutation(rightHalf);

        //taking exor with key2

        int[] permuted_exor = performExorOnArray(permuted_right_half, key2);
        int[] left_half_for_permuted = new int[4];
        int[] right_half_for_permuted = new int[4];
        System.arraycopy(permuted_exor, 0, left_half_for_permuted, 0, 4);
        System.arraycopy(permuted_exor, 4, right_half_for_permuted, 0, 4);

        //getting elements from s0 and s1

        int row = 2*left_half_for_permuted[0]+left_half_for_permuted[3];
        int column = 2*left_half_for_permuted[1]+left_half_for_permuted[2];
        int left_part = s0[row][column];
        row = 2*right_half_for_permuted[0]+right_half_for_permuted[3];
        column = 2*right_half_for_permuted[1]+right_half_for_permuted[2];
        int right_part = s1[row][column];

        //generating 4 bit array of elements

        int[] four_bits = new int[4];
        four_bits[0] = left_part/2;
        four_bits[1] = left_part%2;
        four_bits[2] = right_part/2;
        four_bits[3] = right_part%2;
        four_bits = convertToP4(four_bits);

        //taking exor with left side four bits

        int[] second_time_right_part = performExorOnArray(four_bits, leftHalf);

        //expanding left half

        int[] permuted_left_half = expandedPermutation(second_time_right_part);

        //taking exor with key1

        permuted_exor = performExorOnArray(permuted_left_half, key1);
        left_half_for_permuted = new int[4];
        right_half_for_permuted = new int[4];
        System.arraycopy(permuted_exor, 0, left_half_for_permuted, 0, 4);
        System.arraycopy(permuted_exor, 4, right_half_for_permuted, 0, 4);

        //getting elements from s0 and s1

        row = 2*left_half_for_permuted[0]+left_half_for_permuted[3];
        column = 2*left_half_for_permuted[1]+left_half_for_permuted[2];
        left_part = s0[row][column];
        row = 2*right_half_for_permuted[0]+right_half_for_permuted[3];
        column = 2*right_half_for_permuted[1]+right_half_for_permuted[2];
        right_part = s1[row][column];

        //generating 4 bit array of elements

        four_bits = new int[4];
        four_bits[0] = left_part/2;
        four_bits[1] = left_part%2;
        four_bits[2] = right_part/2;
        four_bits[3] = right_part%2;
        four_bits = convertToP4(four_bits);

        //taking exor with right side four bits

        int[] second_time_left_part = performExorOnArray(four_bits, rightHalf);

        //merging the two arrays

        int[] final_merge = join(second_time_left_part, second_time_right_part);
        decryptedText = inverseOfInitialPermutation(final_merge);
        return decryptedText;
    }

    static int[] convertToP10(int[] givenKey) {
        int[] result = new int[10];
        result[0] = givenKey[2];
        result[1] = givenKey[4];
        result[2] = givenKey[1];
        result[3] = givenKey[6];
        result[4] = givenKey[3];
        result[5] = givenKey[9];
        result[6] = givenKey[0];
        result[7] = givenKey[8];
        result[8] = givenKey[7];
        result[9] = givenKey[5];
        return result;
    }

    static int[] convertToP8(int[] givenKey) {
        int[] result = new int[8];
        result[0] = givenKey[5];
        result[1] = givenKey[2];
        result[2] = givenKey[6];
        result[3] = givenKey[3];
        result[4] = givenKey[7];
        result[5] = givenKey[4];
        result[6] = givenKey[9];
        result[7] = givenKey[8];
        return result;
    }

    static int[] convertToP4(int[] givenKey) {
        int[] result = new int[4];
        result[0] = givenKey[1];
        result[1] = givenKey[3];
        result[2] = givenKey[2];
        result[3] = givenKey[0];
        return result;
    }

    static int[] leftShift(int[] givenKey) {
        int temp = givenKey[givenKey.length-1];
        givenKey[givenKey.length-1] = givenKey[0];
        for(int i=givenKey.length-2;i>=0;i--) {
            int temp_store = givenKey[i];
            givenKey[i] = temp;
            temp = temp_store;
        }
        return givenKey;
    }

    static int[] join(int[] arr1, int[] arr2) {
        int[] result = new int[arr1.length+ arr2.length];
        for(int i=0;i<arr1.length;i++) result[i] = arr1[i];
        for(int i=0;i<arr2.length;i++) result[i+ arr1.length] = arr2[i];
        return result;
    }

    static int[] initialPermutation(int[] input) {
        int[] output = new int[8];
        output[0] = input[1];
        output[1] = input[5];
        output[2] = input[2];
        output[3] = input[0];
        output[4] = input[3];
        output[5] = input[7];
        output[6] = input[4];
        output[7] = input[6];
        return output;
    }

    static int[] inverseOfInitialPermutation(int[] input) {
        int[] output = new int[8];
        output[0] = input[3];
        output[1] = input[0];
        output[2] = input[2];
        output[3] = input[4];
        output[4] = input[6];
        output[5] = input[1];
        output[6] = input[7];
        output[7] = input[5];
        return output;
    }

    static int[] expandedPermutation(int[] input) {
        int[] output = new int[8];
        output[0] = input[3];
        output[1] = input[0];
        output[2] = input[1];
        output[3] = input[2];
        output[4] = input[1];
        output[5] = input[2];
        output[6] = input[3];
        output[7] = input[0];
        return output;
    }

    static int exor(int num1, int num2) {
        return (num1!=num2)?1:0;
    }

    static int[] performExorOnArray(int[] text, int[] key) {
        int[] result = new int[text.length];
        for(int i=0;i<text.length;i++) result[i] = exor(text[i], key[i]);
        return result;
    }
}
