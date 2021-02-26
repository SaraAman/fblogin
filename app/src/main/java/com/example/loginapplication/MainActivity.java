package com.example.loginapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.facebook.FacebookSdk.sdkInitialize;

public class MainActivity extends AppCompatActivity {
    private TextView user_name;
    private TextView email;
    private ImageView profile;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

//        try {
//            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.i("", "printHashKey() Hash Key: " + hashKey);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("", "printHashKey()", e);
//        } catch (Exception e) {
//            Log.e("", "printHashKey()", e);
//        }



        user_name = (TextView)findViewById(R.id.user_name);
        email = (TextView)findViewById(R.id.email);
        profile = findViewById(R.id.profile);
        loginButton = (LoginButton)findViewById(R.id.login_button);


        callbackManager = CallbackManager.Factory.create();

loginButton.setReadPermissions(Arrays.asList("email","public_profile"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                info.setText("User ID: " + loginResult.getAccessToken().getUserId() + "\n" + "Auth Token: "
//                        + loginResult.getAccessToken().getToken());


            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login attempt canceled.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Login attempt failed.", Toast.LENGTH_SHORT).show();

            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    AccessTokenTracker accessTokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null){
                email.setText("");
                user_name.setText("");
                profile.setImageResource(0);
                Toast.makeText(MainActivity.this, "user Logged out", Toast.LENGTH_SHORT).show();
            }
            else {
                loadUserProfile(currentAccessToken);
            }

        }
    };
    private void loadUserProfile(AccessToken accessToken)
    {
        GraphRequest graphRequest=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                if(object!=null){
                try {
                    String first_name=object.getString("first_name") ;
                    String useremail=object.getString("email") ;
                    String last_name=object.getString("last_name") ;
                    String id=object.getString("id") ;
                    String image="https://graph.facebook.com/"+id+ "/picture?type=normal";
                    email.setText(useremail);
                    user_name.setText(first_name+" "+last_name);
                   RequestOptions requestOptions=new RequestOptions();
                    requestOptions.dontAnimate();
                   Glide.with(MainActivity.this).load(image).into(profile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }}
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields"," first_name,last_name,email,id");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}

