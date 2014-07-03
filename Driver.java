import java.io.IOException;
import jxl.read.biff.BiffException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import jxl.write.*;

public class Driver {
   private static final String jcaFolder = "/home/cody/Professional/MillerPacific/Data/Round 3/JCAs";
   private static final String jobNoStr = "111.111";
   
   public static void main(String[] args) {  
      String test = jcaFolder;
      test = test.toUpperCase();
      System.out.println(test);
      /*JCA jca;
      
      try {
         jca = new JCA(jobNoStr, jcaFolder, "Week1");
         jca.prepareJCA();
         jca.writeJCA();
      }
      catch (JCANotFoundException ex) {
         System.out.println("Caught exception " + ex);
      }
      catch (JCAException ex) {
         System.out.println("Caught JCAException " + ex.getMessage());
      }
      catch (WriteException ex) {
         System.out.println("Caught a WriteException");
      }*/
   }
}
