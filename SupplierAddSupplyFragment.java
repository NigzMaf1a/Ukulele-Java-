package com.example.theukuleleband.modules.supplier;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.theukuleleband.R;
import com.example.theukuleleband.modules.shared.Brand;
import com.example.theukuleleband.modules.shared.Description;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupplierAddSupplyFragment extends Fragment {

    private Spinner descriptionSpinner, brandSpinner;
    private EditText priceEditText, supplierNameEditText, supplyDateEditText, phoneNoEditText;
    private Button chooseImageButton, addSupplyButton;
    private ImageView imageView;
    private Bitmap selectedImage;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.supplieraddsupply, container, false);

        // Initialize views
        descriptionSpinner = view.findViewById(R.id.addsupplydescription);
        brandSpinner = view.findViewById(R.id.addsupplybrand);
        priceEditText = view.findViewById(R.id.priceedittext);
        supplierNameEditText = new EditText(getContext()); // Replace with actual supplier name retrieval
        supplyDateEditText = new EditText(getContext());
        phoneNoEditText = new EditText(getContext());
        chooseImageButton = view.findViewById(R.id.chooseimage);
        addSupplyButton = view.findViewById(R.id.addsupplybutton);
        imageView = view.findViewById(R.id.imageView);

        // Populate spinners
        descriptionSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, Description.values()));
        brandSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, Brand.values()));

        // Handle date picker
        supplyDateEditText.setOnClickListener(v -> showDatePicker());

        // Handle image selection
        chooseImageButton.setOnClickListener(v -> openImageChooser());

        // Handle form submission
        addSupplyButton.setOnClickListener(v -> submitSupplyData());

        return view;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            supplyDateEditText.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void submitSupplyData() {
        String price = priceEditText.getText().toString().trim();
        String supplierName = supplierNameEditText.getText().toString().trim();
        String description = descriptionSpinner.getSelectedItem().toString();
        String brand = brandSpinner.getSelectedItem().toString();
        String supplyDate = supplyDateEditText.getText().toString().trim();
        String phoneNo = phoneNoEditText.getText().toString().trim();

        if (price.isEmpty() || supplierName.isEmpty() || supplyDate.isEmpty() || phoneNo.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare request body
        RequestBody requestBody = new FormBody.Builder()
                .add("EquipmentID", "1") // Modify as needed
                .add("Price", price)
                .add("SupplierName", supplierName)
                .add("Description", description)
                .add("Brand", brand)
                .add("SupplyDate", supplyDate)
                .add("PhoneNo", phoneNo)
                .build();

        // Send request
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://yourserver.com/supplierAddSupply.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), jsonResponse.optString("message"), Toast.LENGTH_SHORT).show());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
