import java.util.*;

class RiskBand {
    int riskScore;
    String riskLevel;
    double interestRate;
    String complianceStatus;

    RiskBand(int riskScore, String riskLevel, double interestRate, String complianceStatus) {
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.interestRate = interestRate;
        this.complianceStatus = complianceStatus;
    }

    @Override
    public String toString() {
        return "Score:" + riskScore + " | Level:" + riskLevel +
                " | Rate:" + String.format("%.2f%%", interestRate) +
                " | Status:" + complianceStatus;
    }
}

class SearchResult {
    int comparisons;
    int index;
    String value;
    long searchTime;
    String searchType;

    SearchResult(int comparisons, int index, String value, long searchTime, String searchType) {
        this.comparisons = comparisons;
        this.index = index;
        this.value = value;
        this.searchTime = searchTime;
        this.searchType = searchType;
    }

    @Override
    public String toString() {
        return "Type: " + searchType + " | Index: " + index + " | Value: " + value +
                " | Comparisons: " + comparisons + " | Time: " + (searchTime / 1000.0) + " µs";
    }
}

public class RiskThresholdBinaryLookup {

    // ===== LINEAR SEARCH =====

    /**
     * Linear Search for unsorted risk bands
     * Find threshold match
     */
    static SearchResult linearSearch(int[] riskScores, int threshold) {
        int comparisons = 0;
        int index = -1;

        long startTime = System.nanoTime();

        for (int i = 0; i < riskScores.length; i++) {
            comparisons++;
            if (riskScores[i] == threshold) {
                index = i;
                break;
            }
        }

        long endTime = System.nanoTime();
        String value = index != -1 ? String.valueOf(riskScores[index]) : "Not found";

        return new SearchResult(comparisons, index, value, endTime - startTime, "Linear");
    }

    // ===== BINARY SEARCH VARIANTS =====

