import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

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

    /**
     * Tests getting the party of each candidate and putting it into a list.
     */
    @Test
    public void testGetIRCandidateParties() {
      String[] candidatesWithParty = {"Rosen (D)", "Kleinberg (R)", "Chou (I)", "Royce(L)"};
      char[] resultedParties = BCS.getIRCandidateParties(candidatesWithParty);
      char[] expectedParties = {'D','R','I','L'};
      assertArrayEquals(expectedParties,resultedParties);
    }


    /**
     * Test generating the press release file and adding information to it.
     */
    @Test
    public void testGenerateIRReport() {
      int numCandidates = 2;
      int numBallots = 10;
      Candidate rosen = new Candidate("Rosen",'D');
      Candidate chou = new Candidate("Chou",'R');
      ArrayList<Candidate> availableCandidates = new ArrayList<>();
      availableCandidates.add(rosen);
      ArrayList<Candidate> eliminatedCandidates = new ArrayList<>();
      eliminatedCandidates.add(chou);

      for(int i = 0; i < 7; i++){
        rosen.addVote();
      }
      for(int i = 0; i < 3; i++){
        chou.addVote();
      }
      BCS.generateIRReport(numCandidates,numBallots,rosen,availableCandidates,eliminatedCandidates);
    }

    /**
     * Tests writing the results to the audit file.
     */
    @Test
    public void testPrintResults() {
      int numCandidates = 2;
      int numBallots = 10;

      BCS.setElectionTypeForTest(1);

      BCS.createAuditFile();
      Candidate rosen = new Candidate("Rosen",'D');
      Candidate chou = new Candidate("Chou",'R');
      ArrayList<Candidate> availableCandidates = new ArrayList<>();
      availableCandidates.add(rosen);
      ArrayList<Candidate> eliminatedCandidates = new ArrayList<>();
      eliminatedCandidates.add(chou);

      try {
        BCS.printResults(availableCandidates,eliminatedCandidates,rosen);
      }
      catch (IOException e) {
        System.out.println("There was an error generating the audit file");
      }
    }

    /**
     * Test distributing ir votes to candidates.
     */
    @Test
    public void testDistributeIRVotes() {
      String[] candidateNames = {"Rosen", "Kleinberg", "Chou", "Royce"};
      char[] candidateParties = {'D','R','I','L'};

      String irBallotString1 = "1,2,3,4";
      String irBallotString2 = "2,3,4,1";
      String irBallotString3 = "3,,2,1";
      String irBallotString4 = ",1,,";
      int id = 1;
      ArrayList<IRBallot> irBallotList = new ArrayList<>();
      IRBallot irBallot1 = new IRBallot(irBallotString1,candidateParties,candidateNames,id);
      irBallotList.add(irBallot1);
      id++;
      IRBallot irBallot2 = new IRBallot(irBallotString2,candidateParties,candidateNames,id);
      irBallotList.add(irBallot2);
      id++;
      IRBallot irBallot3 = new IRBallot(irBallotString3,candidateParties,candidateNames,id);
      irBallotList.add(irBallot3);
      id++;
      IRBallot irBallot4 = new IRBallot(irBallotString4,candidateParties,candidateNames,id);
      irBallotList.add(irBallot4);
      id++;

      ArrayList<Candidate> availableCandidates = new ArrayList<>();
      Candidate rosen = new Candidate("Rosen",'D');
      availableCandidates.add(rosen);
      Candidate kleinberg = new Candidate("Kleinberg",'R');
      availableCandidates.add(kleinberg);
      Candidate chou = new Candidate("Chou",'I');
      availableCandidates.add(chou);
      Candidate royce = new Candidate("Royce",'L');
      availableCandidates.add(royce);

      BCS.setElectionTypeForTest(1);
      BCS.createAuditFile();
      BCS.distributeIRVotes(irBallotList,availableCandidates);

    }

    /**
     * Test create audit file for each election type.
     */
    @Test
    public void testCreateAuditFile() {
      System.out.println("Test results for createAuditFile():");
      // Test IR audit file creation
      BCS.setElectionTypeForTest(1);
      BCS.createAuditFile();

      // Test OPL audit file creation
      BCS.setElectionTypeForTest(2);
      BCS.createAuditFile();

      // Test incorrect election type audit file creation
      // This should never happen, and print out an error
      BCS.setElectionTypeForTest(0);
      BCS.createAuditFile();

      BCS.closeFileWriterForTest("An error closing the audit file in testOPLBallotDistribution() occurred.");

      System.out.println("---------------------------------------------------\n");
    }

    /**
     * Test opl ballot distribution. Uses the OPLBallotDistribTest.txt file for the test
     * ballot data. This file is expected to be in the same directory as this program.
     */
    @Test
    public void testOPLBallotDistribution() {
      System.out.println("Test results for createAndDistributeOPLBallots():");

      BCS.openReaderForTest();

      BCS.setElectionTypeForTest(2);
      BCS.createAuditFile();

      ArrayList<OPLBallot> ballots = new ArrayList<OPLBallot>();  // Test file for this has 18 ballots
      String[] candidateInfo = {"Volze,D", "Moll,I", "Wartenberg,G", "Foster,D", "Berg,R", "McClerg,R", "Morey,G", "Pike,D", "Grolnie,R"};

      ArrayList<Party> parties = createTestParties();

      BCS.createAndDistributeOPLBallots(0, ballots, candidateInfo, parties);
      // D (6): Volze = 3, Foster = 1, Pike = 2
      // R (6): Berg = 3, McClerg = 2, Grolnie = 1
      // G (4): Wartenberg = 3, Morey = 1
      // I (2): Moll = 2

      // Check that all the parties have the correct number of total votes
      assertEquals(6, parties.get(0).getPartyVoteCount());  // D gets 6 votes total
      assertEquals(6, parties.get(1).getPartyVoteCount());  // R gets 6 votes total
      assertEquals(4, parties.get(2).getPartyVoteCount());  // G gets 4 votes total
      assertEquals(2, parties.get(3).getPartyVoteCount());  // I gets 2 votes total

      // Check that each individual candidate has the correct number of votes
      for (int i = 0; i < parties.size(); i++) {
        ArrayList<Candidate> candidates = parties.get(i).getCandidateList();
        for (int j = 0; i < candidates.size(); i++) {
          switch(candidates.get(j).getName()) {
            case "Volze":
              assertEquals(3,candidates.get(j).getVoteCount());
              break;
            case "Foster":
              assertEquals(1,candidates.get(j).getVoteCount());
              break;
            case "Pike":
              assertEquals(2,candidates.get(j).getVoteCount());
              break;
            case "Berg":
              assertEquals(3,candidates.get(j).getVoteCount());
              break;
            case "McClerg":
              assertEquals(2,candidates.get(j).getVoteCount());
              break;
            case "Grolnie":
              assertEquals(1,candidates.get(j).getVoteCount());
              break;
            case "Wartenberg":
              assertEquals(3,candidates.get(j).getVoteCount());
              break;
            case "Morey":
              assertEquals(1,candidates.get(j).getVoteCount());
              break;
            case "Moll":
              assertEquals(2,candidates.get(j).getVoteCount());
              break;
          }
        }
      }

        BCS.closeReaderForTest();

        BCS.closeFileWriterForTest("An error closing the audit file in testOPLBallotDistribution() occurred.");

      System.out.println("---------------------------------------------------\n");
    }


    /**
     * Test distributing seats to parties. This follows the same example data from
     * testOPLBallotDistribution().
     */
    @Test
    public void testDistributeSeatsToParties() {
      System.out.println("Test results for testDistributeSeatsToParties():");
      // Continuing example in testOPLBallotDistribution() with 18 total ballots:
      // D (6): Volze = 3, Foster = 1, Pike = 2
      // R (6): Berg = 3, McClerg = 2, Grolnie = 1
      // G (4): Wartenberg = 3, Morey = 1
      // I (2): Moll = 2

      BCS.setElectionTypeForTest(2);
      BCS.createAuditFile();

      // Try with 3 seats to distribute
      ArrayList<Party> parties = createTestParties();
      giveVotesToTestParties(parties);

      BCS.distributeSeatsToParties(18, 3, parties);

      assertEquals(1, parties.get(0).getNumSeatsToDistribute());
      assertEquals(1, parties.get(1).getNumSeatsToDistribute());
      assertEquals(1, parties.get(2).getNumSeatsToDistribute());
      assertEquals(0, parties.get(3).getNumSeatsToDistribute());

      // Try with 4 seats to distribute
      parties = createTestParties();
      giveVotesToTestParties(parties);

      BCS.distributeSeatsToParties(18, 4, parties);

      // The G party should always get 1 seat. The other parties' vote counts vary but total to 3.
      assertEquals(1, parties.get(2).getNumSeatsToDistribute());

      System.out.println("The 4 seats case: ");
      System.out.println("The D party got " + parties.get(0).getNumSeatsToDistribute() + " seats");
      System.out.println("The R party got " + parties.get(1).getNumSeatsToDistribute() + " seats");
      System.out.println("The I party got " + parties.get(3).getNumSeatsToDistribute() + " seats\n");


      // Try with 6 seats to distribute, should get an error print statement
      parties = createTestParties();
      giveVotesToTestParties(parties);

      BCS.distributeSeatsToParties(18, 6, parties);
      assertEquals(2, parties.get(0).getNumSeatsToDistribute());
      assertEquals(2, parties.get(1).getNumSeatsToDistribute());
      assertEquals(1, parties.get(2).getNumSeatsToDistribute());
      assertEquals(1, parties.get(3).getNumSeatsToDistribute());

      // Try with 9 seats to distribute, should get an error print statement
      parties = createTestParties();
      giveVotesToTestParties(parties);

      BCS.distributeSeatsToParties(18, 9, parties);
      assertEquals(3, parties.get(0).getNumSeatsToDistribute());
      assertEquals(3, parties.get(1).getNumSeatsToDistribute());
      assertEquals(2, parties.get(2).getNumSeatsToDistribute());
      assertEquals(1, parties.get(3).getNumSeatsToDistribute());

      // Try with 0 seats to distribute, should get an error print statement
      parties = createTestParties();
      giveVotesToTestParties(parties);

      System.out.println("zero seat case which should be an error");
      BCS.distributeSeatsToParties(18, 0, parties);

      BCS.closeFileWriterForTest("An error closing the audit file in testDistributeSeatsToParties() occurred.");

      System.out.println("---------------------------------------------------\n");
    }


    /**
     * Create test parties array list to aid in the test functions: testOPLBallotDistribution()
     * and testDistributeSeatsToParties().
     *
     * @return the array list of test parties with candidates set.
     */
    public ArrayList<Party> createTestParties() {
      ArrayList<Party> testParties = new  ArrayList<Party>();
      testParties.add(new Party('D'));
      testParties.add(new Party('R'));
      testParties.add(new Party('G'));
      testParties.add(new Party('I'));

      testParties.get(0).addCandidate(new Candidate("Volze", 'D'));
      testParties.get(0).addCandidate(new Candidate("Foster", 'D'));
      testParties.get(0).addCandidate(new Candidate("Pike", 'D'));
      testParties.get(1).addCandidate(new Candidate("Berg", 'R'));
      testParties.get(1).addCandidate(new Candidate("McClerg", 'R'));
      testParties.get(1).addCandidate(new Candidate("Grolnie", 'R'));
      testParties.get(2).addCandidate(new Candidate("Wartenberg", 'G'));
      testParties.get(2).addCandidate(new Candidate("Morey", 'G'));
      testParties.get(3).addCandidate(new Candidate("Moll", 'I'));

      return testParties;
    }


    /**
     * Give votes to test parties. Aids in testDistributeSeatsToParties()
     * test function.
     *
     * @param testParties the test parties with party vote counts set
     */
    public void giveVotesToTestParties(ArrayList<Party> testParties) {
      // D (6): Volze = 3, Foster = 1, Pike = 2
      // R (6): Berg = 3, McClerg = 2, Grolnie = 1
      // G (4): Wartenberg = 3, Morey = 1
      // I (2): Moll = 2
      // Set vote counts for the parties
      testParties.get(0).setPartyVoteCount(6);
      testParties.get(1).setPartyVoteCount(6);
      testParties.get(2).setPartyVoteCount(4);
      testParties.get(3).setPartyVoteCount(2);
    }

    /**
     * This function test if the IR ballot function successfully add valid ballot and ignore the invalid ones.
     */

    @Test
    public void testParseBallotForIR() {

        System.out.println("Test results for testParseBallotsForIR()");
        String[] candidateNames1 = {"Rosen", "Kleinberg", "Chou", "Royce"};
        char[] candidateParties1 = {'D', 'R', 'I', 'L'};


        int id = 1;

        BCS.setFilesForParsingTest("IRBallot.txt");


        ArrayList<IRBallot> irBallotList= new ArrayList<IRBallot>();

        try {
            BCS.parseBallotsForIR(irBallotList, candidateParties1, candidateNames1, id);
        } catch (IOException e) {
            e.printStackTrace();
        }


        assertEquals(3, irBallotList.size());
        assertEquals(1, irBallotList.get(0).getID());
        assertEquals(2, irBallotList.get(1).getID());
        assertEquals(3, irBallotList.get(2).getID());



        //Create another election ballot list with only 3 candidates
        String[] candidateNames2 = {"Rosen", "Kleinberg", "Chou" };

        char[] candidateParties2 = {'D', 'R', 'I'};

        id = 1;


        BCS.setFilesForParsingTest("IRBallot2.txt");


        ArrayList<IRBallot> irBallotList2= new ArrayList<IRBallot>();

        try {
            BCS.parseBallotsForIR(irBallotList2, candidateParties2, candidateNames2, id);
        } catch (IOException e) {
            e.printStackTrace();
        }


        assertEquals(5, irBallotList2.size());
        assertEquals(5, irBallotList2.get(4).getID());
        assertEquals(4, irBallotList2.get(3).getID());
        assertEquals(3, irBallotList2.get(2).getID());
        assertEquals(2, irBallotList2.get(1).getID());
        assertEquals(1, irBallotList2.get(0).getID());


    }

  /**
  * Testing parsing for opl ballots
  * testing the function parseBallotsForOPL
  */
  @Test
  public void testparseBallotsForOPL(){
    System.out.println("Test results for testparseBallotsForOPL()");

    BCS.setFilesForParsingTest("OPLBallotDistribTest.txt");
    
    ArrayList<OPLBallot> ballots = new ArrayList<OPLBallot>(); //Test file for this has 18 ballots
    ArrayList<Party> parties = createTestParties();
    String[] candidateInfo = {"Volze,D", "Moll,I", "Wartenberg,G", "Foster,D", "Berg,R", "McClerg,R", "Morey,G", "Pike,D", "Grolnie,R"};


    BCS.parseBallotsForOPL(ballots, candidateInfo, parties);
    // D (6): Volze = 3, Foster = 1, Pike = 2
    // R (6): Berg = 3, McClerg = 2, Grolnie = 1
    // G (4): Wartenberg = 3, Morey = 1
    // I (2): Moll = 2
    //It should count 18 ballots
    assertEquals(18, ballots.size());

    // It uses createAndDistributeOPLBallots(); function
    // Check that all the parties have the correct number of total votes
    assertEquals(6, parties.get(0).getPartyVoteCount());  // D gets 6 votes total
    assertEquals(6, parties.get(1).getPartyVoteCount());  // R gets 6 votes total
    assertEquals(4, parties.get(2).getPartyVoteCount());  // G gets 4 votes total
    assertEquals(2, parties.get(3).getPartyVoteCount());  // I gets 2 votes total

    BCS.closeReaderForTest();

  }


}
