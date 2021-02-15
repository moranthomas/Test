package Lambdas;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
public class LambdaPersonTests {

    static List<Person> people;

    @BeforeAll
    public static void setupPersons() {
        people  = Arrays.asList(
            new Person("Charles", "Dickens", 60),
            new Person("Lewis", "Carroll", 42),
            new Person("Thomas", "Francis", 51),
            new Person("Charlotte", "Bronte", 45),
            new Person("Matthew", "Arnold", 39)
        );
    }

    @Test
    public void personWithAnonInnerClassJava7() {
        //Step 1 - sort by last name
        Collections.sort(people, new Comparator<Person>() {

            // Comparator has only one method so can be replaced with a lambda

            @Override
            public int compare(Person o1, Person o2) {
                return o1.getLastName().compareTo(o2.getLastName());
            }
        });

        //Step 2 - Create a method that prints all the elements in the list
        printAll(people);

        //Step 3 - Create a method that prints all people that have last name beginning with C
        printConditionally(people, new Condition() {
            @Override
            public boolean test(Person p) {
                return p.getLastName().startsWith("C");
            }
        });

        //Step 4 - Create a method that prints all people that have FIRST name beginning with C
        printConditionally(people, new Condition() {
            @Override
            public boolean test(Person p) {
                return p.getFirstName().startsWith("C");
            }
        });

    }

    @Test
    public void personWithLambdasJava8() {

        //Step 1 - sort by last name
        Collections.sort(people, (Person o1, Person o2) -> o1.getLastName().compareTo(o2.getLastName()));

        //Step 2 - Create a method that prints all the elements in the list
        log.info("\n\n   Print all the elements in the list: ");
        printAll(people);

        //Step 3 - Create a method that prints all people that have last name beginning with C
        log.info("\n\n  Now print all people that have LAST name beginning with C: ");
        printConditionally(people, (Person p) ->  p.getLastName().startsWith("C"));


        //Step 4 - Create a method that prints all people that have FIRST name beginning with C
        log.info("\n\n  Now print all people that have FIRST name beginning with C: ");
        printConditionally(people, (Person p) -> p.getFirstName().startsWith("C"));

    }

    @Test
    public void LambdasJava8PredicatesAndConsumer() {

        //This method utilises some of the Function Interfaces from java.util.function package.

        //Step 1 - sort by last name
        Collections.sort(people, (Person o1, Person o2) -> o1.getLastName().compareTo(o2.getLastName()));

        //Step 2 - Create a method that prints all the elements in the list
        log.info("\n\n   Print all the elements in the list: ");
        performConditionally(people, p-> true, p -> log.info(String.valueOf(p)));

        //Step 3 - Create a method that prints all people that have last name beginning with C
        log.info("\n\n  Now print all people that have LAST name beginning with C: ");
        performConditionally(people, p-> p.getLastName().startsWith("C"), p -> log.info(String.valueOf(p)));


        //Step 4 - Create a method that prints all people that have FIRST name beginning with C
        log.info("\n\n  Now print all people that have FIRST name beginning with C: ");
        performConditionally(people, p-> p.getFirstName().startsWith("C"), p -> log.info(p.getFirstName()));

    }


    private static void performConditionally(List<Person> people, Predicate<Person> predicate, Consumer<Person> consumer) {
        for(Person p: people) {
            if(predicate.test(p)) {
                consumer.accept(p);
            }
        }
    }



    private static void printConditionally(List<Person> people, Condition condition) {
        for(Person p: people) {
            if(condition.test(p)) {
                log.info(String.valueOf(p));
            }
        }
    }

    private static void printAll(List<Person> people) {
        for(Person p: people) {
            log.info(String.valueOf(p));
        }
    }
}

interface Condition {
    boolean test(Person p);
}
