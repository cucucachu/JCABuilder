import jxl.*;
import jxl.write.*;
import java.io.File;
import java.io.IOException;
import jxl.read.biff.BiffException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

public class Recap {
   public static final int nameCol = 0;
   public static final int classCodeCol = 1;
   public static final int jobNoCol = 2;
   public static final int initialsCol = 3;
   public static final int hoursCol = 4;
   public static final int milesCol = 5;
   public static final int fdtCol = 6;
   public static final int otherCol = 7;
   public static final int typeCol = 8;
   public static final String recapEnd = "End of Recap";

   ArrayList<Job> allJobs;
   ArrayList<Job> badJobs;
   WritableWorkbook recapWorkbook;
   Workbook readWorkbook;
   WritableSheet sheet;
   
   
   public Recap(String filePath) throws RecapFormatException{
      allJobs = new ArrayList<Job>();
      try {
         readRecap(filePath);
      }
      catch (RecapFormatException ex) {
         throw ex;
      }
   }
   
   public Recap(ArrayList<Job> allJobs) {
      this.allJobs = allJobs; 
      badJobs = null;
   }
   
   public void makeRecap(String outFilePath) {
      try {
         recapWorkbook = Workbook.createWorkbook(new File(outFilePath));
         sheet = recapWorkbook.createSheet("Recap", 0);
   
         Label nameLabel = new Label(nameCol, 0, "Project Name");
         Label classCodeLabel = new Label(classCodeCol, 0, "ClassCode/P#");
         Label jobNoLabel = new Label(jobNoCol, 0, "Job Number");
         Label initialsLabel = new Label(initialsCol, 0, "Employee Initials");
         Label hoursLabel = new Label(hoursCol, 0, "Hours");
         Label milesLabel = new Label(milesCol, 0, "Miles");
         Label fdtLabel = new Label(fdtCol, 0, "FDT");
         Label otherLabel = new Label(otherCol, 0, "Other");
         Label typeLabel = new Label(typeCol, 0, "Type");
         Label endCell;
         
         jxl.write.Number hours;
         jxl.write.Number miles;
         jxl.write.Number fdt;
         
         int curRow = 1;
         
         sheet.addCell(nameLabel);
         sheet.addCell(classCodeLabel);
         sheet.addCell(jobNoLabel);
         sheet.addCell(initialsLabel);
         sheet.addCell(hoursLabel);
         sheet.addCell(milesLabel);
         sheet.addCell(fdtLabel);
         sheet.addCell(otherLabel);
         sheet.addCell(typeLabel);
         Collections.sort(allJobs);
         
         for (Job job : allJobs) {
            if (job.jobName != null) {
               nameLabel = new Label(nameCol, curRow, job.jobName);
               sheet.addCell(nameLabel);
            }
            
            jobNoLabel = new Label(jobNoCol, curRow, job.jobNoStr);
            sheet.addCell(jobNoLabel);
            
            if (job.initials != null) {
               initialsLabel = new Label(initialsCol, curRow, job.initials);   
               sheet.addCell(initialsLabel);
            }
            
            if (job.hours != 0) {
               hours = new jxl.write.Number(hoursCol, curRow, job.hours);
               sheet.addCell(hours);
            }
            
            if (job.prevWage == null)
               classCodeLabel = new Label(classCodeCol, curRow, job.classCode);
            else
               classCodeLabel = new Label(classCodeCol, curRow, job.prevWage);
            
            sheet.addCell(classCodeLabel);
            
            if (job.type != null) {
               typeLabel = new Label(typeCol, curRow, job.type);
               sheet.addCell(typeLabel);
            }
            
            if (job.miles != null) {
               miles = new jxl.write.Number(milesCol, curRow, Double.parseDouble(job.miles));
               sheet.addCell(miles);
            }
            
            if (job.fdt != null && Double.parseDouble(job.fdt) != 0) {
               fdt = new jxl.write.Number(fdtCol, curRow, Double.parseDouble(job.fdt));
               sheet.addCell(fdt);
            }
            
            if (job.other != null) {
               otherLabel = new Label(otherCol, curRow, job.other);
               sheet.addCell(otherLabel);
            }
            
            curRow++;
         }
         
         endCell = new Label(0, curRow, recapEnd);
         sheet.addCell(endCell);
      }
      catch (Exception ex) {
         System.out.println("Caught " + ex + " when making recap.");
      }
   }
   
