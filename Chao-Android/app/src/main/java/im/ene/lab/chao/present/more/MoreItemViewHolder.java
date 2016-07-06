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

package im.ene.lab.chao.present.more;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import im.ene.lab.chao.R;
import im.ene.lab.chao.base.recyclerview.ViewHolder;
import im.ene.lab.chao.data.DataSource;
import im.ene.lab.chao.data.model.Sentence;
import im.ene.lab.chao.util.CircleTransform;

/**
 * Created by eneim on 7/3/16.
 */
public class MoreItemViewHolder extends ViewHolder {

  static final int LAYOUT_RES = R.layout.vh_more_translation;

  @BindView(R.id.translation_language_flag) ImageView flag;
  @BindView(R.id.item_text_translated) TextView text;

  public MoreItemViewHolder(View itemView) {
    super(itemView);
    circleTransform = new CircleTransform(context);
    ButterKnife.bind(this, itemView);
  }

  private final CircleTransform circleTransform;
  private Sentence translation;

  @Override public <T extends RecyclerView.Adapter> void bind(T parent, Object item) {
    if (!(item instanceof Sentence)) {
      return;
    }

    translation = (Sentence) item;
    text.setText(translation.getText());

    Glide.with(context)
        .load(DataSource.getFlagResource(translation.getTextLanguage()))
        .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_circle))
        .error(ContextCompat.getDrawable(context, R.drawable.placeholder_circle))
        .transform(circleTransform)
        .into(flag);
  }
}
