package com.fptu.prm391.projectprm.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.model.Application;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private List<Application> applicationList;
    private OnConfirmClickListener confirmClickListener;
    private final OnWithdrawClickListener withdrawClickListener;
    private final boolean isRecruiterMode;

    // Interface for confirm click
    public interface OnConfirmClickListener {
        void onConfirmClick(Application application, int position);
    }

    // Interface for withdraw click
    public interface OnWithdrawClickListener {
        void onWithdrawClick(int applicationId);
    }

    // Set confirm click listener
    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.confirmClickListener = listener;
    }

    // Constructor
    public ApplicationAdapter(List<Application> applicationList, OnWithdrawClickListener withdrawListener, boolean isRecruiterMode) {
        this.applicationList = applicationList;
        this.withdrawClickListener = withdrawListener;
        this.isRecruiterMode = isRecruiterMode;
        Log.d("ApplicationAdapter", "Initialized with " + (applicationList != null ? applicationList.size() : 0) + " applications, isRecruiterMode: " + isRecruiterMode);
    }

    // Update application list
    public void updateApplications(List<Application> newApplications) {
        this.applicationList = newApplications;
        Log.d("ApplicationAdapter", "Updated application list with " + (newApplications != null ? newApplications.size() : 0) + " applications");
        notifyDataSetChanged();
    }

    public void setData(List<Application> list) {
        this.applicationList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application app = applicationList.get(position);
        Log.d("ApplicationAdapter", "Binding application at position " + position + ", id: " + app.getId());
        holder.bind(app);

        if (app.getInternship() != null) {
            holder.tvTitle.setText(app.getInternship().getTitle());
            holder.tvCompany.setText(app.getInternship().getCompany());
        } else {
            holder.tvTitle.setText("Không rõ");
            holder.tvCompany.setText("Không rõ");
        }

        holder.tvStatus.setText(app.getStatus());
        holder.tvAppliedAt.setText(app.getAppliedAt());

        // Hiển thị thông tin phỏng vấn nếu có
        if (app.getInterviewScheduledTime() != null && !app.getInterviewScheduledTime().isEmpty()) {
            holder.tvInterviewTime.setText("Thời gian: " + app.getInterviewScheduledTime());
            holder.tvInterviewTime.setVisibility(View.VISIBLE);
        } else {
            holder.tvInterviewTime.setVisibility(View.GONE);
        }

        if (app.getInterviewStatus() != null && !app.getInterviewStatus().isEmpty()) {
            holder.tvInterviewStatus.setText("Trạng thái: " + app.getInterviewStatus());
            holder.tvInterviewStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvInterviewStatus.setVisibility(View.GONE);
        }

        if (app.getInterviewNotes() != null && !app.getInterviewNotes().isEmpty()) {
            holder.tvInterviewNotes.setText("Ghi chú: " + app.getInterviewNotes());
            holder.tvInterviewNotes.setVisibility(View.VISIBLE);
        } else {
            holder.tvInterviewNotes.setVisibility(View.GONE);
        }

        // Điều chỉnh hiển thị nút dựa trên isRecruiterMode
        if (isRecruiterMode) {
            holder.btnConfirm.setVisibility("Confirmed".equalsIgnoreCase(app.getInterviewStatus()) ? View.GONE : View.VISIBLE);
            holder.btnConfirm.setOnClickListener(v -> {
                if (confirmClickListener != null) {
                    confirmClickListener.onConfirmClick(app, position);
                }
            });
            holder.btnWithdraw.setVisibility(View.GONE);
        } else {
            holder.btnWithdraw.setVisibility(View.VISIBLE);
            holder.btnWithdraw.setEnabled(app.getStatus().equals("Pending") || app.getStatus().equals("Under Review"));
            holder.btnWithdraw.setOnClickListener(v -> {
                if (withdrawClickListener != null) {
                    Log.d("ApplicationAdapter", "Withdraw button clicked for applicationId: " + app.getId());
                    withdrawClickListener.onWithdrawClick(app.getId());
                } else {
                    Log.w("ApplicationAdapter", "WithdrawClickListener is null for applicationId: " + app.getId());
                }
            });
            holder.btnConfirm.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        int size = applicationList != null ? applicationList.size() : 0;
        Log.d("ApplicationAdapter", "Item count: " + size);
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvCompany, tvStatus, tvAppliedAt;
        private TextView tvInterviewTime, tvInterviewStatus, tvInterviewNotes;
        private Button btnConfirm;
        private Button btnWithdraw;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAppTitle);
            tvCompany = itemView.findViewById(R.id.tvAppCompany);
            tvStatus = itemView.findViewById(R.id.tvAppStatus);
            tvAppliedAt = itemView.findViewById(R.id.tvAppAppliedAt);
            tvInterviewTime = itemView.findViewById(R.id.tvInterviewTime);
            tvInterviewStatus = itemView.findViewById(R.id.tvInterviewStatus);
            tvInterviewNotes = itemView.findViewById(R.id.tvInterviewNotes);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnWithdraw = itemView.findViewById(R.id.btnWithdraw);
            Log.d("ApplicationAdapter", "ViewHolder created for item");
        }

        public void bind(Application application) {
            Log.d("ApplicationAdapter", "Binding application id: " + application.getId() + ", status: " + application.getStatus());
        }
    }
}