    /**
     * Binary Search - Find Floor (largest value ≤ target)
     */
    static SearchResult binarySearchFloor(int[] sortedRisks, int threshold) {
        int comparisons = 0;
        int low = 0;
        int high = sortedRisks.length - 1;
        int floorIndex = -1;

        long startTime = System.nanoTime();

        while (low <= high) {
            int mid = low + (high - low) / 2;
            comparisons++;

            if (sortedRisks[mid] == threshold) {
                floorIndex = mid;
                break;
            } else if (sortedRisks[mid] < threshold) {
                floorIndex = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        long endTime = System.nanoTime();
        String value = floorIndex != -1 ? String.valueOf(sortedRisks[floorIndex]) : "None";

        return new SearchResult(comparisons, floorIndex, value, endTime - startTime, "Binary Floor");
    }

    /**
     * Binary Search - Find Ceiling (smallest value ≥ target)
     */
    static SearchResult binarySearchCeiling(int[] sortedRisks, int threshold) {
        int comparisons = 0;
        int low = 0;
        int high = sortedRisks.length - 1;
        int ceilingIndex = -1;

        long startTime = System.nanoTime();

        while (low <= high) {
            int mid = low + (high - low) / 2;
            comparisons++;

            if (sortedRisks[mid] == threshold) {
                ceilingIndex = mid;
                break;
            } else if (sortedRisks[mid] < threshold) {
                low = mid + 1;
            } else {
                ceilingIndex = mid;
                high = mid - 1;
            }
        }

        long endTime = System.nanoTime();
        String value = ceilingIndex != -1 ? String.valueOf(sortedRisks[ceilingIndex]) : "None";

        return new SearchResult(comparisons, ceilingIndex, value, endTime - startTime, "Binary Ceiling");
    }

    /**
     * Lower Bound - First element ≥ target
     */
    static SearchResult lowerBound(int[] sortedRisks, int threshold) {
        int comparisons = 0;
        int low = 0;
        int high = sortedRisks.length;
        int index = high;

        long startTime = System.nanoTime();

        while (low < high) {
            int mid = low + (high - low) / 2;
            comparisons++;

            if (sortedRisks[mid] < threshold) {
                low = mid + 1;
            } else {
                index = mid;
                high = mid;
            }
        }

        long endTime = System.nanoTime();
        String value = index < sortedRisks.length ? String.valueOf(sortedRisks[index]) : "None";

        return new SearchResult(comparisons, index, value, endTime - startTime, "Lower Bound");
    }

    /**
     * Upper Bound - First element > target
     */
    static SearchResult upperBound(int[] sortedRisks, int threshold) {
        int comparisons = 0;
        int low = 0;
        int high = sortedRisks.length;
        int index = high;

        long startTime = System.nanoTime();

        while (low < high) {
            int mid = low + (high - low) / 2;
            comparisons++;

            if (sortedRisks[mid] <= threshold) {
                low = mid + 1;
            } else {
                index = mid;
                high = mid;
            }
        }

        long endTime = System.nanoTime();
        String value = index < sortedRisks.length ? String.valueOf(sortedRisks[index]) : "None";

        return new SearchResult(comparisons, index, value, endTime - startTime, "Upper Bound");
    }

    /**
     * Find insertion point for new client risk score
     */
    static int findInsertionPoint(int[] sortedRisks, int newRisk) {
        int low = 0;
        int high = sortedRisks.length;

        while (low < high) {
            int mid = low + (high - low) / 2;

            if (sortedRisks[mid] < newRisk) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        return low;
    }

    // ===== SORTING =====

    static void quickSort(int[] arr, int left, int right) {
        if (left < right) {
            int pivotIndex = partition(arr, left, right);
            quickSort(arr, left, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, right);
        }
    }

    static int partition(int[] arr, int left, int right) {
        int mid = left + (right - left) / 2;
        int pivot = arr[mid];
        swap(arr, mid, right);

        int i = left - 1;

        for (int j = left; j < right; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, right);
        return i + 1;
    }

    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // ===== UTILITY FUNCTIONS =====

    static void displayRiskBands(RiskBand[] bands, String title) {
        System.out.println("\n" + title);
        for (int i = 0; i < bands.length; i++) {
            System.out.println((i + 1) + ". " + bands[i]);
        }
    }

    static void displayRiskScores(int[] scores, String title) {
        System.out.println("\n" + title);
        System.out.print("Scores: [");
        for (int i = 0; i < scores.length; i++) {
            System.out.print(scores[i]);
            if (i < scores.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    static RiskBand[] generateRandomRiskBands(int count) {
        RiskBand[] bands = new RiskBand[count];
        Random rand = new Random();
        String[] levels = {"Low", "Medium", "High", "Critical"};
        String[] statuses = {"Compliant", "Warning", "Restricted"};

        int[] riskScores = new int[count];
        for (int i = 0; i < count; i++) {
            riskScores[i] = 10 + (i * 20);
        }

        for (int i = 0; i < count; i++) {
            String level = levels[i % levels.length];
            double rate = 2.0 + (i * 0.5);
            String status = statuses[rand.nextInt(statuses.length)];
            bands[i] = new RiskBand(riskScores[i], level, rate, status);
        }

        return bands;
    }

    // ===== MAIN TEST CASES =====

    public static void main(String[] args) {
        System.out.println("===== Risk Threshold Binary Lookup System =====");

        // Test Case 1: Small Dataset
        System.out.println("\n\n--- Test Case 1: Small Risk Band Dataset ---");
        int[] smallRisks = {10, 25, 50, 100};
        displayRiskScores(smallRisks, "Sorted Risk Bands:");

        int threshold = 30;
        System.out.println("\nSearching for threshold: " + threshold);

        // Linear Search
        SearchResult linearResult = linearSearch(smallRisks, threshold);
        System.out.println("\nLinear Search: " + linearResult);

        // Floor
        SearchResult floorResult = binarySearchFloor(smallRisks, threshold);
        System.out.println("Binary Floor: " + floorResult);
        System.out.println("  → Largest risk band ≤ " + threshold + " is " + floorResult.value);

        // Ceiling
        SearchResult ceilingResult = binarySearchCeiling(smallRisks, threshold);
        System.out.println("Binary Ceiling: " + ceilingResult);
        System.out.println("  → Smallest risk band ≥ " + threshold + " is " + ceilingResult.value);

        // Test Case 2: Exact Match
        System.out.println("\n\n--- Test Case 2: Exact Match Search ---");
        int exactThreshold = 50;
        System.out.println("Searching for threshold: " + exactThreshold);

        SearchResult exactFloor = binarySearchFloor(smallRisks, exactThreshold);
        SearchResult exactCeiling = binarySearchCeiling(smallRisks, exactThreshold);

        System.out.println("Floor: " + exactFloor);
        System.out.println("Ceiling: " + exactCeiling);

        // Test Case 3: Insertion Point
        System.out.println("\n\n--- Test Case 3: New Client Risk Band Insertion ---");
        int newClientRisk = 35;
        int insertPoint = findInsertionPoint(smallRisks, newClientRisk);

        System.out.println("New client risk score: " + newClientRisk);
        System.out.println("Insertion point: Index " + insertPoint);
        System.out.println("Between bands: " +
                (insertPoint > 0 ? smallRisks[insertPoint - 1] : "START") +
                " and " +
                (insertPoint < smallRisks.length ? smallRisks[insertPoint] : "END"));

        // Test Case 4: Lower and Upper Bounds
        System.out.println("\n\n--- Test Case 4: Lower/Upper Bounds ---");
        int targetBound = 30;
        System.out.println("Target: " + targetBound);

        SearchResult lower = lowerBound(smallRisks, targetBound);
        SearchResult upper = upperBound(smallRisks, targetBound);

        System.out.println("Lower Bound (≥): " + lower);
        System.out.println("Upper Bound (>): " + upper);

        // Test Case 5: Large Dataset
        System.out.println("\n\n--- Test Case 5: Large Risk Band Dataset ---");
        int[] largeRisks = new int[10000];
        for (int i = 0; i < 10000; i++) {
            largeRisks[i] = (i + 1) * 10;
        }

        int largeThreshold = 50000;
        System.out.println("Dataset: 10,000 sorted risk bands");
        System.out.println("Searching for threshold: " + largeThreshold);

        SearchResult largeFloor = binarySearchFloor(largeRisks, largeThreshold);
        SearchResult largeCeiling = binarySearchCeiling(largeRisks, largeThreshold);

        System.out.println("\nFloor Result: " + largeFloor);
        System.out.println("Ceiling Result: " + largeCeiling);

        System.out.println("\nEfficiency:");
        System.out.println("  Max possible comparisons (linear): " + largeRisks.length);
        System.out.println("  Actual comparisons (binary): " + largeFloor.comparisons);
        System.out.println("  Reduction: " + String.format("%.2f%%",
                (1 - (double)largeFloor.comparisons / largeRisks.length) * 100));

        // Test Case 6: Dynamic Risk Pricing
        System.out.println("\n\n--- Use Case: Dynamic Risk Pricing Tables ---");
        RiskBand[] pricingBands = {
                new RiskBand(10, "Low", 2.5, "Compliant"),
                new RiskBand(25, "Low-Medium", 3.0, "Compliant"),
                new RiskBand(50, "Medium", 4.0, "Warning"),
                new RiskBand(75, "High", 6.0, "Warning"),
                new RiskBand(100, "Critical", 8.5, "Restricted")
        };

        displayRiskBands(pricingBands, "Current Pricing Bands:");

        int clientRisk = 55;
        int[] priceRisks = {10, 25, 50, 75, 100};

        SearchResult clientFloor = binarySearchFloor(priceRisks, clientRisk);
        SearchResult clientCeiling = binarySearchCeiling(priceRisks, clientRisk);

        System.out.println("\nNew Client Risk Assessment:");
        System.out.println("  Client Risk Score: " + clientRisk);
        System.out.println("  Floor Band: " + clientFloor.value + "% (Index: " + clientFloor.index + ")");
        System.out.println("  Ceiling Band: " + clientCeiling.value + "% (Index: " + clientCeiling.index + ")");
        System.out.println("  Recommended Rate: " +
                String.format("%.2f%%", (4.0 + 6.0) / 2) + " (interpolated)");
        System.out.println("  Status: ✓ Band assignment complete");

        // Test Case 7: Compliance Band Assignment
        System.out.println("\n\n--- Use Case: Compliance Band Assignment ---");
        int[] complianceThresholds = {20, 40, 60, 80, 100};
        String[] complianceLevels = {"Approved", "Monitored", "Restricted", "Flagged", "Blocked"};

        displayRiskScores(complianceThresholds, "Compliance Thresholds:");

        int[] clientRisks = {15, 35, 65, 95, 105};

        System.out.println("\nCompliance Assessment:");
        for (int risk : clientRisks) {
            SearchResult floor = binarySearchFloor(complianceThresholds, risk);
            int bandIndex = floor.index >= 0 ? floor.index : 0;
            String status = bandIndex < complianceLevels.length ?
                    complianceLevels[bandIndex] : "Unknown";

            System.out.println("  Client Risk " + risk + " → " + status +
                    " (Comparisons: " + floor.comparisons + ")");
        }

        // Test Case 8: Performance Comparison
        System.out.println("\n\n--- Performance Comparison ---");
        int[] perfRisks = new int[100000];
        for (int i = 0; i < 100000; i++) {
            perfRisks[i] = i * 2;
        }

        int perfThreshold = 150000;

        long linearTime = 0;
        long binaryTime = 0;

        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            linearSearch(perfRisks, perfThreshold);
            linearTime += System.nanoTime() - start;

            start = System.nanoTime();
            binarySearchFloor(perfRisks, perfThreshold);
            binaryTime += System.nanoTime() - start;
        }

        System.out.println("Dataset: 100,000 risk bands (10 iterations each)");
        System.out.println("Linear Search Average: " + (linearTime / 10 / 1_000_000.0) + " ms");
        System.out.println("Binary Search Average: " + (binaryTime / 10 / 1_000_000.0) + " ms");
        System.out.println("Speedup: " + String.format("%.2fx", (double)linearTime / binaryTime));
    }
}