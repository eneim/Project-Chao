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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import im.ene.lab.chao.R;
import im.ene.lab.chao.base.recyclerview.Adapter;
import im.ene.lab.chao.base.recyclerview.OnItemClickListener;
import im.ene.lab.chao.base.recyclerview.ViewHolder;
import im.ene.lab.chao.data.model.Article;
import io.realm.RealmResults;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by eneim on 7/2/16.
 */
public class TimelineAdapter extends Adapter<ViewHolder> {

  int TYPE_LOAD_MORE = 1000;

  private final AtomicBoolean loadingMore;
  private final RealmResults<Article> articles;

  public TimelineAdapter(RealmResults<Article> articles) {
    this.articles = articles;
    loadingMore = new AtomicBoolean(false);
  }

  public boolean isLoadingMore() {
    return loadingMore.get();
  }

  public void setLoadingMore(boolean loadingMore) {
    this.loadingMore.set(loadingMore);
    notifyDataSetChanged();
  }

  int getRealCount() {
    return articles != null && articles.isValid() ? articles.size() : 0;
  }

  @Override public int getItemViewType(int position) {
    return isLoadingMore() && position == getRealCount() ? TYPE_LOAD_MORE
        : super.getItemViewType(position);
  }

  @Override public Object getItem(int position) {
    return isLoadingMore() && position == getRealCount() ? null : this.articles.get(position);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view;
    if (viewType == TYPE_LOAD_MORE) {
      view = LayoutInflater.from(parent.getContext())
          .inflate(LoadMoreViewHolder.LAYOUT_RES, parent, false);
      return new LoadMoreViewHolder(view);
    }

    view = LayoutInflater.from(parent.getContext())
        .inflate(TimelineItemViewHolder.LAYOUT_RES, parent, false);
    final TimelineItemViewHolder viewHolder = new TimelineItemViewHolder(view);
    viewHolder.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        int pos = viewHolder.getAdapterPosition();
        if (pos == RecyclerView.NO_POSITION) {
          return;
        }

        if (onItemClickListener != null) {
          onItemClickListener.onItemClick(TimelineAdapter.this, viewHolder, view, pos,
              getItemId(pos));
        }
      }
    });
    return viewHolder;
  }

  @Override public int getItemCount() {
    return isLoadingMore() ? 1 + getRealCount() : getRealCount();
  }

  public static abstract class ItemClickHandler implements OnItemClickListener {

    public abstract void thumbUpClicked(ImageButton button, Article item);

    public abstract void thumbDownClicked(ImageButton button, Article item);

    @Override
    public void onItemClick(Adapter adapter, ViewHolder viewHolder, View view, int position,
        long id) {
      if (!(viewHolder instanceof TimelineItemViewHolder)) {
        return;
      }

      TimelineItemViewHolder holder = (TimelineItemViewHolder) viewHolder;
      Article item = (Article) adapter.getItem(position);
      if (view == holder.thumbUp) {
        thumbUpClicked((ImageButton) view, item);
      } else if (view == holder.thumbDown) {
        thumbDownClicked((ImageButton) view, item);
      }
    }
  }

  public static class LoadMoreViewHolder extends ViewHolder {

    static final int LAYOUT_RES = R.layout.vh_load_more;

    public LoadMoreViewHolder(View itemView) {
      super(itemView);
    }

    @Override public <T extends RecyclerView.Adapter> void bind(T parent, Object item) {

    }
  }
}
