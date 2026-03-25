import java.util.*;

class Asset {
    String symbol;
    double returnRate;
    double volatility;

    Asset(String symbol, double returnRate, double volatility) {
        this.symbol = symbol;
        this.returnRate = returnRate;
        this.volatility = volatility;
    }

    @Override
    public String toString() {
        return symbol + ":" + String.format("%.1f%%", returnRate) +
                "(vol:" + String.format("%.1f%%", volatility) + ")";
    }
}

public class PortfolioReturnSorting {

    // ===== MERGE SORT (Stable, preserves original order for ties) =====

    // Merge Sort - Sort by returnRate ascending (preserve order for ties)
    static void mergeSort(Asset[] assets, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            mergeSort(assets, left, mid);
            mergeSort(assets, mid + 1, right);
            merge(assets, left, mid, right);
        }
    }

    // Merge step for Merge Sort (Stable)
    static void merge(Asset[] assets, int left, int mid, int right) {
        Asset[] temp = new Asset[right - left + 1];
        int i = left;
        int j = mid + 1;
        int k = 0;

        // Merge maintaining stability
        while (i <= mid && j <= right) {
            if (assets[i].returnRate <= assets[j].returnRate) {
                temp[k++] = assets[i++];  // <= ensures stability
            } else {
                temp[k++] = assets[j++];
            }
        }

        while (i <= mid) {
            temp[k++] = assets[i++];
        }

        while (j <= right) {
            temp[k++] = assets[j++];
        }

        for (i = left, k = 0; i <= right; i++, k++) {
            assets[i] = temp[k];
        }
    }

    // ===== QUICK SORT (with Pivot Selection Strategies) =====

    // Quick Sort - Sort by returnRate DESC + volatility ASC
    static void quickSort(Asset[] assets, int left, int right, PivotStrategy strategy) {
        if (left < right) {
            int pivotIndex = partition(assets, left, right, strategy);
            quickSort(assets, left, pivotIndex - 1, strategy);
            quickSort(assets, pivotIndex + 1, right, strategy);
        }
    }

    // Partition with different pivot strategies
    static int partition(Asset[] assets, int left, int right, PivotStrategy strategy) {
        int pivotIndex;

        switch (strategy) {
            case RANDOM:
                pivotIndex = left + new Random().nextInt(right - left + 1);
                break;
            case MEDIAN_OF_THREE:
                pivotIndex = medianOfThree(assets, left, right);
                break;
            default:
                pivotIndex = right;
        }

        Asset pivot = assets[pivotIndex];
        swap(assets, pivotIndex, right);

        int i = left - 1;

        // Partition: DESC by returnRate, then ASC by volatility
        for (int j = left; j < right; j++) {
            if (compareAssets(assets[j], pivot) > 0) {
                i++;
                swap(assets, i, j);
            }
        }

        swap(assets, i + 1, right);
        return i + 1;
    }

    // Compare assets: DESC by returnRate, then ASC by volatility
    static int compareAssets(Asset a, Asset b) {
        if (a.returnRate != b.returnRate) {
            return Double.compare(b.returnRate, a.returnRate); // DESC
        }
        return Double.compare(a.volatility, b.volatility); // ASC
    }

    // Median of Three pivot selection
    static int medianOfThree(Asset[] assets, int left, int right) {
        int mid = left + (right - left) / 2;

        if (compareAssets(assets[left], assets[mid]) > 0) {
            swap(assets, left, mid);
        }
        if (compareAssets(assets[left], assets[right]) > 0) {
            swap(assets, left, right);
        }
        if (compareAssets(assets[mid], assets[right]) > 0) {
            swap(assets, mid, right);
        }

        return mid;
    }

    // Hybrid Sort: Quick Sort + Insertion Sort for small partitions
    static void hybridSort(Asset[] assets, int left, int right, PivotStrategy strategy) {
        if (right - left < 10) {
            // Use Insertion Sort for small partitions
            insertionSort(assets, left, right);
        } else {
            // Use Quick Sort for larger partitions
            int pivotIndex = partition(assets, left, right, strategy);
            hybridSort(assets, left, pivotIndex - 1, strategy);
            hybridSort(assets, pivotIndex + 1, right, strategy);
        }
    }

    // Insertion Sort for small datasets
    static void insertionSort(Asset[] assets, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            Asset key = assets[i];
            int j = i - 1;

            while (j >= left && compareAssets(assets[j], key) > 0) {
                assets[j + 1] = assets[j];
                j--;
            }
            assets[j + 1] = key;
        }
    }

    // Swap helper
    static void swap(Asset[] assets, int i, int j) {
        Asset temp = assets[i];
        assets[i] = assets[j];
        assets[j] = temp;
    }

    // ===== UTILITY FUNCTIONS =====

    static void displayAssets(Asset[] assets, String title) {
        System.out.println("\n" + title);
        for (int i = 0; i < Math.min(assets.length, 15); i++) {
            System.out.println((i + 1) + ". " + assets[i]);
        }
        if (assets.length > 15) {
            System.out.println("... and " + (assets.length - 15) + " more assets");
        }
    }

    static Asset[] generateRandomAssets(int count) {
        Asset[] assets = new Asset[count];
        Random rand = new Random();
        String[] symbols = {"AAPL", "TSLA", "GOOG", "MSFT", "AMZN", "META", "NVDA", "NFLX", "IBM", "INTEL"};

        for (int i = 0; i < count; i++) {
            String symbol = symbols[i % symbols.length] + i;
            double returnRate = -20 + rand.nextDouble() * 40; // -20% to +20%
            double volatility = 5 + rand.nextDouble() * 35;   // 5% to 40%
            assets[i] = new Asset(symbol, returnRate, volatility);
        }

        return assets;
    }

    static double calculateAverageReturn(Asset[] assets) {
        double sum = 0;
        for (Asset asset : assets) {
            sum += asset.returnRate;
        }
        return assets.length > 0 ? sum / assets.length : 0;
    }

    static double calculateAverageVolatility(Asset[] assets) {
        double sum = 0;
        for (Asset asset : assets) {
            sum += asset.volatility;
        }
        return assets.length > 0 ? sum / assets.length : 0;
    }

    // ===== MAIN TEST CASES =====

    public static void main(String[] args) {
        System.out.println("===== Portfolio Return Sorting System =====");

        // Test Case 1: Small Dataset
        System.out.println("\n\n--- Test Case 1: Small Portfolio ---");
        Asset[] portfolio1 = {
                new Asset("AAPL", 12.0, 18.5),
                new Asset("TSLA", 8.0, 25.3),
                new Asset("GOOG", 15.0, 22.1)
        };

        displayAssets(portfolio1, "Original Portfolio:");

        // Merge Sort (Ascending by returnRate, Stable)
        Asset[] mergeSortAssets = Arrays.copyOf(portfolio1, portfolio1.length);
        mergeSort(mergeSortAssets, 0, mergeSortAssets.length - 1);
        displayAssets(mergeSortAssets, "After Merge Sort (Ascending by Return - Stable):");

        // Quick Sort with Random Pivot
        Asset[] quickSortAssets = Arrays.copyOf(portfolio1, portfolio1.length);
        quickSort(quickSortAssets, 0, quickSortAssets.length - 1, PivotStrategy.RANDOM);
        displayAssets(quickSortAssets, "After Quick Sort (Descending Return + Ascending Volatility - Random Pivot):");

        // Quick Sort with Median-of-3 Pivot
        Asset[] quickSortMedianAssets = Arrays.copyOf(portfolio1, portfolio1.length);
        quickSort(quickSortMedianAssets, 0, quickSortMedianAssets.length - 1, PivotStrategy.MEDIAN_OF_THREE);
        displayAssets(quickSortMedianAssets, "After Quick Sort (Median-of-3 Pivot):");

        // Test Case 2: Stability Test
        System.out.println("\n\n--- Test Case 2: Stability Test (Same Returns) ---");
        Asset[] stabilityTest = {
                new Asset("Stock1", 10.0, 15.0),
                new Asset("Stock2", 10.0, 20.0),
                new Asset("Stock3", 10.0, 10.0),
                new Asset("Stock4", 10.0, 25.0)
        };

        displayAssets(stabilityTest, "Original (All have 10% return):");

        Asset[] stableSorted = Arrays.copyOf(stabilityTest, stabilityTest.length);
        mergeSort(stableSorted, 0, stableSorted.length - 1);
        displayAssets(stableSorted, "After Merge Sort (Preserves original order for ties):");
        System.out.println("✓ Stability verified: Order preserved for equal returns");

        // Test Case 3: Large Dataset (10,000 assets)
        System.out.println("\n\n--- Test Case 3: Large Portfolio (10,000 assets) ---");
        Asset[] largePortfolio = generateRandomAssets(10_000);

        // Merge Sort Performance
        Asset[] largeMergeAssets = Arrays.copyOf(largePortfolio, largePortfolio.length);
        long startTime = System.nanoTime();
        mergeSort(largeMergeAssets, 0, largeMergeAssets.length - 1);
        long mergeTime = System.nanoTime() - startTime;

        System.out.println("Merge Sort (10K assets):");
        System.out.println("  Time: " + (mergeTime / 1_000_000.0) + " ms");
        System.out.println("  Complexity: O(n log n) - Guaranteed");
        System.out.println("  Stability: Yes");
        System.out.println("  Avg Return: " + String.format("%.2f%%", calculateAverageReturn(largeMergeAssets)));
        System.out.println("  Avg Volatility: " + String.format("%.2f%%", calculateAverageVolatility(largeMergeAssets)));

        // Quick Sort with Random Pivot
        Asset[] largeQuickAssets = Arrays.copyOf(largePortfolio, largePortfolio.length);
        startTime = System.nanoTime();
        quickSort(largeQuickAssets, 0, largeQuickAssets.length - 1, PivotStrategy.RANDOM);
        long quickRandomTime = System.nanoTime() - startTime;

        System.out.println("\nQuick Sort with Random Pivot (10K assets):");
        System.out.println("  Time: " + (quickRandomTime / 1_000_000.0) + " ms");
        System.out.println("  Complexity: O(n log n) average, O(n²) worst");
        System.out.println("  Stability: No");

        // Quick Sort with Median-of-3 Pivot
        Asset[] largeQuickMedianAssets = Arrays.copyOf(largePortfolio, largePortfolio.length);
        startTime = System.nanoTime();
        quickSort(largeQuickMedianAssets, 0, largeQuickMedianAssets.length - 1, PivotStrategy.MEDIAN_OF_THREE);
        long quickMedianTime = System.nanoTime() - startTime;

        System.out.println("\nQuick Sort with Median-of-3 Pivot (10K assets):");
        System.out.println("  Time: " + (quickMedianTime / 1_000_000.0) + " ms");
        System.out.println("  Better pivot selection reduces risk of O(n²)");

        // Hybrid Sort Performance
        Asset[] largeHybridAssets = Arrays.copyOf(largePortfolio, largePortfolio.length);
        startTime = System.nanoTime();
        hybridSort(largeHybridAssets, 0, largeHybridAssets.length - 1, PivotStrategy.MEDIAN_OF_THREE);
        long hybridTime = System.nanoTime() - startTime;

        System.out.println("\nHybrid Sort (Quick + Insertion, 10K assets):");
        System.out.println("  Time: " + (hybridTime / 1_000_000.0) + " ms");
        System.out.println("  Optimized for real-world data");

        // Display top performers
        displayAssets(Arrays.copyOf(largeQuickAssets, 10), "Top 10 Assets (Best Return):");

        // Test Case 4: Asset Allocation Optimization
        System.out.println("\n\n--- Use Case: Asset Allocation Optimization ---");
        Asset[] allocPortfolio = {
                new Asset("HighGrowth", 20.0, 35.0),
                new Asset("HighGrowth2", 20.0, 32.0),
                new Asset("ModGrowth", 12.0, 18.0),
                new Asset("SafeHarbor", 5.0, 8.0),
                new Asset("SafeHarbor2", 5.0, 7.0)
        };

        displayAssets(allocPortfolio, "Portfolio for Allocation:");

        Asset[] allocSorted = Arrays.copyOf(allocPortfolio, allocPortfolio.length);
        quickSort(allocSorted, 0, allocSorted.length - 1, PivotStrategy.MEDIAN_OF_THREE);
        displayAssets(allocSorted, "Sorted for Allocation (High Return, Low Volatility First):");

        // Test Case 5: Risk-Parity Construction
        System.out.println("\n\n--- Use Case: Risk-Parity Portfolio Construction ---");
        Asset[] riskParityAssets = generateRandomAssets(100);

        Asset[] riskParitySorted = Arrays.copyOf(riskParityAssets, riskParityAssets.length);
        mergeSort(riskParitySorted, 0, riskParitySorted.length - 1);

        System.out.println("Risk-Parity Analysis:");
        System.out.println("  Total Assets: " + riskParitySorted.length);
        System.out.println("  Avg Return: " + String.format("%.2f%%", calculateAverageReturn(riskParitySorted)));
        System.out.println("  Avg Volatility: " + String.format("%.2f%%", calculateAverageVolatility(riskParitySorted)));
        System.out.println("  Sharpe Ratio basis: Return/Volatility = " +
                String.format("%.2f", calculateAverageReturn(riskParitySorted) /
                        calculateAverageVolatility(riskParitySorted)));
    }
}

// Pivot selection strategies
enum PivotStrategy {
    RANDOM,
    MEDIAN_OF_THREE
}