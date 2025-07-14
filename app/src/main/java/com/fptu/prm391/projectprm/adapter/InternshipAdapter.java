package com.fptu.prm391.projectprm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.model.Internship;

import java.util.List;

public class InternshipAdapter extends RecyclerView.Adapter<InternshipAdapter.ViewHolder> {

    private List<Internship> internshipList;
    private OnItemClickListener listener;

    // Interface cho sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Internship internship);
    }

    // Constructor
    public InternshipAdapter(List<Internship> internshipList, OnItemClickListener listener) {
        this.internshipList = internshipList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_internship, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Internship internship = internshipList.get(position);
        holder.bind(internship);
    }

    @Override
    public int getItemCount() {
        return internshipList != null ? internshipList.size() : 0;
    }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvCompany, tvCreatedAt;
        private TextView tvLocation, tvDuration, tvField, tvStipend, tvDeadline;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvField = itemView.findViewById(R.id.tvField);
            tvStipend = itemView.findViewById(R.id.tvStipend);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
        }

        public void bind(final Internship internship) {
            tvTitle.setText(internship.getTitle());
            tvCompany.setText(internship.getCompany());
            tvCreatedAt.setText(internship.getCreatedAt());
            tvLocation.setText(internship.getLocation());
            tvDuration.setText(internship.getDuration());
            tvField.setText(internship.getField());
            tvStipend.setText(internship.getStipend());
            tvDeadline.setText(internship.getDeadline());


            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(internship);
                }
            });
        }
    }

    // Cập nhật lại danh sách nếu cần
    public void setInternshipList(List<Internship> list) {
        this.internshipList = list;
        notifyDataSetChanged();
    }
}
