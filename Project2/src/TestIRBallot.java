

import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;


/** TestIRBallot.java
 *  This file is used to test the IRBallot class to make sure it is working properly.
 *
 * Created by Brandon Schenck.
 */


public class TestIRBallot {

    private String[] candidateNames = {"Rosen", "Kleinberg", "Chou", "Royce"};
    private char[] candidateParties = {'D','R','I','L'};

    @Test
    public void testIRBallotCreation() {
        int id = 1;
        //Normal creation
        String irBallotString1 = "1,2,3,4";
        String irBallotString2 = "2,3,4,1";
        String irBallotString3 = "3,,2,1";
        String irBallotString4 = ",1,,";

        IRBallot irBallot1 = new IRBallot(irBallotString1,candidateParties,candidateNames,id);
        id++;
        IRBallot irBallot2 = new IRBallot(irBallotString2,candidateParties,candidateNames,id);
        id++;
        IRBallot irBallot3 = new IRBallot(irBallotString3,candidateParties,candidateNames,id);
        id++;
        IRBallot irBallot4 = new IRBallot(irBallotString4,candidateParties,candidateNames,id);
        id++;

        assertEquals("Rosen",irBallot1.getTopPick());
        assertEquals('D',irBallot1.getTopPickParty());
        assertEquals(1,irBallot1.getID());

        assertEquals("Royce", irBallot2.getTopPick());
        assertEquals('L',irBallot2.getTopPickParty());
        assertEquals(2,irBallot2.getID());

        assertEquals("Royce", irBallot3.getTopPick());
        assertEquals('L',irBallot3.getTopPickParty());
        assertEquals(3,irBallot3.getID());

        assertEquals("Kleinberg", irBallot4.getTopPick());
        assertEquals('R',irBallot4.getTopPickParty());
        assertEquals(4,irBallot4.getID());
    }

    @Test
    public void testSetNewTopPick() {

        ArrayList<Candidate> eliminatedCandidates = new ArrayList<>();
        Candidate rosen = new Candidate("Rosen",'D');
        Candidate kleinberg = new Candidate("Kleinberg",'R');
        Candidate chou = new Candidate("Chou",'I');
        Candidate royce = new Candidate("Royce",'L');

        eliminatedCandidates.add(kleinberg);
        eliminatedCandidates.add(chou);

        int id = 1;
        String irBallotString1 = "1,2,3,4";
        String irBallotString2 = "2,3,4,1";
        String irBallotString3 = "3,2,1,";
        String irBallotString4 = ",1,,";

        IRBallot irBallot1 = new IRBallot(irBallotString1,candidateParties,candidateNames,id);
        id++;
        IRBallot irBallot2 = new IRBallot(irBallotString2,candidateParties,candidateNames,id);
        id++;
        IRBallot irBallot3 = new IRBallot(irBallotString3,candidateParties,candidateNames,id);
        id++;
        IRBallot irBallot4 = new IRBallot(irBallotString4,candidateParties,candidateNames,id);
        id++;

        assertEquals(true,irBallot1.setNewTopPick(eliminatedCandidates));
        assertEquals(true,irBallot2.setNewTopPick(eliminatedCandidates));
        assertEquals(true, irBallot3.setNewTopPick(eliminatedCandidates));
        assertEquals(false,irBallot4.setNewTopPick(eliminatedCandidates));

    }

}
