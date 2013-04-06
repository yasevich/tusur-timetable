package com.synergy.android.timetable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.synergy.android.timetable.parsers.FacultyParser;
import com.synergy.android.timetable.parsers.GroupParser;
import com.synergy.android.timetable.utils.StringUtils;
import com.synergy.android.timetable.web.OnPageLoaderListener;
import com.synergy.android.timetable.web.PageLoader;

public class GroupActivity extends Activity {
    public static final int REQUEST_CODE = 101;
    
    private ViewHolder viewHolder;
    private ApplicationSettings settings;
    
    private String group;
    private String url;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        
        settings = ApplicationSettings.getInstance(this);
        initViews();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    static void startActivityForResult(Activity from) {
        if (from == null) {
            throw new NullPointerException("The activity should not be null.");
        }
        
        Intent intent = new Intent(from, GroupActivity.class);
        from.startActivityForResult(intent, REQUEST_CODE);
    }
    
    @SuppressLint("NewApi")
    private void initViews() {
        if (!StringUtils.isNullOrEmpty(settings.getGroup()) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        viewHolder = new ViewHolder();
        viewHolder.group = (EditText) findViewById(R.id.activityGroupGroupEditText);
        viewHolder.error = (TextView) findViewById(R.id.activityGroupErrorTextView);
        viewHolder.submit = (Button) findViewById(R.id.activityGroupContinueButton);
        viewHolder.progressBar = (ProgressBar) findViewById(R.id.activityGroupProgressBar);
        
        viewHolder.group.setText(settings.getGroup());
        
        SubmitGroupListener listener = new SubmitGroupListener();
        viewHolder.group.setOnEditorActionListener(listener);
        viewHolder.submit.setOnClickListener(listener);
    }
    
    private static class ViewHolder {
        private EditText group;
        private TextView error;
        private Button submit;
        private ProgressBar progressBar;
    }
    
    private class SubmitGroupListener implements OnClickListener, OnEditorActionListener {
        @Override
        public void onClick(View v) {
            searchGroup();
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                searchGroup();
            }
            return false;
        }
        
        private void searchGroup() {
            viewHolder.error.setVisibility(View.GONE);
            group = StringUtils.toUpperCase(viewHolder.group.getText().toString());
            if (StringUtils.isNullOrEmpty(group)) {
                showError(R.string.activity_group_error_invalid);
                return;
            }
            
            String facultyUrl = GroupParser.getFacultyUrlByGroup(group);
            if (facultyUrl == null) {
                showError(R.string.activity_group_error_invalid);
                return;
            }
            
            PageLoader loader = new PageLoader(new OnPageLoaderListener() {
                @Override
                public void onPreExecute() {
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                }
                
                @Override
                public void onPostExecute(String pageData) {
                    if (pageData == null) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        showError(R.string.error_connection);
                        return;
                    }
                    FacultyParser parser = new FacultyParser(getGroupFormatString(group));
                    url = parser.parse(pageData);
                    viewHolder.progressBar.setVisibility(View.GONE);
                    if (url != null) {
                        submitSettings();
                    } else {
                        showError(R.string.activity_group_error_notfound);
                    }
                }
            });
            loader.execute(facultyUrl);
        }
        
        private void showError(int resId) {
            viewHolder.error.setVisibility(View.VISIBLE);
            viewHolder.error.setText(getString(resId));
        }
        
        private String getGroupFormatString(String group) {
            char[] chars = group.toCharArray();
            StringBuilder result = new StringBuilder()
                    .append(chars[0]);
            for (int i = 1; i < chars.length; ++i) {
                result.append("(-)?").append(chars[i]);
            }
            Log.v("Group", result.toString());
            return result.toString();
        }
    }
    
    private void submitSettings() {
        settings.setGroup(group);
        settings.setUrl(url);
        setResult(RESULT_OK);
        finish();
    }
}
