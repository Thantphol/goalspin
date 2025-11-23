package com.example.goalspin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    // 1. ย้ายตัวแปรมาประกาศตรงนี้ (Class Level) เพื่อให้ setupAdapter มองเห็น
    private RecyclerView rvCollection;
    private List<Integer> collectionImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        TextView tvTicketCount = findViewById(R.id.tvTicketCount);
        if (tvTicketCount != null) {
            tvTicketCount.setText(String.valueOf(MainActivity.globalTicketCount));
        }

        // 2. ใน onCreate ไม่ต้องมี Type นำหน้าแล้ว (ใช้ตัวแปรข้างบน)
        collectionImages = new ArrayList<>();
        collectionImages.add(R.drawable.background_default);
        collectionImages.add(R.drawable.hidden_forest);
        collectionImages.add(R.drawable.coastal_port);
        collectionImages.add(R.drawable.floating_castle);
        collectionImages.add(R.drawable.gothic_mountain);

        rvCollection = findViewById(R.id.rvCollection);
        rvCollection.setLayoutManager(new GridLayoutManager(this, 2));

        Button btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // อัปเดตพื้นหลัง
        SharedPreferences prefs = getSharedPreferences("GoalSpinPrefs", MODE_PRIVATE);
        int bgResId = prefs.getInt("CURRENT_BACKGROUND", R.drawable.background_default);
        View root = findViewById(R.id.inventoryRoot);
        if (root != null) {
            root.setBackgroundResource(bgResId);
        }

        // อัปเดตกรอบรูปตามธีม
        int themeFrameRes = prefs.getInt("CURRENT_THEME_COLLECTION_FRAME", R.drawable.default_frame);
        setupAdapter(themeFrameRes);

        // อัปเดตHeader
        int headerBgRes = prefs.getInt("CURRENT_HEADER_BG", R.drawable.header_default);
        // หา View ที่เป็น Header Container
        View headerContainer = findViewById(R.id.headerContainer);
        if (headerContainer != null) {
            headerContainer.setBackgroundResource(headerBgRes);
        }
    }

    // ฟังก์ชันสร้าง Adapter
    private void setupAdapter(int frameResId) {
        // ตอนนี้ setupAdapter จะมองเห็น collectionImages และ rvCollection แล้ว ไม่แดงแน่นอนครับ
        CollectionAdapter adapter = new CollectionAdapter(collectionImages, new CollectionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = null;

                if (position == 0) {
                    intent = new Intent(InventoryActivity.this, CollectionDefaultActivity.class);
                } else if (position == 1) {
                    intent = new Intent(InventoryActivity.this, CollectionForestActivity.class);
                } else if (position == 2) {
                    intent = new Intent(InventoryActivity.this, CollectionPortActivity.class);
                } else if (position == 3) {
                    intent = new Intent(InventoryActivity.this, CollectionCastleActivity.class);
                } else if (position == 4) {
                    intent = new Intent(InventoryActivity.this, CollectionMountainActivity.class);
                }

                if (intent != null) {
                    int selectedImageResId = collectionImages.get(position);
                    intent.putExtra("SELECTED_IMAGE", selectedImageResId);
                    startActivity(intent);
                }
            }
        });

        // เปลี่ยนรูปกรอบ
        adapter.setFrameResource(frameResId);

        rvCollection.setAdapter(adapter);
    }

}