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
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

public class CustomerBookingFragment extends Fragment {

    private Spinner genreSpinner, timeSpinner;
    private TextView amountTextView;
    private Button bookButton;
    private String selectedGenre;
    private int selectedHours;
    private static final String BOOKING_URL = "https://yourserver.com/customerBooking.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customerbooking, container, false);

        genreSpinner = view.findViewById(R.id.bookgenrespinner);
        timeSpinner = view.findViewById(R.id.booktimespinner);
        amountTextView = view.findViewById(R.id.bookdispamount);
        bookButton = view.findViewById(R.id.book_button);

        setupGenreSpinner();
        setupTimeSpinner();

        bookButton.setOnClickListener(v -> sendBookingRequest());

        return view;
    }

    private void setupGenreSpinner() {
        ArrayAdapter<Genre> genreAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Genre.values());
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(genreAdapter);

        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGenre = Genre.values()[position].name();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupTimeSpinner() {
        Integer[] hours = {1, 2, 3, 4, 5};
        ArrayAdapter<Integer> timeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, hours);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHours = (Integer) parent.getItemAtPosition(position);
                updateAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateAmount() {
        int ratePerHour = 1000; // Example rate
        int totalAmount = selectedHours * ratePerHour;
        amountTextView.setText(String.valueOf(totalAmount));
    }

    private void sendBookingRequest() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int cost = Integer.parseInt(amountTextView.getText().toString());
        String phoneNo = "0712345678"; // Placeholder, get from user input

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("Genre", selectedGenre);
            jsonRequest.put("BookingDate", currentDate);
            jsonRequest.put("Cost", cost);
            jsonRequest.put("Hours", selectedHours);
            jsonRequest.put("PhoneNo", phoneNo);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(jsonRequest.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(BOOKING_URL).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Booking failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Booking successful", Toast.LENGTH_SHORT).show());
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Booking error", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
