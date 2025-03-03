package com.example.theukuleleband.modules.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.theukuleleband.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomerReportFragment extends Fragment {

    private TextView genreLabel, costLabel, hoursLabel, serviceTypeLabel;
    private RecyclerView recyclerView;
    private ReportAdapter adapter;
    private List<Service> serviceList;
    private static final String REPORT_URL = "http://yourserver.com/customerReport.php"; // Change this to your actual endpoint

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customerreport, container, false);

        // Initialize labels
        genreLabel = view.findViewById(R.id.genreLabel);
        costLabel = view.findViewById(R.id.costLabel);
        hoursLabel = view.findViewById(R.id.hoursLabel);
        serviceTypeLabel = view.findViewById(R.id.serviceTypeLabel);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        serviceList = new ArrayList<>();
        adapter = new ReportAdapter(serviceList);
        recyclerView.setAdapter(adapter);

        fetchReportData();

        return view;
    }

    private void fetchReportData() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, REPORT_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray servicesArray = response.getJSONArray("services");
                                serviceList.clear();
                                for (int i = 0; i < servicesArray.length(); i++) {
                                    JSONObject service = servicesArray.getJSONObject(i);
                                    serviceList.add(new Service(
                                            service.getString("Genre"),
                                            service.getString("Cost"),
                                            service.getString("Hours"),
                                            service.getString("ServiceType")
                                    ));
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

    private static class Service {
        String genre, cost, hours, serviceType;
        public Service(String genre, String cost, String hours, String serviceType) {
            this.genre = genre;
            this.cost = cost;
            this.hours = hours;
            this.serviceType = serviceType;
        }
    }

    private static class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
        private final List<Service> services;

        public ReportAdapter(List<Service> services) {
            this.services = services;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Service service = services.get(position);
            holder.genreText.setText(service.genre);
            holder.costText.setText(service.cost);
            holder.hoursText.setText(service.hours);
            holder.serviceTypeText.setText(service.serviceType);
        }

        @Override
        public int getItemCount() {
            return services.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView genreText, costText, hoursText, serviceTypeText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                genreText = itemView.findViewById(R.id.genreText);
                costText = itemView.findViewById(R.id.costText);
                hoursText = itemView.findViewById(R.id.hoursText);
                serviceTypeText = itemView.findViewById(R.id.serviceTypeText);
            }
        }
    }
}
