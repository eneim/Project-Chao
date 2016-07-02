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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.ene.lab.chao.base.recyclerview.Adapter;
import im.ene.lab.chao.data.entity.Article;

/**
 * Created by eneim on 7/2/16.
 */
public class TimelineAdapter extends Adapter<TimelineItemViewHolder> {

  @Override public Object getItem(int position) {
    return new Article();
  }

  @Override public TimelineItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(TimelineItemViewHolder.LAYOUT_RES, parent, false);
    return new TimelineItemViewHolder(view);
  }

  @Override public int getItemCount() {
    return 100;
  }
}
