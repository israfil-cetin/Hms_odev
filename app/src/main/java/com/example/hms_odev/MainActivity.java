package com.example.hms_odev;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Trace;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.result.HuaweiIdAuthResult;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

public class MainActivity extends AppCompatActivity {
    Button btnLogin;
    TextView loginTxt;
    HuaweiIdAuthResult result = new HuaweiIdAuthResult();

    HuaweiIdAuthService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnLogin = findViewById(R.id.login_button);
        loginTxt = findViewById(R.id.user_id);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.i("TAGdeneme", "result:" +result.isSuccess());
                if (result.isSuccess()) {
                    Log.i("TAGdeneme", "result:" +result.isSuccess());
                    loginTxt.setText(R.string.outx);

                    service.signOut();
                    result = new HuaweiIdAuthResult();


                }else {
                    HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setId()
                            .setEmail() // bu metot e-mail almamıza yardımcı olanak sağlar. giriş yaparken emailinizi alacaktır diye sorar
                            .setAccessToken()
                            .createParams();
                    service = HuaweiIdAuthManager.getService(MainActivity.this, authParams);
                    startActivityForResult(service.getSignInIntent(), 8888);


                }

            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Process the authorization result to obtain an ID token from AuthHuaweiId.
        super.onActivityResult(requestCode, resultCode, data);

        handleSignInResult(data);

        if (requestCode == 8888) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                // The sign-in is successful, and the user's HUAWEI ID information and ID token are obtained.
                AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();

                loginTxt.setText(huaweiAccount.getEmail());
                Log.i("TAG", "idToken:" + huaweiAccount.getIdToken());
            } else {
                // The sign-in failed. No processing is required. Logs are recorded to facilitate fault locating.
                Log.e("TAG", "sign in failed : " +((ApiException)authHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }
    private void handleSignInResult(Intent data) {


        // Obtain the authorization response from the intent.
        result = HuaweiIdAuthAPIManager.HuaweiIdAuthAPIService.parseHuaweiIdFromIntent(data);
//        Log.d("TAG", "handleSignInResult status = " + result.getStatus() + ", result = " + result.isSuccess());


    }
}