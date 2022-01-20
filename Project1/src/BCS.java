import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.io.IOException;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Collections;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * BCS.java
 * The main program of our Ballot Counting System (BCS).
 * Created by Jason Phadnis, Kobin Khadka, Brandon Schenck, and Gavin Huang
 */
public class BCS {
  private static int electionType;   // 1 = IR, 2 = OPL
  private static File ballotFile;
  private static Scanner ballotReader;
  private static File auditFile;
  private static FileWriter auditFileWriter;
  private static String auditFileName;

  /**
   * The entry point of our application. It will ask the user to input the name of a file within the same directory
   * as this program. It will indicate and error and ask the user for the filename again if an invalid name
   * is given. This input file is to have information about the election to be run, including the type, the candidates,
   * and the ballots themselves. The main also sets up the input file reader and the audit file writer with the
   * creatAuditFile() function. It then chooses whether to run the IR or the OPL algorithm based on the input file.
   *
   * @param args the input arguments
   * @throws IOException the io exception
   */
  public static void main(String[] args) throws IOException {
    Scanner userInput = new Scanner(System.in);
    System.out.println("Welcome to the Ballot Counting System!\n");
    System.out.println("Enter a filename (don't forget the file extension)");

    boolean fileFound = false;
    while (!fileFound) {
      String filename = userInput.nextLine();

      try {
        ballotFile = new File(filename);
        ballotReader = new Scanner(ballotFile);
        if (ballotReader.hasNextLine()) {
          String data = ballotReader.nextLine();
          if (data.compareTo("IR") == 0) {
            electionType = 1;
            System.out.println("IR election set");
          }
          else if (data.compareTo("OPL") == 0) {
            electionType = 2;
            System.out.println("OPL election set");
          }
          else {
            System.out.println("Error: the first line of the ballot info file was: " + data);
          }
          fileFound = true;
        }
      }
      catch (FileNotFoundException e) {
        System.out.println("The filename you entered was not found in the directory of this program. Please enter a valid filename (don't forget the file extension).");
      }
    }

    // Create the audit file for writing to during the election algorithms
    createAuditFile();

    if(electionType == 1) {
      ir();
    }
    else if (electionType == 2) {
      opl();
    }

    ballotReader.close();
    try {
      auditFileWriter.close();
    }
    catch (IOException e) {
      System.out.println("An error closing the audit file occurred.");
      e.printStackTrace();
    }

  }


