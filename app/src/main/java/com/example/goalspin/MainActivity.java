package com.example.goalspin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static int globalTicketCount = 2;

    // ตัวแปรนับจำนวนการกระทำสำหรับ Quest (static เพื่อให้หน้า Quest เรียกดูได้ง่าย)
    public static int questAddCount = 0;   // สำหรับ Quest 2 (Add 3 task)
    public static int questDoneCount = 0;  // สำหรับ Quest 3 (Complete 3 task)

    private TextView tvTicketCount;
    private List<Task> tasks;
    private TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // เรียกฟังก์ชันอัปเดตพื้นหลังครั้งแรก
        updateBackground();
        updatePet();

        tvTicketCount = findViewById(R.id.tvTicketCount);

        tasks = new ArrayList<>();

        checkAndResetDaily();

        if (tasks.isEmpty()) {
            tasks.add(new Task("", false));
        }

        updateTicketDisplay();

        RecyclerView rvTasks = findViewById(R.id.rvTasks);

        // ส่ง Listener เข้าไปใน Adapter เพื่อนับแต้ม
        adapter = new TodoAdapter(tasks, new TodoAdapter.OnQuestActionListener() {
            @Override
            public void onTaskSaved() {
                // Quest 2: เมื่อกด Save -> บวกแต้ม Add
                questAddCount++;
                saveTaskStatus(); // บันทึกลงเครื่องทันที
            }

            @Override
            public void onTaskCompleted() {
                // Quest 3: เมื่อกด Check -> บวกแต้ม Done
                questDoneCount++;
                saveTaskStatus(); // บันทึกลงเครื่องทันที
            }
        });

        updateTheme();

        rvTasks.setAdapter(adapter);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        rvTasks.setAdapter(adapter);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        ImageView btnAddNewRow = findViewById(R.id.btnAddNewRow);
        btnAddNewRow.setOnClickListener(v -> {
            tasks.add(new Task("", false));
            adapter.notifyItemInserted(tasks.size() - 1);
            rvTasks.scrollToPosition(tasks.size() - 1);
        });

        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DailyQuestActivity.class);
            startActivity(intent);
        });

        // ปุ่ม INVENTORY
        Button btnInventoryMain = findViewById(R.id.btnInventory); // ต้องตรงกับ ID ใน activity_main.xml (ดูแล้วยังไม่มี ID นี้ในไฟล์ xml ที่ให้มา แต่ถ้าเพิ่มแล้วก็ใช้ได้)
        // หมายเหตุ: ในไฟล์ XML ล่าสุดที่ให้มา ปุ่ม Inventory ไม่มี ID ดังนั้นต้องไปเติม android:id="@+id/btnInventory" ใน activity_main.xml ด้วยนะครับ
        // แต่ถ้ามีแล้ว หรือจะใช้โค้ดนี้เตรียมไว้ก่อนก็ได้ครับ
        // ในตัวอย่างเก่าผมใช้ชื่อ btnInventory แต่ในไฟล์ xml ล่าสุดไม่ได้ระบุ ID ไว้
        // เพื่อความชัวร์ ผมใส่ null check ไว้ให้ครับ
        if (btnInventoryMain != null) {
            btnInventoryMain.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
                startActivity(intent);
            });
        } else {
            // ถ้าหาปุ่มไม่เจอ อาจจะเพราะยังไม่ได้ตั้ง ID ใน layout
            // ลองหาด้วยวิธีอื่นหรือข้ามไปก่อน
            // แนะนำให้ไปเติม android:id="@+id/btnInventory" ที่ปุ่ม Inventory ใน activity_main.xml ครับ
            // <androidx.appcompat.widget.AppCompatButton android:id="@+id/btnInventory" ... />

            // *แก้ขัด* ถ้าใน layout เป็นปุ่มแรกใน footerLayout อาจจะหาด้วยวิธีอื่นได้แต่วิธีที่ถูกคือเติม ID ครับ
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTicketDisplay();
        saveTaskStatus();
        updateBackground();
        updatePet();
        updateTheme();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveTaskStatus();
    }

    // --- เพิ่มฟังก์ชันใหม่สำหรับเปลี่ยนพื้นหลัง ---
    private void updateBackground() {
        // 1. อ่านค่าจาก SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", MODE_PRIVATE);

        // ถ้าไม่มีค่าบันทึกไว้ ให้ใช้ bg_main_screen เป็นค่าเริ่มต้น
        int bgResId = prefs.getInt("CURRENT_BACKGROUND", R.drawable.background_default);

        // 2. หาตัว Layout หลัก (ต้องมี android:id="@+id/main" ใน xml)
        View rootLayout = findViewById(R.id.main);

        // 3. เปลี่ยนรูปพื้นหลัง
        if (rootLayout != null) {
            rootLayout.setBackgroundResource(bgResId);
        }
    }

    private void updatePet() {
        SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", MODE_PRIVATE);
        // อ่านค่า Pet ID (ถ้าไม่มีให้เป็น 0)
        int petResId = prefs.getInt("CURRENT_PET", 0);

        ImageView ivEquippedPet = findViewById(R.id.ivEquippedPet);
        if (ivEquippedPet != null) {
            if (petResId != 0) {
                // ถ้ามีสัตว์เลี้ยง -> โชว์รูป
                ivEquippedPet.setImageResource(petResId);
                ivEquippedPet.setVisibility(View.VISIBLE);
            } else {
                // ถ้าไม่มี -> ซ่อน
                ivEquippedPet.setVisibility(View.GONE);
            }
        }
    }

    // --- เพิ่มฟังก์ชันอัปเดตธีม ---
    private void updateTheme() {
        SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", MODE_PRIVATE);

        // 1. เปลี่ยนรูปพื้นหลังของ Header (ไม่ใช่ Profile)
        // ใช้ key "CURRENT_HEADER_BG" แทน (ถ้าไม่มีใช้ header_default)
        int headerBgRes = prefs.getInt("CURRENT_HEADER_BG", R.drawable.header_default);

        // หา View ที่เป็น Header Container
        View headerContainer = findViewById(R.id.headerContainer);
        if (headerContainer != null) {
            headerContainer.setBackgroundResource(headerBgRes);
        }

        // 2. เปลี่ยน Task Frame (เหมือนเดิม)
        int taskBgRes = prefs.getInt("CURRENT_TASK_BG", R.drawable.woodfame);
        if (adapter != null) {
            adapter.setTaskBackground(taskBgRes);
        }
    }


        private void updateTicketDisplay() {
        tvTicketCount.setText(String.valueOf(globalTicketCount));
    }

    private void checkAndResetDaily() {
        SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", Context.MODE_PRIVATE);
        String lastRunDate = prefs.getString("LAST_RUN_DATE", "");
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (!lastRunDate.equals(todayDate)) {
            // *** วันใหม่ -> รีเซ็ตทุกอย่าง ***
            resetAllTasks();

            // รีเซ็ต Quest Counters
            questAddCount = 0;
            questDoneCount = 0;

            // รีเซ็ตสถานะการรับรางวัลของ Quest (Key: Q1_CLAIMED, Q2_CLAIMED, Q3_CLAIMED)
            prefs.edit()
                    .putBoolean("Q1_CLAIMED", false)
                    .putBoolean("Q2_CLAIMED", false)
                    .putBoolean("Q3_CLAIMED", false)
                    .putString("LAST_RUN_DATE", todayDate)
                    .apply();

        } else {
            // วันเดิม -> โหลดข้อมูล
            loadTaskStatus();
        }
        globalTicketCount = prefs.getInt("TICKET_COUNT", 2);
    }

    private void resetAllTasks() {
        for (Task task : tasks) {
            task.setCompleted(false);
            task.setClaimed(false);
            task.setLocked(false);
            task.setText("");
        }
    }

    private void saveTaskStatus() {
        SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            String safeText = t.getText().replace("|", "").replace(",", "");
            sb.append(safeText).append("|")
                    .append(t.isCompleted()).append("|")
                    .append(t.isClaimed()).append("|")
                    .append(t.isLocked());
            if (i < tasks.size() - 1) {
                sb.append(",");
            }
        }
        editor.putString("TASK_DATA", sb.toString());
        editor.putInt("TICKET_COUNT", globalTicketCount);

        // บันทึกตัวนับ Quest
        editor.putInt("QUEST_ADD_COUNT", questAddCount);
        editor.putInt("QUEST_DONE_COUNT", questDoneCount);

        editor.apply();
    }

    private void loadTaskStatus() {
        SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", Context.MODE_PRIVATE);

        // โหลดตัวนับ Quest
        questAddCount = prefs.getInt("QUEST_ADD_COUNT", 0);
        questDoneCount = prefs.getInt("QUEST_DONE_COUNT", 0);

        String dataString = prefs.getString("TASK_DATA", "");
        if (!dataString.isEmpty()) {
            tasks.clear();
            String[] items = dataString.split(",");
            for (String item : items) {
                String[] parts = item.split("\\|");
                if (parts.length >= 2) {
                    String text = parts[0];
                    boolean completed = Boolean.parseBoolean(parts[1]);
                    Task t = new Task(text, completed);
                    if (parts.length > 2) t.setClaimed(Boolean.parseBoolean(parts[2]));
                    if (parts.length > 3) t.setLocked(Boolean.parseBoolean(parts[3]));
                    tasks.add(t);
                }
            }
        }
    }
}