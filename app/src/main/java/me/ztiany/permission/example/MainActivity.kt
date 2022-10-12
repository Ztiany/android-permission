package me.ztiany.permission.example

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(DebugTree())
    }

    fun readPermission(view: View) {
        PermissionX.init(this).permissions(android.Manifest.permission.READ_EXTERNAL_STORAGE).request { allGranted, grantedList, deniedList ->
            Timber.d("allGranted = $allGranted")
        }
    }

    fun writePermission(view: View) {
        PermissionX.init(this).permissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE).request { allGranted, grantedList, deniedList ->
            Timber.d("allGranted = $allGranted")
        }
    }

    fun fileManager(view: View) {
        PermissionX.init(this).permissions(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            .onExplainRequestReason { scope, deniedList ->
                val message = "PermissionX需要您同意以下权限才能正常使用"
                scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
            }
            .request { allGranted, grantedList, deniedList ->
            Timber.d("allGranted = $allGranted")
        }
    }

    /** 没有 MANAGE_EXTERNAL_STORAGE 权限，则只能列出文件夹。*/
    fun listRootFile(view: View) {
        Timber.d(Environment.getExternalStorageDirectory().absolutePath)
        Environment.getExternalStorageDirectory()?.listFiles()?.forEach {
            Timber.d(it.absolutePath)
            if (it.absolutePath.contains("Download")) {
                it.listFiles()?.forEach {
                    Timber.d("     ${it.absolutePath}")
                    it.listFiles()?.forEach {
                        Timber.d("     ${it.absolutePath}")
                    }
                }
            }
        }
    }


}