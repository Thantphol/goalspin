package com.example.goalspin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DailyQuestActivity extends AppCompatActivity {

    private TextView tvTicketCount;
    private Button btnClaim1, btnClaim2, btnClaim3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_quest);

        updateBackground();
        updatePet();
        updateTheme();

        tvTicketCount = findViewById(R.id.tvTicketCount);
        updateTicketDisplay();

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // ผูกปุ่ม
        btnClaim1 = findViewById(R.id.btnClaim1);
        btnClaim2 = findViewById(R.id.btnClaim2);
        btnClaim3 = findViewById(R.id.btnClaim3);

        // --- ตั้งค่า Logic แต่ละ Quest ---

        // Quest 1: Login Once a Day (เงื่อนไข: เข้าหน้านี้ได้ก็คือ Login แล้ว)
        setupQuestButton(btnClaim1, "Q1_CLAIMED", true);

        // Quest 2: Add 3 Tasks (เงื่อนไข: MainActivity.questAddCount >= 3)
        setupQuestButton(btnClaim2, "Q2_CLAIMED", MainActivity.questAddCount >= 3);

        // Quest 3: Complete 3 Tasks (เงื่อนไข: MainActivity.questDoneCount >= 3)
        setupQuestButton(btnClaim3, "Q3_CLAIMED", MainActivity.questDoneCount >= 3);
    }

    // ฟังก์ชันจัดการสถานะปุ่ม (Claim, Complete, Disable)
    private void setupQuestButton(Button button, String prefsKey, boolean isConditionMet) {
        SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", Context.MODE_PRIVATE);
        boolean isClaimed = prefs.getBoolean(prefsKey, false);

        if (isClaimed) {
            // รับไปแล้ว -> ขึ้นว่า COMPLETE
            setButtonState(button, "COMPLETE", Color.GRAY, false);
        } else if (isConditionMet) {
            // เงื่อนไขผ่าน แต่ยังไม่รับ -> ปุ่ม CLAIM ปกติ
            setButtonState(button, "CLAIM", Color.WHITE, true);

            // ตั้ง Listener ให้กดรับได้
            button.setOnClickListener(v -> {
                MainActivity.globalTicketCount++;
                updateTicketDisplay();

                // บันทึกว่ารับแล้ว
                prefs.edit().putBoolean(prefsKey, true).apply();

                // เปลี่ยนสถานะเป็น Complete
                setButtonState(button, "COMPLETE", Color.GRAY, false);
                Toast.makeText(this, "Quest Completed! +1 Ticket", Toast.LENGTH_SHORT).show();
            });
        } else {
            // เงื่อนไขไม่ผ่าน -> ปุ่ม CLAIM สีเทา กดไม่ได้
            setButtonState(button, "CLAIM", Color.GRAY, false);
        }
    }

    private void setButtonState(Button button, String text, int color, boolean enabled) {
        button.setText(text);
        button.setTextColor(color);
        button.setEnabled(enabled);

        // ถ้าปุ่มเป็นสถานะ Enabled ให้ใช้พื้นหลังปกติ ถ้า Disabled ให้ดูจางๆ (Optional)
        if (enabled) {
            button.setAlpha(1.0f);
            // button.setBackgroundResource(R.drawable.inventory_button_bg);
        } else {
            button.setAlpha(0.5f); // ทำให้ดูจางลงว่าเป็นปุ่มที่กดไม่ได้
        }

        Button btnInventoryMain = findViewById(R.id.btnInventory); // ต้องตรงกับ ID ใน activity_main.xml (ดูแล้วยังไม่มี ID นี้ในไฟล์ xml ที่ให้มา แต่ถ้าเพิ่มแล้วก็ใช้ได้)
        // หมายเหตุ: ในไฟล์ XML ล่าสุดที่ให้มา ปุ่ม Inventory ไม่มี ID ดังนั้นต้องไปเติม android:id="@+id/btnInventory" ใน activity_main.xml ด้วยนะครับ
        // แต่ถ้ามีแล้ว หรือจะใช้โค้ดนี้เตรียมไว้ก่อนก็ได้ครับ
        // ในตัวอย่างเก่าผมใช้ชื่อ btnInventory แต่ในไฟล์ xml ล่าสุดไม่ได้ระบุ ID ไว้
        // เพื่อความชัวร์ ผมใส่ null check ไว้ให้ครับ
        if (btnInventoryMain != null) {
            btnInventoryMain.setOnClickListener(v -> {
                Intent intent = new Intent(DailyQuestActivity.this, InventoryActivity.class);
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

    private void updateTicketDisplay() {
        tvTicketCount.setText(String.valueOf(MainActivity.globalTicketCount));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBackground();
        updatePet();
        updateTheme();
        // โค้ดเปลี่ยนพื้นหลัง
        SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", MODE_PRIVATE);
        int bgResId = prefs.getInt("CURRENT_BACKGROUND", R.drawable.background_default);

        // หา ID ของ Layout หน้านี้
        android.view.View root = findViewById(R.id.rootLayout); // หรือ id ที่คุณตั้ง
        if (root != null) {
            root.setBackgroundResource(bgResId);
        }
    }

    // เพิ่มฟังก์ชัน updatePet
    private void updatePet() {
        SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", MODE_PRIVATE);
        int petResId = prefs.getInt("CURRENT_PET", 0);

        ImageView ivQuestPet = findViewById(R.id.ivQuestPet);
        if (ivQuestPet != null) {
            if (petResId != 0) {
                ivQuestPet.setImageResource(petResId);
                ivQuestPet.setVisibility(View.VISIBLE);
            } else {
                ivQuestPet.setVisibility(View.GONE);
            }
        }
    }

    // --- แก้ไขฟังก์ชันนี้ ---
    private void updateTheme() {
        SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", MODE_PRIVATE);

        int headerBgRes = prefs.getInt("CURRENT_HEADER_BG", R.drawable.header_default);

        // หา View ที่เป็น Header Container
        View headerContainer = findViewById(R.id.headerContainer);
        if (headerContainer != null) {
            headerContainer.setBackgroundResource(headerBgRes);
        }

        int themeFrameRes = prefs.getInt("CURRENT_THEME_FRAME", 0);

        // +++ ดึงชื่อ Theme มา (ถ้าไม่มีให้ใช้ "Daily Quests" เป็นค่าเริ่มต้น) +++
        String themeTitle = prefs.getString("CURRENT_THEME_TITLE", "Daily Quests");

        TextView tvQuestTitleFrame = findViewById(R.id.tvQuestTitleFrame);

        if (tvQuestTitleFrame != null) {
            if (themeFrameRes != 0) {
                tvQuestTitleFrame.setVisibility(View.VISIBLE);
                tvQuestTitleFrame.setBackgroundResource(themeFrameRes);

                // +++ ตั้งค่าข้อความให้ตรงกับที่บันทึกไว้ +++
                tvQuestTitleFrame.setText(themeTitle);
            } else {
                tvQuestTitleFrame.setVisibility(View.GONE);
            }
        }
    }


        // เพิ่มฟังก์ชัน updateBackground (ถ้ายังไม่มี)
    private void updateBackground() {
        SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", MODE_PRIVATE);
        int bgResId = prefs.getInt("CURRENT_BACKGROUND", R.drawable.background_default);
        // ต้องไปเพิ่ม ID ให้ root layout ใน xml ก่อนนะครับ (เช่น android:id="@+id/rootLayout")
        // View root = findViewById(R.id.rootLayout);
        // if (root != null) root.setBackgroundResource(bgResId);
    }

}