  /**
   * Runs the IR voting algorithm. This makes use of a few helper functions found below.
   *
   * @throws IOException the io exception. This function throws this exception when there is an error with generating the audit file.
   */
  public static void ir() throws IOException {
    int id = 1;
    int numCandidates;
    int numBallots;
    String candidateInfo;
    ArrayList<Candidate> availableCandidates = new ArrayList<Candidate>();
    ArrayList<Candidate> eliminatedCandidates = new ArrayList<Candidate>();
    ArrayList<IRBallot> irBallotList = new ArrayList<IRBallot>();

    //grabs number of candidates
    numCandidates = Integer.parseInt(ballotReader.nextLine());
    //grabs candidate name and party
    candidateInfo = ballotReader.nextLine();

    String[] candidateWithParty = candidateInfo.split(", ");
    //Goes through each candidate name and grabs the party out of the string
    char[] candidateParties = getIRCandidateParties(candidateWithParty);

    // Creating an array of just candidate names. Using .clone() so candidateNameOnly does not refer to the same
    // location as candidateWithParty
    String[] candidateNamesOnly = candidateWithParty.clone();
    // remove all parties from each string
    for(int i = 0; i < candidateNamesOnly.length; i++) {
      candidateNamesOnly[i] = candidateNamesOnly[i].replaceAll("\\(.*\\)","");
    }
    //grabs number of ballots
    numBallots = Integer.parseInt(ballotReader.nextLine());

    //turns ballots into IRBallot objects and adds them to irBallotList
    int ballot = 0;
    while(ballotReader.hasNextLine()){
      String irBallotString = ballotReader.nextLine();
      IRBallot irBallot = new IRBallot(irBallotString, candidateParties, candidateNamesOnly,id);
      irBallotList.add(irBallot);
      auditFileWriter.write("ID: "+ irBallotList.get(ballot).getID() + ", Ballot: "+ irBallotString + "\n");
      ballot++;
      id++;
    }

    // Step 1: Create Candidate Object for each candidate
    for(int i = 0; i < candidateNamesOnly.length;i++){
      Candidate candidate = new Candidate(candidateNamesOnly[i],candidateParties[i]);
      availableCandidates.add(candidate);
    }
    // Step 2: Distribute votes to each candidate based on top available pick of ballots write distribution to audit file

    distributeIRVotes(irBallotList,availableCandidates);

    // Step 3: If a candidate has over 50% of the votes,
    // Declare a winner and write winner to audit file and display results to the screen and END
    // Else, Everyone has <=50% of the total votes and move on to Step 4
    ArrayList<Double> votePercentage = new ArrayList<Double>();
    for(int i = 0; i < availableCandidates.size(); i++) {
      votePercentage.add((double)(availableCandidates.get(i).getVoteCount()) / (double)(numBallots));
      if (votePercentage.get(i) > 0.5) {
        Candidate winner = availableCandidates.get(i);
        Report irReport = new Report("IR",numCandidates,numBallots,winner.getName());
        irReport.printReport();
        String candidateStandings = "";
        for(int j = 0; j < availableCandidates.size(); j++) {
          candidateStandings += availableCandidates.get(j).getName() +
                  " (" + availableCandidates.get(j).getParty() + ") total votes: " + availableCandidates.get(j).getVoteCount() + "\n";
        }
        irReport.generateReportFile(getDateTime(),candidateStandings);
        printResults(availableCandidates, eliminatedCandidates, winner);
        return;
      }
    }

    // Step 4: If there is a tie for the lowest vote count, determine a candidate to remove with a coin toss and then eliminate the candidate who loses
    // the coin toss, redistribute their votes, and repeat step 2 through 4, else, there is no tie, eliminate the candidate with the least amount of votes,
    // redistribute their votes, and repeat steps 2 through 4.

    while(availableCandidates.size() > 1) {
      //find smallest total votes
      int minVotes = availableCandidates.get(0).getVoteCount();
      for(int i = 1; i < availableCandidates.size(); i++) {
        minVotes = Math.min( minVotes,availableCandidates.get(i).getVoteCount());
      }
      //check to see if there are multiple of same smallest vote count
      ArrayList<Candidate> lowestVoteCountCandidates = new ArrayList<Candidate>();
      for(int i = 0; i < availableCandidates.size(); i++) {
        if(minVotes == availableCandidates.get(i).getVoteCount()){
          lowestVoteCountCandidates.add(availableCandidates.get(i));
        }
      }
      //declare winner if only two left
      if(availableCandidates.size() == 2) {
        if(availableCandidates.get(0).getVoteCount() > availableCandidates.get(1).getVoteCount()) {
          Candidate winner = availableCandidates.get(0);
          generateIRReport(numCandidates,numBallots,winner,availableCandidates,eliminatedCandidates);
          printResults(availableCandidates, eliminatedCandidates, winner);
          return;
        }
        else if(availableCandidates.get(0).getVoteCount() < availableCandidates.get(1).getVoteCount()) {
          Candidate winner = availableCandidates.get(1);
          generateIRReport(numCandidates,numBallots,winner,availableCandidates,eliminatedCandidates);
          printResults(availableCandidates, eliminatedCandidates, winner);
          return;
        }
        else {
          auditFileWriter.write("There is a tie between "+availableCandidates.get(0).getName()+" and "
                  +availableCandidates.get(1).getName()+". A coin toss will determine the winner of the election. \n");
          int winnerIndex = coinFlip();
          Candidate winner = availableCandidates.get(winnerIndex);
          generateIRReport(numCandidates,numBallots,winner,availableCandidates,eliminatedCandidates);
          printResults(availableCandidates, eliminatedCandidates, winner);
          return;
        }
      }
      //if there are multiple of same vote count run coin flip
      if(lowestVoteCountCandidates.size() > 1) {
        ArrayList<Candidate>safeCandidates = new ArrayList<>();
        while(lowestVoteCountCandidates.size() > 1) {
          int index = coinFlip();
          safeCandidates.add(lowestVoteCountCandidates.get(index));
          lowestVoteCountCandidates.remove(index);
        }
        auditFileWriter.write("There is a tie for elimination between candidates. A coin toss will determine who will be eliminated \n");
        //int eliminateIndex = new Random().nextInt(lowestVoteCountCandidates.size()-1);
        eliminatedCandidates.add(lowestVoteCountCandidates.get(0));
        availableCandidates.remove(lowestVoteCountCandidates.get(0));
        ArrayList<IRBallot> eliminatedCandidatesBallots =eliminatedCandidates.get(eliminatedCandidates.size()-1).getIRBallotList();
        // sets new top picks in the ballots or throws them out if they can't
        ArrayList<IRBallot> throwOutBallots = new ArrayList<IRBallot>();
        for(int i = 0; i < eliminatedCandidatesBallots.size(); i++) {
          if (eliminatedCandidatesBallots.get(i).setNewTopPick(eliminatedCandidates) == false){
            throwOutBallots.add(eliminatedCandidatesBallots.get(i));
          }
        }

        for(int i = 0; i < throwOutBallots.size();i++) {
          auditFileWriter.write("Ballot with ID: " + throwOutBallots.get(i).getID() +
                  " Was thrown out because they did not have any more preferred candidates available \n");
          eliminatedCandidatesBallots.remove(throwOutBallots.get(i));
        }

        throwOutBallots.clear();
        auditFileWriter.write(eliminatedCandidates.get(eliminatedCandidates.size()-1).getName() +
                " Has been eliminated. Their votes will be redistributed \n");
        lowestVoteCountCandidates.clear();
        // redistribute ballots
        distributeIRVotes(eliminatedCandidatesBallots,availableCandidates);

        //runs another check to see if someone has majority
        votePercentage.clear();
        for(int i = 0; i < availableCandidates.size(); i++) {
          votePercentage.add((double)(availableCandidates.get(i).getVoteCount()) / (double)(numBallots));
          if (votePercentage.get(i) > 0.5) {
            Candidate winner = availableCandidates.get(i);
            generateIRReport(numCandidates,numBallots,winner,availableCandidates,eliminatedCandidates);
            printResults(availableCandidates, eliminatedCandidates, winner);
            return;
          }
        }
      }
      //eliminate smallest vote count and redistribute
      else {
        availableCandidates.remove(lowestVoteCountCandidates.get(0));
        eliminatedCandidates.add(lowestVoteCountCandidates.get(0));
        auditFileWriter.write(eliminatedCandidates.get(eliminatedCandidates.size()-1).getName() +
                " Has been eliminated. Their votes will be redistributed \n");
        lowestVoteCountCandidates.clear();
        ArrayList<IRBallot> eliminatedCandidatesBallots =eliminatedCandidates.get(eliminatedCandidates.size()-1).getIRBallotList();

        //reset Top picks for these ballots or removes them if they do not have another pick
        ArrayList<IRBallot> throwOutBallots = new ArrayList<IRBallot>();
        for(int i = 0; i < eliminatedCandidatesBallots.size(); i++) {
          if (eliminatedCandidatesBallots.get(i).setNewTopPick(eliminatedCandidates) == false){
            throwOutBallots.add(eliminatedCandidatesBallots.get(i));
          }
        }
        for(int i = 0; i < throwOutBallots.size();i++) {
          auditFileWriter.write("Ballot with ID: " + throwOutBallots.get(i).getID() +
                  "Was thrown out because they did not have any more preferred candidates available \n");
          eliminatedCandidatesBallots.remove(throwOutBallots.get(i));
        }
        throwOutBallots.clear();
        //redistributes eliminated candidates ballots
        distributeIRVotes(eliminatedCandidatesBallots,availableCandidates);

        //runs another check to see if someone has majority
        votePercentage.clear();
        for(int i = 0; i < availableCandidates.size(); i++) {
          votePercentage.add((double)(availableCandidates.get(i).getVoteCount()) / (double)(numBallots));
          if (votePercentage.get(i) > 0.5) {
            Candidate winner = availableCandidates.get(i);
            generateIRReport(numCandidates,numBallots,winner,availableCandidates,eliminatedCandidates);
            printResults(availableCandidates, eliminatedCandidates, winner);
            return;
          }
        }
      }
    }
  } //end ir()

