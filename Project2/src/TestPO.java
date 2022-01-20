import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Kobin Khadka
 * 
 * Class to test the POBallot class with Junit
 *
 */
public class TestPO {

    /**
     * This test function tests  the PO Ballot
     */
    @Test
    public void testPOBallot(){
        String[] candidateList = {"Pike,D", "Foster,D", "Deutsch,R", "Borg,R", "Jones,R", "Smith,I"};

        String ballotInput1 = "1,,,,,";
        String ballotInput2 = ",,,1,,";
        String ballotInput3 = ",,,,,1";

        int id = 1;

        POBallot ballot1 = new POBallot(ballotInput1, candidateList, id);
        id++;
        OPLBallot ballot2 = new POBallot(ballotInput2, candidateList, id);
        id++;
        POBallot ballot3 = new POBallot(ballotInput3, candidateList, id);
        id++;


        assertEquals("Pike", ballot1.getCandidate());
        assertEquals('D', ballot1.getParty());
        assertEquals(1, ballot1.getID());

        assertEquals("Borg", ballot2.getCandidate());
        assertEquals('R', ballot2.getParty());
        assertEquals(2, ballot2.getID());

        assertEquals("Smith", ballot3.getCandidate());
        assertEquals('I', ballot3.getParty());
        assertEquals(3, ballot3.getID());



    }




}
