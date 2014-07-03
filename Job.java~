public class Job implements Comparable<Job> {
   String jobName;
   String jobNoStr;
   String initials;
   String classCode;
   double hours;
   String miles;
   String fdt;
   String other;
   String type;
   String prevWage;
   
   
   public Job(String jobName, String jobNoStr, String initials, String classCode,
      String hours, String miles, String fdt, String other, String type,
      String prevWage) {
      
      this.jobName = jobName;
      this.jobNoStr = jobNoStr;
      this.initials = initials.toUpperCase();
      this.classCode = classCode;
      this.hours = Double.parseDouble(hours);
      this.miles = miles;
      this.fdt = fdt;
      this.other = other;
      this.type = type;
      this.prevWage = prevWage;
      
      emptyStringsToNull();
   }
   
   public int compareTo(Job other) {
      double jobNoThis;
      double jobNoOther;
      
      try {
         jobNoThis = Double.parseDouble(this.jobNoStr);
         jobNoOther = Double.parseDouble(other.jobNoStr);
         
         if (jobNoThis > jobNoOther)
            return 10;
         else if (jobNoThis < jobNoOther)
            return -10;
         else 
            return this.initials.compareTo(other.initials);
      }
      catch (NumberFormatException ex) {
         return this.jobNoStr.compareTo(other.jobNoStr);
      }
   }
   
   public String toString() {
      return jobName + ": " + jobNoStr + " " + initials;
   }
   
   private void emptyStringsToNull() {
      if (jobName != null && jobName.isEmpty())
         jobName = null;
      if (initials != null && initials.isEmpty())
         initials = null;
      if (classCode != null && classCode.isEmpty())
         classCode = null;
      if (miles != null && miles.isEmpty())
         miles = null;
      if (fdt != null && fdt.isEmpty())
         fdt = null;
      if (other != null && other.isEmpty())
         other = null;
      if (type != null && type.isEmpty())
         type = null;
      if (prevWage != null && prevWage.isEmpty())
         prevWage = null;
   }
   
   public boolean isVehicle() {
      if (initials.contains("-v") || initials.contains("-V"))
         return true;
      if (miles != null)
         return true;
      return false;
   }
   
   public boolean isNormal() {
      if (miles == null && fdt == null && other == null && type == null)
         return true;
      return false;
   }
   
   public boolean isInsideCharge() {
      if (isVehicle() || fdt != null)
         return true;
      return false;
   }
   
   public boolean isReimbursableCharge() {
      if (other != null || type != null)
         return true;
      else
         return false;
   }
}
