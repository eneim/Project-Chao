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

package im.ene.lab.chao.present;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import im.ene.lab.chao.present.timeline.TimelineActivity;

/**
 * Created by eneim on 7/2/16.
 */
public class LaunchActivity extends AppCompatActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
      startActivity(new Intent(this, TimelineActivity.class));
    } else {
      startActivity(new Intent(this, AuthActivity.class));
    }
    // startService(new Intent(this, TatoebaDataParserService.class));
    finish();
  }
}
