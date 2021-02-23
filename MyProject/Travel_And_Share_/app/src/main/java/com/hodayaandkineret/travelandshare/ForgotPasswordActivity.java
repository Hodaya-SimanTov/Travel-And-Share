package com.hodayaandkineret.travelandshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
EditText inputMail;
Button btnSave;
FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        inputMail=findViewById(R.id.activity_forgetPassword_inputMail);
        btnSave=findViewById(R.id.activity_forgetPassword_btn_send);
        mAuth=FirebaseAuth.getInstance();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLinkToMail();
            }
        });
    }

    private void sendLinkToMail() {
        String mail=inputMail.getText().toString();
        if(mail.isEmpty()){
            Toast.makeText(ForgotPasswordActivity.this,"Please Enter Your Email", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(ForgotPasswordActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else{
            mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ForgotPasswordActivity.this,"Please Check Your Email", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(ForgotPasswordActivity.this,"Email was not Send! ", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });

        }

    }
}