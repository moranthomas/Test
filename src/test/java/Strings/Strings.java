package Strings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;
import java.io.*;

public class Strings {

    @Test
    public void testLexicalSubstrings() {

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
