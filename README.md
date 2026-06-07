# VitalBite Spring Boot

Microservicio documental de VitalBite. Se encarga de generar PDFs, guardar
metadatos en PostgreSQL/Supabase y subir documentos a AWS S3.

## Requisitos

- Java 21
- Maven Wrapper incluido en el proyecto (`mvnw.cmd` en Windows)
- PostgreSQL/Supabase configurado
- Credenciales de AWS S3

## Configuracion

1. Entra al proyecto:

   ```powershell
   cd C:\Proyectos\SW2_VitalBite\vitalBite-Spring-Boot
   ```

2. Crea el archivo `.env` desde el ejemplo si todavia no existe:

   ```powershell
   copy .env-example .env
   ```

3. Completa las variables del `.env`:

   ```env
   DB_URL=jdbc:postgresql://db.tuproyecto.supabase.co:5432/postgres
   DB_USERNAME=postgres
   DB_PASSWORD=tu_password

   AWS_BUCKET_NAME=vitalbite-documentos
   AWS_REGION=us-east-1
   AWS_ACCESS_KEY=tu_access_key
   AWS_SECRET_KEY=tu_secret_key

   JWT_SECRET=una_clave_secreta_muy_larga_y_segura_minimo_32_caracteres
   ```

El proyecto carga estas variables automaticamente usando `dotenv-java`.

## Instalacion de dependencias

En Windows:

```powershell
.\mvnw.cmd clean install
```

En Linux/Mac:

```bash
./mvnw clean install
```

## Ejecucion

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux/Mac:

```bash
./mvnw spring-boot:run
```

El backend se levanta en:

```text
http://localhost:8082/api/v1
```

## URLs utiles

- Swagger UI: `http://localhost:8082/api/v1/swagger-ui/index.html`
- Health documental: `http://localhost:8082/api/v1/documents/health`
- Actuator health: `http://localhost:8082/api/v1/actuator/health`

## Endpoints principales

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| `POST` | `/api/v1/documents/pdf/diet` | Genera un PDF de dieta, lo sube a S3 y devuelve la URL prefirmada |
| `POST` | `/api/v1/documents/pdf/diet/preview` | Genera una vista previa del PDF sin guardar en base de datos |
| `GET` | `/api/v1/documents/health` | Verifica que el modulo documental este activo |

## Pruebas

```powershell
.\mvnw.cmd test
```

## Notas

- El puerto configurado es `8082`.
- El contexto base configurado es `/api/v1`.
- Hibernate esta configurado con `spring.jpa.hibernate.ddl-auto=update`.
- No subas el archivo `.env` al repositorio porque contiene credenciales.
