# repo-Team5
Team5 (Huang, Khadka, Phadnis, Schenck)

BCS.java is the main file. To run it:

1. open command line
2. navigate to Project2/src/ directory
3. build the files in src/ using ```javac BCS.java```
4. run ```java BCS```
5. type file name into the program when prompted.

To run test cases first compile in terminal with:
javac -cp .:/usr/share/java/junit/junit4.jar [test class name 1] [test class name 2] ...

The run the files with:
java -cp .:/usr/share/java/junit/junit4.jar org.junit.runner.JUnitCore [test class name]

Please note that TestReport.java is an exception to this as it doesn't use Junit. Please run this like a normal java program (like BCS.java). Of course all input files that may be necessary for tests will need to be moved from the testing folder into the src folder with all the code. 
