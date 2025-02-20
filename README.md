# 🔐 Sistema de Cifrado y Keylogger en Kotlin

Este proyecto implementa un sistema de cifrado de archivos y un keylogger en **Kotlin**, utilizando **AES** para el cifrado de datos y **JNativeHook** para la captura de teclas. También cuenta con la funcionalidad de enviar datos por correo electrónico.

## 🚀 **Finalidad del Proyecto**

Este sistema tiene como propósito demostrar el uso de técnicas de **cifrado de archivos**, **registro de pulsaciones de teclas** y **envío de correos electrónicos** en aplicaciones de seguridad y auditoría de datos.

### **⚠️ Advertencia**
> Este código tiene fines educativos. Su uso para actividades malintencionadas o sin consentimiento es ilegal.

---

## 🛠 **Tecnologías Utilizadas**

- **Kotlin** → Lenguaje de programación principal.
- **AES (Advanced Encryption Standard)** → Algoritmo de cifrado para proteger archivos.
- **JNativeHook** → Librería para capturar eventos del teclado.
- **JavaMail API** → Para el envío de correos electrónicos con datos adjuntos.
- **Base64 Encoding** → Para la conversión y almacenamiento de claves de cifrado.
- **SMTP (Simple Mail Transfer Protocol)** → Envío de correos a través de servidores SMTP.

---

## 📂 **Estructura del Proyecto**

El proyecto se divide en tres paquetes principales:

### **1️⃣ `happyIdPack` - Cifrado y Descifrado de Archivos**
> Este paquete contiene las funcionalidades para cifrar y descifrar archivos en un directorio específico.

- `generarClaveSimetrica()` → Genera una clave AES de 128 bits.
- `cifrarArchivo(archivo: File, clave: Key)` → Cifra un archivo y lo almacena con la extensión `.enc`.
- `descifrarArchivo(archivo: File, clave: Key)` → Descifra un archivo cifrado previamente.
- `guardarClaveEnArchivo(clave: Key, idSesion: String)` → Almacena la clave de cifrado en un archivo con un ID de sesión único.
- `recuperarClaveDesdeArchivo(idSesion: String): Key?` → Recupera la clave desde el archivo correspondiente al ID de sesión.

### **2️⃣ `happyKeylogger` - Registro de Teclas y Envío de Datos**
> Implementa un **keylogger** que registra las teclas presionadas y las almacena en un archivo de texto.

- `Keylogger : NativeKeyListener` → Escucha y registra las pulsaciones de teclas.
- `logKey(key: String)` → Almacena las teclas capturadas en un archivo `keylogger_logs.txt`.
- `enviarArchivoPorCorreo(logFile: File)` → Envía el archivo de registro por correo electrónico.
- `main()` → Inicia el keylogger y lo detiene después de un tiempo determinado.

### **3️⃣ `happyPackCorreoInicial` - Cifrado y Envío de Claves**
> Este módulo complementa el cifrado de archivos enviando la clave de descifrado por correo electrónico.

- `enviarClavePorCorreo(clave: Key)` → Convierte la clave a formato Base64 y la envía por correo.
- `convertirClaveDesdeBase64(base64: String): Key` → Decodifica la clave en formato Base64.

---

## 📌 **Instalación y Ejecución**

1️⃣ **Clonar el Repositorio**
```sh
  git clone https://github.com/tu-usuario/nombre-del-repositorio.git
  cd nombre-del-repositorio
```

2️⃣ **Ejecutar el Keylogger**
```sh
  kotlin happyKeylogger.MainKt
```

3️⃣ **Ejecutar el Cifrador de Archivos**
```sh
  kotlin happyIdPack.MainKt
```

4️⃣ **Descifrar Archivos con la Clave Base64**
```sh
  kotlin happyPackCorreoInicial.MainKt
```

---

## 📌 **Posibles Mejoras y Funcionalidades Futuras**

✅ Implementar cifrado con claves de mayor longitud (AES-256).  
✅ Mejorar la gestión de errores y excepciones.  
✅ Integración con bases de datos para almacenamiento seguro de claves y logs.  
✅ Implementar interfaz gráfica para mejorar la usabilidad.  

---

## 📜 **Licencia**

Este proyecto es de código abierto y se encuentra bajo la licencia **MIT**.

📢 **IMPORTANTE:** Usa este código con responsabilidad. 🚀

