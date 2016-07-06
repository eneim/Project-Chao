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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.lab.chao.R;
import im.ene.lab.chao.data.model.Article;
import im.ene.lab.chao.widget.FullScreenDialogFragment;
import io.realm.Realm;

/**
 * Created by eneim on 7/3/16.
 */
public class MoreTranslationFragment extends FullScreenDialogFragment {

  public static final String TAG = "MoreTranslationFragment";

  public static MoreTranslationFragment newInstance(String id) {
    MoreTranslationFragment fragment = new MoreTranslationFragment();
    Bundle args = new Bundle();
    args.putString(TAG, id);
    fragment.setArguments(args);
    return fragment;
  }

  private String itemId;

  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  private MoreTranslationAdapter adapter;
  private LinearLayoutManager layoutManager;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      itemId = getArguments().getString(TAG);
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_timeline, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Article article =
        Realm.getDefaultInstance().where(Article.class).equalTo("id", itemId).findFirst();
    adapter = new MoreTranslationAdapter(article);

    layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

    recyclerView.setPadding(0, 0, 0, 0);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setHasFixedSize(false);
    recyclerView.setAdapter(adapter);
  }
}
