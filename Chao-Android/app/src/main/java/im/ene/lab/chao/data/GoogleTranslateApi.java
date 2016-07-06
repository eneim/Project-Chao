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

import im.ene.lab.chao.BuildConfig;
import im.ene.lab.chao.data.entity.Item;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by eneim on 7/2/16.
 */
public interface GoogleTranslateApi {

  String BASE_URL = /* "https://www.googleapis.com/" */ BuildConfig.BASE_URL;

  //@GET("language/translate/v2") //
  //Observable<Item> translate(@Query("source") Language source, @Query("target") Language target,
  //    @Query("q") String content);

  @FormUrlEncoded @POST("api/translate/") //
  Observable<Item> translate(
      @Field("from") Language source, @Field("to") Language des,
      @Field("text") String content);
}
