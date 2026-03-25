import java.util.*;

class Transaction {
    String accountId;
    long timestamp;
    double amount;
    String type;

    Transaction(String accountId, long timestamp, double amount, String type) {
        this.accountId = accountId;
        this.timestamp = timestamp;
        this.amount = amount;
        this.type = type;
    }

    @Override
    public String toString() {
        return accountId + " | " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(timestamp)) + " | $" +
                String.format("%.2f", amount) + " | " + type;
    }
}

class SearchResult {
    int comparisons;
    int occurrences;
    List<Integer> indices;
    long searchTime;

    SearchResult(int comparisons, int occurrences, List<Integer> indices, long searchTime) {
        this.comparisons = comparisons;
        this.occurrences = occurrences;
        this.indices = indices;
        this.searchTime = searchTime;
    }

    @Override
    public String toString() {
        return "Comparisons: " + comparisons + " | Occurrences: " + occurrences +
                " | Time: " + searchTime + " ns";
    }
}

public class AccountIDLookup {

    // ===== LINEAR SEARCH =====

    /**
     * Linear Search - O(n)
     * Find first/last occurrence and count all occurrences
     */
    static SearchResult linearSearch(Transaction[] logs, String targetAccountId) {
        int comparisons = 0;
        int occurrences = 0;
        List<Integer> indices = new ArrayList<>();

        long startTime = System.nanoTime();

        for (int i = 0; i < logs.length; i++) {
            comparisons++;
            if (logs[i].accountId.equals(targetAccountId)) {
                occurrences++;
                indices.add(i);
            }
        }

        long endTime = System.nanoTime();
        long searchTime = endTime - startTime;

        return new SearchResult(comparisons, occurrences, indices, searchTime);
    }

    // ===== BINARY SEARCH =====

