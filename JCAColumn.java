import jxl.*;
import jxl.write.*;
import java.io.File;
import java.io.IOException;
import jxl.read.biff.BiffException;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Point;

public class JCAColumn {
   public static final String PersonnelChargesStart = "PERSONNEL";
   public static final String InsideChargesStart = "INSIDE CHGS";
   public static final String ReimbursableChargesStart = "REIMB CHGS";
   public static final String lastRowTerminator = "Actual Budget Remaining";
   public static final String WeekEndDate = "WEEK ENDING DATE:";
   public static final String CopyStartLabel = "PERSONNEL";
   public static final String CopyEndLabel = "Actual Budget Remaining";
   public static final String TotalsStartLabel = "TOTAL LABOR";
   public static final String BillingsLabel = "Billings\\Retainer";
   public static final String WriteOffLabel = "Write Off's (On's are negative #)";
   public static final int StartOfDataColumn = 4;
   public static final int SumColumn = 2;

   public static void shiftChargesColumn(WritableSheet sheet, int col)
      throws WriteException, JCAException {
      WritableCell toCopy;
      WritableCell copy;
      String cellContents;
      Cell searchCell;
      
      int destination = col + 1;
      int curRow;
      int copyStart;
      int copyEnd;

      searchCell = sheet.findCell(CopyStartLabel);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"PERSONNEL\" heading.");
      copyStart = searchCell.getRow();
      
