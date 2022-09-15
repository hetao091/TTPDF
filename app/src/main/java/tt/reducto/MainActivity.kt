package tt.reducto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import tt.reducto.databinding.ActivityMainBinding
import tt.reducto.pdf.viewer.DocumentActivity
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/* BaseActivity<ActivityMainBinding>()  */
class MainActivity : AppCompatActivity() {
    //
    private val mBinding by inflate(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.tvChoose.setOnClickListener {
//            val intent = Intent()
//            if (VERSION.SDK_INT >= 29 && applicationInfo.targetSdkVersion >= 29) {
//                intent.action = Intent.ACTION_OPEN_DOCUMENT
//            } else {
//                intent.action = Intent.ACTION_GET_CONTENT
//            }
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "*/*"
//
//            startActivityForResult(
//                intent,
//                12
//            )
            myActivityLauncher.launch("application/pdf")
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        ) ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), 42
        )
    }

    private val myActivityLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            Log.d("---myActivityLauncher", "$it")
            val intent = Intent(this, DocumentActivity::class.java)
            // API>=21: intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT); /* launch as a new document */
            // API>=21: intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT); /* launch as a new document */
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET) /* launch as a new document */
            intent.action = Intent.ACTION_VIEW
            intent.data = it
            startActivity(intent)
        }


}


abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    protected var mBinding: T? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val superclass: Type = javaClass.genericSuperclass
        Log.d("--------", superclass.toString())
        val aClass = (superclass as ParameterizedType).actualTypeArguments[0] as Class<*>
        try {
            val method: Method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
            mBinding = method.invoke(null, layoutInflater) as T
            setContentView(mBinding!!.root)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }
}
