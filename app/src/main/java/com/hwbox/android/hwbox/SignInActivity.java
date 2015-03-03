package com.hwbox.android.hwbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.File;

/**
 * Created by omer on 23.12.2014.
 */
public class SignInActivity extends Activity
{
    private String userInfo;

    private Button signInButton;
    private Button signUpButton;
    private EditText emailET;
    private EditText nameET;
    private EditText passwordET;

    private static boolean isConnected;

    private static String userName;
    private static String userPassword;
    private static ParseUser userPointer;



    protected static String getUserName()
    {
        return userName;
    }
    protected static String getUserPassword()
    {
        return userPassword;
    }
    protected static ParseUser getUserPointer()
    {
        return userPointer;
    }
    protected static Boolean getConnectionState()
    {
        return isConnected;
    }



    private boolean checkInternetConnection()
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.sign_in);




        isConnected = checkInternetConnection();
        Log.d("internet connection ", isConnected + "");
        Toast.makeText(this, "connected " + isConnected, Toast.LENGTH_LONG).show();

        signInButton = (Button) findViewById( R.id.sign_in);
        signInButton.setOnClickListener( buttonListener);
        signUpButton = (Button) findViewById( R.id.sign_up);
        signUpButton.setOnClickListener(buttonListener);

        emailET = (EditText) findViewById( R.id.email_entry);
        nameET = (EditText) findViewById( R.id.name_entry);
        passwordET = (EditText) findViewById( R.id.password_entry);
    }


    View.OnClickListener buttonListener = new View.OnClickListener()
    {

        private boolean isStored( String userInfo)
        {
            return (new File( "/data/data/" + getPackageName() + "/shared_prefs/" + userInfo + ".xml")).exists();
        }


        @Override
        public void onClick(View v)
        {
            final String name = nameET.getText().toString();
            final String email = emailET.getText().toString();
            final String password = passwordET.getText().toString();
            userInfo = email + password;

            if( isConnected)
            {


                if( v == signInButton)
                {
                    ParseUser.logInInBackground(name, password, new LogInCallback()
                    {
                        @Override
                        public void done(ParseUser parseUser, ParseException e)
                        {
                            if (e == null)
                            {
                                Toast.makeText(getApplicationContext(), "user " + parseUser.getUsername()
                                        + " signed in successfully", Toast.LENGTH_LONG).show();


                                userName = name;
                                userPassword = password;
                                userPointer = parseUser;

                                //store in preferences if not already stored.
                                if (!isStored(userInfo))
                                {
                                    SharedPreferences userPrefs = getSharedPreferences(userInfo, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = userPrefs.edit();
                                    editor.putString("userName", userName);
                                    editor.putString("userPassword", userPassword);
                                    editor.putString("userPointer", userPointer.getObjectId());
                                    editor.apply();
                                }




                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                // must give appropriate messages on errors.
                                Log.d("SignIn exception", e.getMessage() + " message code: " + e.getCode());
                            }
                        }
                    });
                }
                else if( v == signUpButton)
                {
                    ParseUser user = new ParseUser();
                    user.setEmail( email);
                    user.setUsername( name);
                    user.setPassword( password);
                    user.signUpInBackground( new SignUpCallback()
                    {
                        @Override
                        public void done(ParseException e)
                        {

                            if( e == null)
                            {
                                Toast.makeText( getApplicationContext(), "sign up successful", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent( getApplicationContext(), MainActivity.class);

                                userName = name;
                                userPassword = password;
                                userPointer = ParseUser.getCurrentUser();


                                //save user to shared preferences.
                                SharedPreferences userPrefs = getSharedPreferences( userInfo, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = userPrefs.edit();
                                editor.putString( "userName", userName);
                                editor.putString( "userPassword", userPassword);



                                Log.d("user pointer first save", userPointer + "  ");
                                editor.putString("userPointer", userPointer.getObjectId()); // this may be null control it
                                editor.apply();



                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                //like the previous one
                                Log.d("SignUp exception", e.getMessage());
                            }
                        }
                    });
                }
            }
            else
            {


                if( v == signInButton )
                {
                    //look for the user in shared preferences.

                    Log.d("offline authentication", "is a member " + isStored( userInfo));

                    if( isStored( userInfo))
                    {
                        SharedPreferences userPrefs = getSharedPreferences( userInfo, Context.MODE_PRIVATE);
                        userName = userPrefs.getString( "userName", null);
                        userPassword = userPrefs.getString( "userPassword", null);
                        userPointer = new ParseUser();
                        userPointer.setObjectId(userPrefs.getString( "userPointer", null));


                        if( userPassword.equals(password))
                        {
                            Intent intent = new Intent( getApplicationContext(), MainActivity.class);
                            startActivity( intent);
                            finish();
                        }

                    }
                    else
                    {
                        Toast.makeText( getBaseContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                    }


                }
                else if( v == signUpButton )
                {
                    Toast.makeText( getBaseContext(), "Oops, you cannot sign up in offline mode.", Toast.LENGTH_SHORT).show();

                }
            }
        }
    };
}
