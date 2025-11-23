package com.example.goalspin;

public class Task {
    private String text;
    private boolean isCompleted;
    private boolean isClaimed;
    private boolean isLocked; // เพิ่มตัวแปรนี้

    public Task(String text, boolean isCompleted) {
        this.text = text;
        this.isCompleted = isCompleted;
        this.isClaimed = false;
        this.isLocked = false; // ค่าเริ่มต้นคือยังไม่ล็อค (พิมพ์ได้)
    }

    // Getters and Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public boolean isClaimed() { return isClaimed; }
    public void setClaimed(boolean claimed) { isClaimed = claimed; }

    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }
}