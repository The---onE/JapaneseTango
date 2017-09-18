package com.xmx.tango.module.sentence;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.atilika.kuromoji.ipadic.Token;
import com.google.android.flexbox.FlexboxLayout;
import com.xmx.tango.R;
import com.xmx.tango.base.activity.BaseTempActivity;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.module.speaker.SpeakTangoManager;
import com.xmx.tango.module.tango.TangoConstants;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

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
    @ViewInject(R.id.text_loading)
    private TextView loadingView;
    @ViewInject(R.id.layout_sentence)
    private FlexboxLayout sentenceLayout;

    List<Token> tokenList;
    Typeface typeface;

    @Override
    protected void initView(Bundle savedInstanceState) {
        final String sentence = getIntent().getStringExtra("sentence");
        if (sentence == null || sentence.trim().length() == 0) {
            finish();
        }
        sentenceView.setText(sentence);
        sentenceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpeakTangoManager.getInstance().speak(sentence);
            }
        });

        new AsyncTask<Void, Void, List<Token>>() {
            @Override
            protected List<Token> doInBackground(Void... voids) {
                return SentenceUtil.analyzeSentence(sentence);
            }

            @Override
            protected void onPostExecute(List<Token> tokens) {
                super.onPostExecute(tokens);
                tokenList = tokens;

                loadingView.setVisibility(View.GONE);

                for (Token token : tokens) {
                    View v = View.inflate(SentenceActivity.this, R.layout.item_sentence_tango, null);
                    TextView writingItem = (TextView) v.findViewById(R.id.text_writing);
                    writingItem.setTypeface(typeface);
                    TextView readingItem = (TextView) v.findViewById(R.id.text_reading);
                    readingItem.setTypeface(typeface);
                    TextView partItem = (TextView) v.findViewById(R.id.text_part);
                    partItem.setTypeface(typeface);
                    final TextView hintItem = (TextView) v.findViewById(R.id.text_hint);
                    writingItem.setText(token.getSurface());
                    partItem.setText(token.getPartOfSpeechLevel1());
//                    hintItem.setText(token.getAllFeatures());
                    String s = token.getSurface() + "\n" +
                            "品詞:" + token.getPartOfSpeechLevel1() + "\n" +
                            "品詞細分1:" + token.getPartOfSpeechLevel2() + "\n" +
                            "品詞細分2:" + token.getPartOfSpeechLevel3() + "\n" +
                            "品詞細分3:" + token.getPartOfSpeechLevel4() + "\n" +
                            "活用型:" + token.getConjugationType() + "\n" +
                            "活用形:" + token.getConjugationForm() + "\n" +
                            "基本形:" + token.getBaseForm() + "\n" +
                            "読み:" + token.getReading() + "\n" +
                            "発音:" + token.getPronunciation() + "\n";
                    hintItem.setText(s);

                    String reading = token.getReading();
                    String surface = token.getSurface();
                    if (!"*".equals(reading) && !reading.equals(surface)) {
                        String hiraReading = SentenceUtil.convertKana(reading);
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
        setJapaneseFont();
    }

    private void setJapaneseFont() {
        AssetManager mgr = getAssets();
        String title = DataManager.getInstance().getJapaneseFontTitle();
        String font = null;
        if (title != null) {
            font = TangoConstants.JAPANESE_FONT_MAP.get(title);
        }
        typeface = Typeface.DEFAULT;
        if (font != null) {
            typeface = Typeface.createFromAsset(mgr, font);
        }
        sentenceView.setTypeface(typeface);
        hintView.setTypeface(typeface);
    }

}
