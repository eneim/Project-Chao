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

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by eneim on 7/3/16.
 */
public abstract class MugenScrollListener extends RecyclerView.OnScrollListener {

  // The minimum number of items remaining before we should loading more.
  private static final int VISIBLE_THRESHOLD = 8;

  private final LinearLayoutManager layoutManager;
  private final Callback presenter;

  public MugenScrollListener(LinearLayoutManager layoutManager, Callback presenter) {
    this.layoutManager = layoutManager;
    this.presenter = presenter;
  }

  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    // bail out if scrolling upward or already loading data
    if (dy < 0 || presenter.isLoading()) {
      return;
    }

    final int visibleItemCount = recyclerView.getChildCount();
    final int totalItemCount = layoutManager.getItemCount();
    final int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

    if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
      onLoadMore();
    }
  }

  public abstract void onLoadMore();

  public interface Callback {

    boolean isLoading();
  }
}