  /**
   * This function returns a character array that contains the parties of each candidate.
   *
   * @param candidateWithParty is a string array that contains the candidates along with the party.
   * @return a character array listing the candidate parties.
   */
//Goes through each candidate name and grabs the party out of the string
  public static char[] getIRCandidateParties(String[] candidateWithParty) {
    char[] candidateParties = new char[candidateWithParty.length];
    //Goes through each candidate name and grabs the party out of the string
    for (int i = 0; i < candidateWithParty.length; i++) {
      for (int j = 0; j < candidateWithParty[i].length(); j++) {
        if (candidateWithParty[i].charAt(j) == '(') {
          candidateParties[i] = candidateWithParty[i].charAt(j+1);
          //This continue is used to break out of the inner loop when the party character is found
          continue;
        }
      }
    }
    return candidateParties;
  }//getIRCandidateParties()

  /**
   * Generates the press release file and prints the results of the election to the screen.
   *
   * @param numCandidates        is the number of candidates in the election
   * @param numBallots           the number of ballots in the election
   * @param winner               the winner of the election
   * @param availableCandidates  the available candidates that are still in the running.
   * @param eliminatedCandidates the eliminated candidates that are out of the running.
   */
//Creates IR press release file and prints results to the screen
  public static void generateIRReport(int numCandidates,int numBallots, Candidate winner, ArrayList<Candidate> availableCandidates, ArrayList<Candidate> eliminatedCandidates) {
    Report irReport = new Report("IR",numCandidates,numBallots,winner.getName());
    irReport.printReport();
    String candidateStandings = "";
    for(int j = 0; j < availableCandidates.size(); j++) {
      candidateStandings += availableCandidates.get(j).getName() +
              " (" + availableCandidates.get(j).getParty() + ") total votes: " + availableCandidates.get(j).getVoteCount() + "\n";
    }
    if(eliminatedCandidates.size() > 0) {
      candidateStandings += "Eliminated candidates: \n";
      for(int k = 0; k < eliminatedCandidates.size();k++) {
        candidateStandings += eliminatedCandidates.get(k).getName() +
                " (" + eliminatedCandidates.get(k).getParty() + ") total votes: "+ eliminatedCandidates.get(k).getVoteCount() + "\n";
      }
    }

    irReport.generateReportFile(getDateTime(),candidateStandings);
  } //generateIRReport()

  /**
   * Writes the results of the election to the audit file
   *
   * @param availableCandidates  the available candidates that are still in the running.
   * @param eliminatedCandidates the eliminated candidates that are out of the running.
   * @param winner               the winner of the election
   * @throws IOException the io exception. This function throws this exception when there is an error with generating the audit file.
   */
  public static void printResults(ArrayList<Candidate> availableCandidates, ArrayList<Candidate> eliminatedCandidates,Candidate winner) throws IOException {

    auditFileWriter.write("Final Vote Counts: \n");
    auditFileWriter.write("====================================== \n");
    for(int i = 0; i < availableCandidates.size(); i++){
      auditFileWriter.write(availableCandidates.get(i).getName() +
              " ("+ availableCandidates.get(i).getParty()+") total votes: "+ availableCandidates.get(i).getVoteCount()+"\n");
    }

    if(eliminatedCandidates.size() > 0) {
      auditFileWriter.write("Eliminated candidates: \n");
      for(int j = 0; j < eliminatedCandidates.size(); j++) {
         auditFileWriter.write(eliminatedCandidates.get(j).getName() +
                " (" + eliminatedCandidates.get(j).getParty() + ") total votes: "+ eliminatedCandidates.get(j).getVoteCount() + "\n");
      }
    }
    auditFileWriter.write("Winner of the election: " + winner.getName()+"\n");
  }//printResults()

