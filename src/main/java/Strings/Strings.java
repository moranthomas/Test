package Strings;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Strings {

    final String WHITESPACE = " ";
    String reversed;

    public String reverseUsingStringBuilder(String inputString) {

        // METHOD 1 - Use StringBuilder to reverse only the words first
        String[] words = inputString.split(WHITESPACE);
        StringBuilder reversedString = new StringBuilder();

        for (String word: words) {
            StringBuilder reverseWord = new StringBuilder();
            for (int i = word.length() - 1; i >= 0; i--) {
                reverseWord.append(word.charAt(i));
            }
            reversedString.append(reverseWord).append(WHITESPACE);
        }
        reversed = reversedString.toString();
        return reversed;
    }

    public String reverseUsingJava8(String inputString) {

        // METHOD 2 - Do the same as above but using Java 8:
        final Pattern PATTERN = Pattern.compile(" +");
        reversed = PATTERN.splitAsStream(inputString)
            .map(w -> new StringBuilder(w).reverse())
            .collect(Collectors.joining(" "));

        System.out.println("The reversed words in the same string order is : " + reversed);

        return reversed;
    }

    public String reverseUsingStringBuilderReverse(String inputString) {

        // METHOD 3 - Reverse the letters of each word and the words themselves
        // Use the built-in StringBuilder.reverse() method
        StringBuilder revStr =  new StringBuilder(inputString).reverse();
        System.out.println("The reversed words and the words themselves reversed is : " + revStr);

        return revStr.toString();
    }

}
