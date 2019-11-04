package lesson6;

import javafx.concurrent.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class Task2Test {
    @Parameterized.Parameters
    public static Collection data() {
        Collection<Object[]> c = new ArrayList<>();
        c.add(new Object[]{new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0}, new int[]{5, 6, 7, 8, 9, 0}});
        c.add(new Object[]{new int[]{0, 9, 8, 7, 6, 5, 4, 3, 2, 1}, new int[]{3, 2, 1}});
        c.add(new Object[]{new int[]{1, 2, 3, 5, 6, 7, 8, 9, 0}, new int[]{}});
        c.add(new Object[]{new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 4}, new int[]{}});
        c.add(new Object[]{new int[]{1, 2, 3, 4, 5, 4, 4, 8, 9}, new int[]{8, 9}});
        c.add(new Object[]{new int[]{4, 2, 3, 7, 5, 5, 5, 8, 9}, new int[]{2, 3, 7, 5, 5, 5, 8, 9}});
        c.add(new Object[]{new int[]{}, new int[]{}});
        return c;
    }

    private int[] arr;
    private int[] arrResult;

    public Task2Test(int[] arr, int[] arrResult) {
        this.arr = arr;
        this.arrResult = arrResult;
    }

    @Test
    public void testing() {
        try {
            Assert.assertArrayEquals(
                    arrResult, Task2.afterFourArray(arr));
        } catch (AssertionError e) {
            Assert.fail(e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Нет четверок");
        }
    }
}

