/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

public class MergeArrays {

    /**
     * This method merges any number of arrays of any count.
     *
     * @param arrays
     *
     * @return merged array
     */
    public static <V> V[] merge(V[]... arrays) {
        // Count the number of arrays passed for merging and the total size of resulting array
        int count = 0;
        for (final V[] array : arrays) {
            count += array.length;
        }

        // Create new array and copy all array contents
        final V[] mergedArray = (V[]) java.lang.reflect.Array.newInstance(arrays[0][0].getClass(), count);
        int start = 0;
        for (final V[] array : arrays) {
            System.arraycopy(array, 0, mergedArray, start, array.length);
            start += array.length;
        }

        return mergedArray;
    }

    public static void main(String[] args) {
        final String[] a = { "This ", "is ", "just " };
        final String[] b = { "a ", "test ", "case " };
        final String[] c = { "to ", "test " };
        final String[] d = { "array ", "merge " };
        printArray(merge(a, b));
        printArray(merge(a, b, c));
        printArray(merge(a, b, c, d));
    }

    public static void printArray(String[] x) {
        System.out.print("Array Content : ");
        for (final String var : x) {
            System.out.print(var);
        }
        System.out.println();
        System.out.println();
    }
}