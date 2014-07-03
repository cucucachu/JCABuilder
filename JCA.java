import jxl.*;
import jxl.write.*;
import java.io.File;
import java.io.IOException;
import jxl.read.biff.BiffException;
import java.util.ArrayList;
import java.util.Iterator;

public class JCA {
   public static final int JobRowNotFound = -1;
   public static final int CurrentWeekColumn = 4;
   private static final String IChargesLabel = "INSIDE CHGS";
   private static final String RChargesLabel = "REIMB CHGS";
   private static final String VHoursLabel = "VEHICLE (HRS)";
   private static final String MilesLabel = "MILES";
   private static final String FDTLabel = "FDT TEST";
   private static final String WeekLabel = "WEEK ENDING DATE:";
   private static final String WPAssistantLabel = "WP/Assistant";

   private String jobNoStr;
   private String week;
   private ArrayList<Job> jobs;
   private ArrayList<Job> failedJobs;
   private String jcaFolder;
   private File jcaFile;
   private WritableWorkbook jcaWorkbook;
   private WritableSheet sheet;
   
   private double weeklyMiles;
   private double weeklyVehicleHours;
   private double weeklyFdtTotal;
   
   public JCA (String jobNoStr, String jcaFolder, String week) {
      this.jobNoStr = jobNoStr;
      this.jcaFolder = jcaFolder;
      this.week = week;
      jobs = new ArrayList<Job>();
      failedJobs = new ArrayList<Job>();
      findFile();
   }
   
   public void loadWorkbook() throws JCANotFoundException {
      if (jcaFile == null) {
         throw new JCANotFoundException("Could not find JCA for job", jobNoStr);
      }
      else {
         try {
            Workbook originalWorkbook = Workbook.getWorkbook(jcaFile);
            jcaWorkbook = Workbook.createWorkbook(new File(jcaFolder+"/"+jobNoStr+"_new.xls"), originalWorkbook);
            //originalWorkbook.close();
            sheet = jcaWorkbook.getSheet(0);
         }
         catch (Exception ex) {
            output("Failed to load Workbook! " + ex);
            System.out.println("Failed to load Workbook!!!!!!!!!!!! " + ex);
         }
      }
   }
   
   private void findFile() {
      File file = new File(jcaFolder+"/"+jobNoStr+".xls");
      System.out.println("Searching for " + jcaFolder+"/"+jobNoStr+".xls");
      if (file.exists())
         jcaFile = file;
      else jcaFile = null;
   }
   
   public void addJob(Job newJob) {
      jobs.add(newJob);
      
      if (jcaFile == null)
         failedJobs.add(newJob);
   }
   
   private int findJobRow(Job job) {
      String toFind;
      Cell searchCell;
      
      if (job.classCode != null && job.classCode.compareTo("WP") == 0) {
         searchCell = sheet.findCell(WPAssistantLabel);
         if (searchCell == null)
            return -1;
         else
            return searchCell.getRow();
      }
      
      if (job.prevWage != null) 
         toFind = job.initials + " " + job.prevWage;
      else
         toFind = job.initials + " " + job.classCode;
      
      searchCell = sheet.findCell(toFind);
      
      if (searchCell == null)
         return JobRowNotFound;
      else
         return searchCell.getRow();
   }
   
   private void calculateInsideCharges() {
      weeklyMiles = weeklyVehicleHours = weeklyFdtTotal = 0;
      
      for (Job job: jobs) {
         if (job.isInsideCharge()) {
            if (job.miles != null) {
               try {
                  weeklyMiles += Double.parseDouble(job.miles);
               }
               catch (NumberFormatException ex) {
                  System.out.println("Vehicle miles in job " + job + "is not "
                     + "a number.");
               }
            }
            
            weeklyVehicleHours += job.hours;
            
            if (job.fdt != null) {
               try {
                  weeklyFdtTotal += Double.parseDouble(job.fdt);
               }
               catch (NumberFormatException ex) {
                  System.out.println("FDT in job " + job + "is not "
                     + "a number.");
               }
            }
         }
      }
   }
   
   public void prepareJCA() throws JCAException, WriteException {
      JCAColumn.prepareJCA(sheet);
   }
   
