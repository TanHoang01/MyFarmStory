package com.example.myfarmstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.myfarmstory.databinding.ActivityForgotBinding;
import com.example.myfarmstory.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {
    ActivityForgotBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btSendPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.eEmail.getText().toString().length() == 0){
                    Toast.makeText(ForgotActivity.this,"Bạn Chưa Điền Đủ Thông Tin!",Toast.LENGTH_SHORT).show();
                }
                else{
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(binding.eEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotActivity.this, "Hãy Kiểm Tra Email Để Đổi Mật Khẩu", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(ForgotActivity.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });
        binding.tvbacktosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}