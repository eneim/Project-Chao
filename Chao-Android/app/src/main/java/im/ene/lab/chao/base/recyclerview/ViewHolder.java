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

package im.ene.lab.chao.base.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.ButterKnife;

/**
 * Created by eneim on 7/2/16.
 */
public abstract class ViewHolder extends RecyclerView.ViewHolder {

  protected final Context context;

  public ViewHolder(View itemView) {
    super(itemView);
    context = itemView.getContext();
    ButterKnife.bind(this, itemView);
  }

  public abstract <T extends RecyclerView.Adapter> void bind(T parent, Object item);
}
