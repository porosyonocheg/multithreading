package executors;

/** Bubble sort of integer arrays
 * @author Sergey Shershavin*/

public class HardWorkModel {
    public void sort (int[] array) {
        boolean isSorted = false;
        while(!isSorted) {
            isSorted = true;
            for (int j = 0; j < array.length-1; j++) {
                if (array[j] > array[j+1]) {
                    int temp = array[j+1];
                    array[j+1] = array[j];
                    array[j] = temp;
                    isSorted = false;
                }
            }
        }
    }
}
