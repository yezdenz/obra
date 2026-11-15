package com.obra.studio.ui

import android.os.Bundle
import android.net.Uri
import android.widget.ImageView
import android.widget.LinearLayout
import android.view.ScaleGestureDetector
import com.obra.studio.data.Studio

class ViewerActivity:BaseActivity(){
    private var index=0;private var zoom=1f;private var images=arrayListOf<String>();private lateinit var image:ImageView
    override fun onCreate(state:Bundle?){super.onCreate(state);images=intent.getStringArrayListExtra("images")?:arrayListOf();index=state?.getInt("index")?:intent.getIntExtra("index",0);render()}
    override fun onSaveInstanceState(out:Bundle){super.onSaveInstanceState(out);out.putInt("index",index)}
    private fun render(){reset("Image preview");val c=Studio.work(intent.getLongExtra("commissionId",0));note(c?.title?:"Reference ${index+1} of ${images.size}")
        image=ImageView(this);image.contentDescription="Commission reference image";image.scaleType=ImageView.ScaleType.FIT_CENTER;image.background=shape(0xffede5dc.toInt());body.addView(image,LinearLayout.LayoutParams(-1,dp(380)))
        if(images.isEmpty()){empty("Reference placeholder","Sample artwork is not bundled. Attach an image in the commission form to preview your own reference.")}else try{image.setImageURI(Uri.parse(images[index]));if(image.drawable==null)throw IllegalStateException("Unavailable image")}catch(_:Exception){empty("Image unavailable","The image may have moved or access may have expired.");button("Retry"){render()}}
        val scale=ScaleGestureDetector(this,object:ScaleGestureDetector.SimpleOnScaleGestureListener(){override fun onScale(d:ScaleGestureDetector):Boolean{zoom=(zoom*d.scaleFactor).coerceIn(1f,4f);image.scaleX=zoom;image.scaleY=zoom;return true}})
        var x=0f;var y=0f;image.setOnTouchListener{v,e->scale.onTouchEvent(e);when(e.actionMasked){android.view.MotionEvent.ACTION_DOWN->{x=e.rawX;y=e.rawY};android.view.MotionEvent.ACTION_MOVE->if(zoom>1&&!scale.isInProgress){v.translationX+=(e.rawX-x);v.translationY+=(e.rawY-y);x=e.rawX;y=e.rawY};android.view.MotionEvent.ACTION_UP->v.performClick()};true}
        button("Zoom in"){zoom=(zoom+0.5f).coerceAtMost(4f);image.scaleX=zoom;image.scaleY=zoom};button("Fit image"){zoom=1f;image.scaleX=1f;image.scaleY=1f;image.translationX=0f;image.translationY=0f}
        button("Previous image"){index--;zoom=1f;render()}.isEnabled=index>0;button("Next image"){index++;zoom=1f;render()}.isEnabled=index<images.lastIndex
    }
}
