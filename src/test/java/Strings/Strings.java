package Strings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

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

        solution1UsingSortedTreeSet(str, k);      // Using Sorted Treeset
        solution2UsingInlineVariables(str, k);      // Using inline variables

    }

    void solution1UsingSortedTreeSet(String str, int k) {
        SortedSet<String> sets=new TreeSet<String>();
        for(int i=0; i<=str.length()-k; i++){
            sets.add(str.substring(i, i+k));
        }
        System.out.println(sets.first());
        System.out.println(sets.last());
    }

    void solution2UsingInlineVariables(String str, int k) {
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


    @Test
    public String compareTwoStringsBrute(String s1, String s2) {
        // Given two strings, determine if they share a common substring. A substring may be as small as one character.
        String commonSubstring = "NO";

        if(s1.contains(s2)) {
            commonSubstring = "YES";
        }

        int s1Len = s1.length();
        int s2Len = s2.length();

        int shortestStringLength = s1Len > s2Len ? s2Len : s1Len;

        // Loop through each character in s1 and see if
        for(int i =0; i < shortestStringLength; i++) {
            if(s1.contains(s2.substring(i, i+1)) ) {
                commonSubstring = "YES";
            }
        }

        return commonSubstring;
    }

    @Test
    public String compareTwoStringsEfficient(String s1, String s2) {
        // Given two strings, determine if they share a common substring. A substring may be as small as one character.
        String commonSubstring = "NO";

        Set<Character> s1Set = new HashSet<Character>();
        Set<Character> s2Set = new HashSet<Character>();

        for(char c : s1.toCharArray()) { s1Set.add(c); }
        for(char c : s2.toCharArray()) { s2Set.add(c); }

        // store intersection of both in s1Set
        s1Set.retainAll(s2Set);

        if(!s1Set.isEmpty()) {
            commonSubstring = "YES";
        }

        return commonSubstring;

    }
}
