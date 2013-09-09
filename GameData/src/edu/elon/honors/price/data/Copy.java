package edu.elon.honors.price.data;


public class Copy {
	public static String[] copyOf(String[] array, int newSize) {
		String[] newArray = new String[newSize];
		for (int i = 0; i < array.length && i < newSize; i++) {
			newArray[i] = array[i];
		}
		return newArray;
	}
	
	public static boolean[] copyOf(boolean[] array, int newSize) {
		boolean[] newArray = new boolean[newSize];
		for (int i = 0; i < array.length && i < newSize; i++) {
			newArray[i] = array[i];
		}
		return newArray;
	}
	
	public static int[] copyOf(int[] array, int newSize) {
		int[] newArray = new int[newSize];
		for (int i = 0; i < array.length && i < newSize; i++) {
			newArray[i] = array[i];
		}
		return newArray;
	}

	public static int[][] copyOfRange(int[][] original, int start, int end) {
		int[][] newArray = new int[end - start][];
		for (int i = 0; i < newArray.length; i++) {
			if (i + start < original.length) {
				newArray[i] = original[i + start];
			}
		}
		return newArray;
	}
	
	public static int[] copyOfRange(int[] original, int start, int end) {
		int[] newArray = new int[end - start];
		for (int i = 0; i < newArray.length; i++) {
			if (i + start < original.length) {
				newArray[i] = original[i + start];
			}
		}
		return newArray;
	}
}