  /**
   * This function distributes votes to the candidates still in the running. It is also responsible for redistributing
   * an eliminated candidate's votes.
   *
   * @param irBallotList        the list of ballots to be distributed.
   * @param availableCandidates the available candidates that are still in the running.
   */
  public static void distributeIRVotes(ArrayList<IRBallot> irBallotList, ArrayList<Candidate> availableCandidates) {
    int i = 0;
    while(i < irBallotList.size()) {
      String topPickCandidate = irBallotList.get(i).getTopPick();
      for(int j = 0; j < availableCandidates.size(); j++) {
        if(topPickCandidate.equals(availableCandidates.get(j).getName())) {
          //gives candidate the ballot
          availableCandidates.get(j).addBallotToList(irBallotList.get(i));
          //adds 1 to candidates total vote count.
          availableCandidates.get(j).addVote();
          //write event to audit file
          try{
            auditFileWriter.write(availableCandidates.get(j).getName() +
                    " Received the vote from the ballot with the id: "+ irBallotList.get(i).getID()+"\n");
            continue;
          }
          catch (IOException e) {
            System.out.println("There was a problem writing to the audit file");
          }

        }
      }
      i++;
    }
    for (int k = 0; k < availableCandidates.size(); k++) {
      try{
        auditFileWriter.write(availableCandidates.get(k).getName() + " " +
                availableCandidates.get(k).getParty() + " total Votes: " + availableCandidates.get(k).getVoteCount()+"\n");
      }
      catch (IOException e){
        System.out.println("There was a problem writing to the audit file");
      }
    }
  }//distributeIRVotes()

  /**
   * Run the OPL algorithm. This makes use of a few helper functions which can be found
   * below this function.
   */
  public static void opl() {
    int numCandidates;
    String[] candidateInfo;
    ArrayList<Party> parties = new ArrayList<Party>();

    numCandidates = Integer.parseInt(ballotReader.nextLine());

    // Parse line that has the candidates and parties
    // This line is in the form: [candidate1,party],[candidate2,party],...
    // e.g. [Borg,R], [Jones,R],[Smith,I]
    String candidateData = ballotReader.nextLine();
    candidateData = candidateData.replace("[", "");
    candidateInfo = candidateData.split("],");
    candidateInfo[candidateInfo.length-1] = candidateInfo[candidateInfo.length-1].replace("]", "");


    // Create the Party objects, create the Candidate objects, and add Candidates to the
    // correct Party
    createParties(candidateInfo, parties);

    int numSeats = Integer.parseInt(ballotReader.nextLine());
    int numBallots = Integer.parseInt(ballotReader.nextLine());
    OPLBallot ballots[] = new OPLBallot[numBallots];

    createAndDistributeOPLBallots(ballots, candidateInfo, parties);

    distributeSeatsToParties(numBallots,numSeats, parties);


    String pressReleaseInfo = "";
    String winners = "";


    // Distribute seats within each party to the winning candidates
    try {
      // Loop through each party
      for (int i = 0; i < parties.size(); i++) {
        auditFileWriter.write( "The candidates and their votes for the " + parties.get(i).getPartyName() + " party: \n");
        pressReleaseInfo += "\n The " + parties.get(i).getPartyName() + " party: " + parties.get(i).getPartyVoteCount() + " total votes\n";

        // Loop through all candidates in this party
        ArrayList<Candidate> partyCandidates = parties.get(i).getCandidateList();
        for (int j = 0; j < partyCandidates.size(); j++) {
          pressReleaseInfo += partyCandidates.get(j).getName() + ", " + partyCandidates.get(j).getVoteCount() + " votes\n";
          auditFileWriter.write( partyCandidates.get(j).getName() + ", " + partyCandidates.get(j).getVoteCount() + " votes\n");
        }

        // Distribute the seats in the current party to their top candidates
        String innerPartyAudit = parties.get(i).distributeSeatsToCandidates();
        auditFileWriter.write(innerPartyAudit);

        auditFileWriter.write( "Winners of the " + parties.get(i).getPartyName() + " party: ");

        winners += "Winners of the " + parties.get(i).getPartyName() + " party: ";



        ArrayList<Candidate> winningCandidates = parties.get(i).getCandidatesGivenSeats();
        for (int j = 0; j < winningCandidates.size(); j++) {
          auditFileWriter.write( winningCandidates.get(j).getName() + ", ");
          winners += winningCandidates.get(j).getName() + ", ";
        }
        auditFileWriter.write( "\n ");
        winners += "\n";
      }
    }
    catch (IOException e){
      System.out.println("An error writing the winners of OPL to the audit file occurred.");
      e.printStackTrace();
    }

    Report report = new Report("OPL", numCandidates, numBallots, winners);
    report.printReport();
    report.generateReportFile(getDateTime(), pressReleaseInfo);

  } // end opl()

