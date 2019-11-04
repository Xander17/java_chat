package lesson6;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class Task3Test {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Collection<Object[]> c = new ArrayList<>();
        c.add(new Object[]{new int[]{1, 4, 1, 4, 1, 4, 1, 4}, true});
        c.add(new Object[]{new int[]{1, 1, 1, 1, 1, 1, 1}, false});
        c.add(new Object[]{new int[]{2, 3, 5, 6, 7, 8}, false});
        c.add(new Object[]{new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1}, true});
        c.add(new Object[]{new int[]{4, 4, 4, 4, 4, 4, 4, 4}, false});
        c.add(new Object[]{new int[]{1, 4, 1, 4, 1, 4, 1, 6}, false});
        c.add(new Object[]{new int[]{0, 1, 4, 1, 4, 1, 4, 1}, false});
        c.add(new Object[]{new int[]{}, false});
        return c;
    }

    private int[] arr;
    private boolean result;

    public Task3Test(int[] arr, boolean result) {
        this.arr = arr;
        this.result = result;
    }

    @Test
    public void testing() {
        Assert.assertEquals(result,Task3.arrayOfFourAndOne(arr));
    }
}
