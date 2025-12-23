package com.acharyaamrit.medicare.doctor.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.model.TimelineItem;
import com.acharyaamrit.medicare.doctor.model.request.OldPrecriptionRequest;
import com.acharyaamrit.medicare.doctor.model.response.OldPrescriptionResponse;
import com.acharyaamrit.medicare.doctor.model.response.PRelation;
import com.acharyaamrit.medicare.doctor.model.response.PRelationResponse;
import com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter.DetailUserTimelineAdapter;
import com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter.UserTimelineAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
    fetchOldPrescription(String.valueOf(pRelation.getId()), bottomSheetDialog);
    bottomSheetDialog.show();



}


private void fetchOldPrescription(String prelation_id, BottomSheetDialog bottomSheetDialog){
    SharedPreferences sharedPreferences = context.getSharedPreferences("user_preference", MODE_PRIVATE);
    String token = sharedPreferences.getString("token", null);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
    OldPrecriptionRequest oldPrecriptionRequest = new OldPrecriptionRequest(prelation_id);
    Call<OldPrescriptionResponse> call = apiService.fetchOldPrescriptionItem("Bearer " + token, oldPrecriptionRequest);
    call.enqueue(new Callback<OldPrescriptionResponse>() {
        @Override
        public void onResponse(Call<OldPrescriptionResponse> call, Response<OldPrescriptionResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                try{

                    RecyclerView recyclerView = bottomSheetDialog.findViewById(R.id.detailPrescription_medicine);

                    if (recyclerView != null) {

                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        DetailUserTimelineAdapter detailUserTimelineAdapter =
                                new DetailUserTimelineAdapter(response.body().getOldPrescription(), context);
                        recyclerView.setAdapter(detailUserTimelineAdapter);
                    }


                } catch (Exception e) {
                    Toast.makeText(context, "There was some Problem", Toast.LENGTH_SHORT).show();
                }
            }else{

            }
        }

        @Override
        public void onFailure(Call<OldPrescriptionResponse> call, Throwable t) {
        }
    });

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
