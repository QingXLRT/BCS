import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * TestRunner.java
 * The Test Runner class created to run some automated tests.
 * Created by Jason Phadnis
 */
public class TestRunner {
   /**
    * This runs the tests automatically from the TestOPLBallot class and the TestBCS class.
    * The results of those tests are printed to the console.
    *
    * @param args the input arguments
    */
   public static void main(String[] args) {
      Result[] results = {JUnitCore.runClasses(TestOPLBallot.class), JUnitCore.runClasses(TestBCS.class)};

      for (int i = 0; i < results.length; i++) {
         for (Failure failure : results[i].getFailures()) {
            System.out.println(failure.toString());
         }
         System.out.println(results[i].wasSuccessful());
      }
   }
}
