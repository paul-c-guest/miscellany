import java.util.Arrays;

/**
 * A sorting mechanism which aims to use as little space as necessary. For each
 * element to put in sorted position, only as many characters/digits as required
 * are used in order to determine the correct placement in the final order.
 * 
 * CURRENTLY UNDER CONSTRUCTION -- WORKING PARTS: put, extend, and traverse
 * methods are basically untouched and were 'working' during use in
 * CircularSuffixArray for week5 of princeton algs pt II.
 * <p>
 * to implement this as a more general sorting class, it looks like two steps
 * will be important. [1] a way to be able to index in to any position for the
 * given collection, so that it does not need to be internally replicated (thus
 * at least doubling the memory use). [2] dealing with different input; i.e.
 * likely to be strings or numbers. [3] refactoring this class so that it can be
 * called with one static method has broken something else -- progress currently
 * stalled at this point.
 * 
 * @author ballsies
 */
public class TernaryTrieSort {

	// internal node class
	private class Node {
		// each node holds a character
		char character;
		// three possible sub nodes
		Node left, mid, right;
		// for conflict resolution it can be necessary to hold more than one index
		int[] entries;

		public Node(char character) {
			this.character = character;
		}

		public void add(int index) {
			if (entries == null)
				entries = new int[] { index };

			else {
				entries = Arrays.copyOf(entries, entries.length + 1);
				entries[entries.length - 1] = index;
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("char: " + character);
			if (entries != null)
				for (int i : entries)
					sb.append(", " + i);
			return sb.toString();
		}
	}

	private Node root;
	private String[] list;
	private int[] sortedIndices;
	private boolean putNext;
	private int count, wordLen;

	public static void sort(String[] coll) {
		new TernaryTrieSort(coll);
	}

	private TernaryTrieSort(String[] list) {
		this.list = list;

		for (int index = 0; index < this.list.length; index++) {
			putNext = true;
			this.wordLen = list[index].length();
			put(index);
		}

		getSorted(); // populate the sortedIndices array with indexes
		for (int i : sortedIndices)
			System.out.println(i);
	}

	private char getCharAt(int index, int pos) {
		if (list[index].length() > pos)
			return list[index].charAt(pos);
		throw new IllegalArgumentException();
	}

	private void put(int index) {
		root = put(root, index, 0, 0);
	}

	// put a character to its correct position
	private Node put(Node node, int index, int depth, int limiter) {

		// character for this cycle & depth, acts as guide
		char ch = getCharAt(index, depth);
//		System.out.println(ch + " : " + index + ", " + depth);

		if (node == null)
			node = new Node(ch);

		else if (ch < node.character)
			node.left = put(node.left, index, depth, 0);

		else if (ch > node.character)
			node.right = put(node.right, index, depth, 0);

		// extend old path to resolve a char match conflict
		else if (node.entries != null && depth < wordLen - 1)
			node.mid = extend(node, depth + 1);

		// length of word or limiter reached
		else if (limiter > 0 && node.mid == null || depth == wordLen - 1) {
			node.add(index);
			putNext = false;
		}

		else
			node.mid = put(node.mid, index, depth + 1, limiter + 1);

//		System.out.println(node);
		return node;
	}

	// makes a one-step extension on a previously halted branch
	private Node extend(Node parent, int depth) {
		System.out.println("\nextending: " + parent);
		Node extension = new Node(getCharAt(parent.entries[0], depth));
		extension.entries = Arrays.copyOf(parent.entries, parent.entries.length);
		parent.entries = null;
		return extension;
	}

	/**
	 * traverse trie 'left to right' in DFS style, populating an array of discovered
	 * indices for use by the main suffix array class
	 * 
	 * @return an array of suffix index positions
	 */
	private void getSorted() {
		sortedIndices = new int[list.length];
		count = 0;
		collect(root);
	}

	// 'left to right' depth first search for suffix indices
	private void collect(Node node) {
		if (node == null)
			return;

		// recurse as far as possible to the left, before testing for entries
		collect(node.left);

		// if node has index entries, put them to the suffix index array

		if (node.entries != null) {
			for (int entry : node.entries) {
//				System.out.println("found " + entry + " at: " + count);
				sortedIndices[count] = entry;
				count++;
			}
		}

		// continue traversal towards the right, via any middle nodes
		collect(node.mid);
		collect(node.right);
	}

	// testing
	public static void main(String[] args) {

		String[] collection = new String[] { "dog", "cat", "dart", "horse" };
		TernaryTrieSort.sort(collection);

		for (String s : collection)
			System.out.println(s);
	}

}
