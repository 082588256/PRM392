package com.fptu.prm391.projectprm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.model.Application;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private List<Application> applicationList;

    public ApplicationAdapter(List<Application> applicationList) {
        this.applicationList = applicationList;
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
        holder.bind(app);
    }

    @Override
    public int getItemCount() {
        return applicationList != null ? applicationList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvCompany, tvStatus, tvAppliedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAppTitle);
            tvCompany = itemView.findViewById(R.id.tvAppCompany);
            tvStatus = itemView.findViewById(R.id.tvAppStatus);
            tvAppliedAt = itemView.findViewById(R.id.tvAppAppliedAt);
        }

        public void bind(Application application) {
            if (application.getInternship() != null) {
                tvTitle.setText(application.getInternship().getTitle());
                tvCompany.setText(application.getInternship().getCompany());
            } else {
                tvTitle.setText("Không rõ");
                tvCompany.setText("Không rõ");
            }

            tvStatus.setText(application.getStatus());
            tvAppliedAt.setText(application.getAppliedAt());
        }
    }
}