    /**
     * Binary Search - O(log n)
     * Find first occurrence of target
     */
    static int binarySearchFirst(Transaction[] logs, String targetAccountId) {
        int low = 0;
        int high = logs.length - 1;
        int result = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = logs[mid].accountId.compareTo(targetAccountId);

            if (cmp == 0) {
                result = mid;
                high = mid - 1; // Continue searching left for first occurrence
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return result;
    }

    /**
     * Binary Search - Find last occurrence
     */
    static int binarySearchLast(Transaction[] logs, String targetAccountId) {
        int low = 0;
        int high = logs.length - 1;
        int result = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = logs[mid].accountId.compareTo(targetAccountId);

            if (cmp == 0) {
                result = mid;
                low = mid + 1; // Continue searching right for last occurrence
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return result;
    }

    /**
     * Binary Search with comparison counting
     */
    static SearchResult binarySearchWithCount(Transaction[] logs, String targetAccountId) {
        int comparisons = 0;
        int low = 0;
        int high = logs.length - 1;
        int firstIndex = -1;

        long startTime = System.nanoTime();

        // Find first occurrence
        while (low <= high) {
            int mid = low + (high - low) / 2;
            comparisons++;
            int cmp = logs[mid].accountId.compareTo(targetAccountId);

            if (cmp == 0) {
                firstIndex = mid;
                high = mid - 1;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        // If not found
        if (firstIndex == -1) {
            long endTime = System.nanoTime();
            return new SearchResult(comparisons, 0, new ArrayList<>(), endTime - startTime);
        }

        // Find last occurrence
        low = firstIndex;
        high = logs.length - 1;
        int lastIndex = firstIndex;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            comparisons++;
            int cmp = logs[mid].accountId.compareTo(targetAccountId);

            if (cmp == 0) {
                lastIndex = mid;
                low = mid + 1;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        // Count occurrences
        int occurrences = lastIndex - firstIndex + 1;
        List<Integer> indices = new ArrayList<>();
        for (int i = firstIndex; i <= lastIndex; i++) {
            indices.add(i);
        }

        long endTime = System.nanoTime();

        return new SearchResult(comparisons, occurrences, indices, endTime - startTime);
    }

    // ===== SORTING =====

    /**
     * Quick Sort for sorting transaction logs by accountId
     */
    static void quickSortByAccountId(Transaction[] logs, int left, int right) {
        if (left < right) {
            int pivotIndex = partition(logs, left, right);
            quickSortByAccountId(logs, left, pivotIndex - 1);
            quickSortByAccountId(logs, pivotIndex + 1, right);
        }
    }

    static int partition(Transaction[] logs, int left, int right) {
        int mid = left + (right - left) / 2;
        Transaction pivot = logs[mid];
        swap(logs, mid, right);

        int i = left - 1;

        for (int j = left; j < right; j++) {
            if (logs[j].accountId.compareTo(pivot.accountId) <= 0) {
                i++;
                swap(logs, i, j);
            }
        }

        swap(logs, i + 1, right);
        return i + 1;
    }

    static void swap(Transaction[] logs, int i, int j) {
        Transaction temp = logs[i];
        logs[i] = logs[j];
        logs[j] = temp;
    }

    // ===== UTILITY FUNCTIONS =====

    static void displayTransactions(Transaction[] logs, String title) {
        System.out.println("\n" + title);
        for (int i = 0; i < Math.min(logs.length, 10); i++) {
            System.out.println((i + 1) + ". " + logs[i]);
        }
        if (logs.length > 10) {
            System.out.println("... and " + (logs.length - 10) + " more transactions");
        }
    }

    static void displayTransactionsByIndex(Transaction[] logs, List<Integer> indices, String title) {
        System.out.println("\n" + title);
        for (int i = 0; i < Math.min(indices.size(), 10); i++) {
            int idx = indices.get(i);
            System.out.println((i + 1) + ". [" + idx + "] " + logs[idx]);
        }
        if (indices.size() > 10) {
            System.out.println("... and " + (indices.size() - 10) + " more transactions");
        }
    }

    static Transaction[] generateRandomTransactions(int count) {
        Transaction[] transactions = new Transaction[count];
        Random rand = new Random();
        String[] accountIds = {"accA", "accB", "accC", "accD", "accE"};
        String[] types = {"DEPOSIT", "WITHDRAWAL", "TRANSFER", "FEE"};

        long baseTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            String accountId = accountIds[rand.nextInt(accountIds.length)];
            long timestamp = baseTime - rand.nextLong(1_000_000_000L);
            double amount = 10 + rand.nextDouble() * 9990;
            String type = types[rand.nextInt(types.length)];

            transactions[i] = new Transaction(accountId, timestamp, amount, type);
        }

        return transactions;
    }

    // ===== MAIN TEST CASES =====

    public static void main(String[] args) {
        System.out.println("===== Account ID Lookup in Transaction Logs =====");

        // Test Case 1: Small Dataset
        System.out.println("\n\n--- Test Case 1: Small Dataset ---");
        Transaction[] smallLogs = {
                new Transaction("accB", System.currentTimeMillis() - 1000000, 150.00, "DEPOSIT"),
                new Transaction("accA", System.currentTimeMillis() - 900000, 200.00, "WITHDRAWAL"),
                new Transaction("accB", System.currentTimeMillis() - 800000, 75.50, "TRANSFER"),
                new Transaction("accC", System.currentTimeMillis() - 700000, 300.00, "DEPOSIT"),
                new Transaction("accB", System.currentTimeMillis() - 600000, 50.00, "FEE")
        };

        displayTransactions(smallLogs, "Original Logs (Unsorted):");

        // Sort for binary search
        Transaction[] sortedLogs = Arrays.copyOf(smallLogs, smallLogs.length);
        quickSortByAccountId(sortedLogs, 0, sortedLogs.length - 1);
        displayTransactions(sortedLogs, "Sorted Logs (by Account ID):");

        // Linear Search
        System.out.println("\n--- Linear Search on Unsorted Data ---");
        SearchResult linearResult = linearSearch(smallLogs, "accB");
        System.out.println("Searching for: accB");
        System.out.println(linearResult);
        displayTransactionsByIndex(smallLogs, linearResult.indices, "Occurrences Found:");

        // Binary Search
        System.out.println("\n--- Binary Search on Sorted Data ---");
        SearchResult binaryResult = binarySearchWithCount(sortedLogs, "accB");
        System.out.println("Searching for: accB");
        System.out.println(binaryResult);
        displayTransactionsByIndex(sortedLogs, binaryResult.indices, "Occurrences Found:");

        // Test Case 2: Handling Duplicates
        System.out.println("\n\n--- Test Case 2: Handling Duplicates ---");
        Transaction[] duplicateLogs = new Transaction[100];
        for (int i = 0; i < 25; i++) {
            duplicateLogs[i] = new Transaction("accA", System.currentTimeMillis() - i*1000, 100 + i, "DEPOSIT");
        }
        for (int i = 25; i < 50; i++) {
            duplicateLogs[i] = new Transaction("accB", System.currentTimeMillis() - i*1000, 200 + i, "TRANSFER");
        }
        for (int i = 50; i < 75; i++) {
            duplicateLogs[i] = new Transaction("accC", System.currentTimeMillis() - i*1000, 300 + i, "WITHDRAWAL");
        }
        for (int i = 75; i < 100; i++) {
            duplicateLogs[i] = new Transaction("accB", System.currentTimeMillis() - i*1000, 400 + i, "FEE");
        }

        Transaction[] sortedDuplicateLogs = Arrays.copyOf(duplicateLogs, duplicateLogs.length);
        quickSortByAccountId(sortedDuplicateLogs, 0, sortedDuplicateLogs.length - 1);

        System.out.println("Dataset with duplicates: 100 transactions");
        System.out.println("Searching for: accB (appears 50 times)");

        SearchResult duplicateResult = binarySearchWithCount(sortedDuplicateLogs, "accB");
        System.out.println(duplicateResult);
        System.out.println("First occurrence at index: " + duplicateResult.indices.get(0));
        System.out.println("Last occurrence at index: " + duplicateResult.indices.get(duplicateResult.indices.size() - 1));

        // Test Case 3: Large Dataset (1 Million Transactions)
        System.out.println("\n\n--- Test Case 3: Large Dataset (1 Million Transactions) ---");
        Transaction[] largeLogs = generateRandomTransactions(1_000_000);

        System.out.println("Generated 1 million transaction logs");
        System.out.println("Account IDs: accA, accB, accC, accD, accE");

        // Sort for binary search
        long sortStart = System.nanoTime();
        quickSortByAccountId(largeLogs, 0, largeLogs.length - 1);
        long sortTime = System.nanoTime() - sortStart;
        System.out.println("Sort time: " + (sortTime / 1_000_000.0) + " ms");

        // Linear Search on 1M
        System.out.println("\n--- Linear Search Performance (1M transactions) ---");
        SearchResult linearLarge = linearSearch(largeLogs, "accC");
        System.out.println("Searching for: accC");
        System.out.println(linearLarge);
        System.out.println("Found " + linearLarge.occurrences + " occurrences");

        // Binary Search on 1M
        System.out.println("\n--- Binary Search Performance (1M transactions) ---");
        SearchResult binaryLarge = binarySearchWithCount(largeLogs, "accC");
        System.out.println("Searching for: accC");
        System.out.println(binaryLarge);
        System.out.println("Found " + binaryLarge.occurrences + " occurrences");

        // Compare efficiency
        System.out.println("\n--- Efficiency Comparison ---");
        System.out.println("Linear: " + linearLarge.comparisons + " comparisons, " +
                (linearLarge.searchTime / 1_000.0) + " µs");
        System.out.println("Binary: " + binaryLarge.comparisons + " comparisons, " +
                (binaryLarge.searchTime / 1_000.0) + " µs");
        System.out.println("Speedup: " + String.format("%.2fx",
                (double)linearLarge.searchTime / binaryLarge.searchTime));

        // Test Case 4: Citi Transaction Forensics
        System.out.println("\n\n--- Use Case: Citi Transaction Forensics ---");
        Transaction[] citiLogs = generateRandomTransactions(500_000);
        quickSortByAccountId(citiLogs, 0, citiLogs.length - 1);

        String suspectAccount = "accD";
        SearchResult forensicsResult = binarySearchWithCount(citiLogs, suspectAccount);

        System.out.println("Forensic Investigation:");
        System.out.println("  Suspect Account: " + suspectAccount);
        System.out.println("  Total Transactions: " + forensicsResult.occurrences);
        System.out.println("  Search Time: " + (forensicsResult.searchTime / 1_000.0) + " µs");
        System.out.println("  Comparison Count: " + forensicsResult.comparisons);
        System.out.println("  Time Complexity: O(log n)");

        // Test Case 5: Dispute Resolution
        System.out.println("\n\n--- Use Case: Dispute Resolution Lookup ---");
        String disputeAccount = "accA";
        SearchResult disputeResult = binarySearchWithCount(citiLogs, disputeAccount);

        System.out.println("Dispute Resolution:");
        System.out.println("  Account ID: " + disputeAccount);
        System.out.println("  Transactions Found: " + disputeResult.occurrences);
        System.out.println("  Query Time: " + (disputeResult.searchTime / 1_000_000.0) + " ms");
        System.out.println("  Status: ✓ Ready for audit");

        // Test Case 6: Regulatory Reporting
        System.out.println("\n\n--- Use Case: Regulatory Reporting ---");
        System.out.println("Compliance Audit Report:");
        System.out.println("  Dataset Size: 1,000,000 transactions");
        System.out.println("  Search Algorithm: Binary Search");
        System.out.println("  Worst-case Comparisons: " + (int)(Math.log(1_000_000) / Math.log(2)));
        System.out.println("  Sorted: Yes");
        System.out.println("  Audit Status: ✓ Compliant");
    }
}