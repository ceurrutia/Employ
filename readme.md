# Employ API - Plataforma de Gestión de Empleos SaaS

## Arquitectura y Tecnologías Core

Esta API está diseñada bajo una arquitectura en capas (Controller, Service, Repository), lo que garantiza una separación clara de responsabilidades y escalabilidad.

Implementa **Spring Security** junto con **JWT (JSON Web Tokens)** para gestionar una autenticación stateless; esto permite proteger los recursos mediante roles (USER, COMPANY, ADMIN) sin sobrecargar el servidor con sesiones de usuario.

La comunicación de datos se realiza mediante **Java Records**, utilizados como DTOs (Data Transfer Objects) inmutables que aseguran la integridad de la información y simplifican la validación de payloads. Todo el ecosistema está orquestado con Docker, asegurando que el entorno de desarrollo y producción sean idénticos mediante la compilación interna con **Maven y Java 21**.

El proyecto utiliza **Docker Compose** para orquestar la aplicación y la base de datos MySQL, asegurando que el entorno sea idéntico en cualquier máquina.

## Requisitos previos

* Docker y Docker Compose instalados.
* Archivo .env configurado en la raíz (ver sección de Variables de entorno).
---

### Inicio Rápido

1. Clone el repositorio: `git clone https://github.com/ceurrutia/Employ.git`
2. Configure su `.env` basado en el ejemplo.
3. Ejecute: `docker-compose up -d`
4. La API estará disponible en `http://localhost:8080` y Swagger en `http://localhost:8080/swagger-ui.html`.

## Variables de entorno

Crea un archivo .env en la raiz del proyecto

```
# Base de Datos (MySQL)
DB_HOST=localhost
DB_PORT=3306
DB_NAME=nombre_base_de_datos
DB_USER=tu_usuario
DB_PASSWORD=tu_contraseña
DB_ROOT_PASSWORD=tu_root_password

# Seguridad (JWT)
# Se recomienda una clave de 64 caracteres para algoritmos HMAC512
JWT_SECRET=una_clave_muy_secreta_y_aleatoria_de_clave_64
JWT_EXPIRATION=86400000

# Almacenamiento
UPLOAD_DIR=./uploads
```
---

# Seguridad (JWT)

Se recomienda una clave de 64 caracteres para algoritmos HMAC512
```
JWT_SECRET=tu_clave_secreta_super_larga_y_aleatoria
JWT_EXPIRATION=86400000
```

# Almacenamiento
UPLOAD_DIR=./uploads

---
## Roles y Permisos

| Acción | Público | USER | COMPANY | ADMIN |
| :--- | :---: | :---: | :---: | :---: |
| Registrarse / Login | ✅ | ✅ | ✅ | ✅ |
| Ver listado de ofertas | ✅ | ✅ | ✅ | ✅ |
| Crear / Editar sus propias ofertas | ❌ | ❌ | ✅ | ✅ |
| **Desactivar su propia cuenta** | ✅ | ✅ | ✅ | ✅ |
| Crear ofertas para cualquier empresa | ❌ | ❌ | ❌ | ✅ |
| Gestionar usuarios (Activar/Inactivar otros) | ❌ | ❌ | ❌ | ✅ |
| Ver métricas de sistema (Actuator) | ❌ | ❌ | ❌ | ✅ |

---

## Endpoints Principales

### Autenticación y Perfil

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/api/auth/users/register` | Registro de nuevos usuarios o empresas. |
| `POST` | `/api/auth/users/login` | Login para obtener el token JWT. |
| `DELETE` | `/api/auth/users/{id}` | **Desactivación de cuenta** (Baja lógica). |

### Ofertas de Trabajo (`/api/offers`)

| Método | Endpoint | Rol | Descripción |
|:-------| :--- | :--- |:----------------------------------------|
| `GET`  | `/api/offers` | Público | Lista todas las ofertas activas por defecto. |
| `GET`  | `/api/offers?status=CLOSED` | Público | Lista las ofertas filtradas por estado. |
| `GET`  | `/api/offers/{id}` | Público | Detalle de una oferta específica. |
| `POST` | `/api/offers` | COMPANY/ADMIN | Crea una nueva oferta de trabajo. |
| `PATCH`| `/api/offers/{id}/close` | COMPANY/ADMIN | Cierre manual de oferta. |

### Postulaciones (`/api/applications`)

| Método | Endpoint | Rol | Descripción |
|:-------|:---------|:----|:------------|
| `POST` | `/api/applications` | USER | Postulación a oferta (solo `ACTIVE`). Valida duplicados. |
| `GET`  | `/api/applications/my-applications` | USER | Historial del candidato autenticado. |
| `GET`  | `/api/applications/offer/{offerId}` | COMPANY/ADMIN | Lista candidatos de una oferta (solo dueño). |
| `GET`  | `/api/applications/company` | COMPANY | **Dashboard:** Lista agrupada por Título de Oferta. |
| `PATCH`| `/api/applications/{id}/status` | COMPANY/ADMIN | Cambia estado. Si es `HIRED`, la oferta se cierra automáticamente. |

---

## Manejo de Errores (Global Exception Handler)

La API responde con formatos estandarizados para errores de negocio y validación:

### Error de Lógica de Negocio (400 Bad Request)
```json
{
  "error": "Business Logic Error",
  "message": "Ya te has postulado a esta oferta",
  "timestamp": "2026-02-22T00:08:46..."
}
```
```
{
  "jobOfferId": "El ID de la oferta es obligatorio"
}

{
  "error": "Access forbidden",
  "message": "No tienes permisos para acceder a este recurso."
}

```
## Ejemplos:

* POST /api/auth/users/register

```
{
  "username": "modatech",
  "email": "contacto@modatech.com",
  "password": "password123",
  "role": "COMPANY",
  "companyName": "Moda Tech S.A."
}

```
## Crear ofertas

* POST a /api/offers
```
{
  "title": "Senior Java Developer",
  "description": "Buscamos experto en Spring Boot.",
  "category": "IT",
  "workMode": "REMOTE"
}

```

## Cambio de Estado a Contratado (Cierre automático)

* PATCH /api/applications/{id}/status

```
{
    "status": "HIRED"
}

```

## 🛠️ Próximas Mejoras (Roadmap)

* [ ] **Carga de CVs:** Integración con AWS S3 para gestión de documentos PDF.
* [ ] **Notificaciones:** Implementación de Amazon SES o SendGrid para avisos de cambio de estado.
* [ ] **Búsqueda Avanzada:** Filtros dinámicos mediante Spring Data Specifications.
* [ ] **Auditoría:** Trazabilidad completa de cambios en ofertas mediante Hibernate Envers.