package tt.reducto

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * ……。
 *
 * <p>......。</p>
 * <ul><li></li></ul>
 * <br>
 * <strong>Time</strong>&nbsp;&nbsp;&nbsp;&nbsp;2022/3/1 17:29<br>
 * <strong>CopyRight</strong>&nbsp;&nbsp;&nbsp;&nbsp;2021, tt.reducto<br>
 *
 * @version  : 1.0.0
 * @author   : hetao
 */

//@MainThread
//inline fun <reified T : ViewDataBinding> AppCompatActivity.viewDataBinding(crossinline bindingInflater: (LayoutInflater) -> T): Lazy<T> = object : Lazy<T> {
//    /* */
//    private var binding: T? = null
//
//    /* */
//    override fun isInitialized(): Boolean = binding != null
//
//    /* */
//    override val value: T
//        get() = binding ?: createBinding(bindingInflater).also {
//            it.lifecycleOwner = this@viewDataBinding
//            binding = it
//        }
//
//
//    private fun createBinding( bindingInflater: LayoutInflater): T {
//
//            try {
//                val superclass: Type = javaClass.genericSuperclass
//                Log.d("--------",superclass.toString())
//                val aClass = (superclass as ParameterizedType).actualTypeArguments[0] as Class<*>
//                val method: Method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
//                binding = method.invoke(null, bindingInflater) as T
////            activity.setContentView(binding!!.root)
//            } catch (e: NoSuchMethodException) {
//                e.printStackTrace()
//            } catch (e: IllegalAccessException) {
//                e.printStackTrace()
//            } catch (e: InvocationTargetException) {
//                e.printStackTrace()
//            }
//
//
//        return binding!!
//    }
//}
inline fun <reified T : ViewDataBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T) =
    lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater)
    }


fun <T : ViewDataBinding> AppCompatActivity.inflate(inflater: (LayoutInflater) -> T) = lazy(LazyThreadSafetyMode.NONE){
    inflater(layoutInflater).apply { setContentView(root) }
}