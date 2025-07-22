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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.db.InterviewDAO;
import com.fptu.prm391.projectprm.model.InterviewInfo;

import java.util.List;

public class InterviewInfoAdapter extends RecyclerView.Adapter<InterviewInfoAdapter.ViewHolder> {

    private List<InterviewInfo> interviewList;
    private InterviewDAO interviewDAO;

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

            String status = info.getStatus() != null ? info.getStatus().trim() : "";

            boolean isProposed = status.equalsIgnoreCase("Proposed");

            // Cập nhật nút và trạng thái tương tác dựa vào status
            llActionButtons.setVisibility(isProposed ? View.VISIBLE : View.GONE);
            btnAccept.setEnabled(isProposed);
            btnDecline.setEnabled(isProposed);

            tvTime.setTextColor(ContextCompat.getColor(itemView.getContext(),
                    isProposed ? android.R.color.black : android.R.color.darker_gray));

            // Không enable/disable tvTime tại đây – sẽ xử lý trực tiếp trong onClick
            tvTime.setOnClickListener(v -> {
                String currentStatus = info.getStatus() != null ? info.getStatus().trim() : "";

                if (currentStatus.equalsIgnoreCase("Confirmed") || currentStatus.equalsIgnoreCase("Declined")) {
                    Toast.makeText(itemView.getContext(), "Không thể thay đổi thời gian khi đã xác nhận!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(itemView.getContext(), "Hiện DatePicker ở đây", Toast.LENGTH_SHORT).show();
            });

            btnAccept.setOnClickListener(v -> {
                info.setStatus("Confirmed");
                notifyItemChanged(pos);

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
