package Strings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;
import java.io.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Strings {

    @Test
    public void testReversingAString() {

        final String WHITESPACE = " ";
        final String INPUTSTRING = "The brown fox jumped over the lazy dog";
        String reversed;

        // METHOD 1 - Use StringBuilder to reverse only the words first
        String[] words = INPUTSTRING.split(WHITESPACE);
        StringBuilder reversedString = new StringBuilder();

        for (String word: words) {
            StringBuilder reverseWord = new StringBuilder();

            for (int i = word.length() - 1; i >= 0; i--) {
                reverseWord.append(word.charAt(i));
            }

            reversedString.append(reverseWord).append(WHITESPACE);
        }
        reversed = reversedString.toString();


        // METHOD 2 - Do the same as above but using Java 8:
        final Pattern PATTERN = Pattern.compile(" +");
        reversed = PATTERN.splitAsStream(INPUTSTRING)
                .map(w -> new StringBuilder(w).reverse())
                .collect(Collectors.joining(" "));

        System.out.println("The reversed words in the same string order is : " + reversed);


        // METHOD 3 - Reverse the letters of each word and the words themselves
        StringBuilder revStr =  new StringBuilder(INPUTSTRING).reverse();   // Use the built-in StringBuilder.reverse() method
        System.out.println("The reversed words and the words themselves reversed is : " + revStr);


    }

    @Test
    public void testSmallestAndLargestSubstrings() {

        /*Given a string, , and an integer, , complete the function so that it finds the lexicographically smallest and largest substrings of length . */
        String str = "welcometojava";
        int k = 3;

        solution1(str, k);      // Using Sorted Treeset
        solution2(str, k);      // Using inline variables

    }

    void solution1(String str, int k) {
        SortedSet<String> sets=new TreeSet<String>();
        for(int i=0; i<=str.length()-k; i++){
            sets.add(str.substring(i, i+k));
        }
        System.out.println(sets.first());
        System.out.println(sets.last());
    }

    void solution2(String str, int k) {
        String max=str.substring(0,k);
        String min=str.substring(0,k);

        for(int i=0; i+k<=str.length(); i++){

            String substring = str.substring(i, i + k);

            if(substring.compareTo(min)<0) min = substring;
            if(substring.compareTo(max)>0) max = substring;
        }

        System.out.println(min);
        System.out.println(max);
    }

}
