package com.byteshaft.foodtruck.accounts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.byteshaft.foodtruck.R;
import com.byteshaft.foodtruck.utils.AppGlobals;
import com.byteshaft.foodtruck.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class CodeConfirmationActivity extends AppCompatActivity implements
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private EditText codeField;
    private Button confirmButton;
    private String mCodeText;
    private HttpRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("Code Confirmation");
        setContentView(R.layout.activity_code_confirmation);
        codeField = (EditText) findViewById(R.id.et_code);
        confirmButton = (Button) findViewById(R.id.button_activate_account);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    activateUser(mCodeText);
                }
            }
        });
    }

    public boolean validate() {
        boolean valid = true;
        mCodeText = codeField.getText().toString();
        if (mCodeText.trim().isEmpty()) {
            codeField.setError("required");
        } else {
            codeField.setError(null);
        }
        return valid;
    }

    private void activateUser(String email) {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%suser/activate", AppGlobals.BASE_URL));
        request.send(getUserActivationData(email));
        Helpers.showProgressDialog(CodeConfirmationActivity.this, "Activating User");
    }

    private String getUserActivationData(String email) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }
}
