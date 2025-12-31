package com.acharyaamrit.medicare.common.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

/**
 * Custom Alert Dialog using Bottom Sheet
 * Professional and attractive replacement for default AlertDialog
 */
public class CustomAlertDialog {

    public enum AlertType {
        SUCCESS(R.drawable.ic_alert_success, "#E8F5E9", "#4CAF50"),
        ERROR(R.drawable.ic_alert_error, "#FFEBEE", "#F44336"),
        WARNING(R.drawable.ic_alert_warning, "#FFF3E0", "#FF9800"),
        INFO(R.drawable.ic_alert_info, "#E3F2FD", "#2196F3"),
        CONFIRMATION(R.drawable.ic_alert_warning, "#FFF3E0", "#FF9800");

        final int iconRes;
        final String bgColor;
        final String iconColor;

        AlertType(int iconRes, String bgColor, String iconColor) {
            this.iconRes = iconRes;
            this.bgColor = bgColor;
            this.iconColor = iconColor;
        }
    }

    public static class Builder {
        private final Context context;
        private String title;
        private String message;
        private AlertType alertType = AlertType.INFO;
        private String positiveButtonText = "OK";
        private String negativeButtonText = null;
        private DialogInterface.OnClickListener positiveClickListener;
        private DialogInterface.OnClickListener negativeClickListener;
        private boolean cancelable = true;
        private String[] items;
        private DialogInterface.OnClickListener itemClickListener;
        private int checkedItem = -1;
        private View customView;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setAlertType(AlertType alertType) {
            this.alertType = alertType;
            return this;
        }

        public Builder setPositiveButton(String text, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = text;
            this.positiveClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String text, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = text;
            this.negativeClickListener = listener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setItems(String[] items, DialogInterface.OnClickListener listener) {
            this.items = items;
            this.itemClickListener = listener;
            return this;
        }

        public Builder setSingleChoiceItems(String[] items, int checkedItem, DialogInterface.OnClickListener listener) {
            this.items = items;
            this.checkedItem = checkedItem;
            this.itemClickListener = listener;
            return this;
        }

        public Builder setView(View view) {
            this.customView = view;
            return this;
        }

        public BottomSheetDialog create() {
            BottomSheetDialog dialog = new BottomSheetDialog(context);
            View view = LayoutInflater.from(context).inflate(R.layout.alert_bottom_sheet_dialog, null);

            // Setup icon
            ImageView alertIcon = view.findViewById(R.id.alertIcon);
            FrameLayout iconContainer = view.findViewById(R.id.iconContainer);
            alertIcon.setImageResource(alertType.iconRes);
            iconContainer.setBackgroundColor(Color.parseColor(alertType.bgColor));

            // Setup title
            TextView alertTitle = view.findViewById(R.id.alertTitle);
            if (title != null && !title.isEmpty()) {
                alertTitle.setText(title);
                alertTitle.setVisibility(View.VISIBLE);
            } else {
                alertTitle.setVisibility(View.GONE);
            }

            // Setup message
            TextView alertMessage = view.findViewById(R.id.alertMessage);
            if (message != null && !message.isEmpty()) {
                alertMessage.setText(message);
                alertMessage.setVisibility(View.VISIBLE);
            } else {
                alertMessage.setVisibility(View.GONE);
            }

            // Setup list items or custom view
            RecyclerView listView = view.findViewById(R.id.alertListView);
            FrameLayout customViewContainer = view.findViewById(R.id.customViewContainer);

            if (customView != null) {
                // Show custom view
                alertMessage.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                customViewContainer.setVisibility(View.VISIBLE);
                customViewContainer.removeAllViews();
                customViewContainer.addView(customView);
            } else if (items != null && items.length > 0) {
                // Show list items
                alertMessage.setVisibility(View.GONE);
                customViewContainer.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                listView.setLayoutManager(new LinearLayoutManager(context));

                AlertListAdapter adapter = new AlertListAdapter(items, checkedItem, (position) -> {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(dialog, position);
                    }
                    dialog.dismiss();
                });
                listView.setAdapter(adapter);
            } else {
                // Show message only
                customViewContainer.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
            }

            // Setup buttons
            Button positiveButton = view.findViewById(R.id.positiveButton);
            Button negativeButton = view.findViewById(R.id.negativeButton);

            positiveButton.setText(positiveButtonText);
            positiveButton.setOnClickListener(v -> {
                if (positiveClickListener != null) {
                    positiveClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                }
                dialog.dismiss();
            });

            if (negativeButtonText != null) {
                negativeButton.setText(negativeButtonText);
                negativeButton.setVisibility(View.VISIBLE);
                negativeButton.setOnClickListener(v -> {
                    if (negativeClickListener != null) {
                        negativeClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    }
                    dialog.dismiss();
                });
            } else {
                negativeButton.setVisibility(View.GONE);
            }

            dialog.setContentView(view);
            dialog.setCancelable(cancelable);

            // Make background transparent for rounded corners
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            return dialog;
        }

        public void show() {
            create().show();
        }
    }

    // Adapter for list items
    private static class AlertListAdapter extends RecyclerView.Adapter<AlertListAdapter.ViewHolder> {
        private final String[] items;
        private int selectedPosition;
        private final OnItemClickListener listener;

        interface OnItemClickListener {
            void onItemClick(int position);
        }

        AlertListAdapter(String[] items, int selectedPosition, OnItemClickListener listener) {
            this.items = items;
            this.selectedPosition = selectedPosition;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_alert_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.itemText.setText(items[position]);
            holder.radioButton.setChecked(position == selectedPosition);

            holder.itemView.setOnClickListener(v -> {
                int oldPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(oldPosition);
                notifyItemChanged(selectedPosition);
                listener.onItemClick(selectedPosition);
            });
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView itemText;
            RadioButton radioButton;

            ViewHolder(View itemView) {
                super(itemView);
                itemText = itemView.findViewById(R.id.itemText);
                radioButton = itemView.findViewById(R.id.radioButton);
            }
        }
    }
}
