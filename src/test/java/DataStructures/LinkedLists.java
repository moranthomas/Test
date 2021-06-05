package DataStructures;

import org.junit.Test;

import java.util.*;

public class LinkedLists {

    @Test
    public void  removeDuplicates() {
        // Write your code here

        SinglyLinkedListNode llist = new SinglyLinkedListNode(10);
        llist.push(20);
        llist.push(13);
        llist.push(13);
        llist.push(11);
        llist.push(11);
        llist.push(11);


        SinglyLinkedListNode modifiedLList = RemoveDuplicates(llist);
        System.out.println(Arrays.toString(llist.toArray()));

    }

    SinglyLinkedListNode RemoveDuplicates(SinglyLinkedListNode head) {
        if ( head == null ) return null;
        SinglyLinkedListNode nextItem = head.next;
        while ( nextItem != null && head.data == nextItem.data ) {
            nextItem = nextItem.next;
        }
        head.next = RemoveDuplicates( nextItem );
        return head;
    }



}

class SinglyLinkedListNode implements List {

    int data;
    SinglyLinkedListNode next;
    SinglyLinkedListNode head;

    public SinglyLinkedListNode(int new_data) {
        this.data = data;
    }


    /* Utility functions */

    /* Inserts a new Node at front of the list. */
    public void push(int new_data)
    {
        /* 1 & 2: Allocate the Node &
                  Put in the data*/
        SinglyLinkedListNode new_node = new SinglyLinkedListNode(new_data);

        /* 3. Make next of new SinglyLinkedListNode as head */
        new_node.next = head;

        /* 4. Move the head to point to new Node */
        head = new_node;
    }

    /* Function to print linked list */
    void printList()
    {
        SinglyLinkedListNode temp = head;
        while (temp != null)
        {
            System.out.print(temp.data+" ");
            temp = temp.next;
        }
        System.out.println();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public boolean add(Object o) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean addAll(Collection c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public Object get(int index) {
        return null;
    }

    @Override
    public Object set(int index, Object element) {
        return null;
    }

    @Override
    public void add(int index, Object element) {

    }

    @Override
    public Object remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator listIterator() {
        return null;
    }

    @Override
    public ListIterator listIterator(int index) {
        return null;
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return null;
    }

    @Override
    public boolean retainAll(Collection c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection c) {
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        return false;
    }

    @Override
    public Object[] toArray(Object[] a) {
        return new Object[0];
    }
}
