import java.util.*;

class Client {
    String clientId;
    int riskScore;
    double accountBalance;

    Client(String clientId, int riskScore, double accountBalance) {
        this.clientId = clientId;
        this.riskScore = riskScore;
        this.accountBalance = accountBalance;
    }

    @Override
    public String toString() {
        return clientId + ":" + riskScore + "(Balance: $" + accountBalance + ")";
    }
}

public class ClientRiskScoreRanking {

    // Bubble Sort - Sort by riskScore ascending (with swap visualization)
    static void bubbleSort(Client[] clients) {
        int n = clients.length;
        int swapCount = 0;

        System.out.println("\n--- Bubble Sort (Ascending by Risk Score) ---");

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (clients[j].riskScore > clients[j + 1].riskScore) {
                    // Swap
                    Client temp = clients[j];
                    clients[j] = clients[j + 1];
                    clients[j + 1] = temp;
                    swapCount++;

                    // Visualize swaps for demo
                    System.out.println("Swap " + swapCount + ": " + clients[j] + " <-> " + clients[j + 1]);
                }
            }
        }

        System.out.println("Total swaps: " + swapCount);
    }

    // Insertion Sort - Sort by riskScore DESC + accountBalance
    static void insertionSort(Client[] clients) {
        int n = clients.length;

        System.out.println("\n--- Insertion Sort (Descending by Risk Score + Account Balance) ---");

        for (int i = 1; i < n; i++) {
            Client key = clients[i];
            int j = i - 1;

            while (j >= 0 && compare(clients[j], key) < 0) {
                clients[j + 1] = clients[j];
                j--;
            }
            clients[j + 1] = key;
        }
    }

    // Custom comparator: DESC by riskScore, then DESC by accountBalance
    static int compare(Client c1, Client c2) {
        // Compare by riskScore descending
        if (c1.riskScore != c2.riskScore) {
            return c2.riskScore - c1.riskScore; // Descending
        }
        // If riskScore equal, compare by accountBalance descending
        return Double.compare(c2.accountBalance, c1.accountBalance);
    }

    // Identify top N highest risk clients
    static Client[] getTopRiskClients(Client[] clients, int topN) {
        Client[] topClients = new Client[Math.min(topN, clients.length)];
        for (int i = 0; i < topClients.length; i++) {
            topClients[i] = clients[i];
        }
        return topClients;
    }

    // Display clients
    static void displayClients(Client[] clients, String title) {
        System.out.println("\n" + title);
        for (int i = 0; i < clients.length; i++) {
            System.out.println((i + 1) + ". " + clients[i]);
        }
    }

    // Test case with sample data
    public static void main(String[] args) {
        System.out.println("===== Client Risk Score Ranking System =====");

        // Sample Input: clientC:80, clientA:20, clientB:50
        Client[] clients1 = {
                new Client("ClientC", 80, 50000),
                new Client("ClientA", 20, 100000),
                new Client("ClientB", 50, 75000)
        };

        System.out.println("\n--- Test Case 1: Small Dataset ---");
        displayClients(clients1, "Original Clients:");

        // Make a copy for Bubble Sort
        Client[] bubbleClients = Arrays.copyOf(clients1, clients1.length);
        bubbleSort(bubbleClients);
        displayClients(bubbleClients, "After Bubble Sort (Ascending):");

        // Make a copy for Insertion Sort
        Client[] insertionClients = Arrays.copyOf(clients1, clients1.length);
        insertionSort(insertionClients);
        displayClients(insertionClients, "After Insertion Sort (Descending):");

        // Get top 3 risks
        Client[] topRisks = getTopRiskClients(insertionClients, 3);
        displayClients(topRisks, "Top 3 Highest Risk Clients:");

        // Large dataset test
        System.out.println("\n\n--- Test Case 2: Large Dataset (500 clients) ---");
        Client[] largeDataset = generateRandomClients(500);

        Client[] largeBubbleClients = Arrays.copyOf(largeDataset, largeDataset.length);
        long startTime = System.nanoTime();
        bubbleSort(largeBubbleClients);
        long bubbleTime = System.nanoTime() - startTime;
        System.out.println("Bubble Sort Time: " + (bubbleTime / 1_000_000.0) + " ms");

        Client[] largeInsertionClients = Arrays.copyOf(largeDataset, largeDataset.length);
        startTime = System.nanoTime();
        insertionSort(largeInsertionClients);
        long insertionTime = System.nanoTime() - startTime;
        System.out.println("Insertion Sort Time: " + (insertionTime / 1_000_000.0) + " ms");

        displayClients(Arrays.copyOf(largeInsertionClients, 10), "Top 10 Highest Risk Clients:");

        // Real-world scenario: KYC Risk Prioritization
        System.out.println("\n\n--- Use Case: KYC Risk Prioritization ---");
        Client[] kycClients = {
                new Client("KYC_001", 95, 5000),      // High risk, low balance
                new Client("KYC_002", 75, 250000),    // Medium-high risk, high balance
                new Client("KYC_003", 45, 100000),    // Medium risk
                new Client("KYC_004", 85, 50000),     // High risk, medium balance
                new Client("KYC_005", 30, 500000)     // Low risk, very high balance
        };

        Client[] kycSorted = Arrays.copyOf(kycClients, kycClients.length);
        insertionSort(kycSorted);
        displayClients(kycSorted, "KYC Risk Prioritization (Sorted):");

        Client[] priorityReview = getTopRiskClients(kycSorted, 3);
        displayClients(priorityReview, "Clients Requiring Immediate Review:");
    }

    // Generate random clients for testing
    static Client[] generateRandomClients(int count) {
        Client[] clients = new Client[count];
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            String clientId = "Client_" + (i + 1);
            int riskScore = rand.nextInt(101); // 0-100
            double balance = 10000 + rand.nextDouble() * 490000; // $10k-$500k
            clients[i] = new Client(clientId, riskScore, balance);
        }

        return clients;
    }
}