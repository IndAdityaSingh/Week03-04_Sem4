import java.util.*;

public class TransactionFeeSorting {

    // Bubble Sort for small batches (< 100)
    static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    // Swap
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    // Insertion Sort for medium batches (100-1,000)
    static void insertionSort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    // Main function to sort transactions by fee
    static int[] sortTransactionFees(int[] transactions) {
        int n = transactions.length;

        if (n < 100) {
            // Use Bubble Sort for small batches
            bubbleSort(transactions);
        } else if (n <= 1000) {
            // Use Insertion Sort for medium batches
            insertionSort(transactions);
        } else {
            // Use Arrays.sort for large batches
            Arrays.sort(transactions);
        }

        return transactions;
    }

    // Function to flag high outliers (> $80)
    static List<Integer> flagHighOutliers(int[] transactions) {
        List<Integer> outliers = new ArrayList<>();
        for (int fee : transactions) {
            if (fee > 80) {
                outliers.add(fee);
            }
        }
        return outliers;
    }

    // Function to handle duplicates
    static int countDuplicates(int[] transactions) {
        Map<Integer, Integer> freqMap = new HashMap<>();
        int duplicateCount = 0;

        for (int fee : transactions) {
            freqMap.put(fee, freqMap.getOrDefault(fee, 0) + 1);
        }

        for (int count : freqMap.values()) {
            if (count > 1) {
                duplicateCount += (count - 1);
            }
        }

        return duplicateCount;
    }

    // Analyze time complexity
    static void analyzeComplexity(int size) {
        if (size < 100) {
            System.out.println("Algorithm: Bubble Sort | Time Complexity: O(n²)");
        } else if (size <= 1000) {
            System.out.println("Algorithm: Insertion Sort | Time Complexity: O(n²)");
        } else {
            System.out.println("Algorithm: Arrays.sort (Dual-Pivot Quicksort) | Time Complexity: O(n log n)");
        }
    }

    // Main method with test cases
    public static void main(String[] args) {
        System.out.println("===== Transaction Fee Sorting System =====\n");

        // Test case 1: Small batch (Bubble Sort) - size < 100
        int[] t1 = {25, 15, 10, 5, 20};
        System.out.println("Test 1 - Small Batch (Bubble Sort):");
        System.out.println("Original: " + Arrays.toString(t1));
        sortTransactionFees(t1);
        System.out.println("Sorted: " + Arrays.toString(t1));
        analyzeComplexity(t1.length);
        System.out.println();

        // Test case 2: Medium batch (Insertion Sort) - size 100-1000
        int[] t2 = {500, 250, 100, 350, 150, 200, 300, 400, 75, 125};
        System.out.println("Test 2 - Medium Batch (Insertion Sort):");
        System.out.println("Original: " + Arrays.toString(t2));
        sortTransactionFees(t2);
        System.out.println("Sorted: " + Arrays.toString(t2));
        analyzeComplexity(t2.length);
        System.out.println();

        // Test case 3: Flagging outliers
        int[] t3 = {5, 15, 85, 25, 90, 10, 95, 50};
        System.out.println("Test 3 - High Outlier Detection:");
        System.out.println("Original: " + Arrays.toString(t3));
        List<Integer> outliers = flagHighOutliers(t3);
        System.out.println("High outliers (>80): " + outliers);
        System.out.println();

        // Test case 4: Duplicate handling
        int[] t4 = {10, 15, 10, 25, 15, 20, 10};
        System.out.println("Test 4 - Duplicate Handling:");
        System.out.println("Original: " + Arrays.toString(t4));
        int duplicates = countDuplicates(t4);
        System.out.println("Duplicate count: " + duplicates);
        sortTransactionFees(t4);
        System.out.println("Sorted: " + Arrays.toString(t4));
        System.out.println();

        // Test case 5: Large batch (Arrays.sort)
        int[] t5 = new int[1001];
        Random rand = new Random();
        for (int i = 0; i < t5.length; i++) {
            t5[i] = rand.nextInt(500) + 1;
        }
        System.out.println("Test 5 - Large Batch (Arrays.sort):");
        System.out.println("Size: " + t5.length);
        long startTime = System.nanoTime();
        sortTransactionFees(t5);
        long endTime = System.nanoTime();
        System.out.println("Sorted (first 10): " + Arrays.toString(Arrays.copyOf(t5, 10)));
        System.out.println("Time taken: " + (endTime - startTime) + " ns");
        analyzeComplexity(t5.length);
    }
}