package org.treant.dragmove.adapter;

import java.util.List;

import org.treant.dragmove.MainActivity;
import org.treant.dragmove.R;
import org.treant.dragmove.util.Configure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {

	private Context context;
	private List<String> lstDate;
	public int gonePosition;
	private boolean isMoving;
	private int rows;
	private TextView textView;

	public GridViewAdapter(Context context, List<String> list) {
		this.context = context;
		lstDate = list;
		rows = (lstDate.size() - 1) / 3 + 1;
	}

	/**
	 * ��������λ�õ�����
	 * 
	 * @param startPosition
	 * @param endPosition
	 * @param gonePosition
	 */
	public void exchange(int startPosition, int endPosition, int gonePosition) {
		this.gonePosition = gonePosition;
		Object startObject = getItem(startPosition);
		if (startPosition < endPosition) { // (start->end)-->���油һλ(start->end->start)-->ɾ��startλ��(end->start)
			lstDate.add(endPosition + 1, (String) startObject);
			lstDate.remove(startPosition);
		} else {// (end<-start)-->�����һλ(start->end->start)-->ɾ��start+1λ��(start->end)
			lstDate.add(endPosition, (String) startObject);
			lstDate.remove(startPosition + 1);
		}
	}

	/**
	 * �趨�Ƿ����ƶ�״̬
	 * 
	 * @param isMoving
	 */
	public void setMovingState(boolean isMoving) {
		this.isMoving = isMoving;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return (lstDate != null) ? lstDate.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lstDate.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(R.layout.griditem,
				null);
		textView = (TextView) convertView.findViewById(R.id.txt_userAge);
		textView.setText("Item:" + lstDate.get(position));
		if (isMoving && position == gonePosition) {// �����µ�positionʱ�����ô��ڵĸ��Ӳ��ɼ�
													// �ƶ��ĸ������֮
			convertView.setVisibility(View.INVISIBLE);
		}
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(
				(int) (Configure.screenWidth / MainActivity.NUM_COLUMNS - 40 * Configure.screenDensity), // width
				(int) (Configure.screenHeight / rows - 40 * Configure.screenDensity)); // height
		convertView.setLayoutParams(params);
		return convertView;
	}

}
