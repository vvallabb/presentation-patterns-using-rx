package com.yoelglus.presentation.patterns.navigator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yoelglus.presentation.patterns.AddItemActivity;
import com.yoelglus.presentation.patterns.ItemDetailActivity;
import com.yoelglus.presentation.patterns.ItemDetailFragment;
import com.yoelglus.presentation.patterns.R;
import com.yoelglus.presentation.patterns.presentation.navigator.Navigator;


public class AppCompatActivityNavigator implements Navigator {

    private AppCompatActivity mActivity;
    private boolean mTwoPane;

    public AppCompatActivityNavigator(AppCompatActivity activity, boolean twoPane) {
        mActivity = activity;
        mTwoPane = twoPane;
    }

    @Override
    public void navigateToAddItem() {
        mActivity.startActivity(new Intent(mActivity, AddItemActivity.class));
    }

    @Override
    public void navigateToItem(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(mActivity, ItemDetailActivity.class);
            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
            mActivity.startActivity(intent);
        }

    }
}