   public void fillJCA() throws JCAException {
      int jobRow;
      jxl.write.Number hours;
      jxl.write.Number miles;
      jxl.write.Number vHours;
      jxl.write.Number fdt;
      jxl.write.Label date;
      
      Cell searchCell;
      int searchRow;
      int searchCol;
      int vHoursRow;
      int milesRow;
      int fdtRow;
      int weekRow;
      
      jxl.format.CellFormat format;
      
      for (Job job : jobs) {
         if (job.isNormal()) {
            jobRow = findJobRow(job);
            
            if (jobRow == -1)
               failedJobs.add(job);
            else {
               try {
                  output("   Putting " + job + " at row " + jobRow + " in " + this);
                  format = sheet.getCell(CurrentWeekColumn, jobRow).getCellFormat();
                  hours = new jxl.write.Number(CurrentWeekColumn, jobRow, job.hours, format);
                  sheet.addCell(hours);
               }
               catch (Exception ex) {
                  System.out.println(job + " failed to write to JCA");
                  failedJobs.add(job);
               }
            }
         }
         else if (job.isReimbursableCharge()) {
            failedJobs.add(job);
         }
      }
      
      calculateInsideCharges();
      
      searchCell = sheet.findCell(IChargesLabel);
      if (searchCell == null)
         throw new JCAException("Could not find \"INSIDE CHGS\" label.");      
      searchRow = searchCell.getRow();
      searchCol = searchCell.getColumn();
      
      searchCell = sheet.findCell(VHoursLabel, 0, searchRow, 10, searchRow + 10, false);
      if (searchCell == null)
         throw new JCAException("Could not find \"VEHICLE (HRS)\" label.");
      vHoursRow = searchCell.getRow();
      
      searchCell = sheet.findCell(MilesLabel, 0, searchRow, 10, searchRow + 10, false);
      if (searchCell == null)
         throw new JCAException("Could not find \"MILES\" label.");
      milesRow = searchCell.getRow();
      
      searchCell = sheet.findCell(FDTLabel, 0, searchRow, 10, searchRow + 10, false);
      if (searchCell == null)
         throw new JCAException("Could not find \"FDT TEST\" label.");
      fdtRow = searchCell.getRow();
      
      searchCell = sheet.findCell(WeekLabel);
      if (searchCell == null)
         throw new JCAException("Could not find \"WEEK ENDING DATE:\" label.");      
      weekRow = searchCell.getRow();
      
      try {
         if (weeklyVehicleHours != 0) {
            format = sheet.getCell(CurrentWeekColumn, vHoursRow).getCellFormat();
            vHours = new jxl.write.Number(CurrentWeekColumn, vHoursRow, weeklyVehicleHours, format);
            sheet.addCell(vHours);
         }
         
         if (weeklyMiles != 0) {
            format = sheet.getCell(CurrentWeekColumn, milesRow).getCellFormat();
            miles = new jxl.write.Number(CurrentWeekColumn, milesRow, weeklyMiles, format);
            sheet.addCell(miles);
         }
         
         if (weeklyFdtTotal != 0) {
            format = sheet.getCell(CurrentWeekColumn, fdtRow).getCellFormat();
            fdt = new jxl.write.Number(CurrentWeekColumn, fdtRow, weeklyFdtTotal, format);
            sheet.addCell(fdt);
         }
         
         format = sheet.getCell(CurrentWeekColumn, weekRow).getCellFormat();
         date = new Label(CurrentWeekColumn, weekRow, this.week, format);
         sheet.addCell(date);
      }
      catch (Exception ex) {
         System.out.println("Caught " + ex + " when adding inside charges to jca " + this);
      }
   }
   
   public void writeJCA() {      
      try {
         jcaWorkbook.write();
         jcaWorkbook.close();
      }
      catch (Exception ex) {
         System.out.println("Failed to write JCA, caught " + ex);
      }
   }
   
   public String getJobNoStr(){
      return jobNoStr;
   }
   
   public ArrayList<Job> getJobs() {
      return jobs;
   }
   
   public ArrayList<Job> getFailedJobs() {
      return failedJobs;
   }
   
   public String toString() {
      return "JCA: " + jobNoStr;
   }

   public void close() {
      try {
         jcaWorkbook.close();
      }
      catch (Exception ex) {
         System.out.println("Could not close " + this);
      }
   }
   
   private void output(String msg) {
      JCABuilder.output(msg);
   }

}