  /**
   * Create parties for use in OPL. Create the Party objects, create the Candidate objects, and add
   * Candidates to the correct Party.
   *
   * @param candidateInfo the candidate info where each element is in the form of "[candidate name],[party]"
   * @param parties       the parties ArrayList that will store the result of this function
   */
//
  //
  public static void createParties(String[] candidateInfo, ArrayList<Party> parties) {
    for (int i = 0; i < candidateInfo.length; i++) {
      boolean needToCreateParty = true;
      // The last character of an item in candidateInfo is the party character
      // e.g. in Smith,I the last character I is the party
      char partyChar = candidateInfo[i].charAt(candidateInfo[i].length()-1);

      // The last two characters of an item in candidateInfo is .[Party]
      // For example Pike,D
      // Thus the substring would extract Pike, leaving out the ,D
      String candidateName = candidateInfo[i].substring(0,candidateInfo[i].length()-2);

      for (int j = 0; j < parties.size(); j++) {
        // If the party matches, add a Candidate object to that Party object
        if (partyChar == parties.get(j).getPartyName()) {
          parties.get(j).addCandidate(new Candidate(candidateName, partyChar));
          needToCreateParty = false;
          break;
        }
      }

      if (needToCreateParty) {
        // Create the party, because it doesn't exist
        parties.add(new Party(partyChar));
        // Add the new Candidate to this new party
        parties.get(parties.size()-1).addCandidate(new Candidate(candidateName, partyChar));
      }
    }
  }

  /**
   * Create and distribute opl ballots to the appropriate candidates.
   * This also updates the parties' vote counts accordingly.
   *
   * @param ballots       the ballots array to store the ballots. Its length is the number of ballots as                      given by the input file.
   * @param candidateInfo the candidate info where each element is in the form of "[candidate name],[party]"
   * @param parties       the parties ArrayList
   */
  public static void createAndDistributeOPLBallots(OPLBallot ballots[], String[] candidateInfo, ArrayList<Party> parties) {
    int ballotIndex = 0;

    // Go through the file and create OPLBallots and distribute them
    while(ballotReader.hasNextLine()) {
      // Create the OPLBallot
      ballots[ballotIndex] = new OPLBallot(ballotReader.nextLine(), candidateInfo, ballotIndex+1);

      // Now check each party to find the party that the vote goes towards
      for (int i = 0; i < parties.size(); i++) {
        // If party found
        if (ballots[ballotIndex].getParty() == parties.get(i).getPartyName()) {
          parties.get(i).incrementPartyVoteCount();
          ArrayList<Candidate> candidatesInParty = parties.get(i).getCandidateList();

          // Find the candidate that the vote goes towards
          for (int j = 0; j < candidatesInParty.size(); j++) {
            if (candidatesInParty.get(j).getName().equals(ballots[ballotIndex].getCandidate())) {
              candidatesInParty.get(j).addVote();


              try {
                // Write to the audit file
                auditFileWriter.write("Ballot " + (ballotIndex+1) + " was given to " + candidatesInParty.get(j).getName() + " of party " + parties.get(i).getPartyName() + "\n");
              }

              catch (IOException e) {
                System.out.println("An error writing the ballot distribution to parties to the audit file occurred.");
                e.printStackTrace();
              }


                break; // Don't need to check any further candidates for this vote
            }
          }
          break; // Don't need to check any other parties for this vote
        }
      }
      ballotIndex++;

    } // end of while(ballotReader.hasNextLine())
  }

  /**
   * Distribute seats to parties.
   *
   * @param numBallots the number of ballots
   * @param numSeats   the number of seats available in this election
   * @param parties    the parties ArrayList
   */
  public static void distributeSeatsToParties(int numBallots, int numSeats, ArrayList<Party> parties) {
    if (numSeats == 0 | numBallots == 0) {
      System.out.println("numSeats: " + numSeats + ", numBallots: " + numBallots);
      System.out.println("numSeats or numBallots is 0 in distributeSeatsToParties() which is not allowed");
      return;
    }
    // Calculate quota
    int quota = numBallots / numSeats;
    try {
      auditFileWriter.write("Given " + numBallots + " ballots and " + numSeats
              + " seats available, the quota is " + quota + "\n");
    }
    catch (IOException e){
      System.out.println("An error writing the quota to the audit file occurred.");
      e.printStackTrace();
    }

    double voteRemainders[] = new double[parties.size()];
    int numSeatsDistributed = 0;

    // Distribute seats to the parties
    try {
      auditFileWriter.write("Begin first round of seat allocation to parties\n");
      for (int i = 0; i < parties.size(); i++) {
        // If the party gets no votes, skip to the next party to avoid a divide by zero error
        if (parties.get(i).getPartyVoteCount() == 0) {
          continue;
        }

        // Figure out how many seats the party gets and get the remainder to use in the second round
        int numSeatsBeingDistributed = parties.get(i).getPartyVoteCount() / quota;
        parties.get(i).setNumSeatsToDistribute(numSeatsBeingDistributed);
        voteRemainders[i] = parties.get(i).getPartyVoteCount() % quota;
        numSeatsDistributed += numSeatsBeingDistributed;


        auditFileWriter.write("The " + parties.get(i).getPartyName() + " party won " +
                parties.get(i).getNumSeatsToDistribute() + " seats given their " +
                parties.get(i).getPartyVoteCount() + " votes. The remainder of votes is : " +
                voteRemainders[i] + "\n");


      }
    }
    catch (IOException e){
      System.out.println("An error writing the first round seat distribution results to the audit file occurred.");
      e.printStackTrace();
    }

    // Distribute the rest of the seats based on the votes voteRemainders
    try {
      auditFileWriter.write(numSeatsDistributed +" have been distributed so far. Begin second round of seat allocation to parties\n");
      while (numSeatsDistributed < numSeats) {
        double maxRemainder = -1;
        int maxIndex = 0;
        for (int i = 0; i < voteRemainders.length; i++) {
          if (voteRemainders[i] > maxRemainder) {
            maxRemainder = voteRemainders[i];
            maxIndex = i;
          }
          else if (voteRemainders[i] == maxRemainder) {
            // Coin flip gives a 0 for the current max vote candidate and a 1 for
            // the new max vote contender
            auditFileWriter.write("A coin toss determines priority between " + parties.get(i).getPartyName() +
                   " and " + parties.get(maxIndex).getPartyName() + "\n");
            if(coinFlip() == 1) {
              maxIndex = i;
            }
            auditFileWriter.write("The coin toss prioritizes " + parties.get(maxIndex).getPartyName() + "\n");
          }
        }

        // Now use max remainder to give seat
        parties.get(maxIndex).setNumSeatsToDistribute(parties.get(maxIndex).getNumSeatsToDistribute()+1);
        voteRemainders[maxIndex] = -1;  // Don't give one party multiple votes in the second allocation round
        numSeatsDistributed++;

        auditFileWriter.write("The " + parties.get(maxIndex).getPartyName() +
                " party won " + parties.get(maxIndex).getNumSeatsToDistribute() + " seats in total\n");

      }
    }
    catch (IOException e){
      System.out.println("An error writing the second round seat distribution results to the audit file occurred.");
      e.printStackTrace();
    }
  }