   private void readRecap(String filePath) throws RecapFormatException{
      Sheet sheet;
      Cell jobNameCell;
      Cell jobNoCell;
      Cell classCodeCell;
      Cell initialsCell;
      Cell hoursCell;
      Cell milesCell;
      Cell fdtCell;
      Cell otherCell;
      Cell typeCell;
         
      String jobName;
      String jobNoStr;
      String initials;
      String classCode; 
      String hours;
      String miles;
      String fdt;
      String other;
      String type;
      String prevWage;  
       
      int curRow;
      
      badJobs = new ArrayList<Job>();
            
      try {
         File file = new File(filePath);
         
         if (!file.exists()) {
            throw new RecapFormatException("The recap file " + filePath 
               + " does not exist.");
         }
         
         readWorkbook = Workbook.getWorkbook(file);
         sheet = readWorkbook.getSheet(0);
         
         if (sheet.findCell(recapEnd) == null)
            throw new RecapFormatException("Could not find the end of recap.");
         
         curRow = 1;
         
         jobNameCell = sheet.getCell(nameCol, curRow);
         jobNoCell = sheet.getCell(jobNoCol, curRow);
         classCodeCell = sheet.getCell(classCodeCol, curRow);
         initialsCell = sheet.getCell(initialsCol, curRow);
         hoursCell = sheet.getCell(hoursCol, curRow);
         milesCell = sheet.getCell(milesCol, curRow);
         fdtCell = sheet.getCell(fdtCol, curRow);
         otherCell = sheet.getCell(otherCol, curRow);
         typeCell = sheet.getCell(typeCol, curRow);
         
         while (sheet.getCell(0,curRow).getContents().compareTo(recapEnd) != 0) {
            jobName = jobNameCell.getContents();
            jobNoStr = jobNoCell.getContents();
            initials = initialsCell.getContents();
            classCode = classCodeCell.getContents();
            hours = hoursCell.getContents();
            miles = milesCell.getContents();
            fdt = fdtCell.getContents();
            other = otherCell.getContents();
            type = typeCell.getContents();
            
            if (classCode.compareTo("P3") == 0 || classCode.compareTo("P4") == 0) {
               prevWage = classCode;
               classCode = "";  
            }
            else {
               prevWage = "";
            }
            
            if (hours == "")
               hours = "0";
            
            if (jobNoStr.compareTo("") == 0 || initials.compareTo("") == 0 
               || classCode.compareTo("") == 0 && prevWage.compareTo("") == 0) {
               badJobs.add(new Job(jobName, jobNoStr, initials, classCode, hours,
                     miles, fdt, other, type, prevWage));
            }
            else
               allJobs.add(new Job(jobName, jobNoStr, initials, classCode, hours,
                     miles, fdt, other, type, prevWage));
                     
            curRow++;
            
            jobNameCell = sheet.getCell(nameCol, curRow);
            jobNoCell = sheet.getCell(jobNoCol, curRow);
            classCodeCell = sheet.getCell(classCodeCol, curRow);
            initialsCell = sheet.getCell(initialsCol, curRow);
            hoursCell = sheet.getCell(hoursCol, curRow);
            milesCell = sheet.getCell(milesCol, curRow);
            fdtCell = sheet.getCell(fdtCol, curRow);
            otherCell = sheet.getCell(otherCol, curRow);
            typeCell = sheet.getCell(typeCol, curRow);
         }
      }
      catch (RecapFormatException ex) {
         throw ex;
      }
      catch (Exception ex) {
         System.out.println("Failed to read recap. " + ex);
      }
   }
   
   public void writeRecap() {
      try {
         recapWorkbook.write();
         recapWorkbook.close();
      }
      catch (IOException|WriteException ex) {
         System.out.println("Caught " + ex + "when writing recap.");
      }
   }
   
   public void printJobs() {
      System.out.println("All Jobs:");
      for (Job job: allJobs) 
         System.out.println("   " + job);
      
      System.out.println("\nBad Jobs:");
      for (Job job: badJobs)
         System.out.println("   " + job);
   }
   
   public ArrayList<Job> getJobs() {
      return allJobs;
   }
   
   public ArrayList<Job> getBadJobs() {
      return badJobs;
   }
}
