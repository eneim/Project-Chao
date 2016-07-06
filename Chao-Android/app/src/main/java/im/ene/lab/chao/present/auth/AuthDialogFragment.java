/*
 * Copyright 2016 Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.chao.present.auth;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import im.ene.lab.chao.R;
import im.ene.lab.chao.widget.FullScreenDialogFragment;

/**
 * Created by eneim on 7/3/16.
 */
public class AuthDialogFragment extends FullScreenDialogFragment
    implements FacebookCallback<LoginResult> {

  public static final String TAG = "AuthFragment";

  public static AuthDialogFragment newInstance() {
    return new AuthDialogFragment();
  }

  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;

  private CallbackManager callbackManager;
  private LoginButton loginButton;
  private View fbLoginButton;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.content_main, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mAuth = FirebaseAuth.getInstance();
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          // User is signed in
          Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
          // User is signed out
          Log.d(TAG, "onAuthStateChanged:signed_out");
        }
        // ...
      }
    };

    callbackManager = CallbackManager.Factory.create();
    loginButton = (LoginButton) view.findViewById(R.id.login_button);
    loginButton.setReadPermissions("email", "public_profile", "user_friends");
    loginButton.registerCallback(callbackManager, this);
    loginButton.setFragment(this);

    fbLoginButton = view.findViewById(R.id.overlap_facebook_login);
    fbLoginButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        loginButton.performClick();
      }
    });
  }

  @Override public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthListener);
  }

  @Override public void onStop() {
    super.onStop();
    if (mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
    }
  }

  // Facebook login
  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void onSuccess(LoginResult loginResult) {
    Log.d(TAG, "facebook:onSuccess:" + loginResult);
    handleFacebookAccessToken(loginResult.getAccessToken());
  }

  @Override public void onCancel() {
    Log.d(TAG, "facebook:onCancel");
  }

  @Override public void onError(FacebookException error) {
    Log.d(TAG, "facebook:onError", error);
  }
  // Facebook login

  // [START auth_with_facebook]
  private void handleFacebookAccessToken(AccessToken token) {
    Log.d(TAG, "handleFacebookAccessToken:" + token);
    // [START_EXCLUDE silent]
    // showProgressDialog();
    // [END_EXCLUDE]
    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
          @Override public void onComplete(@NonNull Task<AuthResult> task) {
            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            if (!task.isSuccessful()) {
              Log.w(TAG, "signInWithCredential", task.getException());
              Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
            // [START_EXCLUDE]
            // hideProgressDialog();
            dismiss();
            // [END_EXCLUDE]
          }
        });
  }

  @Override public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (getTargetFragment() != null) {
      getTargetFragment().onActivityResult(1000, Activity.RESULT_OK, null);
    }
  }

  // [END auth_with_facebook]
}
