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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import im.ene.lab.chao.Chao;
import im.ene.lab.chao.R;
import im.ene.lab.chao.data.model.Article;
import im.ene.lab.chao.present.editor.EditorFragment;
import io.realm.Realm;

/**
 * Created by eneim on 7/3/16.
 */
public class RequestBottomSheetFragment extends BottomSheetDialogFragment {

  @BindView(R.id.sentence_text) TextView itemText;
  @BindView(R.id.requester_checkbox) CheckBox permanentHide;

  @OnClick(R.id.button_cancel) void cancel() {
    Chao.getPref().edit().putBoolean(Chao.PREF_SHOW_REQUEST, !permanentHide.isChecked()).apply();
    dismiss();
  }

  @OnClick(R.id.button_ok) void openEditor() {
    EditorFragment editorFragment = EditorFragment.newInstance(itemId);
    editorFragment.show(getActivity().getSupportFragmentManager(), EditorFragment.TAG);
    dismiss();
  }

  public static final String ARGS_ARTICLE_ID = "editor_request_article_id";

  private String itemId;

  public static RequestBottomSheetFragment newInstance(String id) {
    RequestBottomSheetFragment fragment = new RequestBottomSheetFragment();
    Bundle args = new Bundle();
    args.putString(ARGS_ARTICLE_ID, id);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      itemId = getArguments().getString(ARGS_ARTICLE_ID);
    }
  }

  @Override public void setupDialog(Dialog dialog, int style) {
    super.setupDialog(dialog, style);
    View contentView = View.inflate(getContext(), R.layout.layout_bottom_sheet, null);
    dialog.setContentView(contentView);
    ButterKnife.bind(this, contentView);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Article item =
        Realm.getDefaultInstance().where(Article.class).equalTo("id", itemId).findFirst();
    if (item != null) {
      itemText.setText(item.getSource().getText());
    }
  }
}
