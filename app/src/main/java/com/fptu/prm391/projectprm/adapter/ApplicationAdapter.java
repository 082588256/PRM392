package com.fptu.prm391.projectprm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.db.ApplicationDAO;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.InternshipDAO;
import com.fptu.prm391.projectprm.db.NotificationDAO;
import com.fptu.prm391.projectprm.model.Application;
import com.fptu.prm391.projectprm.model.Internship;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private List<Application> applicationList;
    private final boolean isRecruiterMode;
    private final ApplicationDAO applicationDAO;
    private final NotificationDAO notificationDAO;
    private final Context context;

    private OnItemClickListener itemClickListener;
    private OnWithdrawClickListener withdrawClickListener;
    private OnConfirmClickListener confirmClickListener;

    public interface OnItemClickListener {
        void onItemClick(Application application);
    }

    public interface OnWithdrawClickListener {
        void onWithdrawClick(int applicationId);
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(Application application, int position, String newStatus);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnWithdrawClickListener(OnWithdrawClickListener listener) {
        this.withdrawClickListener = listener;
    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.confirmClickListener = listener;
    }

    public ApplicationAdapter(Context context, List<Application> applicationList, boolean isRecruiterMode,
                              ApplicationDAO applicationDAO, NotificationDAO notificationDAO) {
        this.context = context;
        this.applicationList = applicationList;
        this.isRecruiterMode = isRecruiterMode;
        this.applicationDAO = applicationDAO;
        this.notificationDAO = notificationDAO;
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

        holder.tvActionMessage.setText("");
        holder.tvActionMessage.setVisibility(View.GONE);

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
                    int result = applicationDAO.updateApplicationStatus(app.getId(), "Confirmed");
                    if (result > 0) {
                        Application application = applicationDAO.getApplicationById(app.getId());
                        InternshipDAO internshipDAO = new InternshipDAO(DatabaseHelper.getInstance(context).getReadableDatabase());
                        Internship internship = internshipDAO.getInternshipById(application.getInternshipId());
                        String companyName = (internship != null) ? internship.getCompany() : "Nhà tuyển dụng";
                        String message = companyName + " đã xác nhận phỏng vấn của bạn.";
                        notificationDAO.insertNotification(message, application.getStudentId());
                        Toast.makeText(context, "Đã xác nhận phỏng vấn", Toast.LENGTH_SHORT).show();
                        app.setInterviewStatus("Confirmed");
                        notifyItemChanged(position);
                    } else {
                        Toast.makeText(context, "Không thể xác nhận!", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.btnReject.setOnClickListener(v -> {
                    int result = applicationDAO.updateApplicationStatus(app.getId(), "Declined");
                    if (result > 0) {
                        Application application = applicationDAO.getApplicationById(app.getId());
                        notificationDAO.insertNotification("Nhà tuyển dụng đã từ chối đơn ứng tuyển của bạn.", application.getStudentId());
                        Toast.makeText(context, "Đã từ chối đơn ứng tuyển", Toast.LENGTH_SHORT).show();
                        app.setInterviewStatus("Declined");
                        notifyItemChanged(position);
                    } else {
                        Toast.makeText(context, "Không thể từ chối!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            holder.btnWithdraw.setVisibility(View.GONE);
        } else {
            String interviewStatus = app.getInterviewStatus() != null ? app.getInterviewStatus() : "";
            boolean showAcceptDecline = interviewStatus.equalsIgnoreCase("Proposed");
            holder.btnConfirm.setVisibility(showAcceptDecline ? View.VISIBLE : View.GONE);
            holder.btnReject.setVisibility(showAcceptDecline ? View.VISIBLE : View.GONE);

            holder.btnConfirm.setText("Accept");
            holder.btnReject.setText("Decline");
            holder.btnConfirm.setEnabled(showAcceptDecline);
            holder.btnReject.setEnabled(showAcceptDecline);

            holder.btnConfirm.setOnClickListener(v -> {
                int result = applicationDAO.updateApplicationStatus(app.getId(), "Accepted");
                if (result > 0) {
                    int recruiterId = applicationDAO.getRecruiterIdByApplicationId(app.getId());
                    if (recruiterId != -1) {
                        notificationDAO.insertNotification("Ứng viên đã xác nhận phỏng vấn.", recruiterId);
                    }
                    app.setInterviewStatus("Accepted");
                    app.setStatus("Accepted");
                    notifyItemChanged(position);
                    holder.tvActionMessage.setText("Bạn đã xác nhận phỏng vấn.");
                    holder.tvActionMessage.setVisibility(View.VISIBLE);
                    holder.btnConfirm.setVisibility(View.GONE);
                    holder.btnReject.setVisibility(View.GONE);
                } else {
                    Toast.makeText(context, "Có lỗi khi xác nhận!", Toast.LENGTH_SHORT).show();
                }
            });

            holder.btnReject.setOnClickListener(v -> {
                int result = applicationDAO.updateApplicationStatus(app.getId(), "Declined");
                if (result > 0) {
                    int recruiterId = applicationDAO.getRecruiterIdByApplicationId(app.getId());
                    if (recruiterId != -1) {
                        notificationDAO.insertNotification("Ứng viên đã từ chối phỏng vấn.", recruiterId);
                    }
                    app.setInterviewStatus("Declined");
                    app.setStatus("Declined");
                    notifyItemChanged(position);
                    holder.tvActionMessage.setText("Người dùng này không thể phỏng vấn vào ngày này.");
                    holder.tvActionMessage.setVisibility(View.VISIBLE);
                    holder.btnConfirm.setVisibility(View.GONE);
                    holder.btnReject.setVisibility(View.GONE);
                } else {
                    Toast.makeText(context, "Có lỗi khi từ chối!", Toast.LENGTH_SHORT).show();
                }
            });

            holder.btnWithdraw.setVisibility(View.VISIBLE);
            holder.btnWithdraw.setEnabled(app.getStatus().equals("Pending") || app.getStatus().equals("Under Review"));

            holder.btnWithdraw.setOnClickListener(v -> {
                if (withdrawClickListener != null) {
                    withdrawClickListener.onWithdrawClick(app.getId());
                }
            });

            holder.itemView.setOnClickListener(v -> {
                if ("Confirmed".equalsIgnoreCase(interviewStatus)) {
                    Toast.makeText(v.getContext(), "Phỏng vấn đã xác nhận. Không thể thay đổi thời gian!", Toast.LENGTH_SHORT).show();
                } else if ("Declined".equalsIgnoreCase(interviewStatus)) {
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
        private final TextView tvTitle, tvCompany, tvStatus, tvAppliedAt;
        private final TextView tvInterviewTime, tvInterviewStatus, tvInterviewNotes;
        private final Button btnConfirm, btnReject, btnWithdraw;
        private final TextView tvActionMessage;

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
            tvActionMessage = itemView.findViewById(R.id.tvActionMessage);
        }

        public void bind(Application application) {
            // Optional binding logic if needed
        }
    }
}