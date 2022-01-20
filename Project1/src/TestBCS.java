import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * TestBCS.java
 * The class with unit tests for the main program BCS.java. Note that some tests needed private
 * variables from BCS and are thus in that class instead of here.
 * Created by Jason Phadnis
 */
public class TestBCS {
    /**
     * Test the party creation helper function used for OPL.
     */
    @Test
    public void testPartyCreation() {
        System.out.println("Test results for testPartyCreation():");
        System.out.println("With no parties:");
        ArrayList<Party> parties = new ArrayList<Party>();
        String[] emptyCandidateInfo = {};
        BCS.createParties(emptyCandidateInfo, parties);

        assertEquals(0, parties.size());
        printPartiesCreated(parties);

        System.out.println("Just one party:");
        String[] onePartyCandidateInfo = {"Smith,I", "Jackson,I", "Dickson,I", "Sarah,I"};
        parties = new ArrayList<Party>();
        BCS.createParties(onePartyCandidateInfo, parties);

        assertEquals(1, parties.size());
        printPartiesCreated(parties);


        System.out.println("\nMultiple candidates and parties:");
        String[] candidateInfo = {"Smith,I", "Jones,R", "Dickson,I", "Rosen,D", "Phil,D"};
        parties = new ArrayList<Party>();
        BCS.createParties(candidateInfo, parties);

        assertEquals(3, parties.size());
        printPartiesCreated(parties);

        System.out.println("---------------------------------------------------\n");
    }

    /**
     * Print the parties created to aid in verifying the results of the test function
     * testPartyCreation().
     *
     * @param parties the parties ArrayList to print information about.
     */
    public void printPartiesCreated(ArrayList<Party> parties) {
        for (int i = 0; i < parties.size(); i++) {
            System.out.println("The candidates in party " + parties.get(i).getPartyName() + " are: ");
            ArrayList<Candidate> partyCandidates = parties.get(i).getCandidateList();
            for (int j = 0; j < partyCandidates.size(); j++) {
                System.out.print(partyCandidates.get(j).getName() + ", ");
            }
            System.out.println("");
        }
    }

    /**
     * Test the date and time generating function.
     */
    @Test
    public void testDateTime() {
        System.out.println("Test results for testDateTime():");
        String[] dateTime = BCS.getDateTime().split("_");
        System.out.println("The date is: " + dateTime[0]);
        System.out.println("The time is: " + dateTime[1]);
        System.out.println("---------------------------------------------------\n");
    }

    /**
     * Test the coin flip function. The results should show that there is
     * about a 50% chance of a one and a 50% chance for a zero. Furthermore,
     * it should demonstrate that the results are different between runs.
     */
    @Test
    public void testCoinFlip() {
        System.out.println("Test results for testCoinFlip():");
        int zeroCount = 0;
        int oneCount = 0;
        for (int i = 0; i < 100; i++) {
            int result = BCS.coinFlip();

            if (i < 5) {
                System.out.println("Coin toss " + i + " is: " + result);
            }

            if (result == 0) {
                zeroCount++;
            }
            else if (result == 1) {
                oneCount++;
            }
            else {
                System.out.println("We should never get a result other than 1 or 0 from coinFlip()");
            }
        }
        assertEquals(100, zeroCount+oneCount);
        System.out.println("Number of zeros/heads: " + zeroCount);
        System.out.println("Number of ones/tails: " + oneCount);
        System.out.println("---------------------------------------------------\n");
    }
}
