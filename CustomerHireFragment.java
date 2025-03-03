package com.example.theukuleleband.modules.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.theukuleleband.R;
import com.example.theukuleleband.modules.shared.Genre;
import com.example.theukuleleband.modules.shared.SoundSystemCategory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomerHireFragment extends Fragment {
    private Spinner genreSpinner, categorySpinner, hoursSpinner;
    private TextView amountTextView;
    private Button hireButton;
    private int costPerHour = 1000; // Example cost per hour

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customerhire, container, false);

        genreSpinner = view.findViewById(R.id.hiregenrespinner);
        categorySpinner = view.findViewById(R.id.categoryspinner);
        hoursSpinner = view.findViewById(R.id.hiretimespinner);
        amountTextView = view.findViewById(R.id.hiredispamount);
        hireButton = view.findViewById(R.id.hireButton);

        setupSpinners();
        setupHireButton();

        return view;
    }

    private void setupSpinners() {
        // Populate Genre Spinner
        ArrayAdapter<Genre> genreAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Genre.values());
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(genreAdapter);

        // Populate Category Spinner
        ArrayAdapter<SoundSystemCategory> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, SoundSystemCategory.values());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Populate Hours Spinner
        ArrayAdapter<Integer> hoursAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hoursSpinner.setAdapter(hoursAdapter);

        hoursSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int hours = (Integer) parent.getItemAtPosition(position);
                int totalCost = hours * costPerHour;
                amountTextView.setText(String.valueOf(totalCost));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupHireButton() {
        hireButton.setOnClickListener(v -> sendHireRequest());
    }

    private void sendHireRequest() {
        Genre selectedGenre = (Genre) genreSpinner.getSelectedItem();
        SoundSystemCategory selectedCategory = (SoundSystemCategory) categorySpinner.getSelectedItem();
        int selectedHours = (Integer) hoursSpinner.getSelectedItem();
        int totalCost = selectedHours * costPerHour;
        String lendingDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("EquipmentID", 1); // Example EquipmentID
            requestData.put("LendingDate", lendingDate);
            requestData.put("Cost", totalCost);
            requestData.put("Hours", selectedHours);
            requestData.put("PhoneNo", "0712345678"); // Example phone number
            requestData.put("Genre", selectedGenre.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HireRequestTask(requireContext(), requestData).execute("https://yourserver.com/customerHire.php");
    }
}
