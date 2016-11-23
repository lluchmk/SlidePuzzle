package dam.android.dipuzzle;

import android.app.Activity;
import android.content.ClipData;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PuzzleActivity extends Activity {

    private ImageView[] images = new ImageView[8];
    private PuzzleTable puzzleTable;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        puzzleTable = new PuzzleTable();
        bundle = new Bundle();

        findViewById(R.id.activity_puzzle).setOnDragListener(new MyDragListener());
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.activity_puzzle);
        for (int i=0; i<viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof LinearLayout) {
                child.setOnDragListener(new MyDragListener());
            }
        }

        loadImages();
    }

    private void loadImages() {
        for (int i = 0; i < images.length; i++) {
            images[i] = new ImageView(this);
            images[i].setImageDrawable(ContextCompat.getDrawable(this,
                    getResources().getIdentifier("dwpuzzle" + (i+1), "drawable", getPackageName())));
            images[i].setOnTouchListener(touchListener);
            images[i].setId(View.generateViewId());
            images[i].setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        }
        int width = images[0].getDrawable().getIntrinsicWidth();
        int height = images[0].getDrawable().getIntrinsicHeight();

        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.activity_puzzle);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String tag = i + "-" + j;
                LinearLayout ll = (LinearLayout) layout.findViewWithTag(tag);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ll.getLayoutParams();
                params.width = width;
                params.height = height;
                ll.setLayoutParams(params);
                int imagenumber = puzzleTable.getImage(i, j);
                if (imagenumber > 0) {
                    ll.addView(images[imagenumber - 1]);
                }
            }
        }
    }

    private class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                if (v.getId() == R.id.activity_puzzle) {
                    (findViewById(bundle.getInt("DraggedImage"))).setVisibility(View.VISIBLE);
                } else {
                    LinearLayout orglayout = (LinearLayout) (findViewById(bundle.getInt("DraggedImage"))).getParent();
                    View orgview = orglayout.getChildAt(0);
                    int srcX = Integer.parseInt(((String) orglayout.getTag()).split("-")[0]);
                    int srcY = Integer.parseInt(((String) orglayout.getTag()).split("-")[1]);
                    int destX = Integer.parseInt(((String) v.getTag()).split("-")[0]);
                    int destY = Integer.parseInt(((String) v.getTag()).split("-")[1]);
                    int move = moveDone(srcX, srcY, destX, destY);
                    if (puzzleTable.isValidMove(srcX, srcY, move)) {
                        puzzleTable.move(srcX, srcY, move);
                        LinearLayout destlayout = (LinearLayout) v;
                        orglayout.removeView(orgview);
                        destlayout.addView(orgview);
                        if (puzzleTable.isCompleted()) {
                            completePuzzle();
                        }
                    }
                    orgview.setVisibility(View.VISIBLE);
                }
            }
            return true;
        }
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
                v.setVisibility(View.INVISIBLE);
                bundle.putInt("DraggedImage", v.getId());
                return true;
            } else {
                return false;
            }
        }
    };

    private int moveDone(int srcX, int srcY, int destX, int destY) {
        //Horizontal
        if (srcX != destX) {
            //Left
            if (srcX > destX) {
                return PuzzleTable.LEFT;
            } else { //Right
                return PuzzleTable.RIGHT;
            }
        } else if (srcY != destY){ //Vertical
            //Up
            if (srcY > destY) {
                return PuzzleTable.UP;
            } else { //Down
                return PuzzleTable.DOWN;
            }
        } else {
            return PuzzleTable.NO_MOVE;
        }
    }

    private void completePuzzle() {
        Toast.makeText(getApplicationContext(), "Completado!", Toast.LENGTH_LONG).show();
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.activity_puzzle);
        for (int i=0; i<viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof LinearLayout) {
                child.setOnDragListener(null);
            }
        }
        for (ImageView iv: images) {
            iv.setOnTouchListener(null);
        }

        ImageView view = new ImageView(this);
        view.setImageDrawable(ContextCompat.getDrawable(this,
                getResources().getIdentifier("dwpuzzlelast", "drawable", getPackageName())));
        ((LinearLayout) (findViewById(R.id.activity_puzzle)).findViewWithTag("2-2")).addView(view);
    }
}
