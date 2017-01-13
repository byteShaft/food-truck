package com.byteshaft.foodtruck.accounts;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.byteshaft.foodtruck.R;
import com.github.siyamed.shapeimageview.CircularImageView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private CircularImageView mImageView;
    private Button mRegisterButton;
    private EditText mUsername;
    private EditText mEmailAddress;
    private EditText mPassword;
    private EditText mVerifyPassword;
    private EditText mAddress;
    private EditText mPhoneNumber;
    private EditText mTruckName;
    private EditText mProducts;
    private EditText mLocation;

    private String mUsernameString;
    private String mEmailAddressString;
    private String mVerifyPasswordString;
    private String mPasswordString;
    private String mAddressString;
    private String mPhoneNumberString;
    private String mTruckNameString;
    private String mProductsString;
    private String mLocationString;

    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mImageView = (CircularImageView) findViewById(R.id.photo);
        mAddress = (EditText) findViewById(R.id.address);
        mPhoneNumber = (EditText) findViewById(R.id.phone_number);
        mTruckName = (EditText) findViewById(R.id.truck_name);
        mProducts = (EditText) findViewById(R.id.products);
        mUsername = (EditText) findViewById(R.id.user_name);
        mLocation = (EditText) findViewById(R.id.location);
        mEmailAddress = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mVerifyPassword = (EditText) findViewById(R.id.verify_password);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(this);
        mImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.photo:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
                break;

            case R.id.register_button:
                validateEditText();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                mImageView.setImageURI(selectedImageUri);
            }
        }
    }

    private boolean validateEditText() {
        boolean valid = true;
        mPasswordString = mPassword.getText().toString();
        mVerifyPasswordString = mVerifyPassword.getText().toString();
        mEmailAddressString = mEmailAddress.getText().toString();
        mUsernameString = mUsername.getText().toString();

        mLocationString = mLocation.getText().toString();
        mAddressString = mAddress.getText().toString();
        mPhoneNumberString = mPhoneNumber.getText().toString();
        mTruckNameString = mTruckName.getText().toString();
        mProductsString = mProducts.getText().toString();

        if (mAddressString.trim().isEmpty()) {
            mAddress.setError("required");
        } else {
            mAddress.setError(null);
        }

        if (mLocationString.trim().isEmpty()) {
            mLocation.setError("required");
        } else {
            mLocation.setError(null);
        }

        if (mPhoneNumberString.trim().isEmpty()) {
            mPhoneNumber.setError("required");
        } else {
            mPhoneNumber.setError(null);
        }

        if (mTruckNameString.trim().isEmpty()) {
            mTruckName.setError("required");
        } else {
            mTruckName.setError(null);
        }

        if (mProductsString.trim().isEmpty()) {
            mProducts.setError("required");
        } else {
            mProducts.setError(null);
        }

        if (mUsernameString.trim().isEmpty()) {
            mUsername.setError("required");
        } else {
            mUsername.setError(null);
        }

        if (mPasswordString.trim().isEmpty() || mPasswordString.length() < 3) {
            mPassword.setError("enter at least 3 characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        if (mVerifyPasswordString.trim().isEmpty() || mVerifyPasswordString.length() < 3 ||
                !mVerifyPasswordString.equals(mPasswordString)) {
            mVerifyPassword.setError("password does not match");
            valid = false;
        } else {
            mVerifyPassword.setError(null);
        }

        if (mEmailAddressString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddressString).matches()) {
            mEmailAddress.setError("please provide a valid email");
            valid = false;
        } else {
            mEmailAddress.setError(null);
        }
        return valid;
    }
}
