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

package im.ene.lab.chao.data;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import im.ene.lab.chao.Chao;
import im.ene.lab.chao.data.entity.Item;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by eneim on 7/2/16.
 */
public class DataSource {

  static final OkHttpClient client;
  static final Retrofit retrofit;
  static final Gson gson;

  static final GoogleTranslateApi translateApi;

  static {
    gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    client = new OkHttpClient.Builder().cache(
        new Cache(Chao.getInstance().getCacheDir(), 16 * 1024 * 1024))
        .addNetworkInterceptor(new StethoInterceptor())
        .build();
    retrofit = new Retrofit.Builder().baseUrl(GoogleTranslateApi.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
        .build();

    translateApi = retrofit.create(GoogleTranslateApi.class);
  }

  public static Observable<Item> translate(String content) {
    return translateApi.translate(Language.VIETNAMESE, Language.JAPANESE, content);
  }
}
