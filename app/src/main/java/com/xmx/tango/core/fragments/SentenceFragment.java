package com.xmx.tango.core.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;

import com.xmx.tango.R;
import com.xmx.tango.base.fragment.xUtilsFragment;
import com.xmx.tango.module.sentence.SentenceActivity;
import com.xmx.tango.module.speaker.SpeakTangoManager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_sentence)
public class SentenceFragment extends xUtilsFragment {

    @ViewInject(R.id.edit_sentence)
    private EditText editSentence;
    @ViewInject(R.id.list_sentence)
    private ListView sentenceList;

    private ListAdapter adapter;
    private List<String> sentences = new ArrayList<>();

    @Event(value = R.id.btn_kuromoji)
    private void onKuromojiClick(View view) {
        String sentence = editSentence.getText().toString();
        startSentenceActivity(getContext(), sentence);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        sentences.add("春の魔法に陽射しは変わって");
        sentences.add("人も街も明るめに着替えた");
        sentences.add("風に誘われ気づけば知らずに");
        sentences.add("僕は口ずさんでいた");
        sentences.add("遠い昔の記憶の彼方に");
        sentences.add("忘れかけてた２人のfavorite song");
        sentences.add("なぜこの曲が浮かんだのだろう？");
        sentences.add("突然に");
        sentences.add("愛しさは");
        sentences.add("いつも　ずっと前から");
        sentences.add("準備してる");
        sentences.add("ノイズだらけのRadioが");
        sentences.add("聴こえて来たんだ");
        sentences.add("歳月（とき）を超え…");
        sentences.add("君はメロディー　メロディー");
        sentences.add("懐かしいハーモニー　ハーモニー");
        sentences.add("好きだよと言えず抑えていた胸の痛み");
        sentences.add("僕のメロディー　メロディー ");
        sentences.add("サビだけを覚えてる");
        sentences.add("若さは切なく");
        sentences.add("輝いた日々が");
        sentences.add("蘇（よみがえ）るよ");

        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, sentences);
        sentenceList.setAdapter(adapter);
        sentenceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startSentenceActivity(getContext(), sentences.get(i));
            }
        });
        sentenceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                SpeakTangoManager.getInstance().speak(sentences.get(i));
                return true;
            }
        });
    }

    private void startSentenceActivity(Context context, String sentence) {
        Intent intent = new Intent(getActivity(), SentenceActivity.class);
        intent.putExtra("sentence", sentence);
        startActivity(intent);
    }
}
