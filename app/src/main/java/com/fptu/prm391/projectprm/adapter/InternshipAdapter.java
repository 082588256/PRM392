package com.fptu.prm391.projectprm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.model.Internship;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class InternshipAdapter extends RecyclerView.Adapter<InternshipAdapter.ViewHolder> {

    private List<Internship> internshipList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Internship internship);
    }

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvCompany, tvCreatedAt;
        private TextView tvLocation, tvDuration, tvField, tvStipend, tvDeadline;
        private TextView tvStatus;

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
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(final Internship internship) {
            tvTitle.setText(internship.getTitle());
            tvCompany.setText(internship.getCompany());
            tvCreatedAt.setText(internship.getCreatedAt());
            tvLocation.setText(internship.getLocation());
            tvDuration.setText(internship.getDuration());
            tvField.setText(internship.getField());
            tvDeadline.setText(internship.getDeadline());

            // Format stipend
            String stipendRaw = internship.getStipend(); // dạng "1111111|VND"
            String stipendText = stipendRaw;
            if (stipendRaw != null && stipendRaw.contains("|")) {
                String[] parts = stipendRaw.split("\\|");
                try {
                    long amount = Long.parseLong(parts[0]);
                    String currency = parts[1];
                    NumberFormat nf;
                    if ("VND".equals(currency)) {
                        nf = NumberFormat.getInstance(new Locale("vi", "VN"));
                        stipendText = nf.format(amount) + " VND";
                    } else if ("USD".equals(currency)) {
                        nf = NumberFormat.getInstance(Locale.US);
                        stipendText = nf.format(amount) + " USD";
                    } else {
                        stipendText = stipendRaw;
                    }
                } catch (Exception e) {
                    stipendText = stipendRaw;
                }
            }
            tvStipend.setText(stipendText);

            if ("close".equalsIgnoreCase(internship.getStatus())) {
                tvStatus.setText("Đã đóng");
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
            } else {
                tvStatus.setText("Đang nhận hồ sơ");
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(internship);
                }
            });
        }
    }

    public void setInternshipList(List<Internship> list) {
        this.internshipList = list;
        notifyDataSetChanged();
    }

    public void updateList(List<Internship> newData) {
        this.internshipList.clear();
        this.internshipList.addAll(newData);
        notifyDataSetChanged();
    }
}