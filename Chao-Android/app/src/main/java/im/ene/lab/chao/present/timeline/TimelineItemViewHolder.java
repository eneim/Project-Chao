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

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import im.ene.lab.chao.R;
import im.ene.lab.chao.base.recyclerview.ViewHolder;
import im.ene.lab.chao.data.DataSource;
import im.ene.lab.chao.data.model.Article;
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

  @BindView(R.id.more_translations) View moreTranslations;
  @BindView(R.id.more_flag) ImageView moreFlag;
  @BindView(R.id.more_content) TextView moreContent;
  @BindView(R.id.positive_count) TextView positiveCount;

  @BindView(R.id.icon_thumb_up) ImageButton thumbUp;
  @BindView(R.id.icon_thumb_down) ImageButton thumbDown;

  @BindDimen(R.dimen.avatar_radius) int avatarRadius;

  int[] colors = { Color.RED, Color.BLUE, Color.YELLOW, Color.GRAY };

  private final CircleTransform circleTransform;
  private Article article;

  public TimelineItemViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
    circleTransform = new CircleTransform(itemView.getContext());
  }

  @Override public <T extends RecyclerView.Adapter> void bind(T parent, Object item) {
    if (!(item instanceof Article)) {
      throw new IllegalArgumentException("Not support");
    }

    article = (Article) item;

    String ownerName = article.getSource().getCreator().getUserName();
    TextDrawable drawable = TextDrawable.builder()
        .buildRoundRect(ownerName.substring(0, 1).toUpperCase(),
            ColorGenerator.MATERIAL.getColor(ownerName), avatarRadius);

    userAvatar.setImageDrawable(drawable);

    userName.setText(article.getSource().getCreator().getUserName());
    userLanguage.setText(article.getSource().getCreator().getMotherTongue());

    itemText.setText(article.getSource().getText());

    if (article.getTranslations().size() > 0) {
      itemTranslated.setVisibility(View.VISIBLE);
      itemTranslated.setText(article.getTranslations().get(0).getText());
    } else {
      itemTranslated.setVisibility(View.GONE);
    }

    Glide.with(context)
        .load(DataSource.getFlagResource(article.getSource().getTextLanguage()))
        .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_circle))
        .error(ContextCompat.getDrawable(context, R.drawable.placeholder_circle))
        .transform(circleTransform)
        .into(itemLangFlag);

    if (article.getTranslations().size() > 0) {
      translationFlag.setVisibility(View.VISIBLE);
      Glide.with(context)
          .load(DataSource.getFlagResource(article.getTranslations().get(0).getTextLanguage()))
          .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_circle))
          .error(ContextCompat.getDrawable(context, R.drawable.placeholder_circle))
          .transform(circleTransform)
          .into(translationFlag);
    } else {
      translationFlag.setVisibility(View.GONE);
    }

    positiveCount.setText(article.getPositiveCount() + "");
    if (article.getEvaluation() == 1) {
      thumbDown.setImageResource(R.drawable.icon_circle_down);
      thumbUp.setImageResource(R.drawable.icon_circle_up_filled);
    } else if (article.getEvaluation() == -1) {
      thumbDown.setImageResource(R.drawable.icon_circle_down_filled);
      thumbUp.setImageResource(R.drawable.icon_circle_up);
    } else {
      thumbDown.setImageResource(R.drawable.icon_circle_down);
      thumbUp.setImageResource(R.drawable.icon_circle_up);
    }

    if (article.getTranslations().size() > 1) {
      moreTranslations.setVisibility(View.VISIBLE);
      Glide.with(context)
          .load(DataSource.getFlagResource(article.getTranslations().get(1).getTextLanguage()))
          .placeholder(R.drawable.placeholder_circle)
          .error(R.drawable.placeholder_circle)
          .transform(circleTransform)
          .into(moreFlag);
      moreContent.setText("その他" + (article.getTranslations().size() - 1) + "翻訳");
    } else {
      moreTranslations.setVisibility(View.GONE);
    }
  }

  @Override public void setOnClickListener(View.OnClickListener listener) {
    super.setOnClickListener(listener);
    thumbDown.setOnClickListener(listener);
    thumbUp.setOnClickListener(listener);
    moreTranslations.setOnClickListener(listener);
  }
}
