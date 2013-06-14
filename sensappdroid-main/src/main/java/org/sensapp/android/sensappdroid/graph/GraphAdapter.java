/**
 * Copyright (C) 2012 SINTEF <fabien@fleurey.com>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sensapp.android.sensappdroid.graph;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import org.sensapp.android.sensappdroid.R;

import java.util.List;

public class GraphAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	private List<GraphWrapper> wrappers;
	
	private static class ViewHolder {
		GraphDetailsView graph;
	}
	
	public GraphAdapter(Context context, List<GraphWrapper> wrappers) {
		this.inflater = LayoutInflater.from(context);
		this.wrappers = wrappers;
	}
	
	@Override
	public int getCount() {
		return wrappers.size();
	}

	@Override
	public Object getItem(int position) {
		return wrappers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		View view = convertView;
		if(view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.graph_row, parent, false);
			holder.graph = (GraphDetailsView) view.findViewById(R.id.gv_graphrow_graph);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.graph.registerWrapper(wrappers.get(position));
		return view;
	}

}
