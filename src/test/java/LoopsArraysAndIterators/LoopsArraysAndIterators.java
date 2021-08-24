package LoopsArraysAndIterators;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class LoopsArraysAndIterators {

    // Create a new list of Integers and initialize
    List<Integer> intList = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));

    @Test
    public void printAllAvailableZoneIds() {

        Set<String> allZoneIds = ZoneId.getAvailableZoneIds();

        // Iterate Set Using For-Each Loop (also called the Enhanced For-Loop)
        for(String str : allZoneIds) {
            log.info(str);
        }

        // Iterate Set Using the Java Stream API
        Stream<String> stream = allZoneIds.stream();
        stream.forEach((element) -> { log.info(element); });
    }

    @Test
    public void iteratingAList() {
        // Iterating a list using an Iterator
        List<String> arraylist = new ArrayList<>();
        //adds at the end of list
        arraylist.add("Sachin");                            //[Sachin]
        //adds at the end of list
        arraylist.add("Dravid");                            //[Sachin, Dravid]
        //adds at the index 0
        arraylist.add(0, "Ganguly");          //[Ganguly, Sachin, Dravid]
        //List allows duplicates - Sachin is present in the list twice
        arraylist.add("Sachin");                            //[ Ganguly, Sachin, Dravid, Sachin]

        System.out.println(arraylist.size());   //4

        Iterator<String> arraylistIterator = arraylist.iterator();
        while (arraylistIterator.hasNext()) {
            String str = arraylistIterator.next();
            System.out.println(str);                    //Prints the 4 names in the list on separate lines.
        }

        assertThat(arraylist.contains("Dravid")).isTrue();
    }

//    @Test
//    public void testIntstreamToHashMap() {
//        Map<Integer, Integer> myMap = new HashMap<>();
//        myMap = intList.stream()
//                    .filter(x -> x%2 == 0)
//                    .collect(Collectors.toMap(x -> x[0], x -> ))
//    }


    @Test
    public void testPairedSocksInList() {

        List<Integer> intList = new ArrayList<Integer>(Arrays.asList(10, 20, 20, 10, 10, 30, 50, 10, 20));

        // Loop through the list and compare each element with all the others and count, then modulo 2

        Set<Integer> set = new HashSet<Integer>();
        int pairCount = 0;

        for(int i=0; i<intList.size(); i++) {
            int element = intList.get(i);
            if(set.contains(element)) {
                set.remove(element);
                pairCount++;
            }
            else {
                set.add(element);
            }
        }
        System.out.println("The number of pairs in the list is : " + pairCount);

//        System.out.println("The number of pairs in the list is : " +
//        intList.stream()
//            .filter(x-> x%2==0)
//            .count());
    }


    @Test
    public void testIntStream() {

        // Create a new list of Integers and initialize
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));

        // Print out the list of all the numbers squared
        System.out.println("The list of numbers squared: ");
        list.stream().map(x -> x * x)
                .forEach(System.out::println);

        // Create an array from this List (Optional Step)
        int[] intArray = new int[list.size()];
        for(int i = 0; i < list.size(); i++) {
            intArray[i] = list.get(i);
        }
        // You can then call all the same operators on the stream as if you went straight to list

        // Two ways of creating an IntStream from this array:

        // 1. Arrays.stream -> IntStream
        System.out.println("\n The list of numbers that are exactly divisible by two: ");
        IntStream intStream1 = Arrays.stream(intArray);
        intStream1.filter(x-> x%2==0)
        .forEach(System.out::println);

        // 2. Stream.of -> Stream<int[]>
        //  Stream<int[]> temp = Stream.of(intArray);
        //
           //  // Cant print Stream<int[]> directly, convert / flat it to IntStream
        //  IntStream intStream2 = temp.flatMapToInt(x -> Arrays.stream(x));
        //  intStream2.forEach(x -> System.out.println(x));
    }

    @Test
    public void testStringStreamFromArray() {

        String[] array = {"a", "b", "c", "d", "e"};

        //Arrays.stream
        Stream<String> stream1 = Arrays.stream(array);
        stream1.forEach(x -> System.out.println(x));

        //Stream.of
        Stream<String> stream2 = Stream.of(array);
        stream2.forEach(x -> System.out.println(x));

    }

    @Test
    public void testCountingValleys() {

        String path = "DDUUUUDD";
        int steps = 8;

        int valleyCount = 0;
        Stack<Integer> stack = new Stack<>();

        // start means you have had at least one valley
        boolean start = false;
        for(int i=0; i < path.length(); i++) {
            if(path.charAt(i)=='D') {
                start = true;
                stack.push(1);
            }
            else if(start && path.charAt(i) == 'U') {
                stack.pop();
                if(stack.size() == 0) {
                    valleyCount++;
                    start = false;
                }
            }
        }
        System.out.println(valleyCount);
    }

    @Test
    public void testLeftRotationOfArrayUsingQueues() {

        List<Integer> a = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));
        int d = 4;      //number of left rotations (shifts) to perform
        // e.g. [1,2,3,4,5] -> [2,3,4,5,1] -> [3,4,5,2,1] -> [4,5,1,2,3] -> [5,1,2,3,4]
        Queue<Integer> queue = new ConcurrentLinkedQueue<>();

        Iterator iterator = a.iterator();
        while(iterator.hasNext()) {
            queue.add((Integer) iterator.next());
        }
        System.out.println("Initial Queue: " + queue);

        for(int i = 0; i < d; i++) {
             int top = queue.remove();
             queue.add(top);
             System.out.println(queue);
        }

        System.out.println("Final Queue: " + queue);
        List l = new ArrayList(queue);

    }

    @Test
    public void simpleArraySum() {

        List<Integer> a = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));

        int sum = 0;
        for( int x : a) {
            sum = sum +x;
        }
        System.out.println(sum);
    }

    @Test
    public void makeAnagram() {

        String a = "cat";
        String b = "abacats";

        // Write your code here
        int minNoOfDeletions = 0;

        // a.substring(b) (in any order but same length and frequency ) must be non null

        int[] a_frequencies = new int [26];
        int[] b_frequencies = new int [26];

        for (int i =0; i< a.length(); i++) {
            char currenChar = a.charAt(i);
            int charToInt = (int)currenChar;
            int position = charToInt - (int)'a';
            a_frequencies[position]++;
        }

        for (int i =0; i< b.length(); i++) {
            char currenChar = b.charAt(i);
            int charToInt = (int)currenChar;
            int position = charToInt - (int)'a';
            b_frequencies[position]++;
        }

        for (int i=0; i < 26; i++) {
            int diff = Math.abs(a_frequencies[i] - b_frequencies[i] );
            minNoOfDeletions += diff;
        }

        System.out.println(minNoOfDeletions);
        //return minNoOfDeletions;

    }



}
