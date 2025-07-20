package com.fptu.prm391.projectprm.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.model.Application;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Application> scheduleList;

    public ScheduleAdapter(List<Application> scheduleList) {
        this.scheduleList = scheduleList;
        Log.d("ScheduleAdapter", "Initialized with " + (scheduleList != null ? scheduleList.size() : 0) + " schedules");
    }

    public void updateScheduleList(List<Application> newScheduleList) {
        this.scheduleList = newScheduleList;
        Log.d("ScheduleAdapter", "Updated schedule list with " + (newScheduleList != null ? newScheduleList.size() : 0) + " schedules");
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Application app = scheduleList.get(position);

        if (app.getStudent() != null) {
            holder.tvStudentEmail.setText(app.getStudent().getEmail());
        } else {
            holder.tvStudentEmail.setText("Không có email");
        }

        if (app.getInternship() != null) {
            holder.tvInternshipCompany.setText("Vị trí: " + app.getInternship().getTitle());
            holder.tvInternshipCompany.append("\nCông ty: " + app.getInternship().getCompany());
        } else {
            holder.tvInternshipCompany.setText("Không có công ty");
        }

        holder.tvInterviewTime.setText("Thời gian: " + (app.getInterviewScheduledTime() != null && !app.getInterviewScheduledTime().isEmpty() ? app.getInterviewScheduledTime() : "Chưa có"));
        holder.tvInterviewStatus.setText("Trạng thái: " + (app.getInterviewStatus() != null && !app.getInterviewStatus().isEmpty() ? app.getInterviewStatus() : "Chưa cập nhật"));
        holder.tvInterviewNotes.setText("Ghi chú: " + (app.getInterviewNotes() != null && !app.getInterviewNotes().isEmpty() ? app.getInterviewNotes() : "Không có"));
    }

    @Override
    public int getItemCount() {
        return scheduleList != null ? scheduleList.size() : 0;
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentEmail, tvInternshipCompany, tvInterviewTime, tvInterviewStatus, tvInterviewNotes;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvInternshipCompany = itemView.findViewById(R.id.tvInternshipCompany);
            tvInterviewTime = itemView.findViewById(R.id.tvInterviewTime);
            tvInterviewStatus = itemView.findViewById(R.id.tvInterviewStatus);
            tvInterviewNotes = itemView.findViewById(R.id.tvInterviewNotes);
        }
    }
}