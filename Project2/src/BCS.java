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

/**
 * BCS.java
 * The main program of our Ballot Counting System (BCS).
 * Created by Jason Phadnis, Kobin Khadka, Brandon Schenck, and Gavin Huang
 */
public class BCS {
  private static int electionType;   // 1 = IR, 2 = OPL
//  private static File ballotFile;
  private static ArrayList<File> ballotFiles;
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


    ballotFiles = new ArrayList<File>();

    boolean checkingFiles = true;

    while (checkingFiles){

      boolean fileFound = false;
      while (!fileFound) {

        try {
          String filename = userInput.nextLine();
          File newFile = new File(filename);
          ballotReader = new Scanner(newFile);
          ballotFiles.add(newFile);
          fileFound = true;

        } catch (FileNotFoundException e) {
          System.out.println("The filename you entered was not found in the directory of this program. Please enter a valid filename (don't forget the file extension).");
        }
      }

      // Did the user give an appropriate answer to whether or not there is
      // another file to input
      boolean hasMultipleFileAnswer = false;
      
      System.out.println("Do you have more file to enter ??  Enter 1 for yes or Enter 0 for no");
      while (!hasMultipleFileAnswer) {
          String answer = userInput.nextLine();
          
          if (answer.equals("0")) {
            checkingFiles = false;
            hasMultipleFileAnswer = true;
          }
          
          else if (answer.equals("1")) {
        	 hasMultipleFileAnswer = true;
        	 System.out.println("Enter a filename (don't forget the file extension)");
          }
          
          else {
        	  System.out.println("That wasn't proper input. Enter 1 to enter another file or 0 for no more files");
          }
      }
      

    }


