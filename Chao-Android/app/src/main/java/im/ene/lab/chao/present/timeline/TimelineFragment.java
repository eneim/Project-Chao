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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import im.ene.lab.chao.R;
import im.ene.lab.chao.base.BaseFragment;

/**
 * Created by eneim on 7/2/16.
 */
public class TimelineFragment extends BaseFragment {

  public static TimelineFragment newInstance() {
    return new TimelineFragment();
  }

  @BindView(R.id.recycler_view) RecyclerView recyclerView;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_timeline, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  private TimelineAdapter adapter;
  private LinearLayoutManager layoutManager;

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    adapter = new TimelineAdapter();
    layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setHasFixedSize(false);
    recyclerView.setAdapter(adapter);
  }
}
