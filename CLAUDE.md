# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository layout

Two parallel implementations of the same "Mini Blog / Notes" domain (posts + comments):

- `src/` — the **original monolith** (`com.example.miniblog`, Spring Boot + JPA, single MySQL schema `miniblog`). Kept as a reference; not part of the microservices runtime.
- `microservices/` — the **active** system: four independent Spring Boot apps that replace the monolith.
- `docker-compose.yml` at the root only provisions the MySQL 8.4 container (`miniblog-mysql` on `:3306`). It does **not** start any of the Java apps.
- `microservices/README.md` is the authoritative long-form architecture doc (with sequence diagrams and full API reference). Read it before making architectural changes.

## Build & run

Java 21 + Maven. Each of the five Maven projects (root monolith + four microservices) is independent — there is no aggregator POM, so you build and run them one at a time.

Windows note: the shell here is PowerShell — chain commands with `;` (not `&&`). The Bash tool is also available for POSIX examples copied from the README.

### Common commands (run from each module's directory)

```powershell
mvn clean package                # build a runnable jar into target/
mvn spring-boot:run              # run in place
mvn test                         # run tests
mvn test -Dtest=ClassName#method # run a single test
java -jar target/<artifact>-0.0.1-SNAPSHOT.jar
```

### Bringing the microservices system up

Order matters — Eureka must be up before the clients register:

1. `docker compose up -d` (root) — starts MySQL.
2. First-time only, create both schemas and grant access:
   ```
   docker exec miniblog-mysql mysql -uroot -prootpass -e "CREATE DATABASE IF NOT EXISTS posts_db; CREATE DATABASE IF NOT EXISTS comments_db; GRANT ALL PRIVILEGES ON posts_db.* TO 'miniblog'@'%'; GRANT ALL PRIVILEGES ON comments_db.* TO 'miniblog'@'%'; FLUSH PRIVILEGES;"
   ```
3. Start `microservices/eureka-server` (`:8761`), wait ~10s, then start `post-service` (`:8081`), `comment-service` (`:8082`), and `api-gateway` (`:8080`) in any order.
4. All external traffic goes through the gateway on `:8080`; backend ports are internal only. Registry dashboard: <http://localhost:8761>.

The monolith (`src/`) also binds `:8080` — do **not** run it alongside `api-gateway`.

## Architecture

### High-level

Clients → **api-gateway** (`:8080`, Spring Cloud Gateway, reactive/WebFlux) → routed via `lb://<service-name>` to either **post-service** or **comment-service**. All three register with **eureka-server** (`:8761`); names are resolved to live instances at request time. Path-based routing rules live in `microservices/api-gateway/src/main/resources/application.yml`.

- `/api/posts/**` → `lb://post-service`
- `/api/comments/**` → `lb://comment-service`

### Database-per-service (deliberate)

- `post-service` owns `posts_db` (`posts` table).
- `comment-service` owns `comments_db` (`comments` table).
- **No foreign key** links them. `comments.post_id` is a plain `Long`, not a `@ManyToOne` — no cross-database joins are possible or intended.
- Do **not** re-introduce a JPA relationship between `Post` and `Comment` in the microservices code; the monolith's `src/` still models it that way for reference.

### Inter-service communication

When creating a comment, `comment-service` must confirm the post exists. It does **not** touch `posts_db`. Instead:

- `post-service` exposes `GET /api/posts/{id}/exists` — a status-only endpoint (200 or 404, no body). Preserve this signature; other services depend on it.
- `comment-service` calls it through a `@LoadBalanced RestClient` (`config/RestClientConfig.java`) whose `baseUrl` is the Eureka **service name** `http://post-service` (no host/port). The name is resolved via Eureka at request time.
- If `post-service` is unreachable, the resulting `RestClientException` maps to `503`; a `404` from `/exists` maps to `404 "Post not found"`. Keep these mappings intact.

### Gateway constraints

`api-gateway` is reactive (Netty). Do **not** add `spring-boot-starter-web` to it — the servlet stack will conflict with `spring-cloud-starter-gateway`. It must remain a Eureka client for the `lb://` scheme to resolve.

### Lombok

Not used anywhere in `microservices/` (the runtime JDK is incompatible with the Lombok annotation processor per the microservices README). Use plain getters/setters. The monolith `src/` does use Lombok — do not port Lombok annotations from it when moving code into a microservice.

### Config conventions

- Database connection is env-driven: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` (defaults target the docker-compose MySQL).
- Eureka location is env-driven: `EUREKA_URL` (default `http://localhost:8761/eureka/`).
- JPA is `ddl-auto=update` on every service — schemas are created/evolved automatically on startup; there are no migration files.
