import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Report.java
 * Creates a press release file and print the results to the terminal
 * Created by Jason Phadnis and Gavin Huang
 */

public class Report {
    //Class instance variables
    private String electionType;
    private int numCandidates;
    private int numBallots;
    private String winner;

    /**
    * This is the Report constructor. This method constructs the report object
    *
    * @param electionType   A string that contains the type of election that is being run
    * @param numCandidates  A integer that contains the number of candidates
    * @param numBallots     A integer that contains the number of ballots
    * @param winner         A string that contains the winner
    */
    public Report(String electionType, int numCandidates, int numBallots, String winner) {
        this.electionType = electionType;
        this.numCandidates = numCandidates;
        this.numBallots = numBallots;
        this.winner = winner;
    }

    /**
    * Prints our the results of the election to the terminal
    *
    *
    */
    public void printReport() {
        System.out.println("Below are the results of the " + electionType + " election");
        System.out.println("The number of candidates in this election was " + numCandidates);
        System.out.println("The number of ballots in this election was " + numBallots);

        if (electionType.equals("IR")) {
            System.out.println("And the winning candidate of the Instant Runoff election is:");
            System.out.println(winner);
        }
        else if (electionType.equals("OPL")) {
            System.out.println("And the candidates who won seats in this Open Party Listing election are:");
            System.out.println(winner);
        }
        // We should never hit this else
        else {
            System.out.println("This Report object did not get an appropriate election type string");
            // TODO: Consider throwing and error
        }
    }


    /**
    * Generates the press release file
    *
    * @param dateTime             It is a string that contains the date of when the file is being produces
    * @param candidateStandings   It is a string that contains the candidate standings 
    */
    public void generateReportFile(String dateTime, String candidateStandings) {
        try {
            // Set the filename and create the press release file
            String pressReleaseFileName =  dateTime + "PressRelease.txt";
            File pressReleaseFile = new File(pressReleaseFileName);

            if (pressReleaseFile.createNewFile()) {
                System.out.println("Press release file named: " + pressReleaseFile.getName() + " created successfully");
            }

            // Create the press release file writer
            FileWriter pressReleaseFileWriter = new FileWriter(pressReleaseFileName);
            pressReleaseFileWriter.write("Here lies the results of the " + electionType + " election\n");
            pressReleaseFileWriter.write("The number of candidates in this election was " + numCandidates + "\n");
            pressReleaseFileWriter.write("The number of ballots in this election was " + numBallots + "\n");

            if (electionType.equals("IR")) {
                pressReleaseFileWriter.write("The individual candidate vote counts are as follows:\n" + candidateStandings);
                pressReleaseFileWriter.write("And the winning candidate of the Instant Runoff election is:\n" + winner);
            }
            else if (electionType.equals("OPL")) {
                pressReleaseFileWriter.write("The final party and individual candidate vote counts are as follows:\n" + candidateStandings);
                pressReleaseFileWriter.write("\nAnd the candidates who won seats in this Open Party Listing election are:\n" + winner);
            }
            // We should never hit this else
            else {
                System.out.println("This Report object did not get an appropriate election type string");
                // TODO: Consider throwing and error
            }

            pressReleaseFileWriter.close();
        }

        catch (IOException e){
            System.out.println("An error creating the press release file occurred.");
            e.printStackTrace();
        }
    }

}
