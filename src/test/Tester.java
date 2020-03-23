import java.util.Arrays;

public class Tester {


  public static void main(String[] args) {
    int a[][] = {{1, 2, 3, 4, 99},
        {5, 6, 7, 8, 98},
        {9, 10, 11, 12, 97},
        {13, 14, 15, 16, 96},
        {133, 143, 153, 163, 963}
    };

//    int a[][] = {{1,2,3,4},
//        {5,6,7,8},
//        {9,10,11,12},
//        {13,14,15,16}
//    };
//

    System.out.println(Arrays.deepToString(a));
    int N = 5;
    // Traverse each cycle
    for (int i = 0; i < N/2; i++)
    {
      for (int j = i; j < N - i - 1; j++)
      {

        // Swap elements of each cycle
        // in clockwise direction
        int temp = a[i][j];
        a[i][j] = a[N - 1 - j][i];
        a[N - 1 - j][i] = a[N - 1 - i][N - 1 - j];
        a[N - 1 - i][N - 1 - j] = a[j][N - 1 - i];
        a[j][N - 1 - i] = temp;
      }
    }
    System.out.println(Arrays.deepToString(a));


    }


}
