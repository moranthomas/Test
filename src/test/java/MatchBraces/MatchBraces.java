package MatchBraces;

import org.junit.Test;

import java.util.Stack;
import static org.assertj.core.api.Assertions.assertThat;

public class MatchBraces {

    @Test
    public void testBasicBrackets() {
        System.out.println(countBasicBrackets("())("));
        System.out.println(countBasicBrackets("(((())))"));
        System.out.println(countBasicBrackets("))))"));
    };

    @Test
    public void testBalancedBrackets() {
        assertThat(matchBalancedBrackets("[](){{")).isEqualTo("NO");
        assertThat(matchBalancedBrackets("[({})]")).isEqualTo("YES");
    }


    public static int countBasicBrackets(String brackets) {
        int open = 0;
        int count = 0;

        for (int i = 0; i < brackets.length(); i++) {
            if (brackets.charAt(i) == '(') {
                open++;
            } else if (brackets.charAt(i) == ')') {
                open--;
            }
            if (open < 0) {
                count++;
                open++;
            }
        }
        return count + open;
    }

    // Hackerrank question - use Stacks
    public static String matchBalancedBrackets(String s) {

        Stack<Character> stack = new Stack();

        for(int i=0; i<s.length(); i++) {
            if (s.charAt(i) == '(' || s.charAt(i) == '{' || s.charAt(i) == '[') {
                stack.push(s.charAt(i));
            } else {
                if (stack.isEmpty()) {
                    return "NO";
                } else {
                    char popValue = stack.pop();
                    if (s.charAt(i) == ')' && popValue != '(') {
                        return "NO";
                    } else if (s.charAt(i) == '}' && popValue != '{') {
                        return "NO";
                    } else if (s.charAt(i) == ']' && popValue != '[') {
                        return "NO";
                    }
                }
            }
        }
        if(stack.isEmpty()) {
            return "YES";
        }
        else {
            return "NO";
        }
    }

}


