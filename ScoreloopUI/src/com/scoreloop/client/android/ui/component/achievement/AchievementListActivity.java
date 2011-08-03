/*
 * In derogation of the Scoreloop SDK - License Agreement concluded between
 * Licensor and Licensee, as defined therein, the following conditions shall
 * apply for the source code contained below, whereas apart from that the
 * Scoreloop SDK - License Agreement shall remain unaffected.
 * 
 * Copyright: Scoreloop AG, Germany (Licensor)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.scoreloop.client.android.ui.component.achievement;

import android.content.Intent;
import android.os.Bundle;

import com.scoreloop.client.android.core.controller.AchievementsController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.model.Achievement;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Manager;
import com.scoreloop.client.android.ui.component.entry.EntryListItem;
import com.scoreloop.client.android.ui.component.post.PostOverlayActivity;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;

public class AchievementListActivity extends ComponentListActivity<AchievementListItem> {

	private AchievementsController	_achievementsController;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new BaseListAdapter<EntryListItem>(this));

		_achievementsController = new AchievementsController(getRequestControllerObserver());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// in the case the user is the session user, we try to sync achievements first so that all unlocked awards
		// will have identifiers and can be used for posting messages.
		if (isSessionUser()) {
			showSpinner();
			final Manager manager = getManager();
			manager.submitAchievements(new Runnable() {
				public void run() {
					hideSpinner();
					setNeedsRefresh();
				}
			});
		} else {
			setNeedsRefresh();
		}
	}

	@Override
	public void onListItemClick(final AchievementListItem item) {
		final Achievement achievement = (item).getAchievement();
		if (item.isEnabled() && !PostOverlayActivity.isPosted(getApplicationContext(), achievement)) {
			final Intent intent = new Intent(this, PostOverlayActivity.class);
			PostOverlayActivity.setMessageTarget(achievement);
			startActivity(intent);
		}
	}

	@Override
	public void onRefresh(final int flags) {
		showSpinnerFor(_achievementsController);
		_achievementsController.setUser(getUser());
		_achievementsController.loadAchievements();
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController aRequestController) {
		final BaseListAdapter<AchievementListItem> adapter = getBaseListAdapter();
		adapter.clear();
		final boolean isSessionUser = isSessionUser();
		for (final Achievement achievement : _achievementsController.getAchievements()) {
			adapter.add(new AchievementListItem(this, achievement, isSessionUser));
		}
	}
}
