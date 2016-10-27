package net.macdidi.Jigsaw-Puzzle;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    
    private boolean isAnimRun = false;
    private boolean isGameStart = false;
    private ImageView[][] iv_game_arr = new ImageView[3][5];
    private GridLayout gl_main_game;
    private ImageView iv_null_ImageView;
    private GestureDetector gestureDetector;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        gestureDetector = new GestureDetector (this, new GestureDetector.OnGestureListener () {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            
                int type = getDirByGes(motionEvent.getX (), motionEvent.getY(), motionEvent1.getX(), motionEvent1.getY());
                changeByDir(type);
                
                return false;
            }
        });

        setContentView (R.layout.activity_main);
        
        Bitmap bigBm = ((BitmapDrawable) getResources().getDrawable(R.drawable.a_02)).getBitmap();
        int tuWandH = bigBm.getWidth() / 5;
        
        int ivWandH = getWindowManager().getDefaultDisplay().getWidth() / 5;

        for(int i = 0; i < iv_game_arr.length; i++) {
            for(int j = 0; j < iv_game_arr[0].length; j++) {
                Bitmap bm = Bitmap.createBitmap(bigBm, j * tuWandH, i * tuWandH, tuWandH, tuWandH);

                iv_game_arr[i][j] = new ImageView(this);
                iv_game_arr[i][j].setImageBitmap(bm);
                iv_game_arr[i][j].setLayoutParams(new LinearLayout.LayoutParams(ivWandH, ivWandH));
                iv_game_arr[i][j].setPadding(2, 2, 2, 2);
                iv_game_arr[i][j].setTag(new GameData(bm, i, j));
                iv_game_arr[i][j].setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View view) {
                        boolean flag = isHasByNullImageView((ImageView) view);
                        if(flag) {
                            changeDataByImageView((ImageView) view);
                        }
                    }
                });
            }
        }

        gl_main_game = (GridLayout) findViewById (R.id.gl_main_game);
        for(int i = 0; i < iv_game_arr.length; i++) {
            for(int j = 0; j < iv_game_arr[0].length; j++) {
                gl_main_game.addView(iv_game_arr[i][j]);
            }
        }

        setNullImageView(iv_game_arr[0][0]);
        
        randomMove();
        
        isGameStart = true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent (event);
    }
    
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent (ev);
        
        return super.dispatchTouchEvent (ev);
    }


    public void setNullImageView(ImageView mImageView) {
        mImageView.setImageBitmap(null);
        
        iv_null_ImageView = mImageView;
    }


    public boolean isHasByNullImageView(ImageView mImageView) {    
    
        GameData mNullGameData = (GameData) iv_null_ImageView.getTag ();
        GameData mGameData = (GameData) mImageView.getTag ();
        if(mNullGameData.y == mGameData.y && mNullGameData.x == mGameData.x + 1) {
            return true;
        }
        else if(mNullGameData.y == mGameData.y && mNullGameData.x == mGameData.x - 1) {
            return true;
        }
        else if(mNullGameData.y == mGameData.y + 1 && mNullGameData.x == mGameData.x) {
            return true;
        }
        else if(mNullGameData.y == mGameData.y - 1 && mNullGameData.x == mGameData.x) {
            return true;
        }

        return false;
    }


    class GameData {
    
        public int x = 0;
        public int y = 0;
        public Bitmap bm;
        public int p_x = 0;
        public int p_y = 0;

        public GameData(Bitmap bm, int x, int y) {
            this.bm = bm;
            this.p_x = x;
            this.p_y = y;
            this.x = x;
            this.y = y;
        }

        public boolean isTrue() {
            if(x == p_x && y == p_y) {
                return true;
            }

            return false;
        }
    }


    public void randomMove() {
        for(int i = 0; i < 12; i++) {
            int type = (int) (Math.random() * 4) + 1;
            changeByDir(type, false);
        }
    }
    
    
    public void changeDataByImageView(final ImageView mImageView) {
        changeDataByImageView(mImageView, true);
    }


    public void changeDataByImageView(final ImageView mImageView, boolean isAnim) {
    
        if(isAnimRun) {
            return;
        }

        if(!isAnim) {
            GameData mGameData = (GameData) mImageView.getTag();
            iv_null_ImageView.setImageBitmap(mGameData.bm);

            GameData mNullGameData = (GameData) iv_null_ImageView.getTag();
            mNullGameData.bm = mGameData.bm;
            mNullGameData.p_x = mGameData.p_x;
            mNullGameData.p_y = mGameData.p_y;

            setNullImageView(mImageView);

            if(isGameStart) {
                isGameOver();
            }

            return;
        }

        TranslateAnimation translateAnimation = null;
        if(mImageView.getX() > iv_null_ImageView.getX()) {
            translateAnimation = new TranslateAnimation(0.1f, - mImageView.getHeight(), 0.1f, 0.1f);
        }
        else if(mImageView.getX() < iv_null_ImageView.getX()) {
            translateAnimation = new TranslateAnimation(0.1f, mImageView.getHeight(), 0.1f, 0.1f);
        }
        else if(mImageView.getY() > iv_null_ImageView.getY()) {
            translateAnimation = new TranslateAnimation(0.1f, 0.1f, 0.1f, - mImageView.getWidth());
        }
        else if(mImageView.getY() < iv_null_ImageView.getY()) {
            translateAnimation = new TranslateAnimation(0.1f, 0.1f, 0.1f, mImageView.getWidth());
        }
        
        translateAnimation.setDuration(130);
        translateAnimation.setFillAfter(true);
        
        translateAnimation.setAnimationListener (new Animation.AnimationListener () {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimRun = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimRun = false;

                mImageView.clearAnimation();

                GameData mGameData = (GameData) mImageView.getTag();
                iv_null_ImageView.setImageBitmap(mGameData.bm);

                GameData mNullGameData = (GameData) iv_null_ImageView.getTag();
                mNullGameData.bm = mGameData.bm;
                mNullGameData.p_x = mGameData.p_x;
                mNullGameData.p_y = mGameData.p_y;

                setNullImageView(mImageView);

                if(isGameStart) {
                    isGameOver();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mImageView.startAnimation(translateAnimation);
    }


    public void isGameOver() {
        boolean isGameOver = true;

        for(int i = 0; i < iv_game_arr.length; i++) {
            for(int j = 0; j < iv_game_arr[0].length; j++) {
                if(iv_game_arr[i][j] == iv_null_ImageView) {
                    continue;
                }

                GameData mGameData = (GameData) iv_game_arr[i][j].getTag();
                if(!mGameData.isTrue()) {
                    isGameOver = false;

                    break;
                }
            }
        }

        if(isGameOver) {
            Toast.makeText(this, "拼圖完成, 你太棒了 !", Toast.LENGTH_SHORT).show ();
        }

    }


    public void changeByDir(int type) {
        changeByDir(type, true);
    }


    public void changeByDir(int type, boolean isAnim) {
        GameData mNullGameData = (GameData) iv_null_ImageView.getTag();

        int new_x = mNullGameData.x;
        int new_y = mNullGameData.y;

        if(type == 1) {
            new_x++;
        }
        else if(type == 2) {
            new_x--;
        }
        else if(type == 3) {
            new_y++;
        }
        else if(type == 4) {
            new_y--;
        }

        if(new_x >= 0 && new_x < iv_game_arr.length && new_y >= 0 && new_y < iv_game_arr[0].length) {
            if (isAnim) {
                changeDataByImageView (iv_game_arr[new_x][new_y]);
            } else {
                changeDataByImageView (iv_game_arr[new_x][new_y], isAnim);
            }
        }
        else {

        }
    }


    public int getDirByGes(float start_x, float start_y, float end_x, float end_y) {
        boolean isLeftOrRight = (Math.abs(start_x - end_x) > Math.abs(start_y - end_y)) ? true : false;

        if(isLeftOrRight) {
            boolean isLeft = start_x - end_x > 0 ? true : false;

            if(isLeft) {
                return 3;  
            }
            else {
                return 4;  
            }
        }
        else {
            boolean isUp = start_y - end_y > 0 ? true : false;

            if(isUp) {
                return 1;  
            }
            else {
                return 2;  
            }
        }
    }
}
