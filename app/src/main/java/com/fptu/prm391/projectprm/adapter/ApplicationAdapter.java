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
    private final OnWithdrawClickListener withdrawClickListener;

    // Interface for withdraw click
    public interface OnWithdrawClickListener {
        void onWithdrawClick(int applicationId);
    }

    // Constructor
    public ApplicationAdapter(List<Application> applicationList, OnWithdrawClickListener listener) {
        this.applicationList = applicationList;
        this.withdrawClickListener = listener;
        Log.d("ApplicationAdapter", "Initialized with " + (applicationList != null ? applicationList.size() : 0) + " applications");
    }

    // Update application list
    public void updateApplications(List<Application> newApplications) {
        this.applicationList = newApplications;
        Log.d("ApplicationAdapter", "Updated application list with " + (newApplications != null ? newApplications.size() : 0) + " applications");
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
    }

    @Override
    public int getItemCount() {
        int size = applicationList != null ? applicationList.size() : 0;
        Log.d("ApplicationAdapter", "Item count: " + size);
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvCompany, tvStatus, tvAppliedAt;
        private Button btnWithdraw;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAppTitle);
            tvCompany = itemView.findViewById(R.id.tvAppCompany);
            tvStatus = itemView.findViewById(R.id.tvAppStatus);
            tvAppliedAt = itemView.findViewById(R.id.tvAppAppliedAt);
            btnWithdraw = itemView.findViewById(R.id.btnWithdraw);
            Log.d("ApplicationAdapter", "ViewHolder created for item");
        }

        public void bind(Application application) {
            Log.d("ApplicationAdapter", "Binding application id: " + application.getId() + ", status: " + application.getStatus());
            if (application.getInternship() != null) {
                tvTitle.setText(application.getInternship().getTitle());
                tvCompany.setText(application.getInternship().getCompany());
            } else {
                tvTitle.setText("Không rõ");
                tvCompany.setText("Không rõ");
            }

            tvStatus.setText(application.getStatus());
            tvAppliedAt.setText(application.getAppliedAt());

            // Enable withdraw button only for valid statuses
            String status = application.getStatus();
            btnWithdraw.setEnabled(status.equals("Pending") || status.equals("Under Review"));
            Log.d("ApplicationAdapter", "Withdraw button for applicationId " + application.getId() + " enabled: " + btnWithdraw.isEnabled());
            btnWithdraw.setOnClickListener(v -> {
                if (withdrawClickListener != null) {
                    Log.d("ApplicationAdapter", "Withdraw button clicked for applicationId: " + application.getId());
                    withdrawClickListener.onWithdrawClick(application.getId());
                } else {
                    Log.w("ApplicationAdapter", "WithdrawClickListener is null for applicationId: " + application.getId());
                }
            });
        }


    }
}