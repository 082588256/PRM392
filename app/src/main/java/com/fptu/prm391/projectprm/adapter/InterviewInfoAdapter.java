package com.fptu.prm391.projectprm.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.db.InterviewDAO;
import com.fptu.prm391.projectprm.model.InterviewInfo;

import java.util.List;

public class InterviewInfoAdapter extends RecyclerView.Adapter<InterviewInfoAdapter.ViewHolder> {

    private List<InterviewInfo> interviewList;
    private InterviewDAO interviewDAO;

    // Bắt buộc truyền InterviewDAO vào constructor để update DB (nên dùng)
    public InterviewInfoAdapter(List<InterviewInfo> interviewList, InterviewDAO interviewDAO) {
        this.interviewList = interviewList;
        this.interviewDAO = interviewDAO;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_interview_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(interviewList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return interviewList != null ? interviewList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCompany, tvEmail, tvTime, tvStatus, tvNotes;
        private LinearLayout llActionButtons;
        private Button btnAccept, btnDecline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            llActionButtons = itemView.findViewById(R.id.llActionButtons);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
        }

        public void bind(InterviewInfo info, int pos) {
            Log.d("InterviewAdapter", "Status: '" + info.getStatus() + "'");

            tvCompany.setText("Company: " + info.getCompany());
            tvEmail.setText("Email: " + info.getEmail());
            tvTime.setText("Time: " + info.getScheduledTime());
            tvStatus.setText("Status: " + info.getStatus());
            tvNotes.setText("Notes: " + (info.getNotes() == null ? "" : info.getNotes()));

            if (info.getStatus() != null && info.getStatus().trim().equalsIgnoreCase("Proposed")) {
                llActionButtons.setVisibility(View.VISIBLE);
            } else {
                llActionButtons.setVisibility(View.GONE);
            }

            btnAccept.setOnClickListener(v -> {
                // Cập nhật trạng thái UI
                info.setStatus("Confirmed");
                notifyItemChanged(pos);
                // Cập nhật DB
                if (interviewDAO != null) {
                    int updated = interviewDAO.updateInterviewStatus(info.getInterviewId(), "Confirmed");
                    if (updated > 0) {
                        Toast.makeText(itemView.getContext(), "Đã xác nhận lịch phỏng vấn!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(itemView.getContext(), "Lỗi cập nhật trạng thái!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnDecline.setOnClickListener(v -> {
                info.setStatus("Declined");
                notifyItemChanged(pos);
                if (interviewDAO != null) {
                    int updated = interviewDAO.updateInterviewStatus(info.getInterviewId(), "Declined");
                    if (updated > 0) {
                        Toast.makeText(itemView.getContext(), "Bạn đã từ chối lịch này!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(itemView.getContext(), "Lỗi cập nhật trạng thái!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
