package com.fptu.prm391.projectprm.adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.model.Application;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Application> scheduleList;

    public ScheduleAdapter(List<Application> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public void updateScheduleList(List<Application> newScheduleList) {
        this.scheduleList = newScheduleList;
        notifyDataSetChanged();
    }

    public List<Application> getScheduleList() {
        return scheduleList;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Application app = scheduleList.get(position);

        // Email sinh viên
        holder.tvStudentEmail.setText(app.getStudent() != null
                ? app.getStudent().getEmail()
                : "Không có email");

        // Công ty thực tập
        holder.tvInternshipCompany.setText(app.getInternship() != null
                ? "Vị trí: " + app.getInternship().getTitle() + "\nCông ty: " + app.getInternship().getCompany()
                : "Không có công ty");

        // Thời gian phỏng vấn
        String scheduledTime = app.getInterviewScheduledTime();
        Log.d("DEBUG", "Interview time at position " + position + ": " + app.getInterviewScheduledTime());

        holder.etInterviewDate.setText((scheduledTime != null && !scheduledTime.isEmpty())
                ? scheduledTime
                : "Chọn thời gian");

        holder.etInterviewDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Bước 1: Mở DatePicker
            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                    (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                        // Bước 2: Sau khi chọn ngày, mở TimePicker
                        TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                                (TimePicker timeView, int hourOfDay, int minute) -> {
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendar.set(Calendar.MINUTE, minute);
                                    calendar.set(Calendar.SECOND, 0);

                                    // Định dạng thời gian
                                    String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                            .format(calendar.getTime());

                                    holder.etInterviewDate.setText(datetime);
                                    app.setInterviewScheduledTime(datetime); // cập nhật vào Application
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true);
                        timePickerDialog.show();
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Trạng thái
        holder.tvInterviewStatus.setText("Trạng thái: " +
                (app.getInterviewStatus() != null ? app.getInterviewStatus() : "Chưa cập nhật"));

        // Ghi chú
        holder.tvInterviewNotes.setText("Ghi chú: " +
                (app.getInterviewNotes() != null ? app.getInterviewNotes() : "Không có"));
    }

    @Override
    public int getItemCount() {
        return scheduleList != null ? scheduleList.size() : 0;
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentEmail, tvInternshipCompany, tvInterviewStatus, tvInterviewNotes;
        EditText etInterviewDate;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvInternshipCompany = itemView.findViewById(R.id.tvInternshipCompany);
            tvInterviewStatus = itemView.findViewById(R.id.tvInterviewStatus);
            tvInterviewNotes = itemView.findViewById(R.id.tvInterviewNotes);
            etInterviewDate = itemView.findViewById(R.id.etInterviewDate);
        }
    }
}
