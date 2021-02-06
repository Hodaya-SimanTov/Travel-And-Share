package com.hodayaandkineret.travelandshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout InputMail, InputPassword;
    Button loginBtn;
    TextView forgotPassword,createAccount;
    ProgressDialog myLoadingDialog;
    FirebaseAuth myAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InputMail=findViewById(R.id.activity_login_input_mail);
        InputPassword=findViewById(R.id.activity_login_input_password);
        loginBtn=findViewById(R.id.activity_login_btn_signin);
        forgotPassword=findViewById(R.id.activity_login_forget_password);
        createAccount=findViewById(R.id.activity_login_create_new_account);
        myLoadingDialog=new ProgressDialog(this);
        myAuth=FirebaseAuth.getInstance();
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignUp();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLogin();
            }
        });

    }
    private void goToSignUp(){
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);

    }
    private void UserLogin(){
        String mail= InputMail.getEditText().getText().toString();
        String password= InputPassword.getEditText().getText().toString();

        if(mail.isEmpty() || !mail.contains("@")){
            showError(InputMail,"Email Address is not Valid");
        }
        else if(password.isEmpty() || password.length()<6){
            showError(InputPassword,"Password must be longer than 6 characters");
        }
        else{
            myLoadingDialog.setTitle("Login");
            myLoadingDialog.setMessage("Please Wait,Log-in To Your Account");
            myLoadingDialog.setCanceledOnTouchOutside(false);
            myLoadingDialog.show();
            myAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        myLoadingDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Log-in Succeeded",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        myLoadingDialog.dismiss();
                        Toast.makeText(LoginActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }
    private void showError(TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }
}