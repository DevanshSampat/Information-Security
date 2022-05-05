package com.devansh;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class CustomSecurityTechnique {
    private static byte[] data;
    private static byte[] key;
    private static byte[][] block;
    private static byte[] transfer;
    private static byte[] keyForSdes;
    private static byte[] key1;
    private static byte[] key2;
    private static int[][] s0;
    private static int[][] s1;
    private static int pointer;

    public static void main(String[] args) {
        pointer = 0;
        s0 = new int[][]{{1,0,3,2},
                {3,2,1,0},
                {0,2,1,3},
                {3,1,3,2}};
        s1 = new int[][]{{0,1,2,3},
                {2,0,1,3},
                {3,0,1,0},
                {2,1,0,3}};
        data = new byte[new Random().nextInt(128,65536)];
        for(int i=0;i<data.length;i++) {
            data[i] = (byte) new Random().nextInt(0,1);
        }
        key = new byte[128];
        for(int i=0;i<key.length;i++) {
            key[i] = (byte) new Random().nextInt(0,1);
        }
        String file ="C:\\Users\\DEVANSH\\IdeaProjects\\InformationSecurity\\src\\com\\devansh\\images\\jethalal.jpg";
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(file));
            System.out.println(img.getWidth()+" "+img.getHeight());
            encrypt(img);
            decrypt(img, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void encrypt(BufferedImage image) {
        data = new byte[image.getWidth()* image.getHeight()*8*3];
        int count = 0;
        for(int i=0;i<image.getHeight();i++) {
            for(int j=0;j<image.getWidth();j++) {
                int pixel = image.getRGB(i,j);
                Color color = new Color(pixel, true);
                int[] colorData = new int[3];
                colorData[0] = color.getRed();
                colorData[1] = color.getGreen();
                colorData[2] = color.getBlue();
                for(int d: colorData) {
                    int max = 128;
                    for(int index = 0;index<8;index++) {
                        byte dataToPut = 0;
                        if(d>max) {
                            d = d-max;
                            dataToPut = 1;
                        }
                        max = max/2;
                        data[count++] = dataToPut;
                    }
                }
            }
        }
        generateBlocksForEncryption(image);
    }

    private static void generateBlocksForDecryption(BufferedImage image, byte[] data) {
        int length = data.length/128;
        block = new byte[length][128];
        int count = 0;
        while(count<data.length) block[count/128][count%128] = data[count++];
        applyChaining(block[block.length-1]);
        int k=0;
        for(int i=0;i<block.length-1;i++) {
            applySdesDecryption(block[i]);
        }
        transfer = new byte[8];
        for(int i=block.length-1;i>=0;i--) applyChaining(block[i]);
        data = new byte[128*(block.length)];
        for(int i=0;i<block.length;i++) System.arraycopy(block[i],0,data,128*i,128);
        byte[] longKey = new byte[data.length];
        for(int i=0;i< longKey.length;i++) longKey[i] = key[i%key.length];
        for(int i=0;i< data.length;i++) data[i] = exor(data[i], longKey[i]);
        pointer = 0;
        for(int i=0;i<image.getHeight();i++) {
            for(int j=0;j<image.getWidth();j++) {
                byte[][] byteData = new byte[3][8];
                int start = pointer;
                System.arraycopy(data,start,byteData[0],0,8);
                start = start+8;
                System.arraycopy(data,start,byteData[1],0,8);
                start = start+8;
                System.arraycopy(data,start,byteData[2],0,8);
                pointer = start+8;
                image.setRGB(i,j,new Color(convertToInt(byteData[0]), convertToInt(byteData[1]), convertToInt(byteData[2])).getRGB());
            }
        }
        try {
            ImageIO.write(image, "jpg", new File("C:\\Users\\DEVANSH\\IdeaProjects\\InformationSecurity\\src\\com\\devansh\\images\\decrypted.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(byte b: block[0]) System.out.print(b+" ");
        System.out.println();
    }

    private static void decrypt(BufferedImage image, byte[] data) {
        generateBlocksForDecryption(image,data);
    }

    private static void generateBlocksForEncryption(BufferedImage image) {
        byte[] longKey = new byte[data.length];
        for(int i=0;i< longKey.length;i++) longKey[i] = key[i%key.length];
        for(int i=0;i< data.length;i++) data[i] = exor(data[i], longKey[i]);
        int length = data.length/128;
        if(data.length%128!=0) length++;
        block = new byte[length+1][128];
        int count = 0;
        while(count<data.length) block[count/128][count%128] = data[count++];
        int bitsInLastBlock = data.length%128;
        transfer = new byte[8];
        int ptr = 128;
        for(int i=0;i<8;i++) {
            if(bitsInLastBlock>ptr) {
                transfer[i] = 1;
                bitsInLastBlock = bitsInLastBlock - ptr;
            }
            else  transfer[i] = 0;
            ptr = ptr/2;
        }
        for(byte b: block[0]) System.out.print(b+" ");
        System.out.println();
        for(byte[] byteData: block) {
            applyChaining(byteData);
        }
        pointer = 0;
        for(byte[] byteData: block) {
            applySdesEncryption(image, byteData);
        }
        data = new byte[128*(block.length)];
        for(int i=0;i<block.length;i++) System.arraycopy(block[i],0,data,128*i,128);
        pointer = 0;
        for(int i=0;i<image.getHeight();i++) {
            for(int j=0;j<image.getWidth();j++) {
                byte[][] byteData = new byte[3][8];
                int start = pointer;
                System.arraycopy(data,start,byteData[0],0,8);
                start = start+8;
                System.arraycopy(data,start,byteData[1],0,8);
                start = start+8;
                System.arraycopy(data,start,byteData[2],0,8);
                pointer = start+8;
                image.setRGB(i,j,new Color(convertToInt(byteData[0]), convertToInt(byteData[1]), convertToInt(byteData[2])).getRGB());
            }
        }
        try {
            ImageIO.write(image, "jpg", new File("C:\\Users\\DEVANSH\\IdeaProjects\\InformationSecurity\\src\\com\\devansh\\images\\encrypted.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void applySdesEncryption(BufferedImage image, byte[] data) {
        for(int i=0;i< data.length;i=i+8) {
            keyForSdes = new byte[10];
            int begin = pointer%key.length;
            for(int j=begin;j<begin+8;j++) {
                keyForSdes[j-begin] = key[j];
            }
            byte[] plainText = new byte[8];
            for(int j=0;j<8;j++) plainText[j] = data[i+j];
            byte[] cipherText = encryption(plainText);
            for(int j=0;j<8;j++) data[i+j] = cipherText[j];
            pointer = pointer+8;
        }
    }

    private static void applySdesDecryption(byte[] data) {
        for(int i=0;i< data.length;i=i+8) {
            keyForSdes = new byte[10];
            int begin = pointer%key.length;
            for(int j=begin;j<begin+8;j++) {
                keyForSdes[j-begin] = key[j];
            }
            byte[] plainText = new byte[8];
            for(int j=0;j<8;j++) plainText[j] = data[i+j];
            byte[] cipherText = decryption(plainText);
            for(int j=0;j<8;j++) data[i+j] = cipherText[j];
            pointer = pointer+8;
        }
    }

    private static int convertToInt(byte[] byteData) {
        int start = 1;
        int res = 0;
        for(int i=byteData.length-1;i>=0;i--) {
            if(byteData[i] == 1) res = res+start;
            start = start*2;
        }
        return res;
    }

    private static void applyChaining(byte[] byteData) {
        byte[] temp_transfer = new byte[8];
        for(int i=0;i<8;i++) temp_transfer[i] = transfer[i];
        int index = 1;
        for(int i=0;i<8;i++) {
            transfer[i] = byteData[index-1];
            index = index*2;
        }
        index = 1;
        for(int i=0;i<8;i++) {
            byteData[index-1] = temp_transfer[i];
            index = index*2;
        }
    }

    private static byte exor(byte a, byte b) {
        if(a==b) return 0;
        return 1;
    }

    static byte[] encryption(byte[] plainText) {

        //generating keys

        byte[] tempKey = convertToP10(keyForSdes);
        byte[] leftHalf = new byte[5];
        byte[] rightHalf = new byte[5];
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

        byte[] permuted_input = initialPermutation(plainText);
        leftHalf = new byte[4];
        rightHalf = new byte[4];
        System.arraycopy(permuted_input, 0, leftHalf, 0, 4);
        System.arraycopy(permuted_input, 4, rightHalf, 0, 4);

        //expanding right half

        byte[] permuted_right_half = expandedPermutation(rightHalf);

        //taking exor with key1

        byte[] permuted_exor = performExorOnArray(permuted_right_half, key1);
        byte[] left_half_for_permuted = new byte[4];
        byte[] right_half_for_permuted = new byte[4];
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

        byte[] four_bits = new byte[4];
        four_bits[0] = (byte) (left_part/2);
        four_bits[1] = (byte) (left_part%2);
        four_bits[2] = (byte) (right_part/2);
        four_bits[3] = (byte) (right_part%2);
        four_bits = convertToP4(four_bits);

        //taking exor with left side four bits

        byte[] second_time_right_part = performExorOnArray(four_bits, leftHalf);

        //expanding left half

        byte[] permuted_left_half = expandedPermutation(second_time_right_part);

        //taking exor with key2

        permuted_exor = performExorOnArray(permuted_left_half, key2);
        left_half_for_permuted = new byte[4];
        right_half_for_permuted = new byte[4];
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

        four_bits = new byte[4];
        four_bits[0] = (byte) (left_part/2);
        four_bits[1] = (byte) (left_part%2);
        four_bits[2] = (byte) (right_part/2);
        four_bits[3] = (byte) (right_part%2);
        four_bits = convertToP4(four_bits);

        //taking exor with right side four bits

        byte[] second_time_left_part = performExorOnArray(four_bits, rightHalf);

        //merging the two arrays

        byte[] final_merge = join(second_time_left_part, second_time_right_part);
        byte[] cipherText = inverseOfInitialPermutation(final_merge);
        return cipherText;
    }

    static byte[] decryption(byte[] cipherText) {

        //generating keys

        byte[] tempKey = convertToP10(keyForSdes);
        byte[] leftHalf = new byte[5];
        byte[] rightHalf = new byte[5];
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

        byte[] permuted_input = initialPermutation(cipherText);
        leftHalf = new byte[4];
        rightHalf = new byte[4];
        System.arraycopy(permuted_input, 0, leftHalf, 0, 4);
        System.arraycopy(permuted_input, 4, rightHalf, 0, 4);

        //expanding right half

        byte[] permuted_right_half = expandedPermutation(rightHalf);

        //taking exor with key2

        byte[] permuted_exor = performExorOnArray(permuted_right_half, key2);
        byte[] left_half_for_permuted = new byte[4];
        byte[] right_half_for_permuted = new byte[4];
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

        byte[] four_bits = new byte[4];
        four_bits[0] = (byte) (left_part/2);
        four_bits[1] = (byte) (left_part%2);
        four_bits[2] = (byte) (right_part/2);
        four_bits[3] = (byte) (right_part%2);
        four_bits = convertToP4(four_bits);

        //taking exor with left side four bits

        byte[] second_time_right_part = performExorOnArray(four_bits, leftHalf);

        //expanding left half

        byte[] permuted_left_half = expandedPermutation(second_time_right_part);

        //taking exor with key1

        permuted_exor = performExorOnArray(permuted_left_half, key1);
        left_half_for_permuted = new byte[4];
        right_half_for_permuted = new byte[4];
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

        four_bits = new byte[4];
        four_bits[0] = (byte) (left_part/2);
        four_bits[1] = (byte) (left_part%2);
        four_bits[2] = (byte) (right_part/2);
        four_bits[3] = (byte) (right_part%2);
        four_bits = convertToP4(four_bits);

        //taking exor with right side four bits

        byte[] second_time_left_part = performExorOnArray(four_bits, rightHalf);

        //merging the two arrays

        byte[] final_merge = join(second_time_left_part, second_time_right_part);
        byte[] decryptedText = inverseOfInitialPermutation(final_merge);
        return decryptedText;
    }

    static byte[] convertToP10(byte[] givenKey) {
        byte[] result = new byte[10];
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

    static byte[] convertToP8(byte[] givenKey) {
        byte[] result = new byte[8];
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

    static byte[] convertToP4(byte[] givenKey) {
        byte[] result = new byte[4];
        result[0] = givenKey[1];
        result[1] = givenKey[3];
        result[2] = givenKey[2];
        result[3] = givenKey[0];
        return result;
    }

    static byte[] leftShift(byte[] givenKey) {
        byte temp = givenKey[givenKey.length-1];
        givenKey[givenKey.length-1] = givenKey[0];
        for(int i=givenKey.length-2;i>=0;i--) {
            byte temp_store = givenKey[i];
            givenKey[i] = temp;
            temp = temp_store;
        }
        return givenKey;
    }

    static byte[] join(byte[] arr1, byte[] arr2) {
        byte[] result = new byte[arr1.length+ arr2.length];
        for(int i=0;i<arr1.length;i++) result[i] = arr1[i];
        for(int i=0;i<arr2.length;i++) result[i+ arr1.length] = arr2[i];
        return result;
    }

    static byte[] initialPermutation(byte[] input) {
        byte[] output = new byte[8];
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

    static byte[] inverseOfInitialPermutation(byte[] input) {
        byte[] output = new byte[8];
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

    static byte[] expandedPermutation(byte[] input) {
        byte[] output = new byte[8];
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

    static byte[] performExorOnArray(byte[] text, byte[] key) {
        byte[] result = new byte[text.length];
        for(int i=0;i<text.length;i++) result[i] = exor(text[i], key[i]);
        return result;
    }
}
