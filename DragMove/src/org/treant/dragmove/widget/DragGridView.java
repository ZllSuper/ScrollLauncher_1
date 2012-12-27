package org.treant.dragmove.widget;

import java.util.HashMap;

import org.treant.dragmove.R;
import org.treant.dragmove.adapter.GridViewAdapter;
import org.treant.dragmove.util.AnimationListenerImpl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class DragGridView extends GridView{

	private Context mContext;
	/**
	 * ÿ�������ƶ�ʱ��ʼ��λ��    �ƶ������ϱ�holdPosition����    ��������ƶ�ʱ�ᱻ���θ���
	 */
	private int dragPosition;
	/**
	 * �϶�ʱ����λ��List�е�λ��  ����������һ����ʱֵΪ-1
	 */
	private int dropPosition;
	
	//Move Image Parameters
	private int halfBitmapWidth;
	private int halfBitmapHeight;
	private ImageView dragImageView=null;
	private WindowManager windowManager=null;
	private WindowManager.LayoutParams windowParams;
	
	//Calculate Deviation
	private boolean isCountDeviation=false;
	private int mLongClickX;
	private int mLongClickY;
	private int DeviationX;
	private int DeviationY; // Deviation between setOnItemLongClickListener and onTouchEvent
	
	private boolean isActionUp=false; //whether let go after create animation
	private int contentViewTop=0;
	private int margin_left;
	private int margin_top;
	
	private void init(Context context){
		mContext=context;
	}
	public DragGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public DragGridView(Context context){
		super(context);
		init(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(ev.getAction()==MotionEvent.ACTION_DOWN){
			return this.setOnItemLongClickListener(ev);
		}
		return super.onInterceptTouchEvent(ev);
	}
	public boolean setOnItemLongClickListener(final MotionEvent event){
		setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mLongClickX=(int) event.getX();//���������ڿؼ��������Ͻǵ�����
				mLongClickY=(int) event.getY();//ÿ�����ŵ��λ�õ����겻ͬ���ı�    Ӱ�쳤���������ƶ�����
//				Log.i("mLongClickX", ""+mLongClickX);Log.i("mLongClickY", ""+mLongClickY);
				dragPosition=dropPosition=position;
				isActionUp=false;
				((GridViewAdapter)getAdapter()).setMovingState(true);
				ViewGroup itemView=(ViewGroup) getChildAt(dragPosition-getFirstVisiblePosition());//dragPosition-getFirstVisiblePosition()����Ļ�еĵڼ�����(start by 0)
				LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) (itemView.findViewById(R.id.g_one)).getLayoutParams();
				margin_left=lp.leftMargin;    margin_top=lp.topMargin;
				
				itemView.destroyDrawingCache();
				itemView.setDrawingCacheEnabled(true);
				itemView.setDrawingCacheBackgroundColor(0x000000);
				
				Bitmap bm=Bitmap.createBitmap(itemView.getDrawingCache(true));//copy bitmap
				Bitmap bitmap=Bitmap.createBitmap(bm, lp.leftMargin, lp.topMargin, 
						bm.getWidth()-lp.leftMargin-lp.rightMargin, bm.getHeight()-lp.topMargin-lp.bottomMargin);
				
				showCreateDragImageAnimation(itemView, bitmap);
				return false;
			}
			
		});
		return super.onInterceptTouchEvent(event);
		
	}
	/**
	 * ���ɸ���ͼƬ�����Ĺ���
	 * @param itemView
	 * @param bitmap
	 */
	private void showCreateDragImageAnimation(final ViewGroup itemView, final Bitmap bitmap){
		halfBitmapWidth=bitmap.getWidth()/2;  halfBitmapHeight=bitmap.getHeight()/2;
		TranslateAnimation animation=new TranslateAnimation(0, mLongClickX-halfBitmapWidth-itemView.getLeft(), 
				0, mLongClickY-halfBitmapHeight-getContentViewTop()-(getContentViewTop()+itemView.getTop()));	//�����ƶ��ľ�������Լ�adjust
		//ÿ�������ӵ�itemView.get**����������ȷ���ģ� �����λ���޹�    �ۺ������ƶ�����ֻ�͵������mLongClickX/Y�й�
//		Log.i("���㶨?", halfBitmapWidth+"");Log.i("��ߺ㶨?", halfBitmapHeight+"");
//		Log.i("toDeltaX", mLongClickX - halfBitmapWidth - itemView.getLeft()+"");
		Log.i("toDeltaY", mLongClickY - halfBitmapHeight - getContentViewTop() -(itemView.getTop() + getContentViewTop())+"");
//		Log.i("left-top-right-bottom", itemView.getLeft()+"-"+itemView.getTop()+"-"+itemView.getRight()+"-"+itemView.getBottom());
		animation.setFillAfter(false);//If fillAfter is true, the transformation that this animation performed will persist when it is finished.
		animation.setDuration(1000);
		animation.setAnimationListener(new AnimationListenerImpl(){
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if(!isActionUp){
					createBitmapInWindow(bitmap, mLongClickX, mLongClickY);
					itemView.setVisibility(View.GONE);
				}
				super.onAnimationEnd(animation);
			}
		});
		itemView.startAnimation(animation);
		
	}
	/**
	 * ����������ͼƬdragImageView
	 * @param bitmap
	 * @param x
	 * @param y
	 */
	private void createBitmapInWindow(Bitmap bitmap, int x, int y){
		windowParams=new WindowManager.LayoutParams();
		windowParams.gravity=Gravity.TOP|Gravity.LEFT;
		windowParams.width=WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.height=WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.x=x-halfBitmapWidth;
		windowParams.y=y-getContentViewTop()-halfBitmapHeight;
		windowParams.alpha=0.8f;
		ImageView image=new ImageView(getContext());
		image.setImageBitmap(bitmap);
		windowManager=(WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		if(dragImageView!=null){
			windowManager.removeView(dragImageView);
		}
		windowManager.addView(image, windowParams);
		dragImageView=image;  ////����������ɵ�ͼƬiv����dragImageView��  onTouchEvent����Ч
	}

	/**
	 * ��ȡ״̬���߶�
	 * android:theme="@android:style/Theme.Black.NoTitleBar"��ʹ��window.findViewById(Window.ID_ANDROID_CONTENT).getTop()==0
	 * @return
	 */
	private int getContentViewTop(){
		if(contentViewTop==0){
			Window window=((Activity)mContext).getWindow();
			contentViewTop=window.findViewById(Window.ID_ANDROID_CONTENT).getTop(); Log.i("û����if", "û����if"+contentViewTop);
			if(contentViewTop==0){
				Rect rect=new Rect(); 
				window.getDecorView().getWindowVisibleDisplayFrame(rect);
				contentViewTop=rect.top; Log.i("����", "����"+contentViewTop);
			}
		}Log.i("StateBarHeight",contentViewTop+"");
		return contentViewTop;
	}
	
	/**
	 * ����Touch��Move UP�¼�   Down�����洦��
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		int x=(int) ev.getX();
		int y=(int) ev.getY();
		switch(ev.getAction()){
		case MotionEvent.ACTION_MOVE:
			if(dragImageView!=null){   //û�и���ͼƬdragImageView���ɲ�������
				if(!isCountDeviation){
					DeviationX=x-mLongClickX;
					DeviationY=y-mLongClickY;
					isCountDeviation=true;   //Deviationֻ�ڵ�һ��ʱȷ��  ���windowParams.x/y ����getX/Y()ͬ���仯
				}
				onDrag(x,y);
				onItemsMove(x,y);
				//��ʵ���ö���DeviationX/Yϵͳ����
			}
			break;
		case MotionEvent.ACTION_UP:Log.i("ִ�е�UP", "ִ�е�UP");
			isActionUp=true;
			if (dragImageView != null) {
				animationMap.clear();    
				showDropAnimation(x, y);
			}
			break;
		}
		return super.onTouchEvent(ev);
	}
	/**
	 * ���ֶ���
	 * @param x
	 * @param y
	 */
	private void showDropAnimation(int x, int y){
		ViewGroup moveView=(ViewGroup) getChildAt(dragPosition);
		//������Ļ�����꣺����->(x��y)        Ŀ��->(halfBitmapWidth+moveView.getLeft(), halfBitmapHeight+moveView.getTop()) 
		//�����moveView�����꣺����->(?, ?)  Ŀ���moveView�غ�(0,0) �����ֵ(x-halfBitmapWidth-moveView.getLeft(),y-halfBitmapHeight-moveView.getTop())
		TranslateAnimation animation=new TranslateAnimation(x-halfBitmapWidth-moveView.getLeft(), 0,
				y-halfBitmapHeight-moveView.getTop(), 0);
		animation.setFillAfter(false);
		animation.setDuration(300);
		animation.setAnimationListener(new AnimationListenerImpl(){
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				super.onAnimationStart(animation);
				if(dragImageView!=null){
					Log.i("dd", "##############################");
					windowManager.removeView(dragImageView);//�Ƴ�����ͼƬ
					dragImageView=null;
					}
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				GridViewAdapter adapter=(GridViewAdapter)getAdapter();
				adapter.setMovingState(false);
				adapter.notifyDataSetChanged();
			}
		});
		
		moveView.startAnimation(animation);
	}
	/**
	 * �϶�ͼƬʱwindowParams���ű仯  ʵ��ͼƬ�ƶ�
	 * @param x
	 * @param y
	 */
	private void onDrag(int x, int y){Log.i("ִ�е�1", "ִ�е�1");
		if(dragImageView!=null){     //�ж϶��һ��
			windowParams.alpha=0.8f;  //��֮ǰһ��
			windowParams.x=x-DeviationX-halfBitmapWidth;
			windowParams.y=y-DeviationY-getContentViewTop()-halfBitmapHeight;
//			windowParams.x=x-halfBitmapWidth;
//			windowParams.y=y-getContentViewTop()-halfBitmapHeight;
			windowManager.updateViewLayout(dragImageView, windowParams);
		}
	}
	/**
	 * ���������һ��
	 * @param x
	 * @param y
	 */
	private void onItemsMove(int x, int y){
		dropPosition=pointToPosition(x,y);   //��ǰ�ƶ���λ��List�е�λ��   ���滹����exchange�����е�gonePosition����
		if(dropPosition==AbsListView.INVALID_POSITION){ //All valid positions are in the range 0 to 1 less than the number of items in the current adapter 
			return;   //dropû���������������� 
		}
		int MoveNum=dropPosition-dragPosition;  //
		if(MoveNum!=0&&!isMovingFastConflict(MoveNum)){  //drag!=drop  
			int itemMoveNum=Math.abs(MoveNum);//�����ƶ�������
			for(int i=0;i<itemMoveNum;i++){
				int holdPosition=(MoveNum>0)?dragPosition+1:dragPosition-1;
//				if(MoveNum>0){ //����ƶ�
//					holdPosition=dragPosition+1;
//				}else{ //��ǰ�ƶ�
//					holdPosition=dragPosition-1;
//				}
				
				((GridViewAdapter)getAdapter()).exchange(holdPosition, dragPosition, dropPosition);
				View moveView=getChildAt(holdPosition);//Ŀ��λ�õĸ�����ͼ
				Animation animation=this.getMoveAnimation(moveView.getLeft(), moveView.getTop(),
						getChildAt(dragPosition).getLeft(), getChildAt(dragPosition).getTop());
				animation.setAnimationListener(new NotifyDataSetListener(holdPosition));
				dragPosition=holdPosition;//ѭ�����������θ���dragPosition
				moveView.startAnimation(animation);
			}
		}

	}
	/**
	 * �Լ��������ƶ��ĸ��� �����ж� 
	 * һ�����ӱ�����ȫ����ϴ��ƶ���������׼�������´��ƶ�
	 * @param moveNum
	 * @return
	 */
	private boolean isMovingFastConflict(int moveNum){
		int itemsMoveNum=Math.abs(moveNum);
		int temp=dragPosition;   //dragPosition�����ƶ����仯        ����һ������ ����Ӱ��ȫ�ֱ���dragPosition
		for(int i=0;i<itemsMoveNum;i++){
			int holdPosition;////�ƶ������п��ܻ����µ�λ��  ����dragPosition����
			if(moveNum>0){
				holdPosition=temp+1;
			}else{
				holdPosition=temp-1;
			}
			if(animationMap.containsKey(holdPosition)){
				return true;
			}
			temp=holdPosition;
		}
		return false;
	}
	private HashMap<Integer, Boolean> animationMap=new HashMap<Integer, Boolean>();
	
	private class NotifyDataSetListener extends AnimationListenerImpl{
		private int movedPosition;
		public NotifyDataSetListener(int primaryPosition){
			this.movedPosition=primaryPosition;
		}
		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			super.onAnimationStart(animation);
			animationMap.put(movedPosition, true);//put into map when start
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			super.onAnimationEnd(animation);
			if(animationMap.containsKey(movedPosition)){
				animationMap.remove(movedPosition);//remove from map when end
			}
			if(animationMap.isEmpty()){//���ж����ƶ�����������
				((GridViewAdapter)getAdapter()).notifyDataSetChanged();
			}
			
		}
	}
	/**
	 * �ƶ�����  x yλ�Ʒֱ�ΪtoX-x��toY-y
	 * @param x
	 * @param y
	 * @param toX
	 * @param toY
	 * @return
	 */
	private Animation getMoveAnimation(float x, float y, float toX, float toY ){
		TranslateAnimation animation=new TranslateAnimation(0, toX-x, 0, toY-y);
		animation.setFillAfter(true);
		animation.setDuration(300);
		return animation;
	}
	
}
