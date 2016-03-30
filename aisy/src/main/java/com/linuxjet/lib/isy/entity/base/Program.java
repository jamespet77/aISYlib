/*2016 - jamespet */

package com.linuxjet.lib.isy.entity.base;

public class Program {

      private String FID;
      private String Name;
      private String Parent_ID = "0";
      private String Status = "unknown";
      private Boolean Is_Folder = false;
      private Boolean Enabled = false;
      private Boolean RunAtStartup = false;
      private String Running = "idle";
      private String LastRunTime;
  private String LastFinishTime;
  private String NextSceduledRunTime;

  public static class RUN {
    public static int IDLE = 1;
    public static int THEN = 2;
    public static int ELSE = 3;
  }

  public static String[] ProgramRUN = {
      "",
      "IDLE",
      "THEN",
      "ELSE"
  };


  public Program() {

  }

  public Program(Program node) {
    setFID(node.getFID());
    setName(node.getName());
    setParent_ID(node.getParent_ID());
    setStatus(node.getStatus());
    setIs_Folder(node.getIs_Folder());
    setEnabled(node.getEnabled());
    setRunAtStartup(node.getRunAtStartup());
    setRunning(node.getRunning());
    setLastRunTime(node.getLastRunTime());
    setLastFinishTime(node.getLastFinishTime());
    setNextSceduledRunTime(node.getNextSceduledRunTime());
  }

  public Boolean getEnabled() {
    return Enabled;
  }

  public void setEnabled(Boolean enabled) {
    Enabled = enabled;
  }

  public String getFID() {
    return FID;
  }

  public void setFID(String FID) {
    this.FID = FID;
  }

  public Boolean getIs_Folder() {
    return Is_Folder;
  }

  public void setIs_Folder(Boolean is_Folder) {
    Is_Folder = is_Folder;
  }

  public String getLastFinishTime() {
    return LastFinishTime;
  }

  public void setLastFinishTime(String lastFinishTime) {
    LastFinishTime = lastFinishTime;
  }

  public String getNextSceduledRunTime() {
    return NextSceduledRunTime;
  }

  public void setNextSceduledRunTime(String nextSceduledRunTime) {
    NextSceduledRunTime = nextSceduledRunTime;
  }

  public String getLastRunTime() {
    return LastRunTime;
  }

  public void setLastRunTime(String lastRunTime) {
    LastRunTime = lastRunTime;
  }

  public String getName() {
    return Name;
  }

  public void setName(String name) {
    Name = name;
  }

  public String getParent_ID() {
    return Parent_ID;
  }

  public void setParent_ID(String parent_ID) {
    Parent_ID = parent_ID;
  }

  public Boolean getRunAtStartup() {
    return RunAtStartup;
  }

  public void setRunAtStartup(Boolean runAtStartup) {
    RunAtStartup = runAtStartup;
  }

  public String getRunning() {
    return Running;
  }

  public void setRunning(String running) {
    Running = running;
  }

  public String getStatus() {
    return Status;
  }

  public void setStatus(String status) {
    Status = status;
  }
}