      searchCell = sheet.findCell(CopyEndLabel);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"Actual Budget Remaining\" heading.");
      copyEnd = searchCell.getRow();
            
      for (curRow = copyStart - 1; curRow <= copyEnd; curRow++) {
         toCopy = sheet.getWritableCell(col, curRow);
         copy = toCopy.copyTo(destination, curRow);
         sheet.addCell(copy);
      } 
   }

   public static void shiftAllCharges(WritableSheet sheet) throws JCAException {
      int endCol;
      int startCol;
      Cell searchCell;
      
      searchCell = sheet.findCell(WeekEndDate);
      
      if (searchCell == null)
         throw new JCAException("Could not locate the \"Week Ending Date:\" heading.");
      startCol = searchCell.getColumn() + 1;
      
      endCol = sheet.getColumns() - 1;
      
      try {
         for (int curCol = endCol; curCol >= startCol; curCol--) {
            shiftChargesColumn(sheet, curCol);
         }
      }
      catch (WriteException ex) {
         System.out.println("Caught " + ex + " when shifting charges.");
      }
   }
   
   public static void clearNewWeekColumn(WritableSheet sheet)
      throws WriteException, JCAException {
      jxl.write.Blank blankCell;
      WritableCell cell;
      Cell searchCell;
      
      int curRow;
      int pChargesStart;
      int pChargesEnd;
      int iChargesStart;
      int iChargesEnd;
      int rChargesStart;
      int rChargesEnd;
      
      jxl.format.CellFormat format;
            
      searchCell = sheet.findCell(PersonnelChargesStart);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"PERSONNEL\" heading.");
      pChargesStart = searchCell.getRow();
      
      searchCell = sheet.findCell(InsideChargesStart);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"INSIDE CHGS\" heading.");
      pChargesEnd = iChargesStart = searchCell.getRow();
      
      searchCell = sheet.findCell(ReimbursableChargesStart);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"REIMB CHGS\" heading.");
      iChargesEnd = rChargesStart = searchCell.getRow();
      
      searchCell = sheet.findCell(WeekEndDate);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"Week Ending Date:\" heading.");
      rChargesEnd = searchCell.getRow();
      
      for (curRow = pChargesStart + 1; curRow < pChargesEnd; curRow++) {
         format = sheet.getCell(StartOfDataColumn, curRow).getCellFormat();
         blankCell = new jxl.write.Blank(StartOfDataColumn, curRow, format);
         sheet.addCell(blankCell);
      }  
      
      for (curRow = iChargesStart + 1; curRow < iChargesEnd; curRow++) {
         format = sheet.getCell(StartOfDataColumn, curRow).getCellFormat();
         blankCell = new jxl.write.Blank(StartOfDataColumn, curRow, format);
         sheet.addCell(blankCell);
      }  
      
      for (curRow = rChargesStart + 1; curRow <= rChargesEnd; curRow++) {
         format = sheet.getCell(StartOfDataColumn, curRow).getCellFormat();
         blankCell = new jxl.write.Blank(StartOfDataColumn, curRow, format);
         sheet.addCell(blankCell);
      }  
   }
   
   public static void updateFormulas(WritableSheet sheet) throws JCAException {
      jxl.write.Formula newFormulaCell;
      String oldFormula;
      String newFormula;
      jxl.format.CellFormat standardFormat;
      
      Cell searchCell;
      
      int curRow;
      int pChargesStart;
      int pChargesEnd;
      int iChargesStart;
      int iChargesEnd;
      int rChargesStart;
      int rChargesEnd;
      int totalsStart;
      int totalsEnd;
      int billingsRow;
      int writeOffRow;
      
      searchCell = sheet.findCell(PersonnelChargesStart);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"PERSONNEL\" heading.");
      pChargesStart = searchCell.getRow();
      
      searchCell = sheet.findCell(InsideChargesStart);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"INSIDE CHGS\" heading.");
      pChargesEnd = iChargesStart = searchCell.getRow();
      
      searchCell = sheet.findCell(ReimbursableChargesStart);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"REIMB CHGS\" heading.");
      iChargesEnd = rChargesStart = searchCell.getRow();
      
      searchCell = sheet.findCell(WeekEndDate);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"Week Ending Date:\" heading.");
      rChargesEnd = searchCell.getRow();
      
      searchCell = sheet.findCell(TotalsStartLabel);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"TOTAL LABOR\" heading.");
      totalsStart = searchCell.getRow();
      totalsEnd = totalsStart + 3;
      
      searchCell = sheet.findCell(BillingsLabel);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"Billings\\Retainer\" heading.");
      billingsRow = searchCell.getRow();
      
      searchCell = sheet.findCell(WriteOffLabel);
      if (searchCell == null)
         throw new JCAException("Could not locate the \"Write Off's (On's are negative #)\" heading.");
      writeOffRow = searchCell.getRow();
      
      try {
         for (curRow = pChargesStart + 1; curRow < pChargesEnd; curRow++) {
            int row = curRow + 1;
            standardFormat = sheet.getCell(SumColumn,curRow).getCellFormat();
            newFormulaCell = new jxl.write.Formula(SumColumn, curRow, "SUM(E"+row+":GH"+row+")", standardFormat);
            sheet.addCell(newFormulaCell);
         }  
               
         for (curRow = iChargesStart + 1; curRow < iChargesEnd; curRow++) {
            int row = curRow + 1;
            standardFormat = sheet.getCell(SumColumn,curRow).getCellFormat();
            newFormulaCell = new jxl.write.Formula(SumColumn, curRow, "SUM(E"+row+":GH"+row+")", standardFormat);
            sheet.addCell(newFormulaCell);
         }  
         
         for (curRow = rChargesStart + 1; curRow < rChargesEnd; curRow++) {
            int row = curRow + 1;
            standardFormat = sheet.getCell(SumColumn,curRow).getCellFormat();
            newFormulaCell = new jxl.write.Formula(SumColumn, curRow, "SUM(E"+row+":GH"+row+")", standardFormat);
            sheet.addCell(newFormulaCell);
         }  
         
         for (curRow = totalsStart; curRow < totalsEnd; curRow++) {
            int row = curRow + 1;
            standardFormat = sheet.getCell(SumColumn + 1, curRow).getCellFormat();
            newFormulaCell = new jxl.write.Formula(SumColumn + 1, curRow, "SUM(E"+row+":GH"+row+")", standardFormat);
            sheet.addCell(newFormulaCell);
         }  
         
         int row = billingsRow + 1;
         standardFormat = sheet.getCell(SumColumn + 1, billingsRow).getCellFormat();
         newFormulaCell = new jxl.write.Formula(SumColumn + 1, billingsRow, "SUM(E"+row+":GH"+row+")", standardFormat);
         sheet.addCell(newFormulaCell);
         
         row = writeOffRow + 1;
         standardFormat = sheet.getCell(SumColumn + 1, writeOffRow).getCellFormat();
         newFormulaCell = new jxl.write.Formula(SumColumn + 1, writeOffRow, "SUM(E"+row+":GH"+row+")", standardFormat);
         sheet.addCell(newFormulaCell);
      }
      catch (Exception ex) {
         System.out.println("Formula Update failed.");
      }
   }
   
   public static void prepareJCA(WritableSheet sheet) throws JCAException, WriteException {
      shiftAllCharges(sheet);
      clearNewWeekColumn(sheet);
      updateFormulas(sheet);
   }
}
