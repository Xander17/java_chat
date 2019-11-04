package lesson6;

import java.util.Arrays;

public class Task2 {
    public static void main(String[] args) {
    }

    public static int[] afterFourArray(int[] arr) {
        int indexofFour = -1;
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == 4) {
                indexofFour = i;
                break;
            }
        }
        if (indexofFour == -1) throw new RuntimeException();
        return Arrays.copyOfRange(arr, indexofFour + 1, arr.length);
    }
}