//package com.ballsies;

/**
 * A simple queue mechanism to work around inability to create an array of
 * generics such as <b>new Queue<Integer>[]</b> without making ugly cast to
 * Object arrays. This queue performs the basic operations of enqueue, and
 * dequeue; and checking whether the queue is empty.
 */
public class IntegerQueue {

	private class Node {
		Node next;
		final int index;

		public Node(int index) {
			this.index = index;
		}
	}

	private Node first, last;
	private int size = 0;

	/**
	 * Test whether the queue is currently empty
	 * @return true if the queue size is zero, false otherwise
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Put an int to the back of the queue
	 */
	public void enqueue(int index) {
		if (size == 0)
			first = last = new Node(index);
		else {
			last.next = new Node(index);
			last = last.next;
		}
		size++;
	}

	/**
	 * Get the int that is at the front of the queue, and remove it from the queue.
	 * @return the integer from the front of the queue
	 */
	public int dequeue() {
		int i = first.index;
		first = first.next;
		size--;
		return i;
	}
}
