/**
 * OPLBallot.java
 * A class to store data for ballots for OPL elections. These are distributed to Candidates and thier parties
 * based on the information in this class.
 * Created by Jason Phadnis
 */
public class OPLBallot {
  private String selectedCandidate;
  private char selectedParty;
  private int id;

  /**
   * Instantiates a new Opl ballot.
   *
   * @param ballotString  the ballot string from the input file in the form ,,1,,,
   *                      where the 1 represents the candidate that this ballot is voting for.
   *                      There should be no characters other than "," and "1" and there should
   *                      be only one "1" in this string.
   * @param candidateList the candidate list in the form of [name,party] e.g. Pike,D.
   *                      There should be only letter characters for the party and no elements
   *                      should be missing.
   * @param newID         the id number for this new OPLBallot
   */
  public OPLBallot(String ballotString, String[] candidateList, int newID) {
    int candidateIndex = ballotString.indexOf('1');
    String[] candidateAndParty = candidateList[candidateIndex].split(",");
    selectedCandidate = candidateAndParty[0];
    selectedParty = candidateAndParty[1].charAt(0);
    id = newID;
  }

  /**
   * Gets candidate name.
   *
   * @return the selected candidate's name as a String
   */
  public String getCandidate() {
    return selectedCandidate;
  }

  /**
   * Gets party name.
   *
   * @return the character that represents the party's name that this ballot goes towards
   */
  public char getParty() {
    return selectedParty;
  }

  /**
   * Gets id.
   *
   * @return the id number of this ballot
   */
  public int getID() {
    return id;
  }
}
