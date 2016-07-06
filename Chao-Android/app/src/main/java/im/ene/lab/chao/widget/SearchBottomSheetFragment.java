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

package im.ene.lab.chao.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import im.ene.lab.chao.R;
import im.ene.lab.chao.data.DataSource;
import im.ene.lab.chao.data.entity.Item;
import im.ene.lab.chao.data.model.Article;
import im.ene.lab.chao.data.model.Sentence;
import im.ene.lab.chao.data.model.User;
import im.ene.lab.chao.util.IOUtil;
import io.realm.Realm;
import io.realm.RealmList;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by eneim on 7/3/16.
 */
public class SearchBottomSheetFragment extends BottomSheetDialogFragment {

  public static final String TAG = "Search";

  private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback =
      new BottomSheetBehavior.BottomSheetCallback() {

        @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {
          Log.d(TAG, "onStateChanged() called with: "
              + "bottomSheet = ["
              + bottomSheet
              + "], newState = ["
              + newState
              + "]");
          if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            dismiss();
          }
        }

        @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
      };

  public static SearchBottomSheetFragment newInstance(String query) {
    SearchBottomSheetFragment fragment = new SearchBottomSheetFragment();
    Bundle args = new Bundle();
    args.putString(TAG, query);
    fragment.setArguments(args);
    return fragment;
  }

  @BindView(R.id.loading) View loadingView;
  @BindView(R.id.query_text) TextView queryView;
  @BindView(R.id.result_view) TextView resultView;

  Item translateResult = null;
  Realm realm;

  @OnClick(R.id.button_submit) void submit() {
    FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
    if (fbUser != null) {
      User user = realm.where(User.class).equalTo("id", fbUser.getDisplayName()).findFirst();
      if (user == null) {
        user = new User();
        user.id = fbUser.getDisplayName();
        user.motherTongue = "vie";
        user.userName = user.id;
      }

      Sentence source = new Sentence();
      source.creator = user;
      source.text = query;
      source.textLanguage = user.getMotherTongue();
      source.id = Integer.toString(source.getText().hashCode());

      final Article article = new Article();
      article.source = source;
      article.textLength = source.getText().length();
      article.id = source.getId();
      article.evaluation = 0;
      article.positiveCount = 0;
      article.translations = new RealmList<>();

      if (translateResult != null && translateResult.data != null && !IOUtil.isEmpty(
          translateResult.data.translations)) {
        Sentence translation = new Sentence();
        translation.creator = user;
        translation.text = translateResult.data.translations.get(0).translatedText;
        translation.textLanguage = "jpn";
        translation.id = Integer.toString(translation.getText().hashCode());

        article.getTranslations().add(translation);
      }

      realm.executeTransaction(new Realm.Transaction() {
        @Override public void execute(Realm realm) {
          realm.copyToRealmOrUpdate(article);
        }
      });
    } else {
      // TODO
    }

    cancel();
  }

  @OnClick(R.id.button_cancel) void cancel() {
    queryView.postDelayed(new Runnable() {
      @Override public void run() {
        dismiss();
      }
    }, 500);
  }

  BottomSheetBehavior mBottomSheetBehavior;

  private String query;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    realm = Realm.getDefaultInstance();
  }

  @Override public void onDetach() {
    super.onDetach();
    if (realm != null) {
      realm.close();
      realm = null;
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      query = getArguments().getString(TAG);
    }
  }

  @Override public void setupDialog(Dialog dialog, int style) {
    super.setupDialog(dialog, style);
    View view = View.inflate(getContext(), R.layout.layout_search_sheet, null);
    dialog.setContentView(view);
    ButterKnife.bind(this, view);

    mBottomSheetBehavior = BottomSheetBehavior.from(((View) view.getParent()));
    if (mBottomSheetBehavior != null) {
      mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
      mBottomSheetBehavior.setPeekHeight(
          getResources().getDimensionPixelSize(R.dimen.bottom_peak_height));
      view.requestLayout();
    }
  }

  @Override public void onResume() {
    super.onResume();
    loadingView.setVisibility(View.VISIBLE);

    queryView.setText("クエリ：" + query);

    DataSource.translate(query)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Item>() {
          @Override public void call(Item item) {
            translateResult = item;
            Log.d(TAG, "call() called with: " + "item = [" + item + "]");
            if (item.data != null && !IOUtil.isEmpty(item.data.translations)) {
              resultView.setText("検索結果：" + item.data.translations.get(0).translatedText);
            }

            loadingView.setVisibility(View.INVISIBLE);
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {
            resultView.setText(throwable.getLocalizedMessage());
            loadingView.setVisibility(View.INVISIBLE);
          }
        });
  }
}
