# RenderScript
    
    通过RenderScript实现图片高斯模糊效果
    
### 需要在app.gradle中配置renderscript
  
    defaultConfig {
        ...

        renderscriptTargetApi 19
        renderscriptSupportModeEnabled true
    }

### 实现代码如下：

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
