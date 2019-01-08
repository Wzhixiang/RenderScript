package com.wzx.renderscript

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    lateinit var sourceView: ImageView
    lateinit var targetView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sourceView = findViewById(R.id.sourceImage)
        targetView = findViewById(R.id.targetImage)
    }

    override fun onResume() {
        super.onResume()

        sourceView.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_bg))
        targetView.setImageBitmap(blurBitmap(this, BitmapFactory.decodeResource(resources, R.drawable.ic_bg)))
    }

    /**
     * 高斯模糊处理（RenderScript）
     *
     * 需要兼容可以使用v8支持包
     */
    private fun blurBitmap(context: Context, bitmap: Bitmap): Bitmap {
        //创建输出位图
        val outPutBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        //实例化RenderScript
        val renderScript = RenderScript.create(context)
        //通过RenderScript创建ScriptIntrinsicBlur
        val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        //通过RenderScript创建Allocation，
        val inAllocation = Allocation.createFromBitmap(renderScript, bitmap)
        val outAllocation = Allocation.createFromBitmap(renderScript, outPutBitmap)

        //radius取值（0,25]，值越大，模糊效果越明显
        scriptIntrinsicBlur.setRadius(10.00f)

        //设置输入的Allocation
        scriptIntrinsicBlur.setInput(inAllocation)
        //渲染输入的效果，然后赋值给outAllocation
        scriptIntrinsicBlur.forEach(outAllocation)

        //从outAllocation中取出最终的bitmap，赋值给outPutBitmap
        outAllocation.copyTo(outPutBitmap)

        //回收源位图
        bitmap.recycle()

        //销毁RenderScript
        renderScript.destroy()

        return outPutBitmap
    }
}
