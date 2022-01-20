import org.junit.Test;
import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotSame;


import java.util.ArrayList;

/**
 * TestParty.java
 * This class provides unit tests for the Party class.
 * Created by Gavin Huang
 */
public class TestParty {

  /**
   * Test creating a party, making sure the variables are set and obtainable, and make sure the seats
   * are distributed to candidates..
   */
  @Test
  public void testPCreation() {

    ArrayList<Party> parties = new ArrayList<Party>();

    parties.add(new Party('D'));
    parties.add(new Party('R'));
    parties.add(new Party('C'));
    parties.add(new Party('F'));

    //Checks to see if addCandidate adds the candidate
    parties.get(0).addCandidate(new Candidate("Caillou", 'D'));
    parties.get(0).addCandidate(new Candidate("Rosie", 'D'));
    parties.get(1).addCandidate(new Candidate("Elmo", 'R'));
    parties.get(1).addCandidate(new Candidate("CookieMonster", 'R'));
    parties.get(2).addCandidate(new Candidate("Arthur", 'C'));
    parties.get(2).addCandidate(new Candidate("DW", 'C'));
    parties.get(3).addCandidate(new Candidate("George", 'F'));
    parties.get(3).addCandidate(new Candidate("ManInTheYellowHat", 'F'));


    //Checks to see if the party vote count is 0
    assertEquals(0,parties.get(0).getPartyVoteCount());
    assertEquals(0,parties.get(1).getPartyVoteCount());
    assertEquals(0,parties.get(2).getPartyVoteCount());
    assertEquals(0,parties.get(3).getPartyVoteCount());

    //Checks to see if it returns the party name
    assertEquals('D',parties.get(0).getPartyName());
    assertEquals('R',parties.get(1).getPartyName());
    assertEquals('C',parties.get(2).getPartyName());
    assertEquals('F',parties.get(3).getPartyName());

    //Checks the number of candidates
    assertEquals(2,parties.get(0).getCandidateList().size());
    assertEquals(2,parties.get(1).getCandidateList().size());

    parties.get(0).setPartyVoteCount(1);
    parties.get(1).setPartyVoteCount(1);
    parties.get(2).setPartyVoteCount(1);
    parties.get(3).setPartyVoteCount(1);

    parties.get(0).incrementPartyVoteCount();
    parties.get(1).incrementPartyVoteCount();
    parties.get(2).incrementPartyVoteCount();
    parties.get(3).incrementPartyVoteCount();

    //Checks to see if the partyvote count goes up by one
    assertEquals(2,parties.get(0).getPartyVoteCount());
    assertEquals(2,parties.get(1).getPartyVoteCount());
    assertEquals(2,parties.get(2).getPartyVoteCount());
    assertEquals(2,parties.get(3).getPartyVoteCount());

    parties.get(0).setNumSeatsToDistribute(1);
    parties.get(1).setNumSeatsToDistribute(1);

    //sets the number of seats for distribution
    assertEquals(1,parties.get(0).getNumSeatsToDistribute());
    assertEquals(1,parties.get(1).getNumSeatsToDistribute());

    //Checks to see if the seats distributed to candidates is not empty and produces an output
    assertNotSame("", parties.get(0).distributeSeatsToCandidates());
    assertNotSame("", parties.get(1).distributeSeatsToCandidates());

    ArrayList<Candidate> empty = new ArrayList<Candidate>();
    //checks to see if the candidates have been given seats
    assertNotSame(empty,parties.get(0).getCandidatesGivenSeats());
    assertNotSame(empty,parties.get(1).getCandidatesGivenSeats());
  }

}
