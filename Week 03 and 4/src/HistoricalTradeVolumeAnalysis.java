import java.util.*;

class Trade {
    String tradeId;
    int volume;
    String session; // "morning" or "afternoon"

    Trade(String tradeId, int volume, String session) {
        this.tradeId = tradeId;
        this.volume = volume;
        this.session = session;
    }

    @Override
    public String toString() {
        return tradeId + ":" + volume + "(" + session + ")";
    }
}

public class HistoricalTradeVolumeAnalysis {

    // ===== MERGE SORT (Stable, O(n log n)) =====

    // Merge Sort - Sort by volume ascending
    static void mergeSort(Trade[] trades, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            mergeSort(trades, left, mid);           // Sort left half
            mergeSort(trades, mid + 1, right);      // Sort right half
            merge(trades, left, mid, right);        // Merge sorted halves
        }
    }

    // Merge step for Merge Sort
    static void merge(Trade[] trades, int left, int mid, int right) {
        Trade[] temp = new Trade[right - left + 1];
        int i = left;
        int j = mid + 1;
        int k = 0;

        // Merge two sorted subarrays
        while (i <= mid && j <= right) {
            if (trades[i].volume <= trades[j].volume) {
                temp[k++] = trades[i++];
            } else {
                temp[k++] = trades[j++];
            }
        }

        // Copy remaining elements from left subarray
        while (i <= mid) {
            temp[k++] = trades[i++];
        }

        // Copy remaining elements from right subarray
        while (j <= right) {
            temp[k++] = trades[j++];
        }

        // Copy sorted elements back to original array
        for (i = left, k = 0; i <= right; i++, k++) {
            trades[i] = temp[k];
        }
    }

    // ===== QUICK SORT (In-place, Average O(n log n)) =====

    // Quick Sort - Sort by volume descending
    static void quickSort(Trade[] trades, int left, int right) {
        if (left < right) {
            int pivotIndex = lomutoPartition(trades, left, right);
            quickSort(trades, left, pivotIndex - 1);       // Sort left partition
            quickSort(trades, pivotIndex + 1, right);      // Sort right partition
        }
    }

    // Lomuto Partition scheme - Descending order
    static int lomutoPartition(Trade[] trades, int left, int right) {
        // Choose median as pivot
        int mid = left + (right - left) / 2;
        Trade pivot = trades[mid];

        // Move pivot to end temporarily
        swap(trades, mid, right);

        int i = left - 1;

        // Partition: elements > pivot on left, elements <= pivot on right
        for (int j = left; j < right; j++) {
            if (trades[j].volume > pivot.volume) {
                i++;
                swap(trades, i, j);
            }
        }

        // Move pivot to its final position
        swap(trades, i + 1, right);
        return i + 1;
    }

    // Swap helper
    static void swap(Trade[] trades, int i, int j) {
        Trade temp = trades[i];
        trades[i] = trades[j];
        trades[j] = temp;
    }

    // ===== MERGE TWO SORTED LISTS =====

    // Merge two sorted trade lists
    static Trade[] mergeTwoSortedLists(Trade[] list1, Trade[] list2) {
        Trade[] merged = new Trade[list1.length + list2.length];
        int i = 0, j = 0, k = 0;

        System.out.println("\n--- Merging Two Sorted Lists ---");
        System.out.println("List 1 size: " + list1.length + ", List 2 size: " + list2.length);

        while (i < list1.length && j < list2.length) {
            if (list1[i].volume <= list2[j].volume) {
                merged[k++] = list1[i++];
            } else {
                merged[k++] = list2[j++];
            }
        }

        while (i < list1.length) {
            merged[k++] = list1[i++];
        }

        while (j < list2.length) {
            merged[k++] = list2[j++];
        }

        System.out.println("Merged list size: " + merged.length);
        return merged;
    }

    // ===== COMPUTE TOTAL VOLUME =====

    static long computeTotalVolume(Trade[] trades) {
        long total = 0;
        for (Trade trade : trades) {
            total += trade.volume;
        }
        return total;
    }

    // ===== DISPLAY TRADES =====

    static void displayTrades(Trade[] trades, String title) {
        System.out.println("\n" + title);
        for (int i = 0; i < Math.min(trades.length, 15); i++) {
            System.out.println((i + 1) + ". " + trades[i]);
        }
        if (trades.length > 15) {
            System.out.println("... and " + (trades.length - 15) + " more trades");
        }
    }

    // ===== GENERATE RANDOM TRADES =====

    static Trade[] generateRandomTrades(int count, String session) {
        Trade[] trades = new Trade[count];
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            String tradeId = "trade" + (i + 1);
            int volume = 50 + rand.nextInt(950); // 50-1000
            trades[i] = new Trade(tradeId, volume, session);
        }

        return trades;
    }

    // ===== MAIN TEST CASES =====

    public static void main(String[] args) {
        System.out.println("===== Historical Trade Volume Analysis System =====");

        // Test Case 1: Small Dataset
        System.out.println("\n\n--- Test Case 1: Small Dataset ---");
        Trade[] trades1 = {
                new Trade("trade3", 500, "morning"),
                new Trade("trade1", 100, "morning"),
                new Trade("trade2", 300, "morning")
        };

        displayTrades(trades1, "Original Trades:");

        // Merge Sort (Ascending - Stable)
        Trade[] mergeSortTrades = Arrays.copyOf(trades1, trades1.length);
        mergeSort(mergeSortTrades, 0, mergeSortTrades.length - 1);
        displayTrades(mergeSortTrades, "After Merge Sort (Ascending - Stable):");
        long mergeTotal = computeTotalVolume(mergeSortTrades);
        System.out.println("Total Volume (Merge Sort): " + mergeTotal);

        // Quick Sort (Descending)
        Trade[] quickSortTrades = Arrays.copyOf(trades1, trades1.length);
        quickSort(quickSortTrades, 0, quickSortTrades.length - 1);
        displayTrades(quickSortTrades, "After Quick Sort (Descending - Pivot: Median):");
        long quickTotal = computeTotalVolume(quickSortTrades);
        System.out.println("Total Volume (Quick Sort): " + quickTotal);

        // Test Case 2: Merge Two Sessions
        System.out.println("\n\n--- Test Case 2: Merge Morning & Afternoon Sessions ---");
        Trade[] morningSession = {
                new Trade("morning1", 200, "morning"),
                new Trade("morning2", 400, "morning"),
                new Trade("morning3", 600, "morning")
        };

        Trade[] afternoonSession = {
                new Trade("afternoon1", 150, "afternoon"),
                new Trade("afternoon2", 350, "afternoon"),
                new Trade("afternoon3", 500, "afternoon")
        };

        // Sort both sessions
        mergeSort(morningSession, 0, morningSession.length - 1);
        mergeSort(afternoonSession, 0, afternoonSession.length - 1);

        displayTrades(morningSession, "Morning Session (Sorted):");
        displayTrades(afternoonSession, "Afternoon Session (Sorted):");

        // Merge sessions
        Trade[] mergedSessions = mergeTwoSortedLists(morningSession, afternoonSession);
        displayTrades(mergedSessions, "Merged Morning + Afternoon:");
        long mergedTotal = computeTotalVolume(mergedSessions);
        System.out.println("Total Volume (Merged Sessions): " + mergedTotal);

        // Test Case 3: Large Dataset (1 Million Trades)
        System.out.println("\n\n--- Test Case 3: Large Dataset (1 Million Trades) ---");
        Trade[] largeDataset = generateRandomTrades(1_000_000, "trading");

        // Measure Merge Sort performance
        Trade[] mergeLargeDataset = Arrays.copyOf(largeDataset, largeDataset.length);
        long startTime = System.nanoTime();
        mergeSort(mergeLargeDataset, 0, mergeLargeDataset.length - 1);
        long mergeTime = System.nanoTime() - startTime;

        System.out.println("Merge Sort (1M trades):");
        System.out.println("  Time: " + (mergeTime / 1_000_000.0) + " ms");
        System.out.println("  Complexity: O(n log n) - Guaranteed");
        System.out.println("  Stability: Yes");
        System.out.println("  Space Complexity: O(n)");
        displayTrades(mergeLargeDataset, "Top 10 trades (ascending):");

        // Measure Quick Sort performance
        Trade[] quickLargeDataset = Arrays.copyOf(largeDataset, largeDataset.length);
        startTime = System.nanoTime();
        quickSort(quickLargeDataset, 0, quickLargeDataset.length - 1);
        long quickTime = System.nanoTime() - startTime;

        System.out.println("\nQuick Sort (1M trades):");
        System.out.println("  Time: " + (quickTime / 1_000_000.0) + " ms");
        System.out.println("  Complexity: O(n log n) average, O(n²) worst");
        System.out.println("  Stability: No");
        System.out.println("  Space Complexity: O(log n)");
        displayTrades(quickLargeDataset, "Top 10 trades (descending):");

        long largeTotal = computeTotalVolume(quickLargeDataset);
        System.out.println("Total Volume (1M trades): " + largeTotal);

        // Test Case 4: Real-World Use Case - Citi Market Volume Report
        System.out.println("\n\n--- Use Case: Citi Market Volume Report ---");
        Trade[] morningMarket = generateRandomTrades(5000, "morning");
        Trade[] afternoonMarket = generateRandomTrades(5000, "afternoon");

        mergeSort(morningMarket, 0, morningMarket.length - 1);
        mergeSort(afternoonMarket, 0, afternoonMarket.length - 1);

        Trade[] dailyMarket = mergeTwoSortedLists(morningMarket, afternoonMarket);
        quickSort(dailyMarket, 0, dailyMarket.length - 1);

        System.out.println("Daily Market Summary:");
        System.out.println("  Morning Trades: " + morningMarket.length);
        System.out.println("  Afternoon Trades: " + afternoonMarket.length);
        System.out.println("  Total Daily Trades: " + dailyMarket.length);
        System.out.println("  Total Volume: " + computeTotalVolume(dailyMarket));
        displayTrades(dailyMarket, "Top 10 Volume Trades (Descending):");

        // Test Case 5: Portfolio Rebalancing Scenario
        System.out.println("\n\n--- Use Case: Portfolio Rebalancing ---");
        Trade[] portfolio = generateRandomTrades(1000, "rebalancing");
        mergeSort(portfolio, 0, portfolio.length - 1);

        System.out.println("Portfolio Trades Sorted by Volume (Ascending):");
        displayTrades(portfolio, "Lowest Volume Trades (for rebalancing):");
        System.out.println("Recommended action: Monitor/adjust these low-volume positions");
    }
}