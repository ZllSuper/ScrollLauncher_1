package org.treant.scrollgrid2_mutiscreens;

import java.util.ArrayList;

import org.treant.scrollgrid2_mutiscreens.util.AnimationListenerImpl;
import org.treant.scrollgrid2_mutiscreens.util.Configure;
import org.treant.scrollgrid2_mutiscreens.util.DragAdapter;
import org.treant.scrollgrid2_mutiscreens.util.DragGridView;
import org.treant.scrollgrid2_mutiscreens.util.ScrollLauncherLayout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private DragGridView dragGridView;
	private ScrollLauncherLayout scrollLauncher;
	private ImageView runImage;

	ArrayList<String> listData = null;
	TranslateAnimation left, right;
	public static final int COLUMN_SIZE=2;
	public static final int PAGE_SIZE = 6;
	ArrayList<ArrayList<String>> lists = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initWidgets();
		initData();
		runAnimation();
	}

	private void initWidgets() {
		Configure.init(this);
		listData = new ArrayList<String>();
		lists = new ArrayList<ArrayList<String>>();
		scrollLauncher = (ScrollLauncherLayout) this
				.findViewById(R.id.scrollLauncher);
		runImage = (ImageView) this.findViewById(R.id.run_image);
	}
	private void initData(){
		for(int i=0;i<18;i++){
			listData.add("*"+i+"*");
			lists.add(new ArrayList<String>());
		}
		Configure.countPage=(int) Math.ceil(listData.size()/PAGE_SIZE);
		if(dragGridView!=null){
			scrollLauncher.removeAllViews();
		}
		for(int i=0;i<Configure.countPage;i++){   //����ÿһҳ��
			//����һ��ҳ���е�ÿһ��,û�����һҳʱѭ�������϶�ΪPAGE_SIZE
			for(int j=PAGE_SIZE*i;j<(PAGE_SIZE*(i+1)>listData.size()?listData.size():(PAGE_SIZE*(i+1)));j++){
				//�������� �ַ�������ҳ��
				lists.get(i).add(listData.get(j));
			}
			//һ��ҳ��һ��DragGridView  ѭ����ʼ��
			dragGridView=new DragGridView(this);
			dragGridView.setAdapter(new DragAdapter(this, lists.get(i)));
			dragGridView.setNumColumns(COLUMN_SIZE);
			dragGridView.setHorizontalSpacing(0);
			dragGridView.setVerticalSpacing(0);
			dragGridView.setSelector(R.anim.spirit);
			final int temp=i;
			dragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Toast.makeText(MainActivity.this, "��������-->"+lists.get(temp).get(arg2), Toast.LENGTH_SHORT).show();
				}
				
			});
			//�����ڲ��� �趨page����
			dragGridView.setPageListener(new DragGridView.G_PageListener(){

				@Override
				public void page(int page) {
					// TODO Auto-generated method stub
					scrollLauncher.snapToScreen(page);
					new Handler().postDelayed(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Configure.isChangingPage=false;
						}
						
					}, 50);
				}
				
			});
			dragGridView.setOnItemChangeListener(new DragGridView.G_ItemChangeListener() {
				
				@Override
				public void change(int from, int to, int count) {
					// TODO Auto-generated method stub
					String toStr=lists.get(Configure.currentPage-count).get(from);//���to�����from���ݽ���
					lists.get(Configure.currentPage).add(to, toStr);//���λ�� <--currentPageҳ���toλ
					lists.get(Configure.currentPage).remove(to+1);
					//������ȡ��fromStr֮ǰִ��notifyDataSetChanged()����  ����ȡ�����Ǹ��º��toStr
				//	((DragAdapter)(((DragGridView)scrollLauncher.getChildAt(Configure.currentPage)).getAdapter())).notifyDataSetChanged();//֪ͨAdapter���ݸı�
					String fromStr=lists.get(Configure.currentPage ).get(to);
					lists.get(Configure.currentPage-count).add(from,fromStr);//���λ��<-- currentPage-countҳ��fromλ
					lists.get(Configure.currentPage-count).remove(from+1);
					((DragAdapter)(((DragGridView)scrollLauncher.getChildAt(Configure.currentPage)).getAdapter())).notifyDataSetChanged();//֪ͨAdapter���ݸı�
					((DragAdapter)(((DragGridView)scrollLauncher.getChildAt(Configure.currentPage-count)).getAdapter())).notifyDataSetChanged();
					Toast.makeText(MainActivity.this,"��-"+from+"-�ƶ���-"+(to+PAGE_SIZE*Configure.currentPage)+"-��Խ��ҳ��="+count, Toast.LENGTH_LONG).show();
				}
			});
			scrollLauncher.addView(dragGridView);
		}
		scrollLauncher.setPageListener(new ScrollLauncherLayout.PageListener() {
			
			@Override
			public void page(int page) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this,"��������"+Configure.currentPage+"ҳ", Toast.LENGTH_SHORT).show();
			}
		});
	}
	/**
	 * execute background scroll cycling animation
	 */
	private void runAnimation() {
		left = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -2f,
				Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,
				0);
		left.setFillAfter(true);
		left.setDuration(10000);
		left.setAnimationListener(new AnimationListenerImpl(){
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				runImage.startAnimation(right);
			}
		});
		right = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, -2f,
				Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,
				0);
		
		right.setFillAfter(true);
		right.setDuration(10000);
		right.setAnimationListener(new AnimationListenerImpl(){
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				runImage.startAnimation(left);
			}
		});
		runImage.startAnimation(right);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
