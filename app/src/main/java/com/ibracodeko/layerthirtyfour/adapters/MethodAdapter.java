package com.ibracodeko.layerthirtyfour.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.ibracodeko.layerthirtyfour.R;
import com.ibracodeko.layerthirtyfour.models.MethodResponse;
import java.util.List;

public class MethodAdapter extends RecyclerView.Adapter<MethodAdapter.MethodViewHolder> {
    private List<MethodResponse.MethodDetail> methodList;
    private OnMethodClickListener listener;

    public interface OnMethodClickListener {
        void onEditClick(MethodResponse.MethodDetail method);
        void onDeleteClick(MethodResponse.MethodDetail method);
        void onToggleClick(MethodResponse.MethodDetail method);
    }

    public MethodAdapter(List<MethodResponse.MethodDetail> methodList, OnMethodClickListener listener) {
        this.methodList = methodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_method, parent, false);
        return new MethodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MethodViewHolder holder, int position) {
        MethodResponse.MethodDetail method = methodList.get(position);
        holder.bind(method);
    }

    @Override
    public int getItemCount() {
        return methodList.size();
    }

    class MethodViewHolder extends RecyclerView.ViewHolder {
        private CardView cardMethod;
        private TextView tvMethodName, tvMethodType, tvMethodEndpoint, tvMethodDescription;
        private Switch switchEnabled;
        private ImageView ivEdit, ivDelete;

        public MethodViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMethod = itemView.findViewById(R.id.cardMethod);
            tvMethodName = itemView.findViewById(R.id.tvMethodName);
            tvMethodType = itemView.findViewById(R.id.tvMethodType);
            tvMethodEndpoint = itemView.findViewById(R.id.tvMethodEndpoint);
            tvMethodDescription = itemView.findViewById(R.id.tvMethodDescription);
            switchEnabled = itemView.findViewById(R.id.switchEnabled);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }

        public void bind(MethodResponse.MethodDetail method) {
            tvMethodName.setText(method.getName());
            tvMethodType.setText(method.getType().toUpperCase());
            tvMethodEndpoint.setText(method.getEndpoint());
            tvMethodDescription.setText(method.getDescription());
            switchEnabled.setChecked(method.isEnabled());

            // Set type color
            int typeColor;
            switch (method.getType().toLowerCase()) {
                case "web_multiporting":
                    typeColor = itemView.getContext().getColor(R.color.primary_color);
                    break;
                case "ip_troubleshooting":
                    typeColor = itemView.getContext().getColor(R.color.accent_color);
                    break;
                case "proxy_server":
                    typeColor = itemView.getContext().getColor(R.color.secondary_color);
                    break;
                default:
                    typeColor = itemView.getContext().getColor(R.color.on_surface_variant);
                    break;
            }
            tvMethodType.setTextColor(typeColor);

            // Set card alpha based on enabled status
            cardMethod.setAlpha(method.isEnabled() ? 1.0f : 0.6f);

            // Click listeners
            ivEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(method);
                }
            });

            ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(method);
                }
            });

            switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onToggleClick(method);
                }
            });

            cardMethod.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(method);
                }
            });
        }
    }
}