package com.synergy.android.timetable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import com.synergy.android.timetable.domains.Group;
import com.synergy.android.timetable.parsers.GroupsParser;
import com.synergy.android.timetable.parsers.LessonsParser;
import com.synergy.android.timetable.utils.GroupValidator;
import com.synergy.android.timetable.utils.StringUtils;
import com.synergy.android.timetable.web.OnPageLoaderListener;
import com.synergy.android.timetable.web.PageLoader;

import java.util.List;

public class GroupActivity extends ActionBarActivity {
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
        if (!StringUtils.isNullOrEmpty(settings.getGroup())) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            group = viewHolder.group.getText().toString();
            if (StringUtils.isNullOrEmpty(group)) {
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
                    
                    GroupsParser parser = new GroupsParser();
                    List<Group> groups = parser.parse(pageData);
                    List<String> matchingGroups = GroupValidator.getGroupNumber(groups, group);
                    if (matchingGroups == null || matchingGroups.size() == 0) {
                        showError(R.string.activity_group_error_notfound);
                    } else {
                        if (matchingGroups.size() == 1) {
                            submitSettings(matchingGroups.get(0));
                        } else {
                            showGroupSelector(matchingGroups);
                        }
                    }
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            });
            loader.execute(GroupsParser.URL);
        }
        
        private void showError(int resId) {
            viewHolder.error.setVisibility(View.VISIBLE);
            viewHolder.error.setText(getString(resId));
        }
    }
    
    private void submitSettings(String selectedGroup) {
        group = selectedGroup;
        viewHolder.group.setText(group);
        url = String.format(LessonsParser.URL_FORMAT, group);
        settings.setGroup(group);
        settings.setUrl(url);
        setResult(RESULT_OK);
        finish();
    }
    
    private void showGroupSelector(final List<String> groups) {
        String[] items = groups.toArray(new String[groups.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.activity_group_dialog_title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        submitSettings(groups.get(which));
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }
}