      ballotReader = new Scanner(ballotFiles.get(0));

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
        else if (data.compareTo("PO") == 0) {
        	electionType = 3;
            System.out.println("PO election set");
        }
        else {
          System.out.println("Error: the first line of the ballot info file was: " + data);
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
    else if (electionType == 3) {
    	po();
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
    //int ballot = 0;
    
    parseBallotsForIR(irBallotList, candidateParties, candidateNamesOnly, id);
    numBallots = irBallotList.size();
    
//    while(ballotReader.hasNextLine()){
//      String irBallotString = ballotReader.nextLine();
//      IRBallot irBallot = new IRBallot(irBallotString, candidateParties, candidateNamesOnly,id);
//      irBallotList.add(irBallot);
//      auditFileWriter.write("ID: "+ irBallotList.get(ballot).getID() + ", Ballot: "+ irBallotString + "\n");
//      ballot++;
//      id++;
//    }

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
   * This function  will parse the information from IR election ballots given to the system and avoid counting invalid ballots.
   * @param irBallotList      -  Ballot List from IR election
   * @param candidateParties -  Parties of candidates participate in election
   * @param candidateNamesOnly - Names of the candidates participate in election
   * @param id -  uniquely identifies each ballot
   * @throws IOException - throws exeception if ballot reader fails to look for next file.
   */
  
  public static void parseBallotsForIR(ArrayList<IRBallot> irBallotList, char[] candidateParties,  String[] candidateNamesOnly, int id) throws IOException{
	  int ballotIndex = 0;
	  int invalidate_threshold = 0;
	  // Get 50% number of candidates for invalidating ballots.
	  // If number of candidates is odd round up by one
	  if(candidateNamesOnly.length % 2 == 0) {
		  invalidate_threshold = candidateNamesOnly.length / 2;
	  }
	  else {
		  //rounds up by 1
		  int temp = candidateNamesOnly.length / 2;
		  temp++;
		  invalidate_threshold = temp;
	  }
	  
	  // Loop through each ballot input file
	  for (int i = 0; i < ballotFiles.size(); i++) {
		  // For files beyond the first one, skip the first 4 lines as these
		  // are the same across all files. Also skip the 5th line because we can get the total
		  // number of ballots from the final size of the array list of ballots
		  if (i != 0) {
			  for (int j = 0; j < 4; j++) {
				  ballotReader.nextLine();
			  }
		  }
		  // Create IR ballots for the current active file
		  while(ballotReader.hasNextLine()) {
			  String irBallotString = ballotReader.nextLine();
		      IRBallot irBallot = new IRBallot(irBallotString, candidateParties, candidateNamesOnly,id);
		      if(irBallot.getBallotLength() >= invalidate_threshold) {
//		    	  System.out.println("Ballot with ID: "+ irBallot.getID()+" , Ballot: " + irBallotString + " was invalidated due to having < 50% of candidates ranked");
		    	  irBallotList.add(irBallot);
			      auditFileWriter.write("ID: "+ irBallotList.get(ballotIndex).getID() + ", Ballot: "+ irBallotString + "\n");
			      ballotIndex++;
		      }
		      id++;
		  }
		  //ballotIndex = ballots.size();
		  // Set ballotReader to look at the next file if there is one
		  if (i < ballotFiles.size() - 1) {
			  try {
				  ballotReader = new Scanner(ballotFiles.get(i+1));
			  }
			  catch (FileNotFoundException e) {
		        System.out.println("Error setting ballotReader to the next file.");
		      }
			  
		  }
	  }
	  
  }

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
   * Run the PO algorithm  that handles PO election informations.
   */
  public static void po() {
	int numCandidates;
	String[] candidateInfo;
	
	numCandidates = Integer.parseInt(ballotReader.nextLine());
	
	// Parse line that has the candidates and parties
	// This line is in the form: [candidate1,party],[candidate2,party],...
	// e.g. [Borg,R], [Jones,R],[Smith,I]
	String candidateData = ballotReader.nextLine();
	candidateData = candidateData.replace("[", "");
	candidateInfo = candidateData.split("],");
	candidateInfo[candidateInfo.length-1] = candidateInfo[candidateInfo.length-1].replace("]", "");
	
	Integer.parseInt(ballotReader.nextLine());		// Skip the number of ballots line
	
	ArrayList<POBallot> ballots = new ArrayList<POBallot>();

    parseBallotsForPO(ballots, candidateInfo);
    
    int numBallots = ballots.size();
    
    // Debug code for confirming that POBallot objects are created appropriately
    // Uncomment the following for loop for system testing.
//    for (int i = 0; i < ballots.size(); i++) {
//    	System.out.println("Ballot number " + ballots.get(i).getID() + ": " 
//    			+ ballots.get(i).getCandidate() + " " + ballots.get(i).getParty());
//    }
    
	
	
  }

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
    Integer.parseInt(ballotReader.nextLine());		// Skip the number of ballots line
    ArrayList<OPLBallot> ballots = new ArrayList<OPLBallot>();

    parseBallotsForOPL(ballots, candidateInfo, parties);
    
    int numBallots = ballots.size();

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
   * @param startIndex    the first index of the ballots array that the current file should add to
   * @param ballots       the array list to store the ballots. Its length is the sum of the number of ballots as given by the input file(s).
   * @param candidateInfo the candidate info where each element is in the form of "[candidate name],[party]"
   * @param parties       the parties ArrayList
   */
  public static void createAndDistributeOPLBallots(int startIndex, ArrayList<OPLBallot> ballots, String[] candidateInfo, ArrayList<Party> parties) {
    int ballotIndex = startIndex;

    // Go through the file and create OPLBallots and distribute them
    while(ballotReader.hasNextLine()) {
      // Create the OPLBallot
      ballots.add(new OPLBallot(ballotReader.nextLine(), candidateInfo, ballotIndex+1));

      // Now check each party to find the party that the vote goes towards
      for (int i = 0; i < parties.size(); i++) {
        // If party found
        if (ballots.get(ballotIndex).getParty() == parties.get(i).getPartyName()) {
          parties.get(i).incrementPartyVoteCount();
          ArrayList<Candidate> candidatesInParty = parties.get(i).getCandidateList();

          // Find the candidate that the vote goes towards
          for (int j = 0; j < candidatesInParty.size(); j++) {
            if (candidatesInParty.get(j).getName().equals(ballots.get(ballotIndex).getCandidate())) {
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
   * This function parse ballot file and create PO ballots
   * @param ballots file from the PO election
   * @param candidateInfo  is the information about the candidates participated in the election
   */
  public static void parseBallotsForPO(ArrayList<POBallot> ballots, String[] candidateInfo) {
	  int ballotIndex = 0;
	  
	  // Loop through each ballot input file
	  for (int i = 0; i < ballotFiles.size(); i++) {
		  // For files beyond the first one, skip the first 3 lines as these
		  // are the same across all files. Also skip the 4th line because we can get the total
		  // number of ballots from the final size of the array list of ballots
		  if (i != 0) {
			  for (int j = 0; j < 4; j++) {
				  ballotReader.nextLine();
			  }
		  }
		  
		  // Go through the file and create POBallot objects
		  while(ballotReader.hasNextLine()) {
		      // Create the POBallot
			  ballots.add(new POBallot(ballotReader.nextLine(), candidateInfo, ballotIndex+1));
			  ballotIndex++;
		  }
		  
		  
		  // Set ballotReader to look at the next file if there is one
		  if (i < ballotFiles.size() - 1) {
			  try {
				  ballotReader = new Scanner(ballotFiles.get(i+1));
			  }
			  catch (FileNotFoundException e) {
		        System.out.println("Error setting ballotReader to the next file.");
		      }
			  
		  }
	  }
  }

  /**
   * This function will parse election information from OPL ballot file
   * @param ballots - Lists of ballots from the election
   * @param candidateInfo - Information about the candidates who participated in the OPL election
   * @param parties - Parties that participated in the election
   */
  public static void parseBallotsForOPL(ArrayList<OPLBallot> ballots, String[] candidateInfo, ArrayList<Party> parties) {
	  int ballotIndex = 0;
	  
	  // Loop through each ballot input file
	  for (int i = 0; i < ballotFiles.size(); i++) {
		  // For files beyond the first one, skip the first 4 lines as these
		  // are the same across all files. Also skip the 5th line because we can get the total
		  // number of ballots from the final size of the array list of ballots
		  if (i != 0) {
			  for (int j = 0; j < 5; j++) {
				  ballotReader.nextLine();
			  }
		  }
		  
		  
		  // Create and distribute OPL ballots for the current active file
		  createAndDistributeOPLBallots(ballotIndex, ballots, candidateInfo, parties);
		  ballotIndex = ballots.size();
		  
		  // Set ballotReader to look at the next file if there is one
		  if (i < ballotFiles.size() - 1) {
			  try {
				  ballotReader = new Scanner(ballotFiles.get(i+1));
			  }
			  catch (FileNotFoundException e) {
		        System.out.println("Error setting ballotReader to the next file.");
		      }
			  
		  }
	  }
	  
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
      else if (electionType == 3) {
    	  election = "PO_";
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
   * Set the election type for the purpose of testing. Only use in test
   * cases that need to explicitly set the election type.
   * 
   * @param type 		Election type number
   */
  public static void setElectionTypeForTest(int type) {
	  electionType = type;
  }

  /**
   * Close an opened audit file writer for the purpose of testing. Only use in test
   * cases that need naturally open and use the auditFileWriter which then needs to be closed.
   * 
   * @param errorMsg        a String of the error message that should be printed by the catch block.
   */
  public static void closeFileWriterForTest(String errorMsg) {
	  try {
	        auditFileWriter.close();
	  }
	  
      catch (IOException e) {
        System.out.println(errorMsg);
        e.printStackTrace();
      }
  }
  
  /**
   * Open a file reader for the purpose of testing. Only use in test
   * cases that need to explicitly read in a test ballot file.
   */
  public static void openReaderForTest() {
	  try {

	        File ballotFile = new File("OPLBallotDistribTest.txt");
	        ballotReader = new Scanner(ballotFile);
	  }
	  
      catch (FileNotFoundException e) {
        System.out.println("Error trying to test the distribution of OPL ballots");
	   }
  }






  
  /**
   * Closes a file reader for the purpose of testing. Only use in test
   * cases that need to explicitly close ballotReader.
   */
  public static void closeReaderForTest() {
	  ballotReader.close();
  }

  /**
   * This function handles ballot file and add the new files in the ballot file list
   * @param fileName name of the file that will be given to the system
   */
  public static void setFilesForParsingTest(String fileName) {
	  
	  try {
		  ballotFiles = new ArrayList<File>();
		  
		  File ballotFile = new File(fileName);
		  ballotReader = new Scanner(ballotFile);

		  
		  ballotFiles.add(ballotFile);
	  }
  		
  		catch (FileNotFoundException e) {
          System.out.println("The filename you entered was not found in the directory of this program. Please enter a valid filename (don't forget the file extension).");
        }
  }

}