  /**
   * Gets the current date and time to use in system generated file names.
   *
   * @return a string of the form "[current Date]_[current time]"
   */
  public static String getDateTime() {
    String currentData = LocalDate.now().toString();
    String currentTime = LocalTime.now().toString();
    currentTime = currentTime.substring(0, currentTime.indexOf("."));
    currentTime = currentTime.replace(":", "-");
    return currentData + "_" + currentTime;
  }

  /**
   * Create the audit file and have the writer ready to use in other functions.
   */
  public static void createAuditFile() {
    // Get the current date time to use in the audit filename
    String currentDateTime = getDateTime();

    try {
      String election;
      if (electionType == 1) {
        election = "IR_";
      }
      else if (electionType == 2){
        election = "OPL_";
      }
      else {
        System.out.println("Improper election type of " + electionType);
        return; // TODO: perhaps uses System.exit() to end the program gracefully here
      }

      // Set the filename and create the audit file
      auditFileName = election + currentDateTime + "AuditFile.txt";
      auditFile = new File(auditFileName);

      if (auditFile.createNewFile()) {
        System.out.println("Audit file named: " + auditFile.getName() + " created successfully");
      }

      // Create the audit file writer
      auditFileWriter = new FileWriter(auditFileName);
      auditFileWriter.write(election + " audit file created\n");
    }
    catch (IOException e){
      System.out.println("An error creating the audit file occurred.");
      e.printStackTrace();
    }
  }

  /**
   * Simulate a fair coin toss to evaluate ties.
   *
   * @return the int 0 or 1.
   */
  public static int coinFlip() {
//    Random rand = new Random(System.currentTimeMillis());
    Random rand = new Random();
    int randomNum;
    int headsCount = 0;
    int tailsCount = 0;

    for (int i = 0; i < 1000; i++) {
      // Produce a 0 or 1 randomly
      randomNum = rand.nextInt(2);

      // If we get a 0 then that is heads/the first candidate/party in this coin toss
      if (randomNum == 0) {
        headsCount++;
      }

      // Else we get a 1 which represents the second candidate/party in this coin toss
      else {
        tailsCount++;
      }
    }

    if (headsCount > tailsCount) {
      return 0;
    }
    else {
      return 1;
    }
  }

  /**
   * Tests getting the party of each candidate and putting it into a list.
   */
  @Test
  public void testGetIRCandidateParties() {
    String[] candidatesWithParty = {"Rosen (D)", "Kleinberg (R)", "Chou (I)", "Royce(L)"};
    char[] resultedParties = getIRCandidateParties(candidatesWithParty);
    char[] expectedParties = {'D','R','I','L'};
    assertArrayEquals(expectedParties,resultedParties);
  }

  /**
   * Test generating the press release file and adding information to it.
   */
  @Test
  public void testGenerateIRReport() {
    int numCandidates = 2;
    int numBallots = 10;
    Candidate rosen = new Candidate("Rosen",'D');
    Candidate chou = new Candidate("Chou",'R');
    ArrayList<Candidate> availableCandidates = new ArrayList<>();
    availableCandidates.add(rosen);
    ArrayList<Candidate> eliminatedCandidates = new ArrayList<>();
    eliminatedCandidates.add(chou);

    for(int i = 0; i < 7; i++){
      rosen.addVote();
    }
    for(int i = 0; i < 3; i++){
      chou.addVote();
    }
    generateIRReport(numCandidates,numBallots,rosen,availableCandidates,eliminatedCandidates);
  }

  /**
   * Tests writing the results to the audit file.
   */
  @Test
  public void testPrintResults() {
    int numCandidates = 2;
    int numBallots = 10;

    electionType = 1;

    createAuditFile();
    Candidate rosen = new Candidate("Rosen",'D');
    Candidate chou = new Candidate("Chou",'R');
    ArrayList<Candidate> availableCandidates = new ArrayList<>();
    availableCandidates.add(rosen);
    ArrayList<Candidate> eliminatedCandidates = new ArrayList<>();
    eliminatedCandidates.add(chou);

    try {
      printResults(availableCandidates,eliminatedCandidates,rosen);
    }
    catch (IOException e) {
      System.out.println("There was an error generating the audit file");
    }
  }

