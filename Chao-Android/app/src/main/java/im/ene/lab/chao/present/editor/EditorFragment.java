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

package im.ene.lab.chao.present.editor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import im.ene.lab.chao.R;
import im.ene.lab.chao.data.DataSource;
import im.ene.lab.chao.data.model.Article;
import im.ene.lab.chao.data.model.Sentence;
import im.ene.lab.chao.data.model.User;
import im.ene.lab.chao.util.CircleTransform;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by eneim on 7/3/16.
 */
public class EditorFragment extends BottomSheetDialogFragment {

  public static final String TAG = "EditorFragment";

  public static EditorFragment newInstance(String itemId) {
    EditorFragment fragment = new EditorFragment();
    Bundle args = new Bundle();
    args.putString(TAG, itemId);
    fragment.setArguments(args);
    return fragment;
  }

  @BindView(R.id.item_text_translated) TextView itemText;
  @BindView(R.id.translation_language_flag) ImageView sourceFlag;
  @BindView(R.id.content_edittext) EditText itemEdittext;

  @OnClick(R.id.button_dismiss) void cancel() {
    dismiss();
  }

  @OnClick(R.id.button_submit) void submit() {
    final String newContent = itemEdittext.getText().toString();
    if (!TextUtils.isEmpty(newContent)) {
      realm.executeTransaction(new Realm.Transaction() {
        @Override public void execute(Realm realm) {
          Sentence sentence = new Sentence();
          sentence.text = newContent;
          sentence.id = Integer.toString(newContent.hashCode());
          User user = null;
          if (fbUser != null) {
            user = realm.where(User.class).equalTo("id", fbUser.getDisplayName()).findFirst();
            if (user == null) {
              user = new User();
              user.id = fbUser.getDisplayName();
              user.userName = fbUser.getDisplayName();
              user.motherTongue = "vie";
            }
          }

          if (user != null) {
            sentence.creator = user;
            sentence.textLanguage = user.getMotherTongue();
          }

          if (article.getTranslations() == null) {
            article.translations = new RealmList<>();
          }

          article.getTranslations().add(sentence);
          realm.copyToRealmOrUpdate(article);
        }
      });

      dismiss();
    } else {
      Toast.makeText(getContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
      dismiss();
    }
  }

  private String itemId;
  private Article article;
  private Realm realm;
  private FirebaseUser fbUser;
  private CircleTransform circleTransform;

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
      itemId = getArguments().getString(TAG);
    }

    fbUser = FirebaseAuth.getInstance().getCurrentUser();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.layout_editor, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);

    circleTransform = new CircleTransform(getContext());
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    article = realm.where(Article.class).equalTo("id", itemId).findFirst();

    itemText.setText(article.getSource().getText());
    itemEdittext.setHint(article.getSource().getText());

    Glide.with(this)
        .load(DataSource.getFlagResource(article.getSource().getTextLanguage()))
        .placeholder(ContextCompat.getDrawable(getContext(), R.drawable.placeholder_circle))
        .error(ContextCompat.getDrawable(getContext(), R.drawable.placeholder_circle))
        .transform(circleTransform)
        .into(sourceFlag);
  }
}
