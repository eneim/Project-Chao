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

package im.ene.lab.chao.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.util.Date;

/**
 * Created by eneim on 7/2/16.
 */
public class Article extends RealmObject {

  @PrimaryKey @Expose public String id;

  @Expose public Sentence source;

  @Expose public RealmList<Sentence> translations;

  @Expose public int positiveCount;

  public int textLength;

  public int evaluation;  // 0: no selection, 1: thumb up clicked, -1: thumb down clicked;

  @SerializedName("updated_at") @Expose public String updatedAt;

  public String getId() {
    return id;
  }

  public Sentence getSource() {
    return source;
  }

  public RealmList<Sentence> getTranslations() {
    return translations;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public int getPositiveCount() {
    return positiveCount;
  }

  public int getEvaluation() {
    return evaluation;
  }

  @Override public String toString() {
    return "Article{" +
        "id='" + id + '\'' +
        ", source=" + source +
        ", translations=" + translations +
        ", positiveCount=" + positiveCount +
        ", updatedAt='" + updatedAt + '\'' +
        '}';
  }

  public int getTextLength() {
    return textLength;
  }

  public Article() {
    double random = Math.random();
    positiveCount = (int) (random * 256);
    if (random > 0.55) {
      evaluation = 0;
    } else if (random > 0.20) {
      evaluation = 1;
    } else {
      evaluation = -1;
    }

    updatedAt = Long.toString((new Date()).getTime());
  }
}
