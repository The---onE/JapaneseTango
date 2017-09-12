package com.xmx.tango.module.sentence;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.google.android.flexbox.FlexboxLayout;
import com.xmx.tango.R;
import com.xmx.tango.base.activity.BaseTempActivity;
import com.xmx.tango.module.crud.TangoListChangeEvent;
import com.xmx.tango.module.tango.Tango;
import com.xmx.tango.module.tango.TangoEntityManager;
import com.xmx.tango.utils.Timer;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.activity_sentence)
public class SentenceActivity extends BaseTempActivity {

    @ViewInject(R.id.text_sentence)
    private TextView sentenceView;
    @ViewInject(R.id.text_hint)
    private TextView hintView;
    @ViewInject(R.id.layout_sentence)
    private FlexboxLayout sentenceLayout;

    List<Token> tokenList;

    String hiragana = "ぁあぃいぅうぇえぉおかがきぎくぐけげこご" +
            "さざしじすずせぜそぞただちぢっつづてでとどなにぬねの" +
            "はばぱひびぴふぶぷへべぺほぼぽまみむめもゃやゅゆょよらりるれろゎわゐゑをんヴヵヶ";
    String katakana = "ァアィイゥウェエォオカガキギクグケゲコゴ" +
            "サザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノ" +
            "ハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヰヱヲンヴヵヶ";

    @Override
    protected void initView(Bundle savedInstanceState) {
        final String sentence = getIntent().getStringExtra("sentence");
        if (sentence == null || sentence.trim().length() == 0) {
            finish();
        }
        sentenceView.setText(sentence);

        new AsyncTask<Void, Void, List<Token>>() {
            @Override
            protected List<Token> doInBackground(Void... voids) {
                Tokenizer tokenizer = new Tokenizer();
                return tokenizer.tokenize(sentence);
            }

            @Override
            protected void onPostExecute(List<Token> tokens) {
                super.onPostExecute(tokens);
                tokenList = tokens;

                for (Token token : tokens) {
                    View v = View.inflate(SentenceActivity.this, R.layout.item_sentence, null);
                    TextView writingItem = (TextView) v.findViewById(R.id.text_writing);
                    TextView readingItem = (TextView) v.findViewById(R.id.text_reading);
                    TextView partItem = (TextView) v.findViewById(R.id.text_part);
                    final TextView hintItem = (TextView) v.findViewById(R.id.text_hint);
                    writingItem.setText(token.getSurface());
                    partItem.setText(token.getPartOfSpeechLevel1());
                    hintItem.setText(token.getAllFeatures());

                    String reading = token.getReading();
                    String surface = token.getSurface();
                    if (!"*".equals(reading) && !reading.equals(surface)) {
                        String hiraReading = convertKana(reading);
                        if (!hiraReading.equals(surface)) {
                            readingItem.setText(hiraReading);
                        } else {
                            readingItem.setText(" ");
                        }
                    } else {
                        readingItem.setText(" ");
                    }

                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hintView.setText(hintItem.getText());
                        }
                    });

                    sentenceLayout.addView(v);
                }
            }
        }.execute();

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    private String convertKana(String tango) {
        StringBuilder sb = new StringBuilder(tango);
        for (int i = 0; i < sb.length(); ++i) {
            char c = sb.charAt(i);
            int index = katakana.indexOf(c);
            if (index > 0) {
                sb.replace(i, i + 1, "" + hiragana.charAt(index));
            }
        }
        return sb.toString();
    }
}