  /**
   * Test distributing ir votes to candidates.
   */
  @Test
  public void testDistributeIRVotes() {
    String[] candidateNames = {"Rosen", "Kleinberg", "Chou", "Royce"};
    char[] candidateParties = {'D','R','I','L'};

    String irBallotString1 = "1,2,3,4";
    String irBallotString2 = "2,3,4,1";
    String irBallotString3 = "3,,2,1";
    String irBallotString4 = ",1,,";
    int id = 1;
    ArrayList<IRBallot> irBallotList = new ArrayList<>();
    IRBallot irBallot1 = new IRBallot(irBallotString1,candidateParties,candidateNames,id);
    irBallotList.add(irBallot1);
    id++;
    IRBallot irBallot2 = new IRBallot(irBallotString2,candidateParties,candidateNames,id);
    irBallotList.add(irBallot2);
    id++;
    IRBallot irBallot3 = new IRBallot(irBallotString3,candidateParties,candidateNames,id);
    irBallotList.add(irBallot3);
    id++;
    IRBallot irBallot4 = new IRBallot(irBallotString4,candidateParties,candidateNames,id);
    irBallotList.add(irBallot4);
    id++;

    ArrayList<Candidate> availableCandidates = new ArrayList<>();
    Candidate rosen = new Candidate("Rosen",'D');
    availableCandidates.add(rosen);
    Candidate kleinberg = new Candidate("Kleinberg",'R');
    availableCandidates.add(kleinberg);
    Candidate chou = new Candidate("Chou",'I');
    availableCandidates.add(chou);
    Candidate royce = new Candidate("Royce",'L');
    availableCandidates.add(royce);

    electionType = 1;
    createAuditFile();
    distributeIRVotes(irBallotList,availableCandidates);

  }

  /**
   * Test create audit file for each election type.
   */
  @Test
  public void testCreateAuditFile() {
    System.out.println("Test results for createAuditFile():");
    // Test IR audit file creation
    electionType = 1;
    BCS.createAuditFile();

    // Test OPL audit file creation
    electionType = 2;
    BCS.createAuditFile();

    // Test incorrect election type audit file creation
    // This should never happen, and print out an error
    electionType = 0;
    BCS.createAuditFile();

    try {
      auditFileWriter.close();
    }
    catch (IOException e) {
      System.out.println("An error closing the audit file in testOPLBallotDistribution() occurred.");
      e.printStackTrace();
    }

    System.out.println("---------------------------------------------------\n");
  }

  /**
   * Test opl ballot distribution. Uses the OPLBallotDistribTest.txt file for the test
   * ballot data. This file is expected to be in the same directory as this program.
   */
  @Test
  public void testOPLBallotDistribution() {
    System.out.println("Test results for createAndDistributeOPLBallots():");

    try {
      ballotFile = new File("OPLBallotDistribTest.txt");
      ballotReader = new Scanner(ballotFile);
    }
    catch (FileNotFoundException e) {
      System.out.println("Error trying to test the distribution of OPL ballots");
    }
    electionType = 2;
    createAuditFile();

    OPLBallot ballots[] = new OPLBallot[18];  // Test file for this has 18 ballots
    String[] candidateInfo = {"Volze,D", "Moll,I", "Wartenberg,G", "Foster,D", "Berg,R", "McClerg,R", "Morey,G", "Pike,D", "Grolnie,R"};

    ArrayList<Party> parties = createTestParties();

    createAndDistributeOPLBallots(ballots, candidateInfo, parties);
    // D (6): Volze = 3, Foster = 1, Pike = 2
    // R (6): Berg = 3, McClerg = 2, Grolnie = 1
    // G (4): Wartenberg = 3, Morey = 1
    // I (2): Moll = 2

    // Check that all the parties have the correct number of total votes
    assertEquals(6, parties.get(0).getPartyVoteCount());  // D gets 6 votes total
    assertEquals(6, parties.get(1).getPartyVoteCount());  // R gets 6 votes total
    assertEquals(4, parties.get(2).getPartyVoteCount());  // G gets 4 votes total
    assertEquals(2, parties.get(3).getPartyVoteCount());  // I gets 2 votes total

    // Check that each individual candidate has the correct number of votes
    for (int i = 0; i < parties.size(); i++) {
      ArrayList<Candidate> candidates = parties.get(i).getCandidateList();
      for (int j = 0; i < candidates.size(); i++) {
        switch(candidates.get(j).getName()) {
          case "Volze":
            assertEquals(3,candidates.get(j).getVoteCount());
            break;
          case "Foster":
            assertEquals(1,candidates.get(j).getVoteCount());
            break;
          case "Pike":
            assertEquals(2,candidates.get(j).getVoteCount());
            break;
          case "Berg":
            assertEquals(3,candidates.get(j).getVoteCount());
            break;
          case "McClerg":
            assertEquals(2,candidates.get(j).getVoteCount());
            break;
          case "Grolnie":
            assertEquals(1,candidates.get(j).getVoteCount());
            break;
          case "Wartenberg":
            assertEquals(3,candidates.get(j).getVoteCount());
            break;
          case "Morey":
            assertEquals(1,candidates.get(j).getVoteCount());
            break;
          case "Moll":
            assertEquals(2,candidates.get(j).getVoteCount());
            break;
        }
      }
    }

      ballotReader.close();
      try {
          auditFileWriter.close();
      }
      catch (IOException e) {
          System.out.println("An error closing the audit file in testOPLBallotDistribution() occurred.");
          e.printStackTrace();
      }

    System.out.println("---------------------------------------------------\n");
  }

