/** TestCandidate.java
 * This file will test if the candidate class is working prorperly. 
 * Created by Kobin Khadka
 */

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestCandidate {

    /**
     * This method tests  the getName method that returns name of the candidate
     * and getParty method which returns party of the candidate.
     */

    @Test
    public void testCandidateCreation() {
        Candidate candidate = new Candidate("Pike", 'D');
        assertEquals("Pike", candidate.getName());
        assertEquals('D', candidate.getParty());

    }

    /**
     * This method tests addVote method which add new votes to total vote count and
     * getVoteCount method that return totalVotes.
     */
    @Test
    public void testVoteCount() {
        Candidate candidate = new Candidate("Pike", 'R');

        candidate.addVote();
        candidate.addVote();
        assertEquals(2, candidate.getVoteCount());

        int totalVotes = candidate.getVoteCount();
        assertEquals(2, totalVotes);

    }

    /** This method test  add ballotList method that add ballots and getBallotList that
     * returns ballotList
     *
     */
    @Test
    public void ballotList() {
        Candidate candidate = new Candidate("Pike", 'R');

        char[] parties = {'D', 'R', 'I', 'L'};
        String[] candidateNames = {"Rosen", "Kleinberg", "Chou", "Royce"};

        String irBallotString1 = "1,2,3,4";
        String irBallotString2 = "2,3,4,1";
        String irBallotString3 = "3,,2,1";
        String irBallotString4 = ",1,,";


        IRBallot ir = new IRBallot(irBallotString1, parties, candidateNames, 100);
        IRBallot ir2 = new IRBallot(irBallotString2, parties, candidateNames, 101);
        IRBallot ir3 = new IRBallot(irBallotString3, parties, candidateNames, 102);
        IRBallot ir4 = new IRBallot(irBallotString4, parties, candidateNames, 103);

        candidate.addBallotToList(ir);
        candidate.addBallotToList(ir2);

        assertEquals(100, candidate.getIRBallotList().get(0).getID());


        assertEquals(101, candidate.getIRBallotList().get(1).getID());
        assertEquals(2, candidate.getIRBallotList().size());


    }
}
