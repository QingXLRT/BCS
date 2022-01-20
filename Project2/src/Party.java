import java.util.ArrayList;

/**
 * Party.java
 * This class represents a party for the use of OPL elections.
 * It stores the number of votes for the party and a lsit of its candidates
 * Created by Gavin Huang.
 */
public class Party {
  //Class instance variables
  private ArrayList<Candidate> candidates;
  private ArrayList<Candidate> candidatesGivenSeats;
  private char partyName;
  private int partyVoteCount;
  private int numSeatsToDistribute;

  /**
  * This is the Party Constructor
  *
  * @param party      A character that contains a letter of a party
  */
  public Party(char party) {
    partyName = party;
    candidates = new ArrayList<Candidate>();
    candidatesGivenSeats = new ArrayList<Candidate>();
    partyVoteCount = 0;
    numSeatsToDistribute = 0;
  }

  /**
  * Adds a candidate to a arraylist
  *
  * @param newCandidate  newCandidate is a candidate object
  */
  public void addCandidate(Candidate newCandidate) {
    candidates.add(newCandidate);
  }

  /**
  * Gets the candidates from a arraylist
  *
  * @return the candidates that were stored in the candidate arraylist
  */
  public ArrayList<Candidate> getCandidateList() {
    return candidates;
  }

  /**
  * Gets the candidates from a arraylist
  *
  * @param votes    Votes is an integer that represents the amount of votes that is being added to the party vote count
  *
  */
  public void setPartyVoteCount(int votes) {
    partyVoteCount = votes;
  }

  /**
  * Sets and increases the party vote count by one
  *
  *
  */
  public void incrementPartyVoteCount() {
    partyVoteCount++;
  }

  /**
  * Gets the party vote count of a party
  *
  * @return the party vote count of a party
  */
  public int getPartyVoteCount() {
    return partyVoteCount;
  }

  /**
  * Gets the party name of a party
  *
  * @return the party name of a party
  */
  public char getPartyName() {
    return partyName;
  }

  /**
  * Sets
  *
  * @param numSeats   numSeats is an integer
  */
  public void setNumSeatsToDistribute(int numSeats) {
    numSeatsToDistribute = numSeats;
  }

  /**
  * Gets the party name of a party
  *
  * @return the the number of seats that are going to be distributed amongst the candidates
  */
  public int getNumSeatsToDistribute() {
    return numSeatsToDistribute;
  }

  /**
  * It will check the number of seats that are about to distribute. If there are any seats left, it will give a
  * seat to a candidate who the most votes
  * If there is a tie between the candidate, there would be a coin toss
  *
  * @return the candidate(s) who won a seat
  */
  public String distributeSeatsToCandidates() {
    String seatWinnerAudit = "";
    while(numSeatsToDistribute > 0 && candidates.size() > 0) {
      int maxVotes = 0;
      int winnerIndex = 0;
      for (int i = 0; i < candidates.size(); i++) {
        seatWinnerAudit += "Giving out seat number " + (i+1) + "\n";

        if(candidates.get(i).getVoteCount() > maxVotes) {
          maxVotes = candidates.get(i).getVoteCount();
          winnerIndex = i;
        }
        else if (candidates.get(i).getVoteCount() == maxVotes) {
          // Coin flip gives a 0 for the current max vote candidate and a 1 for
          // the new max vote contender
          seatWinnerAudit += "A coin toss determines priority between " + candidates.get(i).getName() +
                  " and " + candidates.get(winnerIndex).getName() + "\n";

          if(BCS.coinFlip() == 1) {
            winnerIndex = i;
          }

          seatWinnerAudit += "The coin toss prioritizes " + candidates.get(winnerIndex).getName() + "\n";
        }
      }

      candidatesGivenSeats.add(candidates.get(winnerIndex));
      seatWinnerAudit += candidates.get(winnerIndex).getName() + " won a seat with their " + candidates.get(winnerIndex).getVoteCount() + " votes\n";
      candidates.remove(winnerIndex);
      numSeatsToDistribute--;
    }
    return seatWinnerAudit;
  }

  /**
  * Gets the candidates who have won seats
  *
  * @return the arraylist of candidates who have won seats
  */
  public ArrayList<Candidate> getCandidatesGivenSeats() {
    return candidatesGivenSeats;
  }

}
