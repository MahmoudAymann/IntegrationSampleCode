/*
 * Copyright (c) 2020 e-finance
 *
 * Unless required by applicable law or agreed to in writing, this software
 * should not be distributed without permission or authorization guaranteed by the owner entity.
 *
 */

package com.efinance.mobilepaymentintegrationsamplecode.Main.Activities;

import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.efinance.mobilepaymentintegrationsamplecode.Main.Utils.CryptoHelp;
import com.efinance.mobilepaymentintegrationsamplecode.R;
import com.efinance.mobilepaymentsdk.PaymentException;
import com.efinance.mobilepaymentsdk.PaymentGateway;
import com.efinance.mobilepaymentsdk.PaymentStatusInquiryCallback;
import com.efinance.mobilepaymentsdk.PaymentStatusInquiryRequest;
import com.efinance.mobilepaymentsdk.PaymentStatusInquiryResponse;

public class PaymentStatusInquiryActivity extends AppCompatActivity {

    PaymentGateway paymentGateway = null;

    TextInputEditText senderID, senderName, senderPassword, senderRequestNumber;

    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_status_inquiry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        paymentGateway = new PaymentGateway(this, "1234",
                "TestFINANCE_HOST", "68", 0, "EGP");

        senderID = findViewById(R.id.sender_id);
        senderName = findViewById(R.id.sender_name);

        senderPassword = findViewById(R.id.sender_password);
        senderRequestNumber = findViewById(R.id.sender_request_number);


        continueButton = findViewById(R.id.btn_Continue);


        continueButton.setOnClickListener(v -> getPaymentStatus());

        senderRequestNumber.setText(getIntent().getStringExtra("senderRequestNumber"));
    }


    public void getPaymentStatus()
    {
        try {

            PaymentStatusInquiryRequest paymentStatusInquiryRequest = new PaymentStatusInquiryRequest();

            paymentStatusInquiryRequest.Sender.Id = senderID.getText().toString();
            paymentStatusInquiryRequest.Sender.Name = senderName.getText().toString();
            paymentStatusInquiryRequest.Sender.Password = senderPassword.getText().toString();

            paymentStatusInquiryRequest.SenderRequestNumber = senderRequestNumber.getText().toString();

            paymentStatusInquiryRequest.serialize();
            //String signature = CryptoHelp.signData(paymentStatusInquiryRequest.serialize(), this);

            paymentGateway.GetPaymentStatus(paymentStatusInquiryRequest, "", new MobilePaymentStatusInquiryCallback());

        } catch (Exception ex) {
            Log.i("Error", ex.getMessage());
        }
    }



    class MobilePaymentStatusInquiryCallback implements PaymentStatusInquiryCallback {

        @Override
        public void onSuccess(PaymentStatusInquiryResponse response) {
            Log.i(CreatePaymentActivity.class.getSimpleName(), "Request Completed Successfully");
            if(response.PaymentRequestStatus != null) {
                Toast.makeText(PaymentStatusInquiryActivity.this, response.PaymentRequestStatus.Name, Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(PaymentStatusInquiryActivity.this, response.ResponseMessage, Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onError(PaymentException paymentException) {
            Log.e(CreatePaymentActivity.class.getSimpleName(), paymentException.details.getMessage());


        }
    }

}