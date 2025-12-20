package com.acharyaamrit.medicare.doctor.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.model.TimelineItem;
import com.acharyaamrit.medicare.doctor.model.response.PRelation;
import com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter.DetailUserTimelineAdapter;
import com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter.UserTimelineAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;


public class PrescriptionRelationForPatientAdapter extends RecyclerView.Adapter<PrescriptionRelationForPatientAdapter.ViewHolder> {

    private final List<PRelation> pRelationList;
    private final Context context;
    private final BottomSheetDialog bottomSheetDialog;

    public PrescriptionRelationForPatientAdapter(List<PRelation> pRelationList, Context context, BottomSheetDialog bottomSheetDialog) {
        this.pRelationList = pRelationList;
        this.context = context;
        this.bottomSheetDialog = bottomSheetDialog;
    }

    public PrescriptionRelationForPatientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_relation, parent, false);
        return new PrescriptionRelationForPatientAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull PrescriptionRelationForPatientAdapter.ViewHolder holder, int position) {
        PRelation prelation = pRelationList.get(position);
        holder.titleView.setText("Checkup With "+prelation.getDoctor_name());
        holder.timeView.setText(prelation.getCreated_at());
        holder.viewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = context.getSharedPreferences("user_preference", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("prescriptionRelation_id", String.valueOf(prelation.getId()));
                editor.apply();
                bottomSheetDialog.dismiss();
            }
        });
        holder.titleView.setOnClickListener(v->{
            showBottomSheet(prelation);
        });


    }

private void showBottomSheet(PRelation pRelation){
    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
    View bottomSheetView = LayoutInflater.from(context).inflate(
            R.layout.item_bottom_sheet_detail_preciption, null);
    bottomSheetDialog.setContentView(bottomSheetView);

    TextView doctorName = bottomSheetDialog.findViewById(R.id.doctor_name);
    doctorName.setText("Checkup With "+pRelation.getDoctor_name());
    bottomSheetDialog.show();

    bottomSheetDialog.show();

}



    public int getItemCount() {
        return pRelationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleView, timeView;
        TextView viewbtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleView = itemView.findViewById(R.id.title);
            timeView = itemView.findViewById(R.id.time);
            viewbtn = itemView.findViewById(R.id.view_btn);
        }
    }
}
