
import java.util.ArrayList;


/** Candidate.java
 *
 * This class is used to store informations like candidate name, parties, total vote and the ballot list of
 * the election
 * Created by Kobin Khadka
 */

public class Candidate {

    private String name;
    private char party;
    private int totalVotes;
    private ArrayList<IRBallot> IRBallotList;

    /**
     * This is a constructor that will be  used to create a new candidates by initializing
     * the name of the candidate and their party.
     *  @param name is the name of the candidates who participate in the election.
     *              name must bepassed as string data type.
     *              For example, Foster, Jones, Smith, Mike.
     *  @param party is the first Letter of the party that candidates belongs to.
     *                party must be one letter and it's a char data type.
     *               For example, R - for republican, D - for democrats.
     */
    public Candidate(String name, char party){
        this.name = name;
        this.party = party;
        this.IRBallotList = new ArrayList<IRBallot>();
        this.totalVotes = 0;
    }
    /** Gets candidate name
     *
     * @return this method return the name of the selected candidate as a String data
     */
    public String getName(){

        return name;

    }
    /**
     * this method gets the name of the party
     * @return this method returns the party name as a char data.
     */
    public char getParty(){

        return party;

    }
    /**
     * this method adds new votes received to the total vote count.
     */
    public void addVote(){

        totalVotes++;

    }
   /** This method gets the vote count
     * @return this method returns the total vote count as a integer.
     */
    public int getVoteCount(){

        return totalVotes;
    }
    /**
     * this method adds new irballot to the IRBallotlist for IR type election.
     * @param irBallot is ballot recevied in the Instant Runoff election that
     *                 is going to be added to the IR ballot list.
     */
    public void addBallotToList(IRBallot irBallot){
        IRBallotList.add(irBallot);

    }
    /**
     * This methods gets ballot list of instant runoff election.
     *
     * @return is a ballot list that is received instant runoff election and it's an Arraylist data type.
     */
    public ArrayList<IRBallot> getIRBallotList(){

        return IRBallotList;
    }



}
