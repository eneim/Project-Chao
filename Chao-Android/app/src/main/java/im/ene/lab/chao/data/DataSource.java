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
import im.ene.lab.chao.R;
import im.ene.lab.chao.data.entity.Item;
import im.ene.lab.chao.data.model.Article;
import im.ene.lab.chao.data.model.Sentence;
import im.ene.lab.chao.data.model.User;
import im.ene.lab.chao.util.IOUtil;
import io.realm.Realm;
import io.realm.RealmList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by eneim on 7/2/16.
 */
public class DataSource {

  static final OkHttpClient client;
  static final Retrofit retrofit;
  static final Gson gson;

  static final OkHttpClient otherClient;

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

    otherClient = new OkHttpClient.Builder().cache(
        new Cache(Chao.getInstance().getCacheDir(), 16 * 1024 * 1024))
        .addNetworkInterceptor(new StethoInterceptor())
        .build();
  }

  public static Observable<Item> translate(String content) {
    return translateApi.translate(Language.VIETNAMESE, Language.JAPANESE, content);
  }

  static final String TATOEBA =
      "https://tatoeba.org/eng/sentences/search/page:{page}?query={query}&from={from}&to={to}";

  public static Observable<List<Article>> search(String text, String from, String to, int page) {
    final String requestUrl = TATOEBA.replace("{page}", page + "")
        .replace("{query}", text == null ? "" : text)
        .replace("{from}", from == null ? "und" : from)
        .replace("{to}", to == null ? "und" : to);

    HttpUrl url = HttpUrl.parse(requestUrl);
    final Request request = new Request.Builder().url(url).get().build();

    return Observable.defer(new Func0<Observable<List<Article>>>() {
      @Override public Observable<List<Article>> call() {
        return Observable.create(new Observable.OnSubscribe<List<Article>>() {
          @Override public void call(Subscriber<? super List<Article>> subscriber) {
            try {
              Response response = otherClient.newCall(request).execute();
              final Document document =
                  Jsoup.parse(response.body().byteStream(), "utf-8", requestUrl);
              Elements blocks = document.getElementsByClass("sentences_set");
              List<Article> articles = new ArrayList<>();
              if (blocks != null && blocks.size() > 0) {
                for (Element element : blocks) {
                  String[] idParts = element.id().split("_");
                  Article article = Realm.getDefaultInstance()
                      .where(Article.class)
                      .equalTo("id", idParts[idParts.length - 1])
                      .findFirst();
                  if (article == null) {
                    article = new Article();
                    article.id = idParts[idParts.length - 1];
                  }

                  User owner = new User();
                  owner.id = element.select(
                      "#sentences_group_{id} > ul > li.adopt > a".replace("{id}", article.id))
                      .text();
                  owner.userName = owner.id;

                  Sentence source = new Sentence();

                  Element content =
                      element.getElementById("translation_{id}_".replace("{id}", article.id));
                  source.id = article.id;
                  source.textLanguage = content.select("div.lang.column > img").attr("alt");
                  source.text = content.select("div.content.column > div.sentenceContent").text();
                  owner.motherTongue = source.textLanguage;
                  source.creator = owner;
                  article.source = source;
                  article.textLength = source.getText().length();

                  // Translations
                  Element translations =
                      element.getElementById("_{id}_translations".replace("{id}", article.id));
                  if (translations != null && !IOUtil.isEmpty(translations.children())) {
                    article.translations = new RealmList<>();
                    for (Element translation : translations.children()) {
                      String[] ids = translation.id().split("_");
                      if (ids.length > 1) {
                        Sentence translated =
                            extract(translation, translation.id().split("_")[1], article.id);
                        article.translations.add(translated);
                      }
                    }
                  }

                  articles.add(article);
                }
              }
              subscriber.onNext(articles);
              subscriber.onCompleted();
            } catch (ArrayIndexOutOfBoundsException | IOException e) {
              e.printStackTrace();
              subscriber.onError(e);
            }
          }
        });
      }
    });
  }

  public static int getFlagResource(String lang) {
    if ("vie".equals(lang)) {
      return R.drawable.vn;
    } else if ("eng".equals(lang)) {
      return R.drawable.us;
    } else if ("cmn".equals(lang)) {
      return R.drawable.cn;
    } else if ("jpn".equals(lang)) {
      return R.drawable.jp;
    } else {
      return R.drawable.placeholder_circle;
    }
  }

  private static Sentence extract(Element source, String fromId, String toId) {
    String selector = "translation_{from}_{to}".replace("{from}", fromId).replace("{to}", toId);

    Sentence sentence = new Sentence();
    sentence.id = fromId;
    sentence.textLanguage =
        source.select("#{selector} > div.lang.column > img".replace("{selector}", selector))
            .attr("alt");
    sentence.text = source.select(
        "#{selector} > div.content.column > div.sentenceContent > div.text.correctnessZero".replace(
            "{selector}", selector)).text();

    return sentence;
  }
}
