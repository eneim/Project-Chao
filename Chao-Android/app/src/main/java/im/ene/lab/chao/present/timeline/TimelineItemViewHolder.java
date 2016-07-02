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

package im.ene.lab.chao.present.timeline;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.bumptech.glide.Glide;
import im.ene.lab.chao.R;
import im.ene.lab.chao.base.recyclerview.ViewHolder;
import im.ene.lab.chao.data.entity.Article;
import im.ene.lab.chao.util.CircleTransform;

/**
 * Created by eneim on 7/2/16.
 */
public class TimelineItemViewHolder extends ViewHolder {

  static final int LAYOUT_RES = R.layout.vh_timeline_item;

  @BindView(R.id.user_avatar) ImageButton userAvatar;
  @BindView(R.id.user_name) TextView userName;
  @BindView(R.id.user_language) TextView userLanguage;

  @BindView(R.id.item_text) TextView itemText;
  @BindView(R.id.item_language_flag) ImageView itemLangFlag;
  @BindView(R.id.item_text_translated) TextView itemTranslated;
  @BindView(R.id.translation_language_flag) ImageView translationFlag;

  @BindView(R.id.more_flag) ImageView moreFlag;
  @BindView(R.id.more_content) TextView moreContent;
  @BindView(R.id.positive_count) TextView positiveCount;

  private final CircleTransform circleTransform;
  private Article article;

  public TimelineItemViewHolder(View itemView) {
    super(itemView);
    circleTransform = new CircleTransform(itemView.getContext());
  }

  @Override public <T extends RecyclerView.Adapter> void bind(T parent, Object item) {
    if (!(item instanceof Article)) {
      throw new IllegalArgumentException("Not support");
    }

    article = (Article) item;

    Glide.with(context)
        .load("http://www.onlinestores.com/flagdetective/images/download/vietnam-hi.jpg")
        .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_circle))
        .error(ContextCompat.getDrawable(context, R.drawable.placeholder_circle))
        .transform(circleTransform)
        .into(userAvatar);

    userName.setText(R.string.app_name);
    userLanguage.setText("Vietnamese");

    itemText.setText(R.string.dummy);
    itemTranslated.setText(R.string.dummy_translated);

    Glide.with(context)
        .load("http://www.onlinestores.com/flagdetective/images/download/vietnam-hi.jpg")
        .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_circle))
        .error(ContextCompat.getDrawable(context, R.drawable.placeholder_circle))
        .transform(circleTransform)
        .into(itemLangFlag);

    Glide.with(context)
        .load("http://www.onlinestores.com/flagdetective/images/download/vietnam-hi.jpg")
        .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_circle))
        .error(ContextCompat.getDrawable(context, R.drawable.placeholder_circle))
        .transform(circleTransform)
        .into(translationFlag);

    Glide.with(context)
        .load("http://www.onlinestores.com/flagdetective/images/download/vietnam-hi.jpg")
        .transform(circleTransform)
        .into(moreFlag);
    moreContent.setText("その他240翻訳");

    positiveCount.setText("256");
  }
}
