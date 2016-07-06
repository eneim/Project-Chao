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
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import im.ene.lab.chao.Chao;
import im.ene.lab.chao.R;
import im.ene.lab.chao.widget.RequestBottomSheetFragment;
import im.ene.lab.chao.widget.SearchBottomSheetFragment;
import java.lang.reflect.Method;

/**
 * Created by eneim on 7/2/16.
 */
public class TimelineActivity extends AppCompatActivity implements TimelineFragment.Callback {

  private static final String TAG = "TimelineActivity";

  FloatingSearchView searchView;

  static final String[] QUERIES = {
      "Anh ơi cho tôi hỏi chút", "Tôi có thể hút thuốc ở đây không", "Đồn cảnh sát ở đâu",
      "Tàu nào sẽ đến Nhà thi đấu Yoyogi"
  };

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_timeline);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.content, TimelineFragment.newInstance())
          .commit();
    }

    searchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
    searchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
      @Override public void onMenuOpened() {

      }

      @Override public void onMenuClosed() {

      }
    });

    searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
      @Override public void onActionMenuItemSelected(MenuItem item) {
        Log.d(TAG, "onActionMenuItemSelected() called with: " + "item = [" + item + "]");
        if (item.getItemId() == R.id.action_search) {
          searchView.clearQuery();
          searchView.setSearchText(QUERIES[(int) (QUERIES.length * Math.random())]);
        }
      }
    });

    searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
      @Override public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
        Log.d(TAG, "onSuggestionClicked() called with: "
            + "searchSuggestion = ["
            + searchSuggestion
            + "]");
      }

      @Override public void onSearchAction(String currentQuery) {
        hideIme(searchView);
        SearchBottomSheetFragment searchFragment =
            SearchBottomSheetFragment.newInstance(currentQuery);
        searchFragment.show(getSupportFragmentManager(), SearchBottomSheetFragment.TAG);
      }
    });
  }

  @Override public void onThumbDownRequestBetterSuggestion(String id) {
    if (Chao.willShowRequest()) {
      RequestBottomSheetFragment fragment = RequestBottomSheetFragment.newInstance(id);
      fragment.show(getSupportFragmentManager(), RequestBottomSheetFragment.class.getSimpleName());
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  public static void showIme(@NonNull View view) {
    InputMethodManager imm =
        (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    // the public methods don't seem to work for me, so… reflection.
    try {
      Method showSoftInputUnchecked =
          InputMethodManager.class.getMethod("showSoftInputUnchecked", int.class,
              ResultReceiver.class);
      showSoftInputUnchecked.setAccessible(true);
      showSoftInputUnchecked.invoke(imm, 0, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void hideIme(@NonNull View view) {
    InputMethodManager imm =
        (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }
}
