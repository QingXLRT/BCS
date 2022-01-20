 /** IRBallot.java
  * A class to store data for ballots for IR elections. System will use this file  distributes votes to
  * candidates, rank candidates and keep record of their parties.
  * Created by Brandon Schenck 
  */


import java.util.Arrays;
import java.util.ArrayList;

/**
 * Creates an IRBallot object. This represents a ballot used in the IR algorithm.
 */
public class IRBallot {
    // class instance variables
    private String[] rankedCandidateNames;
    private char[] rankedCandidateParties;
    private int[] rankedCandidateIndices;
    private int topPickIndex;
    private int id;

    /**
     * This is the IR ballot Constructor. This method constructs the IR ballot object.
     *
     * @param ballotString       A string containing the ranking of each candidate on the ballot. All candidates do not have to be ranked.
     * @param candidateParties   a array of characters that contains each candidate's respective party.
     * @param candidateNamesOnly an array of strings that contains the names of the candidates.
     * @param id                 an integer id that uniquely identifies each ballot.
     */
    public IRBallot(String ballotString, char[] candidateParties, String[] candidateNamesOnly, int id) {
        //Grabs ranking of candidates split by the comma
        String[] temp = ballotString.split(",");
        int count = temp.length;

        // Checks for blanks in the ballots to size how large the ballot should be
        for(int i = 0; i < temp.length; i++){
            if(temp[i].equals("")) {
                count--;
            }
        }

        rankedCandidateNames = new String[count];
        rankedCandidateParties = new char[count];
        rankedCandidateIndices = new int[count];
        for(int i = 0; i < temp.length; i++) {
            // parses each string in the temporary list as an integer and puts it into rankedCandidateIndices
            //if a person did not rank someone, then they get replaced with a -1 indicating that if the redistribution
            // gets that far, the ballot will be thrown out
            if(!(temp[i].equals(""))) {
                rankedCandidateNames[Integer.parseInt(temp[i]) - 1] = candidateNamesOnly[i];
                rankedCandidateParties[Integer.parseInt(temp[i])-1] = candidateParties[i];
                rankedCandidateIndices[Integer.parseInt(temp[i]) - 1] = i;
            }

        }
        this.topPickIndex = 0;
        this.id = id;
    }

    /**
     * Returns the name of the current top pick candidate of the ballot.
     *
     * @return the name of the top pick candidate of the ballot.
     */
    public String getTopPick() {

        return rankedCandidateNames[topPickIndex];
    }

    /**
     * returns true and sets a new top pick candidate if there is one available.
     * Returns false if there is not another candidate available to be selected from the ballot.
     *
     * @param eliminatedCandidates is an ArrayList of eliminated candidates.
     * @return returns true if a new top pick candidate has been found for the ballot or if a ballot does not need to be thrown out and returns false otherwise.
     */
    public Boolean setNewTopPick(ArrayList<Candidate> eliminatedCandidates) {
        for(int i = 0; i < eliminatedCandidates.size(); i++){

            if(topPickIndex >= rankedCandidateNames.length) {
                return false;
            }
            else if(rankedCandidateNames[topPickIndex].equals(eliminatedCandidates.get(i).getName())) {
                topPickIndex++;
                i = -1;
            }
        }
        return true;
    }

    /**
     * Returns the party of the top pick.
     *
     *
     * @return the top pick's party.
     */
    public char getTopPickParty() {

        return rankedCandidateParties[topPickIndex];
    }

    /**
     * Gets the id of the ballot.
     *
     * @return the id of the ballot.
     */
    public int getID() {
        return id;
    }
    
    /**
     * Gets the number of candidates ranked.
     *
     * @return the number of candidates ranked.
     */
    public int getBallotLength() {
    	return rankedCandidateNames.length;
    }

}
