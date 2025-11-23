package com.example.goalspin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    private List<Integer> imageResIds;
    private OnItemClickListener listener; // 1. เพิ่มตัวแปร Listener
    private int frameResId; // เพิ่มตัวแปรเก็บรูปกรอบ

    // 2. สร้าง Interface สำหรับส่งสัญญาณการคลิก
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // 3. แก้ไข Constructor ให้รับ Listener เข้ามาด้วย
    public CollectionAdapter(List<Integer> imageResIds, OnItemClickListener listener) {
        this.imageResIds = imageResIds;
        this.listener = listener;
        this.frameResId = R.drawable.default_frame;
    }

    // ฟังก์ชันเปลี่ยนกรอบ
    public void setFrameResource(int resId) {
        this.frameResId = resId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        int imageResId = imageResIds.get(position);
        holder.ivImage.setImageResource(imageResId);

        // เปลี่ยนรูปกรอบ (ต้องไปเพิ่ม ID ให้ View กรอบใน item_collection.xml ก่อน)
        holder.vFrame.setBackgroundResource(frameResId);

        // 4. เพิ่มคำสั่งเมื่อมีการคลิกรูป
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageResIds.size();
    }

    public static class CollectionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        View vFrame;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCollectionImage);
            vFrame = itemView.findViewById(R.id.frameOverlay);
        }
    }
}