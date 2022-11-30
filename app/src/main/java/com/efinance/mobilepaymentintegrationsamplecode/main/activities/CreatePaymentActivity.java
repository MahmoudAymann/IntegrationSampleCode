/*
 * Copyright (c) 2020 e-finance
 *
 * Unless required by applicable law or agreed to in writing, this software
 * should not be distributed without permission or authorization guaranteed by the owner entity.
 *
 */


package com.efinance.mobilepaymentintegrationsamplecode.main.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.efinance.mobilepaymentintegrationsamplecode.BuildConfig;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.efinance.mobilepaymentintegrationsamplecode.R;
import com.efinance.mobilepaymentsdk.PaymentCreationCallback;
import com.efinance.mobilepaymentsdk.PaymentCreationRequest;
import com.efinance.mobilepaymentsdk.PaymentCreationResponse;
import com.efinance.mobilepaymentsdk.PaymentException;
import com.efinance.mobilepaymentsdk.PaymentGateway;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class CreatePaymentActivity extends AppCompatActivity {

    PaymentGateway paymentGateway = null;
    Calendar calendar = null;

    TextInputEditText senderID, senderName, senderPassword, senderRequestNumber, senderInvoiceNumber, serviceCode, description,
    settlementAccountCode, settlementAmountDescription, settlementAmountValue, currency, mobileNumber, email, userUniqueIdentifier, additionalInfo, expiryDate;

    RadioGroup paymentMechanismType;
    RadioButton mechanismTypeButton;
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_payment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        paymentGateway = new PaymentGateway(this, BuildConfig.EFINANCE_PASSWORD,
                BuildConfig.MERCHANT_ID, BuildConfig.API_VERSION, Integer.parseInt(BuildConfig.REGION), BuildConfig.CURRENCY);

        senderID = findViewById(R.id.sender_id);
        senderName = findViewById(R.id.sender_name);
        senderPassword = findViewById(R.id.sender_password);
        senderRequestNumber = findViewById(R.id.sender_request_number);
        senderInvoiceNumber = findViewById(R.id.sender_invoice_number);
        serviceCode = findViewById(R.id.service_code);
        description = findViewById(R.id.request_description);
        settlementAccountCode = findViewById(R.id.settlement_account_code);
        settlementAmountDescription = findViewById(R.id.settlement_amount_description);
        settlementAmountValue = findViewById(R.id.settlement_amount);
        currency = findViewById(R.id.currency);
        mobileNumber = findViewById(R.id.mobile_number);
        email = findViewById(R.id.email);
        userUniqueIdentifier = findViewById(R.id.user_unique_identifier);
        additionalInfo = findViewById(R.id.additional_info);
        expiryDate = findViewById(R.id.request_expiry_date);

        paymentMechanismType = findViewById(R.id.rg_account_type);

        continueButton = findViewById(R.id.btn_Continue);

        calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
        expiryDate.setOnClickListener(v -> new DatePickerDialog(CreatePaymentActivity.this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        continueButton.setOnClickListener(v -> createPayment());
        senderRequestNumber.setText(generateRequestNumber());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        expiryDate.setText(simpleDateFormat.format(Calendar.getInstance().getTime()));
    }

    public void createPayment() {
        try {

            PaymentCreationRequest paymentCreationRequest = new PaymentCreationRequest();

            paymentCreationRequest.Sender.Id = BuildConfig.SENDER_ID;
            paymentCreationRequest.Sender.Name = senderName.getText().toString();
            paymentCreationRequest.Sender.Password = BuildConfig.SENDER_PASSWORD;
            paymentCreationRequest.Description = description.getText().toString();
            paymentCreationRequest.SenderInvoiceNumber = senderInvoiceNumber.getText().toString();
            paymentCreationRequest.AdditionalInfo = additionalInfo.getText().toString();

            paymentCreationRequest.SenderRequestNumber = senderRequestNumber.getText().toString();

            paymentCreationRequest.ServiceCode = BuildConfig.SERVICE_CODE;

            PaymentCreationRequest.SettlementAmount settlementAmount = new PaymentCreationRequest.SettlementAmount();

            settlementAmount.Amount = Double.parseDouble(settlementAmountValue.getText().toString());
            settlementAmount.SettlementAccountCode = Integer.parseInt(BuildConfig.SETTLEMENT_ACCOUNT_CODE);
            settlementAmount.Description = settlementAmountDescription.getText().toString();

            paymentCreationRequest.SettlementAmounts.add(settlementAmount);

            paymentCreationRequest.Currency = BuildConfig.CURRENCY_CODE;

            mechanismTypeButton = findViewById(paymentMechanismType.getCheckedRadioButtonId());

            

            if (mechanismTypeButton.getText().toString().equals("Card")) {
                paymentCreationRequest.PaymentMechanism.Type = PaymentCreationRequest.PaymentMechanismType.Card;
            } else if (mechanismTypeButton.getText().toString().equals("Channel")){
                paymentCreationRequest.PaymentMechanism.Type = PaymentCreationRequest.PaymentMechanismType.Channel;

                paymentCreationRequest.PaymentMechanism.Channel.Email = email.getText().toString();
                paymentCreationRequest.PaymentMechanism.Channel.MobileNumber = mobileNumber.getText().toString();
            }
            else if(mechanismTypeButton.getText().toString().equals(("Mobile Wallet"))) {
                paymentCreationRequest.PaymentMechanism.MobileWallet.MobileNumber = mobileNumber.getText().toString();
            }
            else if(mechanismTypeButton.getText().toString().equals("Meeza")) {
                paymentCreationRequest.PaymentMechanism.Meeza.Tahweel.MobileNumber = mobileNumber.getText().toString();
            }

            paymentCreationRequest.RequestExpiryDate = expiryDate.getText().toString();

            paymentCreationRequest.UserUniqueIdentifier = userUniqueIdentifier.getText().toString();

            paymentCreationRequest.serialize();
            //String signature = CryptoHelp.signData(paymentCreationRequest.serialize(), this);


            // Your public key encoded as base64 string should be provided here instead
            String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4vDGDLMEPRUJmT7BC4mL\n" +
                    "32e+jORKSMq3rv+FTrXAUzatQ18je2C3YtGMcy1k7m9v4V6gswxJvJEPPHzJE+dZ\n" +
                    "bwWZYhlmgxfyA0yTu8JVrAlcPbX0VHKxAsorbgTmrNyPitdEeYneARKmqDCdYIqx\n" +
                    "e76l3R1YoiILe2CVB185sTQ3TDgtfgCgpfWbCZbhmnyMIW3QiaDX7bfrMtv30qpj\n" +
                    "MG73570cxoX9Zkq3tUj/orYrM+D9+gHscnZke94x7Zwey/VwjUeIFifLuD3XTv01\n" +
                    "ifiwqIgOtbchdmoWDTAmwMfd6lhrK6kr/d9oK6I2vPwc+MyJhut0Njwx8h7OF0zg\n" +
                    "/QIDAQAB";



            paymentGateway.CreatePayment(paymentCreationRequest, "", publicKey, new MobilePaymentCreationCallback());
        } catch (Exception ex) {
            Log.i("Error", ex.getMessage());
        }
    }


    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        expiryDate.setText(sdf.format(calendar.getTime()));
    }

    private String generateRequestNumber() {
        Random random = new Random();
        int requestNumber = random.nextInt(999999999);

        return String.format("%06d", requestNumber);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    class MobilePaymentCreationCallback implements PaymentCreationCallback {

        @Override
        public void onSuccess(PaymentCreationResponse response) {
            Log.i(CreatePaymentActivity.class.getSimpleName(), "Request Completed Successfully");

            Toast.makeText(CreatePaymentActivity.this, "Payment Created Successfully", Toast.LENGTH_LONG).show();
            Log.e("sender", response.OriginalSenderRequestNumber);
            if (mechanismTypeButton.getText().toString().equals("Card")) {

                Intent intent = new Intent(CreatePaymentActivity.this, com.efinance.mobilepaymentintegrationsamplecode.main.activities.ConfirmPaymentActivity.class);

                intent.putExtra("senderRequestNumber", response.OriginalSenderRequestNumber);
                intent.putExtra("cardRequestNumber", response.CardRequestNumber);
                intent.putExtra("sessionID", response.SessionId);
                intent.putExtra("amount", Double.toString(response.TotalAuthorizationAmount));

                startActivity(intent);
            }
            else if(mechanismTypeButton.getText().toString().equals("Channel") ||
                    mechanismTypeButton.getText().toString().equals("Mobile Wallet") ||
                    mechanismTypeButton.getText().toString().equals("Meeza")) {

                Toast.makeText(CreatePaymentActivity.this, "Use this number as a reference: " + response.CardRequestNumber, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(CreatePaymentActivity.this, com.efinance.mobilepaymentintegrationsamplecode.main.activities.PaymentStatusInquiryActivity.class);

                intent.putExtra("senderRequestNumber", response.OriginalSenderRequestNumber);

                startActivity(intent);
            }

        }

        @Override
        public void onError(PaymentException paymentException) {
            Toast.makeText(CreatePaymentActivity.this,  paymentException.details.toString(), Toast.LENGTH_LONG).show();
            Log.e(CreatePaymentActivity.class.getSimpleName(), paymentException.code);
            Log.e(CreatePaymentActivity.class.getSimpleName(), paymentException.details.toString());
        }
    }
}