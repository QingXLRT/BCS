import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * TestReport.java
 * A class with unit tests for the Report class.
 * Created by Gavin Huang
 */
public class TestReport {

  /**
   * The main program to run and test Report.
   *
   * @param args the args
   */
  public static void main(String[] args){

    //"Test cases" for printing out in the terminal
    Report irReport = new Report ("IR",3,1,"Uvuvwevwevwe Onyetenyevwe Ugwemubwem Ossas");
    irReport.printReport();
    Report oplReport = new Report ("OPL",3,1,"Hubert Blaine Wolfeschlegelsteinhausenbergerdorff Sr");
    oplReport.printReport();

    //"Test cases" for generating example files
    irReport.generateReportFile("03-13-21", "IR Press Release Info");
    oplReport.generateReportFile("01-01-01", "OPL Press Release Info");
  }
}
