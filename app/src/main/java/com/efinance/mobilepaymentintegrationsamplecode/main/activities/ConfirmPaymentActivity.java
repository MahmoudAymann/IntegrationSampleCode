/*
 * Copyright (c) 2020 e-finance
 *
 * Unless required by applicable law or agreed to in writing, this software
 * should not be distributed without permission or authorization guaranteed by the owner entity.
 *
 */

package com.efinance.mobilepaymentintegrationsamplecode.main.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.efinance.mobilepaymentintegrationsamplecode.BuildConfig;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import com.efinance.mobilepaymentintegrationsamplecode.R;
import com.efinance.mobilepaymentsdk.PaymentConfirmationCallback;
import com.efinance.mobilepaymentsdk.PaymentConfirmationRequest;
import com.efinance.mobilepaymentsdk.PaymentConfirmationResponse;
import com.efinance.mobilepaymentsdk.PaymentException;
import com.efinance.mobilepaymentsdk.PaymentGateway;

public class ConfirmPaymentActivity extends AppCompatActivity {


    PaymentGateway paymentGateway = null;

    TextInputEditText senderID, senderName, senderPassword, senderRequestNumber, sessionID,
            nameOnCard, cardNumber, cardExpiryYear, cardExpiryMonth, cardCVV, cardToken, cardRequestNumber;

    Switch saveCard;

    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        paymentGateway = new PaymentGateway(this, BuildConfig.EFINANCE_PASSWORD,
                BuildConfig.MERCHANT_ID, BuildConfig.API_VERSION,
                Integer.parseInt(BuildConfig.REGION), BuildConfig.CURRENCY);

        senderID = findViewById(R.id.sender_id);
        senderName = findViewById(R.id.sender_name);

        senderPassword = findViewById(R.id.sender_password);
        senderRequestNumber = findViewById(R.id.sender_request_number);
        sessionID = findViewById(R.id.session_id);

        nameOnCard = findViewById(R.id.card_name);
        cardNumber = findViewById(R.id.card_number);
        cardExpiryYear = findViewById(R.id.card_expiry_year);
        cardExpiryMonth = findViewById(R.id.card_expiry_month);
        cardCVV = findViewById(R.id.card_cvv);
        cardToken = findViewById(R.id.card_token);

        saveCard = findViewById(R.id.save_switch);

        cardRequestNumber = findViewById(R.id.card_request_number);

        continueButton = findViewById(R.id.btn_Continue);


        continueButton.setOnClickListener(v -> confirmPayment());

        senderRequestNumber.setText(getIntent().getStringExtra("senderRequestNumber"));

        sessionID.setText(getIntent().getStringExtra("sessionID"));

        cardRequestNumber.setText(getIntent().getStringExtra("cardRequestNumber"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        paymentGateway.handle3DSecureAuthenticationResult(requestCode, resultCode, data);
    }

    public void confirmPayment()
    {
        try {

            PaymentConfirmationRequest paymentConfirmationRequest = new PaymentConfirmationRequest();

            paymentConfirmationRequest.Sender.Id = BuildConfig.SENDER_ID;
            paymentConfirmationRequest.Sender.Name = senderName.getText().toString();
            paymentConfirmationRequest.Sender.Password = BuildConfig.SENDER_PASSWORD;

            paymentConfirmationRequest.SenderRequestNumber = senderRequestNumber.getText().toString();

            paymentConfirmationRequest.SessionID = sessionID.getText().toString();
            /**
             * Test Cards
             *
             * CardNumber = "5111111111111118";  //3D Secure Not Enrolled
             * CardNumber = "4111111111111111";  //3D Secure Enrolled
             *
             * CardCVV = "123";
             * CardExpiryMonth = "07";
             * CardExpiryYear = "20";
             *
             *
             * */

            /**
             *
             * You need to supply card details or Card Token to complete the payment procedure
             * you can't use both in the same request
             *
             */

            // Pay with Card
            paymentConfirmationRequest.Card.NameOnCard = "Ahmed Abdelhalim";//nameOnCard.getText().toString();
            paymentConfirmationRequest.Card.CardNumber = "4588320011531856"; //cardNumber.getText().toString();
            paymentConfirmationRequest.Card.CardCVV = "000"; //cardCVV.getText().toString();
            paymentConfirmationRequest.Card.CardExpiryMonth = "11"; //cardExpiryMonth.getText().toString();
            paymentConfirmationRequest.Card.CardExpiryYear = "26"; //cardExpiryYear.getText().toString();
            paymentConfirmationRequest.Card.SaveCardFlag = saveCard.isChecked();

            // Or Token
            paymentConfirmationRequest.CardToken = cardToken.getText().toString();

            paymentConfirmationRequest.Amount = 1.0;//Double.parseDouble(getIntent().getStringExtra("amount"));

            paymentConfirmationRequest.CardRequestNumber = cardRequestNumber.getText().toString();

            paymentConfirmationRequest.serialize();
            //String signature = CryptoHelp.signData(paymentConfirmationRequest.serialize(), this);

            paymentGateway.ConfirmPayment(paymentConfirmationRequest, "", new MobilePaymentConfirmationCallback());
        } catch (Exception ex) {
            Log.i("Error", ex.getMessage());
        }
    }

    class MobilePaymentConfirmationCallback implements PaymentConfirmationCallback {

        @Override
        public void onSuccess(PaymentConfirmationResponse response) {
            Log.i(CreatePaymentActivity.class.getSimpleName(), "Request Completed Successfully");

            Toast.makeText(ConfirmPaymentActivity.this, "Payment Confirmed Successfully", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(ConfirmPaymentActivity.this, PaymentStatusInquiryActivity.class);

            intent.putExtra("senderRequestNumber", response.SenderRequestNumber);

            startActivity(intent);

        }

        @Override
        public void onError(PaymentException paymentException) {
            Log.e(CreatePaymentActivity.class.getSimpleName(), paymentException.details.getMessage());


        }
    }

}