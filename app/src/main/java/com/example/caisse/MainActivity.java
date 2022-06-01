package com.example.caisse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caisse.activities.WelcomeActivity;
import com.example.caisse.interfaces.VolleyResponseListener;
import com.example.caisse.requests.AuthRequest;
import com.example.caisse.services.AuthService;
import com.example.caisse.singletons.SharedAuthSingleton;
import com.example.caisse.singletons.SharedStaffSingleton;
import com.example.caisse.utils.EditTextUtils;
import com.example.caisse.utils.MapUtils;
import com.example.caisse.utils.TextViewUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements Validator.ValidationListener {

    @Email
    @NotEmpty
    private EditText editTextEmail;

    @NotEmpty
    private EditText editTextPassword;

    private Validator validator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        redirectUserIfAuthenticated();
        initView();
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    /* Initialise class variables */
    private void initView() {
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        TextView textViewHelp = findViewById(R.id.viewTextHelp);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        textViewHelp.setOnClickListener(this::handleHelp);
        btnSubmit.setOnClickListener(this::handleLogin);
    }

    /* Help handler */
    private void handleHelp(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"some@email.address"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Login Error");
        startActivity(Intent.createChooser(intent, ""));
    }

    /* Login handler */
    private void handleLogin(View view) {
        validator.validate();
    }

    /* Submit data to server after validation form */
    @Override
    public void onValidationSucceeded() {
        /* Get data from fields */
        String email = TextViewUtils.getText(editTextEmail);
        String password = TextViewUtils.getText(editTextPassword);

        /* Initiate custom login service */
        AuthService authService = new AuthService(MainActivity.this);
        AuthRequest authRequest = new AuthRequest(email, password);

        /* Login process */
        authService.login(authRequest, new VolleyResponseListener() {
            /* Handle success response */
            @Override
            public void onResponse(Map<String, String> response) {
                String role = getStringBetweenBrackets(response.get("role"));
                String token = response.get("token");

                authRequest.setToken(token);
                authRequest.setPassword("");
                authRequest.setRole(role);

                SharedAuthSingleton.getInstance(MainActivity.this).userLogin(authRequest);
                SharedStaffSingleton.getInstance(MainActivity.this).userLogin(authRequest);

                finish();
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }

            /* Handle error response */
            @Override
            public void onError(Map<String, String> response) {
                MapUtils.handleErrorResponse(response, getApplicationContext());
                resetForm();
                addErrorEffect();
            }
        });
    }

    /* Display error validation form */
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(MainActivity.this);
            /* Display error messages */
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
                EditTextUtils.setBgResource((EditText) view, R.drawable.ic_error_edit_text_corner);
            } else
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    /* Go to welcome activity if the user is authenticated */
    private void redirectUserIfAuthenticated() {
        boolean isLoggedIn = SharedAuthSingleton.getInstance(MainActivity.this).isLoggedIn();
        if (isLoggedIn) {
            finish();
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
        }
    }

    /* Reset form */
    private void resetForm() {
        EditTextUtils.clearText(editTextEmail);
        EditTextUtils.clearText(editTextPassword);
    }

    /* Add error outline to Edit text fields */
    private void addErrorEffect() {
        EditTextUtils.setBgResource(editTextEmail, R.drawable.ic_edit_text_background);
        EditTextUtils.setBgResource(editTextPassword, R.drawable.ic_edit_text_background);
    }

    /* Prevent to go back */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /* Get a string inside bracket */
    private String getStringBetweenBrackets(String ch) {
        final Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        final Matcher matcher = pattern.matcher(ch);
        boolean isFound = matcher.find();
        return isFound ? matcher.group(1).replace("\"", "").replace("ROLE_", "") : "";
    }

}