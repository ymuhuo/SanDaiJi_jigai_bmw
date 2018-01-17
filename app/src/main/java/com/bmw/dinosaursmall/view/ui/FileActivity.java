package com.bmw.dinosaursmall.view.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;

import com.bmw.dinosaursmall.R;
import com.bmw.dinosaursmall.adapter.FileListAdapter;
import com.bmw.dinosaursmall.utils.UrlUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FileActivity extends AppCompatActivity {

    @Bind(R.id.file_recylerView)
    RecyclerView pRecycler;
    private FileListAdapter adapter;
    private boolean isPicture;
    private List<File> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        ButterKnife.bind(this);
        isPicture = getIntent().getBooleanExtra("isPicture", false);
        initRecyclerView();
    }

    @Override
    protected void onResume() {

        initList();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        adapter.release();
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    private void initList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = null;
                if (isPicture)
                    file = new File(UrlUtil.getLocal_picture_path());
                else
                    file = new File(UrlUtil.getLocal_video_path());
                File[] files = file.listFiles();
                list = new ArrayList<>();
                for(File f:files){
                    list.add(f);
                }
                adapter.setFiles(list);

            }
        }).start();

    }

    private void initRecyclerView() {
        GridLayoutManager gManager = new GridLayoutManager(this, 4);
        pRecycler.setLayoutManager(gManager);
        adapter = new FileListAdapter(this, isPicture);
        pRecycler.setAdapter(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
