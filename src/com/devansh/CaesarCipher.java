package com.devansh;

import java.util.ArrayList;
import java.util.Scanner;

public class CaesarCipher {
    static ArrayList<String> allLettersArrayList;

    public static void main(String[] args) {
        allLettersArrayList = new ArrayList<>();
        int i;
        char baseChar = 'a';
        for(i=0;i<26;i++) {
            allLettersArrayList.add(String.valueOf((char)(baseChar+i)));
        }
        for(i=0;i<9;i++) {
            allLettersArrayList.add(String.valueOf(i));
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your message");
        String message = sc.nextLine();
        message = message.toLowerCase().replace(" ","");
        if(!isValidText(message)) {
            System.out.println("Invalid message");
            return;
        }
        System.out.println("Enter your key (integer only)");
        int shift = 0;
        try{
            shift = sc.nextInt();
        } catch (Exception e) {
            System.out.println("Only integer accepted");
            return;
        }
        String encryptedMessage = "";
        for(i=0;i<message.length();i++) {
            char c = message.charAt(i);
            int index = allLettersArrayList.indexOf(String.valueOf(c));
            index = (index + shift)%allLettersArrayList.size();
            encryptedMessage = encryptedMessage+allLettersArrayList.get(index);
        }
        System.out.println("Encrypted Message :-\n"+encryptedMessage);

        String decryptedMessage = "";
        for(i=0;i<encryptedMessage.length();i++) {
            char c = encryptedMessage.charAt(i);
            int index = allLettersArrayList.indexOf(String.valueOf(c));
            index = (index - shift + allLettersArrayList.size())%allLettersArrayList.size();
            decryptedMessage = decryptedMessage+allLettersArrayList.get(index);
        }
        System.out.println("Decrypted Message:-\n"+decryptedMessage);
        System.out.println("\n\n");
        bruteForce(encryptedMessage);
    }

    private static void bruteForce(String encryptedMessage) {
        int i;
        int shift;
        System.out.println("Brute Force Approach");
        for(shift=0;shift<36;shift++) {
            String decryptedMessage = "";
            for (i = 0; i < encryptedMessage.length(); i++) {
                char c = encryptedMessage.charAt(i);
                int index = allLettersArrayList.indexOf(String.valueOf(c));
                index = (index - shift + allLettersArrayList.size()) % allLettersArrayList.size();
                decryptedMessage = decryptedMessage + allLettersArrayList.get(index);
            }
            System.out.println(decryptedMessage);
        }
    }


    private static boolean isValidText(String message) {
        for(int i=0;i<message.length();i++) {
            char c = message.charAt(i);
            if(!(c>='a'&&c<='z') && !(c>='0'&&c<='9')) return false;
        }
        return true;
    }
}
