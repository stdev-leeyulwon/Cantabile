package net.override.cantabile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

public class SignupActivity extends AppCompatActivity {

    EditText getemail, getnickname, getpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button signup = (Button) findViewById(R.id.register_btn);

        getemail = (EditText) findViewById(R.id.email_et);
        getnickname = (EditText) findViewById(R.id.nickname_et);
        getpassword = (EditText) findViewById(R.id.password_et);

        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = getemail.getText().toString().trim();
                String nickname = getnickname.getText().toString().trim();
                String password = getpassword.getText().toString().trim();
                String pwsha256 = toSHA256(password);

                (new HttpTask()).execute(nickname, email, pwsha256);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this, IntroActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    ProgressDialog mDialog;

    private class HttpTask extends AsyncTask<String, Void, Boolean> {

        String email = getemail.getText().toString().trim();
        String nickname = getnickname.getText().toString().trim();
        String password = getpassword.getText().toString().trim();
        String pwsha256 = toSHA256(password);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(SignupActivity.this);
            mDialog.setIndeterminate(true);
            mDialog.setMessage(getString(R.string.signupdialogue));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                HttpPost postRequest = new HttpPost(Constants.DBSERVER);

                Vector<NameValuePair> nameValue = new Vector<>();
                nameValue.add(new BasicNameValuePair("sheet_name", "Account_list"));
                nameValue.add(new BasicNameValuePair("nickname", nickname));
                nameValue.add(new BasicNameValuePair("email", email));
                nameValue.add(new BasicNameValuePair("password", pwsha256));

                HttpEntity Entity = new UrlEncodedFormEntity(nameValue, "UTF-8");
                postRequest.setEntity(Entity);

                HttpClient mClient = new DefaultHttpClient();
                mClient.execute(postRequest);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        protected void onPostExecute(Boolean value) {
            super.onPostExecute(value);

            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }

            if (value) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

            } else {
                //Tools.getstat(SignupActivity.this);
            }
        }
    }

    public String toSHA256(String str){
        String SHA = "";
        try{
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for(int i = 0 ; i < byteData.length ; i++){
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
            }
            SHA = sb.toString();

        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            SHA = null;
        }
        return SHA;
    }
}
