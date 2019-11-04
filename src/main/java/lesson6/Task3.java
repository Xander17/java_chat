package lesson6;

public class Task3 {
    public static void main(String[] args) {
    }

    public static boolean arrayOfFourAndOne(int[] arr) {
        boolean hasOne = false;
        boolean hasFour = false;
        for (int value : arr) {
            if (value == 1) hasOne = true;
            else if (value == 4) hasFour = true;
            else return false;
        }
        return hasFour && hasOne;
    }
}
