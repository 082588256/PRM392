package com.fptu.prm391.projectprm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private OnItemClickListener itemClickListener;

    // Interface cho sinh viên click vào item (mở DatePicker chẳng hạn)
    public interface OnItemClickListener {
        void onItemClick(Application application);
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(Application application, int position, String newStatus);
    }

    public interface OnWithdrawClickListener {
        void onWithdrawClick(int applicationId);
    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.confirmClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public ApplicationAdapter(List<Application> applicationList, OnWithdrawClickListener withdrawListener, boolean isRecruiterMode) {
        this.applicationList = applicationList;
        this.withdrawClickListener = withdrawListener;
        this.isRecruiterMode = isRecruiterMode;
    }

    public void updateApplications(List<Application> newApplications) {
        this.applicationList = newApplications;
        notifyDataSetChanged();
    }

    public void setData(List<Application> list) {
        this.applicationList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application app = applicationList.get(position);
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

        if (isRecruiterMode) {
            String status = app.getInterviewStatus();
            if ("Confirmed".equalsIgnoreCase(status)) {
                holder.btnConfirm.setText("Approved");
                holder.btnConfirm.setEnabled(false);
                holder.btnConfirm.setVisibility(View.VISIBLE);

                holder.btnReject.setVisibility(View.GONE);
            } else if ("Declined".equalsIgnoreCase(status)) {
                holder.btnReject.setText("Rejected");
                holder.btnReject.setEnabled(false);
                holder.btnReject.setVisibility(View.VISIBLE);

                holder.btnConfirm.setVisibility(View.GONE);
            } else {
                holder.btnConfirm.setText("Approve");
                holder.btnConfirm.setEnabled(true);
                holder.btnConfirm.setVisibility(View.VISIBLE);

                holder.btnReject.setText("Reject");
                holder.btnReject.setEnabled(true);
                holder.btnReject.setVisibility(View.VISIBLE);

                holder.btnConfirm.setOnClickListener(v -> {
                    if (confirmClickListener != null) {
                        confirmClickListener.onConfirmClick(app, position, "Confirmed");
                    }
                });

                holder.btnReject.setOnClickListener(v -> {
                    if (confirmClickListener != null) {
                        confirmClickListener.onConfirmClick(app, position, "Declined");
                    }
                });
            }

            holder.btnWithdraw.setVisibility(View.GONE);
        } else {
            holder.btnWithdraw.setVisibility(View.VISIBLE);
            holder.btnWithdraw.setEnabled(app.getStatus().equals("Pending") || app.getStatus().equals("Under Review"));

            holder.btnWithdraw.setOnClickListener(v -> {
                if (withdrawClickListener != null) {
                    withdrawClickListener.onWithdrawClick(app.getId());
                }
            });

            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);

            // 👉 Xử lý khi sinh viên click vào item (để đổi thời gian phỏng vấn)
            holder.itemView.setOnClickListener(v -> {
                if ("Confirmed".equalsIgnoreCase(app.getInterviewStatus())) {
                    Toast.makeText(v.getContext(), "Phỏng vấn đã xác nhận. Không thể thay đổi thời gian!", Toast.LENGTH_SHORT).show();
                } else if ("Declined".equalsIgnoreCase(app.getInterviewStatus())) {
                    Toast.makeText(v.getContext(), "Phỏng vấn đã bị từ chối. Không thể thay đổi!", Toast.LENGTH_SHORT).show();
                } else {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(app);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return applicationList != null ? applicationList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvCompany, tvStatus, tvAppliedAt;
        private TextView tvInterviewTime, tvInterviewStatus, tvInterviewNotes;
        private Button btnConfirm, btnReject, btnWithdraw;

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
            btnReject = itemView.findViewById(R.id.btnReject);
            btnWithdraw = itemView.findViewById(R.id.btnWithdraw);
        }

        public void bind(Application application) {
            // Optional: setup if needed
        }
    }
}
