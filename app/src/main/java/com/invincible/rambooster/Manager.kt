package com.invincible.rambooster

import android.content.Context
import com.topjohnwu.superuser.Shell

class Manager(private var context: Context) {


    fun stopableApps(): List<String> {
        val a = arrayListOf<String>()
        val result = getShellResult("ls data/data")
        val exception = getNotToStop()
        for (name in result) {
            if (name !in exception) {
                a.add(name)
            }
        }
        return a
    }

    fun stopPackage(name: String) {
        getShellResult("am force-stop $name")
    }

    private fun getNotToStop(): List<String> {
        val list = arrayListOf(context.packageName)
        list.add("com.android.systemui")
        return list
    }

    private fun getShellResult(cmd: String): List<String> {
        val result = Shell.cmd(cmd).exec()
        return result.out
    }

    fun getRamInfo(): String {
        var memory = ""
        val result = getShellResult("cat /proc/meminfo")
        var memTotal = result[0].removePrefix("MemTotal:")
        val memFree = result[1].removePrefix("MemFree:")
        var memAvailable = result[2].removePrefix("MemAvailable:")

        memTotal = memTotal.removeSuffix(" kB").trim()
        memFree.removeSuffix(" kB").trim()
        memAvailable = memAvailable.removeSuffix(" kB").trim()

        /*    Utility.Log(memTotal)
Utility.Log(memFree)
Utility.Log(memTotal)*/

        try {
            memTotal = (memTotal.toInt() / (1024 * 1024)).toString()
            val available = (memAvailable.toFloat() / (1024 * 1024))
            memory = "$available GB || $memTotal GB"
        } catch (_: Exception) {
            memory = "Error In Getting Memory Status"

        }
        return memory
    }

    companion object {
        val IS_ROOT_GRANTED = checkRoot()
        private fun checkRoot(): Boolean {
            Shell.getShell()
            return Shell.isAppGrantedRoot() == true
        }
    }
}


/*object Utility{
    fun Context.Toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_LONG).show()
    }
    fun Log(text : Any){
        Log.v("-----LOGGING------",text.toString())
    }
}
*/
