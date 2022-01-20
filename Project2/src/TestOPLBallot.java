import org.junit.Test;
import static org.junit.Assert.*;

/**
 * TestOPLBallot.java
 * This class has the test cases for the OPLBallot class.
 * Created by Jason Phadnis
 */
public class TestOPLBallot {
  private String[] candidateList = {"Pike,D", "Foster,D", "Deutsch,R", "Borg,R", "Jones,R", "Smith,I"};

  /**
   * Test basic creation of OPLBallot objects. This includes some normal cases and tests the getters in
   * addition to the constructor.
   */
  @Test
  public void testCreation() {

    int idNum = 1;
    // Normal cases
    String ballotInput1 = "1,,,,,";
    String ballotInput2 = ",,,1,,";
    String ballotInput3 = ",,,,,1";

    OPLBallot normalBallot1 = new OPLBallot(ballotInput1, candidateList, idNum);
    idNum++;
    OPLBallot normalBallot2 = new OPLBallot(ballotInput2, candidateList, idNum);
    idNum++;
    OPLBallot normalBallot3 = new OPLBallot(ballotInput3, candidateList, idNum);
    idNum++;

    assertEquals("Pike", normalBallot1.getCandidate());
    assertEquals('D', normalBallot1.getParty());
    assertEquals(1, normalBallot1.getID());

    assertEquals("Borg", normalBallot2.getCandidate());
    assertEquals('R', normalBallot2.getParty());
    assertEquals(2, normalBallot2.getID());

    assertEquals("Smith", normalBallot3.getCandidate());
    assertEquals('I', normalBallot3.getParty());
    assertEquals(3, normalBallot3.getID());

  }

}
