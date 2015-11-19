package com.autopigeonnier.note;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.example.GetStartedWithData.R;

/**
 * Adapter to bind a ToDoItem List to a view
 */
public class NoteAdapter extends ArrayAdapter<Note> {
	/**
	 * Adapter context
	 */
	Context mContext;
	/**
	 * Adapter View layout
	 */
	int mLayoutResourceId;

	public NoteAdapter(Context context, int layoutResourceId) {
		super(context, layoutResourceId);

		mContext = context;
		mLayoutResourceId = layoutResourceId;
	}

	/**
	 * Returns the view for a specific item on the list
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		  View row = convertView;

		final Note currentItem = getItem(position);

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
		}

		row.setTag(currentItem);

		final CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkToDoItem);
		checkBox.setText(currentItem.getmTitle());
		checkBox.setChecked(false);
		checkBox.setEnabled(true);

		checkBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (checkBox.isChecked()) {
					checkBox.setEnabled(false);
					if (mContext instanceof NoteActivity) {
						NoteActivity activity = (NoteActivity) mContext;
						activity.checkItem(currentItem);
					}
				}
			}
		});



		return row;
	}

}
