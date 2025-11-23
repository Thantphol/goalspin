package com.example.goalspin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CollectionPortActivity extends AppCompatActivity {

    private int selectedItemType = 0;
    private View vSelectorPet, vSelectorBg, vSelectorFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_port);

        // Init Header
        TextView tvTicketCount = findViewById(R.id.tvTicketCount);
        if (tvTicketCount != null) {
            tvTicketCount.setText(String.valueOf(MainActivity.globalTicketCount));
        }

        Button btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Bind Views
        FrameLayout flItemPet = findViewById(R.id.flItemPet);
        FrameLayout flItemBg = findViewById(R.id.flItemBg);
        FrameLayout flItemFrame = findViewById(R.id.flItemFrame);

        vSelectorPet = findViewById(R.id.vSelectorPet);
        vSelectorBg = findViewById(R.id.vSelectorBg);
        vSelectorFrame = findViewById(R.id.vSelectorFrame);

        if (flItemPet != null) flItemPet.setOnClickListener(v -> selectItem(1));
        if (flItemBg != null) flItemBg.setOnClickListener(v -> selectItem(2));
        if (flItemFrame != null) flItemFrame.setOnClickListener(v -> selectItem(3));

        // ปุ่ม USE
        Button btnUse = findViewById(R.id.btnUse);
        if (btnUse != null) {
            btnUse.setOnClickListener(v -> {
                if (selectedItemType == 0) {
                    Toast.makeText(this, "Please select an item first", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                try {
                    if (selectedItemType == 2) {
                        // Background
                        editor.putInt("CURRENT_BACKGROUND", R.drawable.coastal_port);
                        editor.apply();

                        updateBackground();
                        Toast.makeText(this, "Background Changed!", Toast.LENGTH_SHORT).show();

                    } else if (selectedItemType == 1) {
                        // Pet
                        editor.putInt("CURRENT_PET", R.drawable.pygmy_puff);
                        editor.apply();
                        Toast.makeText(this, "Pet Equipped!", Toast.LENGTH_SHORT).show();

                    } else if (selectedItemType == 3) {
                        // Frame Theme
                        editor.putInt("CURRENT_HEADER_BG", R.drawable.header_port);
                        editor.putInt("CURRENT_TASK_BG", R.drawable.port_task);
                        editor.putInt("CURRENT_THEME_COLLECTION_FRAME", R.drawable.coastalport_frame);
                        editor.putInt("CURRENT_THEME_FRAME", R.drawable.port_frame);
                        editor.putString("CURRENT_THEME_TITLE", "Luminsea Pathfinder");
                        editor.apply();

                        updateHeader();
                        updateCollectionFrame(); // *** เพิ่มบรรทัดนี้: อัปเดตกรอบทันที ***

                        Toast.makeText(this, "Port Theme Equipped!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBackground();
        updateHeader();
        updateCollectionFrame(); // *** เพิ่มบรรทัดนี้: อัปเดตกรอบตอนเปิดหน้า ***
    }

    // ฟังก์ชันเปลี่ยนพื้นหลัง
    private void updateBackground() {
        try {
            SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", MODE_PRIVATE);
            int bgResId = prefs.getInt("CURRENT_BACKGROUND", R.drawable.background_default);
            View root = findViewById(R.id.portRoot);
            if (root != null) root.setBackgroundResource(bgResId);
        } catch (Exception e) {}
    }

    // ฟังก์ชันเปลี่ยน Header
    private void updateHeader() {
        try {
            SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", MODE_PRIVATE);
            int headerBgRes = prefs.getInt("CURRENT_HEADER_BG", R.drawable.header_default);
            View headerContainer = findViewById(R.id.headerContainer);
            if (headerContainer != null) headerContainer.setBackgroundResource(headerBgRes);
        } catch (Exception e) {}
    }

    // *** ฟังก์ชันใหม่: เปลี่ยนกรอบ Item ***
    private void updateCollectionFrame() {
        try {
            SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", MODE_PRIVATE);
            // อ่านค่า Frame ที่บันทึกไว้ (ถ้าไม่มีให้ใช้ default_frame)
            int frameResId = prefs.getInt("CURRENT_THEME_COLLECTION_FRAME", R.drawable.default_frame);

            // ค้นหา View ที่เป็นกรอบในแต่ละ Item (ต้องไปดู ID ใน xml ว่าเราใช้ View ตัวไหนเป็นกรอบ)
            // ในไฟล์ activity_collection_port.xml เราใช้ View ตัวที่ 2 ใน FrameLayout เป็นกรอบ
            // แต่เราไม่ได้ตั้ง ID ให้มันโดยตรง (หรืออาจจะตั้งซ้ำกันไม่ได้)
            // วิธีแก้ที่ดีที่สุดคือ: กลับไปตั้ง ID ให้ View ที่เป็นกรอบใน XML ทั้ง 3 ตัวให้ไม่ซ้ำกัน
            // เช่น vFramePet, vFrameBg, vFrameFrame

            View vFramePet = findViewById(R.id.vFramePet);
            View vFrameBg = findViewById(R.id.vFrameBg);
            View vFrameFrame = findViewById(R.id.vFrameFrame);

            if (vFramePet != null) vFramePet.setBackgroundResource(frameResId);
            if (vFrameBg != null) vFrameBg.setBackgroundResource(frameResId);
            if (vFrameFrame != null) vFrameFrame.setBackgroundResource(frameResId);

            View vMainFrame = findViewById(R.id.vMainFrame);
            if (vMainFrame != null) {
                vMainFrame.setBackgroundResource(frameResId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectItem(int type) {
        selectedItemType = type;
        if (vSelectorPet != null) vSelectorPet.setVisibility(View.GONE);
        if (vSelectorBg != null) vSelectorBg.setVisibility(View.GONE);
        if (vSelectorFrame != null) vSelectorFrame.setVisibility(View.GONE);

        if (type == 1 && vSelectorPet != null) vSelectorPet.setVisibility(View.VISIBLE);
        else if (type == 2 && vSelectorBg != null) vSelectorBg.setVisibility(View.VISIBLE);
        else if (type == 3 && vSelectorFrame != null) vSelectorFrame.setVisibility(View.VISIBLE);
    }
}