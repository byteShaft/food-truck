package com.byteshaft.foodtruck.accounts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.byteshaft.foodtruck.R;

public class ForgotPasswordActivity extends AppCompatActivity {


    private Button mRecoverButton;
    private EditText mEmail;
    private String mEmailString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        mEmail = (EditText) findViewById(R.id.email_address);
        mRecoverButton = (Button) findViewById(R.id.recover);
        mRecoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    // TODO: 13/01/2017 send request to recover password
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);

    }

    public boolean validate() {
        boolean valid = true;
        mEmailString = mEmail.getText().toString();

        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.
                matcher(mEmailString).matches()) {
            mEmail.setError("enter a valid email address");
            valid = false;
        }
        return valid;
    }
}
