package com.devansh;

import java.util.ArrayList;
import java.util.Scanner;

public class DummyExpt {

    static ArrayList<String> keyLettersArrayList;
    static ArrayList<String> allLettersArrayList;
    static char[][] charMap;

    public static void main(String[] args) {
        keyLettersArrayList = new ArrayList<>();
        allLettersArrayList = new ArrayList<>();
        charMap = new char[6][6];
        char baseChar = 'a';
        Scanner sc = new Scanner(System.in);
        System.out.println("Kuch bhi likho, bass likho");
        String message = sc.nextLine();
        message = message.toLowerCase().replace(" ","");
        if(!isValidText(message)) {
            System.out.println("Invalid message");
        }
        int i;
        for(i=0;i<26;i++) {
            allLettersArrayList.add(String.valueOf((char)(baseChar+i)));
        }
        for(i=0;i<9;i++) {
            allLettersArrayList.add(String.valueOf(i));
        }
        for(i=0;i<allLettersArrayList.size();i++) {
            charMap[i/6][i%6] = allLettersArrayList.get(i).charAt(0);
        }
    }

    private static boolean isValidText(String message) {
        for(int i=0;i<message.length();i++) {
            char c = message.charAt(i);
            if(!(c>='a'&&c<='z') && !(c>='0'&&c<='9')) return false;
        }
        return true;
    }

    static void encryptMessage() {

    }
}
