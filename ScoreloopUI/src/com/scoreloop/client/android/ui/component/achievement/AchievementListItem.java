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

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scoreloop.client.android.core.model.Achievement;
import com.scoreloop.client.android.core.model.Award;
import com.scoreloop.client.android.core.model.Money;
import com.scoreloop.client.android.ui.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.Configuration;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.StringFormatter;
import com.scoreloop.client.android.ui.framework.BaseListItem;

public class AchievementListItem extends BaseListItem {

	private static String getDescriptionText(final Achievement achievement, final Configuration configuration) {
		final Award award = achievement.getAward();
		final String text = award.getLocalizedDescription();

		final Money reward = award.getRewardMoney();
		if ((reward != null) && reward.hasAmount()) {
			final StringBuilder builder = new StringBuilder();
			builder.append(StringFormatter.formatMoney(reward, configuration)).append("\n").append(text);
			return builder.toString();
		}
		return text;
	}

	private static String getTitleText(final Achievement achievement) {
		return achievement.getAward().getLocalizedTitle();
	}

	private final Achievement	_achievement;
	private final boolean		_belongsToSessionUser;
	private final String		_description;

	public AchievementListItem(final ComponentActivity activity, final Achievement achievement, final boolean belongsToSessionUser) {
		super(activity, new BitmapDrawable(achievement.getImage()), getTitleText(achievement));
		_description = getDescriptionText(achievement, activity.getConfiguration());
		_achievement = achievement;
		_belongsToSessionUser = belongsToSessionUser;
	}

	public Achievement getAchievement() {
		return _achievement;
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_ACHIEVEMENT;
	}

	@Override
	public View getView(View view, final ViewGroup parent) {
		if (view == null) {
			view = getLayoutInflater().inflate(R.layout.sl_list_item_achievement, null);
		}

		final ImageView icon = (ImageView) view.findViewById(R.id.sl_list_item_achievement_icon);
		icon.setImageDrawable(getDrawable());

		final TextView title = (TextView) view.findViewById(R.id.sl_list_item_achievement_title);
		title.setText(getTitle());

		final TextView descripiton = (TextView) view.findViewById(R.id.sl_list_item_achievement_description);
		descripiton.setText(_description);

		final View accessory = view.findViewById(R.id.sl_list_item_achievement_accessory);
		accessory.setVisibility(isEnabled() ? View.VISIBLE : View.INVISIBLE);

		return view;
	}

	@Override
	public boolean isEnabled() {
		return _belongsToSessionUser && _achievement.isAchieved() && (_achievement.getIdentifier() != null);
	}
}
