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

package im.ene.lab.chao.util;

import android.os.Environment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by eneim on 7/2/16.
 */
public class IOUtil {

  public static final String SENTENCES_DETAIL = "sentences_detailed.csv";
  public static final String LINKS = "links.csv";

  private static File fetchFile(final String name) {
    File[] fetch = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        .listFiles(new FilenameFilter() {
          @Override public boolean accept(File file, String s) {
            return name.equals(s);
          }
        });

    if (fetch == null || fetch.length == 0) {
      return null;
    }

    return fetch[0];
  }

  public static File openSentenceDetailFile() {
    return fetchFile(SENTENCES_DETAIL);
  }

  public static File openLinksFile() {
    return fetchFile(LINKS);
  }

  public static Observable<String> readFile(final File file) {
    return Observable.create(new Observable.OnSubscribe<String>() {
      @Override public void call(Subscriber<? super String> subscriber) {
        BufferedReader reader = null;
        try {
          reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
          String line;
          while ((line = reader.readLine()) != null) {
            subscriber.onNext(line);
          }
          subscriber.onCompleted();
        } catch (IOException e) {
          e.printStackTrace();
          subscriber.onError(e);
        } finally {
          if (reader != null) {
            try {
              reader.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    });
  }

  public interface IOUtilCallback {

    void onString(String s);

    void onDone(Boolean success);
  }

  public static boolean isEmpty(List item) {
    return item == null || item.size() == 0;
  }
}
