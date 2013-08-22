package com.synergy.android.timetable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.synergy.android.timetable.fragments.TeachersListFragment;

public class TeachersActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new TeachersListFragment())
                .commit();
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

    static void startActivity(Context from) {
        if (from == null) {
            throw new NullPointerException("The activity should not be null.");
        }

        Intent intent = new Intent(from, TeachersActivity.class);
        from.startActivity(intent);
    }
}
