# Daily Procurement System — Web Edition

The same restaurant procurement system from the course paper, now running in the
browser. You still **write and run it in Java + IntelliJ**, and it deploys to
**free hosting** as a single small container.

It keeps the project's "no external frameworks" principle: the web layer is built
on the JDK's built-in HTTP server (`com.sun.net.httpserver`), so there are **zero
dependencies** to download — the JAR is ~32 KB.

## What changed vs. the desktop version

Only the **presentation layer** was swapped. The `model`, `repository`, and
`service` packages are identical — the exact same business logic, validation, and
file persistence. Swing was replaced by:

- a small **REST API** (`web/ApiServer.java`)
- a single-page **web frontend** (`resources/static/index.html`) with the same
  four tabs: Products, Suppliers, Daily Procurement, Reports

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/products` | GET / POST / DELETE | List, add, delete products |
| `/api/suppliers` | GET / POST / DELETE | List, add, delete suppliers |
| `/api/records?from=&to=&category=` | GET | Filtered records + grand total |
| `/api/records` | POST | Add (no `id`) or edit (with `id`) a record |
| `/api/records/{id}` | DELETE | Delete a record |
| `/api/reports?date=` | GET | Daily total + breakdown by category |

## Run locally in IntelliJ

1. **File > Open** → select the `daily-procurement-web` folder
2. Open `src/main/java/com/daily/procurement/WebApp.java`
3. Click ▶ next to `main()`
4. Open <http://localhost:8080> in your browser

Or from the command line:

```bash
mvn compile exec:java
# or without Maven:
find src -name "*.java" > sources.txt
javac -d out @sources.txt
cp -r src/main/resources/static out/
java -cp out com.daily.procurement.WebApp
```

## Deploy to free hosting

The included `Dockerfile` compiles the project with `javac` (no Maven, no network),
so it builds reliably on any host that supports Docker.

### Render.com (recommended, has a free tier)

1. Push this folder to a **GitHub** repository
2. On <https://render.com>: **New > Web Service** → connect the repo
3. Render auto-detects the `Dockerfile`; choose the **Free** plan → **Create**
4. After the build finishes you get a public `https://<name>.onrender.com` URL

The `render.yaml` blueprint lets you do this in one click via **New > Blueprint**.

Other hosts with free Docker tiers work the same way: **Railway**, **Koyeb**,
**Fly.io**. The app reads the `PORT` environment variable they provide and binds
to `0.0.0.0` automatically.

### Test the container locally

```bash
docker build -t daily-procurement .
docker run -p 8080:8080 daily-procurement
# open http://localhost:8080
```

## ⚠️ A note on data persistence on free hosting

Free tiers use **ephemeral disks** — the `data/` CSV files survive while the
container is running, but are wiped on each redeploy or cold start (free services
also sleep after inactivity). On every fresh start the app re-seeds its sample data,
so it always works for a demo.

For permanent storage, follow the recommendation from the paper's conclusions:
replace the file repositories with a database. A free **PostgreSQL** instance
(Render, Neon, or Supabase) plugged in behind the existing `Repository` interface
keeps the rest of the application unchanged — which is exactly the benefit the
repository pattern was chosen for.

## Project structure

```
daily-procurement-web/
├── Dockerfile              # javac build → tiny JRE image
├── render.yaml             # one-click Render blueprint
├── pom.xml                 # for IntelliJ import / local run
└── src/main/
    ├── java/com/daily/procurement/
    │   ├── WebApp.java                 # entry point (reads $PORT)
    │   ├── model/                      # unchanged from desktop version
    │   ├── repository/                 # unchanged
    │   ├── service/                    # unchanged
    │   └── web/
    │       ├── ApiServer.java          # HTTP server + REST routing
    │       └── JsonWriter.java         # tiny JSON output helper
    └── resources/static/
        └── index.html                  # single-page frontend (4 tabs)
```
