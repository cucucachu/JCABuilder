import java.io.IOException;
import jxl.read.biff.BiffException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import jxl.write.*;

public class JCABuilder {
   private static final String jcaFolderTest = "/home/cody/Professional/MillerPacific/Data/Round 3/JCAs";
   private static final String recapFileTest = "/home/cody/Professional/MillerPacific/Data/Round 3/TimeSheets/WeeklyRecap.xls";
   private static final String jobNoStrTest = "111.111";
   public static JCAGui gui;
   
   public static void main(String[] args) {  
      gui = new JCAGui();
   }
   
   public static void buildJCA(String jcaFolder, String recapFile, String week) {
      ArrayList<JCA> jcas;
      ArrayList<Job> allJobs;
      ArrayList<Job> failedJobs;
      Recap recap;
      Recap outRecap;
      
      try {
         recap = new Recap(recapFile);
         allJobs = recap.getJobs();
         jcas = JobSorter.sortJobs(allJobs, jcaFolder, week);
         failedJobs = new ArrayList<Job>();
         
         for (JCA jca : jcas) {
            gui.output("Writing to " + jca);
            try {
               jca.loadWorkbook();
               jca.prepareJCA();
               jca.fillJCA();
               jca.writeJCA();
               failedJobs.addAll(jca.getFailedJobs());
            }
            catch (JCANotFoundException ex) {
               gui.output(ex.toString());
               failedJobs.addAll(jca.getJobs());
            }
            catch (Exception ex) {
               gui.output("Could not write to " + jca + ". Adding those jobs to failed jobs list.");
               gui.output(ex.toString());
               failedJobs.addAll(jca.getJobs());
            }
         }
         
         gui.output("Failed Jobs: ");
         for (Job job : failedJobs) {
            gui.output("   " + job);
         }
         
         gui.output("Writing unsorted jobs to Recap file.");
         outRecap = new Recap(failedJobs);
         
         outRecap.makeRecap(recapFile.substring(0, recapFile.length() - 4) + "_out.xls");
         outRecap.writeRecap();
         gui.output("JCA Compiler Complete.");
      }
      catch (JCANotFoundException ex) {
         System.out.println("Caught exception " + ex);
         gui.output(ex.getMessage());
      }
      catch (RecapFormatException ex) {
         System.out.println("Caught a Recap Format Exception.");
         gui.output("Recap is incorrectly formatted. Please check the recap and try again.");
      }
   }
   
   public static void output(String msg) {
      gui.output(msg);
   }
}
