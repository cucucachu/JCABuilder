import java.util.ArrayList;

public class JobSorter {
   
   public static ArrayList<JCA> sortJobs(ArrayList<Job> jobs, String jcaFolder,
      String week) 
      throws JCANotFoundException {
      
      ArrayList<JCA> jcas;
      boolean sorted;
      
      jcas = new ArrayList<JCA>();
      
      for (Job job : jobs) {
         System.out.println("Sorting " + job);
         sorted = false;
         for (JCA jca : jcas) {
            if (jca.getJobNoStr().compareTo(job.jobNoStr) == 0) {
               System.out.println("JCA found for " + job);
               jca.addJob(job);
               sorted = true;
            }
         }
         if (sorted == false) {
            System.out.println("JCA not found, creating it");
            JCA newJCA = new JCA(job.jobNoStr, jcaFolder, week);
            newJCA.addJob(job);
            jcas.add(newJCA);
         }
      }
      
      return jcas;
   }
}
