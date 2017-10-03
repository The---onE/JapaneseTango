package com.xmx.tango.core.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xmx.tango.R;
import com.xmx.tango.base.fragment.xUtilsFragment;
import com.xmx.tango.common.data.DataManager;
import com.xmx.tango.module.font.JapaneseFontChangeEvent;
import com.xmx.tango.module.sentence.LrcParser;
import com.xmx.tango.module.sentence.SentenceActivity;
import com.xmx.tango.module.speaker.SpeakTangoManager;
import com.xmx.tango.module.tango.TangoConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_sentence)
public class SentenceFragment extends xUtilsFragment {

    @ViewInject(R.id.edit_sentence)
    private EditText editSentence;
    @ViewInject(R.id.list_sentence)
    private ListView sentenceList;

    private SentenceAdapter adapter;
    private List<String> sentences = new ArrayList<>();
    private Typeface typeface;

    private static final int CHOOSE_FILE_RESULT = 1;

    private class SentenceAdapter extends BaseAdapter {
        class ViewHolder {
            TextView textView;
        }

        @Override
        public int getCount() {
            return sentences.size();
        }

        @Override
        public Object getItem(int i) {
            return sentences.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_sentence, viewGroup, false);
                holder = new ViewHolder();
                holder.textView = (TextView) view.findViewById(R.id.item_sentence);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.textView.setText(sentences.get(i));
            if (typeface != null) {
                holder.textView.setTypeface(typeface);
            }

            return view;
        }
    }

    @Event(value = R.id.btn_kuromoji)
    private void onKuromojiClick(View view) {
        String sentence = editSentence.getText().toString();
        startSentenceActivity(sentence);
    }

    @Event(value = R.id.btn_choose_file)
    private void onChooseFileClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, CHOOSE_FILE_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_FILE_RESULT && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                String filePath = uri.getPath();
                if (filePath.contains("primary:")) {
                    final String[] split = filePath.split(":");
                    final String fileType = split[0];
                    filePath = android.os.Environment
                            .getExternalStorageDirectory() + "/" + split[1];
                }
                if (filePath.startsWith("/external_files/")) {
                    filePath = filePath.replaceAll("^/external_files", android.os.Environment
                            .getExternalStorageDirectory().toString());
                }
                if (filePath.startsWith("/external/")) {
                    filePath = filePath.replaceAll("^/external", android.os.Environment
                            .getExternalStorageDirectory().toString());
                }

                try {
                    String prefix = filePath.substring(filePath.lastIndexOf(".") + 1);
                    if ("txt".equals(prefix)) {
                        String charset = charsetDetect(filePath);
                        InputStream is = new FileInputStream(filePath);
                        InputStreamReader isr = new InputStreamReader(is, charset);
                        BufferedReader reader = new BufferedReader(isr);
                        sentences.clear();
                        String text;
                        while ((text = reader.readLine()) != null) {
                            sentences.add(text);
                        }
                        adapter.notifyDataSetChanged();
                        sentenceList.smoothScrollToPosition(0);
                        showToast("打开成功");
                    } else if ("lrc".equals(prefix)) {
                        String charset = charsetDetect(filePath);
                        InputStream is = new FileInputStream(filePath);
                        InputStreamReader isr = new InputStreamReader(is, charset);
                        BufferedReader reader = new BufferedReader(isr);
                        LrcParser.LrcInfo info = LrcParser.INSTANCE.parser(reader);
                        sentences.clear();
                        if (info.getInfo() != null) {
                            for (Map.Entry<Long, String> entry : info.getInfo().entrySet()) {
                                String text = entry.getValue();
                                if (text != null) {
                                    sentences.add(text);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        sentenceList.smoothScrollToPosition(0);
                        showToast("打开成功");
                    } else {
                        showToast("暂不支持打开该类型文件");
                    }
                } catch (Exception e) {
                    filterException(e);
                }
            }
        }
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        sentences.add("君はメロディー　メロディー");
        sentences.add("懐かしいハーモニー　ハーモニー");
        sentences.add("好きだよと言えず抑えていた胸の痛み");
        sentences.add("僕のメロディー　メロディー ");
        sentences.add("サビだけを覚えてる");
        sentences.add("若さは切なく");
        sentences.add("輝いた日々が");
        sentences.add("蘇（よみがえ）るよ");

        try {
            InputStream is = getActivity().getAssets().open("lrc.lrc");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            LrcParser.LrcInfo info = LrcParser.INSTANCE.parser(reader);
            sentences.clear();
            if (info.getInfo() != null) {
                for (Map.Entry<Long, String> entry : info.getInfo().entrySet()) {
                    String text = entry.getValue();
                    if (text != null) {
                        sentences.add(text);
                    }
                }
            }
        } catch (IOException e) {
            filterException(e);
        }

        setJapaneseFont();
        adapter = new SentenceAdapter();
        sentenceList.setAdapter(adapter);
        sentenceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startSentenceActivity(sentences.get(i));
            }
        });
        sentenceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                SpeakTangoManager.INSTANCE.speak(getContext(), sentences.get(i));
                return true;
            }
        });

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void startSentenceActivity(String sentence) {
        Intent intent = new Intent(getActivity(), SentenceActivity.class);
        intent.putExtra("sentence", sentence);
        startActivity(intent);
    }

    private void setJapaneseFont() {
        AssetManager mgr = getContext().getAssets();
        String title = DataManager.getInstance().getJapaneseFontTitle();
        String font = null;
        if (title != null) {
            font = TangoConstants.JAPANESE_FONT_MAP.get(title);
        }
        typeface = Typeface.DEFAULT;
        if (font != null) {
            typeface = Typeface.createFromAsset(mgr, font);
        }
    }

    public String charsetDetect(String path) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(path));
        int p = (bin.read() << 8) + bin.read();
        bin.close();
        String code;

        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }

        return code;
    }

    @Subscribe
    public void onEvent(JapaneseFontChangeEvent event) {
        setJapaneseFont();
        adapter.notifyDataSetChanged();
    }
}
