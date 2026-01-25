# Chetumal AR - Visor de Monumentos en Realidad Aumentada

¡Bienvenido a **Chetumal AR**! Esta es una plataforma interactiva diseñada para explorar y conmemorar la riqueza histórica y cultural de Chetumal, Quintana Roo, a través de la Realidad Aumentada (AR).

## 🚀 Sobre el Proyecto

Chetumal AR permite a los usuarios visualizar monumentos emblemáticos de la ciudad directamente en su entorno real. El proyecto combina una potente aplicación móvil nativa para Android con un panel de administración web moderno para gestionar el catálogo de monumentos en la nube.

### ✨ Características Principales
*   **Visualización AR Inmersiva**: Coloca modelos 3D a escala real de monumentos en cualquier superficie plana.
*   **Catálogo Híbrido**: Incluye monumentos integrados de fábrica y permite descargar nuevos modelos dinámicamente desde internet.
*   **Panel de Administración**: Sitio web oficial para subir nuevos modelos 3D, editar descripciones y gestionar el contenido en tiempo real.
*   **Integración con la Nube**: Conexión directa con Supabase para almacenamiento y base de datos.

## 🏛️ Monumentos Destacados
*   **Manatí**: Homenaje al santuario del manatí en la Bahía de Chetumal.
*   **Lázaro Cárdenas**: Monumento al personaje fundamental en la historia de Quintana Roo.
*   **Monumento a la Mujer**: Representación icónica de la cultura local.
*   *¡Y muchos más disponibles a través de la galería dinámica!*

## 🛠️ Stack Tecnológico

### Aplicación Móvil (Android)
*   **Kotlin**: Lenguaje de programación moderno y seguro.
*   **SceneView (powered by Filament & ARCore)**: Motor 3D de alto rendimiento.
*   **Supabase SDK**: Integración de base de datos y Storage en tiempo real.
*   **Fuel & Kotlinx Serialization**: Manejo eficiente de peticiones API y JSON.

### Plataforma Web (Admin Panel)
*   **Angular 21**: Framework web premium para una experiencia de usuario fluida.
*   **Supabase API**: Gestión centralizada de datos.
*   **Vercel**: Despliegue de alta disponibilidad.

## 📦 Instalación y Configuración

### Requisitos Android
*   Dispositivo compatible con **ARCore**.
*   Android SDK 28 (Pie) o superior (Recomendado SDK 33+).
*   Android Studio Ladybug o superior.

### Pasos para Desarrolladores
1.  Clona este repositorio.
2.  Abre el proyecto `sceneview-android` en Android Studio.
3.  Sincroniza el proyecto con los archivos Gradle.
4.  Configura tus llaves de Supabase en `SupabaseApi.kt`.
5.  Despliega el panel web en `angular-app`.

---
*Desarrollado con ❤️ para preservar la cultura de Chetumal.*