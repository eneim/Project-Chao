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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import butterknife.BindView;
import im.ene.lab.chao.R;
import im.ene.lab.chao.base.BaseFragment;
import im.ene.lab.chao.base.recyclerview.MugenScrollListener;
import im.ene.lab.chao.data.DataSource;
import im.ene.lab.chao.data.model.Article;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.List;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by eneim on 7/2/16.
 */
public class TimelineFragment extends BaseFragment implements MugenScrollListener.Callback {

  public static TimelineFragment newInstance() {
    return new TimelineFragment();
  }

  private boolean isLoading = false;
  private int page = 0;

  private Callback callback;

  @BindView(R.id.recycler_view) RecyclerView recyclerView;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof Callback) {
      this.callback = (Callback) context;
    }
  }

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
  private RealmResults<Article> articles;
  private Subscription updateSubscription;

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    articles = Realm.getDefaultInstance()
        .where(Article.class)
        .findAllSorted("textLength", Sort.DESCENDING);
    adapter = new TimelineAdapter(articles);

    layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setHasFixedSize(false);
    recyclerView.setAdapter(adapter);
    recyclerView.addOnScrollListener(new MugenScrollListener(layoutManager, this) {
      @Override public void onLoadMore() {
        page++;
        isLoading = true;
        adapter.setLoadingMore(true);
        DataSource.search(null, "vie", "jpn", page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<Article>>() {
              @Override public void call(final List<Article> articles) {
                adapter.setLoadingMore(false);
                isLoading = false;
                if (articles != null) {
                  Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                      realm.copyToRealmOrUpdate(articles);
                    }
                  });
                }
              }
            }, new Action1<Throwable>() {
              @Override public void call(Throwable throwable) {
                adapter.setLoadingMore(false);
                isLoading = false;
                Log.d(TAG, "call() called with: " + "throwable = [" + throwable + "]");
              }
            });
      }
    });

    adapter.setOnItemClickListener(new TimelineAdapter.ItemClickHandler() {
      @Override public void thumbUpClicked(ImageButton button, final Article item) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
          @Override public void execute(Realm realm) {
            if (item.getEvaluation() == 1) {
              item.positiveCount--;
              item.evaluation = 0;
            } else {
              item.positiveCount += 1 - item.getEvaluation();
              item.evaluation = 1;
            }

            realm.copyToRealmOrUpdate(item);
          }
        });
      }

      @Override public void thumbDownClicked(ImageButton button, final Article item) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
          @Override public void execute(Realm realm) {
            if (item.getEvaluation() == -1) {
              item.positiveCount++;
              item.evaluation = 0;
            } else {
              item.positiveCount += -1 - item.getEvaluation();
              item.evaluation = -1;

              if (callback != null) {
                callback.onThumbDownRequestBetterSuggestion();
              }
            }

            realm.copyToRealmOrUpdate(item);
          }
        });
      }
    });
  }

  private static final String TAG = "TimelineFragment";

  @Override public void onResume() {
    super.onResume();
    updateSubscription = articles.asObservable().subscribe(new Action1<RealmResults<Article>>() {
      @Override public void call(RealmResults<Article> articles) {
        adapter.notifyDataSetChanged();
      }
    });

    page = 1;
    isLoading = true;
    DataSource.search(null, "vie", "jpn", page)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<Article>>() {
          @Override public void call(final List<Article> articles) {
            isLoading = false;
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
              @Override public void execute(Realm realm) {
                realm.delete(Article.class);
                if (articles != null) {
                  realm.copyToRealmOrUpdate(articles);
                }
              }
            });
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {
            isLoading = false;
            Log.d(TAG, "call() called with: " + "throwable = [" + throwable + "]");
          }
        });
  }

  @Override public void onPause() {
    super.onPause();
    if (updateSubscription != null && !updateSubscription.isUnsubscribed()) {
      updateSubscription.unsubscribe();
    }
  }

  @Override public boolean isLoading() {
    return isLoading;
  }

  public interface Callback {

    void onThumbDownRequestBetterSuggestion();
  }
}