  /**
   * Test distributing seats to parties. This follows the same example data from
   * testOPLBallotDistribution().
   */
  @Test
  public void testDistributeSeatsToParties() {
    System.out.println("Test results for testDistributeSeatsToParties():");
    // Continuing example in testOPLBallotDistribution() with 18 total ballots:
    // D (6): Volze = 3, Foster = 1, Pike = 2
    // R (6): Berg = 3, McClerg = 2, Grolnie = 1
    // G (4): Wartenberg = 3, Morey = 1
    // I (2): Moll = 2

    electionType = 2;
    createAuditFile();

    // Try with 3 seats to distribute
    ArrayList<Party> parties = createTestParties();
    giveVotesToTestParties(parties);

    distributeSeatsToParties(18, 3, parties);

    assertEquals(1, parties.get(0).getNumSeatsToDistribute());
    assertEquals(1, parties.get(1).getNumSeatsToDistribute());
    assertEquals(1, parties.get(2).getNumSeatsToDistribute());
    assertEquals(0, parties.get(3).getNumSeatsToDistribute());

    // Try with 4 seats to distribute
    parties = createTestParties();
    giveVotesToTestParties(parties);

    distributeSeatsToParties(18, 4, parties);

    // The G party should always get 1 seat. The other parties' vote counts vary but total to 3.
    assertEquals(1, parties.get(2).getNumSeatsToDistribute());

    System.out.println("The 4 seats case: ");
    System.out.println("The D party got " + parties.get(0).getNumSeatsToDistribute() + " seats");
    System.out.println("The R party got " + parties.get(1).getNumSeatsToDistribute() + " seats");
    System.out.println("The I party got " + parties.get(3).getNumSeatsToDistribute() + " seats\n");


    // Try with 6 seats to distribute, should get an error print statement
    parties = createTestParties();
    giveVotesToTestParties(parties);

    distributeSeatsToParties(18, 6, parties);
    assertEquals(2, parties.get(0).getNumSeatsToDistribute());
    assertEquals(2, parties.get(1).getNumSeatsToDistribute());
    assertEquals(1, parties.get(2).getNumSeatsToDistribute());
    assertEquals(1, parties.get(3).getNumSeatsToDistribute());

    // Try with 9 seats to distribute, should get an error print statement
    parties = createTestParties();
    giveVotesToTestParties(parties);

    distributeSeatsToParties(18, 9, parties);
    assertEquals(3, parties.get(0).getNumSeatsToDistribute());
    assertEquals(3, parties.get(1).getNumSeatsToDistribute());
    assertEquals(2, parties.get(2).getNumSeatsToDistribute());
    assertEquals(1, parties.get(3).getNumSeatsToDistribute());

    // Try with 0 seats to distribute, should get an error print statement
    parties = createTestParties();
    giveVotesToTestParties(parties);

    System.out.println("zero seat case which should be an error");
    distributeSeatsToParties(18, 0, parties);


    try {
      auditFileWriter.close();
    }
    catch (IOException e) {
      System.out.println("An error closing the audit file in testDistributeSeatsToParties() occurred.");
      e.printStackTrace();
    }

    System.out.println("---------------------------------------------------\n");
  }

  /**
   * Create test parties array list to aid in the test functions: testOPLBallotDistribution()
   * and testDistributeSeatsToParties().
   *
   * @return the array list of test parties with candidates set.
   */
  public ArrayList<Party> createTestParties() {
    ArrayList<Party> testParties = new  ArrayList<Party>();
    testParties.add(new Party('D'));
    testParties.add(new Party('R'));
    testParties.add(new Party('G'));
    testParties.add(new Party('I'));

    testParties.get(0).addCandidate(new Candidate("Volze", 'D'));
    testParties.get(0).addCandidate(new Candidate("Foster", 'D'));
    testParties.get(0).addCandidate(new Candidate("Pike", 'D'));
    testParties.get(1).addCandidate(new Candidate("Berg", 'R'));
    testParties.get(1).addCandidate(new Candidate("McClerg", 'R'));
    testParties.get(1).addCandidate(new Candidate("Grolnie", 'R'));
    testParties.get(2).addCandidate(new Candidate("Wartenberg", 'G'));
    testParties.get(2).addCandidate(new Candidate("Morey", 'G'));
    testParties.get(3).addCandidate(new Candidate("Moll", 'I'));

    return testParties;
  }

  /**
   * Give votes to test parties. Aids in testDistributeSeatsToParties()
   * test function.
   *
   * @param testParties the test parties with party vote counts set
   */
  public void giveVotesToTestParties(ArrayList<Party> testParties) {
    // D (6): Volze = 3, Foster = 1, Pike = 2
    // R (6): Berg = 3, McClerg = 2, Grolnie = 1
    // G (4): Wartenberg = 3, Morey = 1
    // I (2): Moll = 2
    // Set vote counts for the parties
    testParties.get(0).setPartyVoteCount(6);
    testParties.get(1).setPartyVoteCount(6);
    testParties.get(2).setPartyVoteCount(4);
    testParties.get(3).setPartyVoteCount(2);
  }

}
