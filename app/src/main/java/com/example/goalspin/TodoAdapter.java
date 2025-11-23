package com.example.goalspin;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<Task> tasks;
    private OnQuestActionListener questListener;
    private int taskBgResId;

    // Interface สำหรับส่งสัญญาณไปบอก MainActivity ให้นับแต้ม Quest
    public interface OnQuestActionListener {
        void onTaskSaved();      // เรียกเมื่อกด Save (สำหรับ Quest 2)
        void onTaskCompleted();  // เรียกเมื่อติ๊กถูก (สำหรับ Quest 3)
    }

    // Constructor รับ Listener เข้ามาด้วย
    public TodoAdapter(List<Task> tasks, OnQuestActionListener listener) {
        this.tasks = tasks;
        this.questListener = listener;
        this.taskBgResId = R.drawable.woodfame;
    }
    // ฟังก์ชันสำหรับเปลี่ยนรูปพื้นหลัง
    public void setTaskBackground(int resId) {
        this.taskBgResId = resId;
        notifyDataSetChanged(); // รีเฟรชรายการใหม่
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.containerContent.setBackgroundResource(taskBgResId);

        holder.etInput.setText(task.getText());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.isCompleted());

        updateUI(holder, task);

        // Logic Checkbox (Quest 3)
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            updateUI(holder, task);

            // ถ้่าติ๊กถูก ให้แจ้งไปที่ Main เพื่อบวกแต้ม Quest
            if (isChecked && questListener != null) {
                questListener.onTaskCompleted();
            }
        });

        // Logic ปุ่ม Save (Quest 2)
        holder.btnAdd.setOnClickListener(v -> {
            String newText = holder.etInput.getText().toString();
            if (!newText.trim().isEmpty()) {
                task.setText(newText);
                task.setLocked(true);
                updateUI(holder, task);

                // แจ้งไปที่ Main ว่ามีการบันทึกแล้ว
                if (questListener != null) {
                    questListener.onTaskSaved();
                }
            }
        });
    }

    private void updateUI(TodoViewHolder holder, Task task) {
        if (task.isLocked()) {
            holder.etInput.setEnabled(false);
            holder.btnAdd.setVisibility(View.GONE);
        } else {
            holder.etInput.setEnabled(true);
            holder.btnAdd.setVisibility(View.VISIBLE);
        }

        if (task.isCompleted()) {
            holder.etInput.setTextColor(Color.GRAY);
            holder.etInput.setAlpha(0.6f);
        } else {
            holder.etInput.setTextColor(Color.WHITE);
            holder.etInput.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        EditText etInput;
        ImageView btnAdd;
        ConstraintLayout containerContent;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cbTask);
            etInput = itemView.findViewById(R.id.etTaskInput);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            containerContent = itemView.findViewById(R.id.containerContent);
        }
    }
}