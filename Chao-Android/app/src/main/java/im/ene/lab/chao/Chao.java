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

package im.ene.lab.chao;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by eneim on 7/2/16.
 */
public class Chao extends Application {

  private static Chao sInstance;

  public static SharedPreferences getPref() {
    return sInstance.getSharedPreferences("chao.project", Context.MODE_PRIVATE);
  }

  @Override public void onCreate() {
    super.onCreate();
    sInstance = this;

    RealmConfiguration configuration =
        new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded()
            .migration(new RealmMigration() {
              @Override public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
              }
            })
            .name("chao.realm")
            .build();

    Realm.setDefaultConfiguration(configuration);

    Stetho.initialize(Stetho.newInitializerBuilder(this)
        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
        .build());

    FacebookSdk.sdkInitialize(getApplicationContext());
    AppEventsLogger.activateApp(this);
  }

  public static Chao getInstance() {
    return sInstance;
  }
}
