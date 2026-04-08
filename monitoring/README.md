# Monitoreo con Prometheus y Grafana

Integración de k6 con **Prometheus** y **Grafana** para visualización de métricas en tiempo real.

## 🎯 Flujo rápido

### 1. Levantar servicios de monitoreo

**Linux/macOS:**
```bash
bash monitoring/scripts/start-monitoring.sh
```

**Windows:**
```cmd
.\monitoring\scripts\start-monitoring.bat
```

### 2. Ejecutar k6 con Prometheus

**Linux/macOS:**
```bash
bash monitoring/scripts/run-k6-prometheus.sh smoke.test.js
bash monitoring/scripts/run-k6-prometheus.sh load.test.js
bash monitoring/scripts/run-k6-prometheus.sh stress.test.js
bash monitoring/scripts/run-k6-prometheus.sh spike.test.js
```

**Windows:**
```cmd
.\monitoring\scripts\run-k6-prometheus.bat smoke.test.js
.\monitoring\scripts\run-k6-prometheus.bat load.test.js
.\monitoring\scripts\run-k6-prometheus.bat stress.test.js
.\monitoring\scripts\run-k6-prometheus.bat spike.test.js
```

### 3. Acceder a Grafana

- **URL:** http://localhost:3000
- **Usuario:** admin
- **Contraseña:** admin

---

## 📊 Acceso a servicios

| Servicio | URL | Descripción |
|----------|-----|-------------|
| Prometheus | http://localhost:9090 | Base de datos de métricas |
| Grafana | http://localhost:3000 | Visualización de métricas |

---

## 🔧 Configuración

### Estructura

```
monitoring/
├── docker-compose.yml         # Definición de servicios
├── prometheus.yml             # Configuración de Prometheus
├── scripts/
│   ├── start-monitoring.sh    # Levantar servicios (Linux/macOS)
│   ├── start-monitoring.bat   # Levantar servicios (Windows)
│   ├── stop-monitoring.sh     # Detener servicios (Linux/macOS)
│   ├── stop-monitoring.bat    # Detener servicios (Windows)
│   ├── run-k6-prometheus.sh   # Ejecutar k6 con Prometheus (Linux/macOS)
│   └── run-k6-prometheus.bat  # Ejecutar k6 con Prometheus (Windows)
└── dashboards/
    └── (dashboards JSON - ver sección "Dashboards")
```

### Prometheus

**Archivo:** `prometheus.yml`

- **Scrape interval:** 15s
- **Remote write:** Configurado para recibir métricas de k6
- **Retention:** 15 días (por defecto en Prometheus)

### Grafana

**Docker Compose:**

- Usuario por defecto: `admin`
- Contraseña por defecto: `admin`
- Volumen persistente: `grafana-storage`

---

## 🚀 Primeros pasos después de levantar

### 1. Configurar Prometheus como datasource

1. Accede a Grafana: http://localhost:3000
2. Ve a **Configuration** → **Data Sources**
3. Click en **Add data source**
4. Selecciona **Prometheus**
5. URL: `http://prometheus:9090` (dentro de Docker) o `http://localhost:9090` (local)
6. Click en **Save & Test**

### 2. Importar Dashboard oficial de k6

1. En Grafana: **Dashboards** → **Import**
2. ID del dashboard oficial de k6: **2587**
3. Selecciona **Prometheus** como datasource
4. Click **Import**

---

## 📈 Dashboards recomendados

### Dashboard oficial de k6

- **ID:** 2587
- **Autor:** k6 Community
- **Métricas incluidas:**
  - Virtual Users (VUs)
  - HTTP Requests
  - Request Duration (p95, p99)
  - Error Rate
  - Connection States
  - Response Time Distribution

**Importar automáticamente:**

```bash
# URL directa a Grafana
http://localhost:3000/api/dashboards/import?gnetId=2587&orgId=1
```

### Métricas disponibles en Prometheus

Todas las métricas generadas por k6 están disponibles:

- `k6_http_reqs` - Total de requests
- `k6_http_req_duration` - Duración de requests (en ms)
- `k6_http_req_failed` - Requests fallidos
- `k6_vus` - Virtual Users activos
- `k6_checks` - Checks pasados/fallidos
- `k6_group_duration` - Duración de grupos
- `k6_iterations` - Iteraciones completadas

**Consulta ejemplo en Prometheus:**

```promql
# p95 de duración de requests
histogram_quantile(0.95, k6_http_req_duration)

# Error rate
rate(k6_http_req_failed[1m])

# VUs activos
k6_vus
```

---

## 🔄 Variables de entorno

Personaliza la integración con variables:

```bash
# Linux/macOS
export PROMETHEUS_URL="http://localhost:9090"
bash monitoring/scripts/run-k6-prometheus.sh load.test.js

# Windows
set PROMETHEUS_URL=http://localhost:9090
.\monitoring\scripts\run-k6-prometheus.bat load.test.js
```

---

## 🛑 Detener servicios

**Linux/macOS:**
```bash
bash monitoring/scripts/stop-monitoring.sh
```

**Windows:**
```cmd
.\monitoring\scripts\stop-monitoring.bat
```

**Limpiar volúmenes (elimina datos):**

```bash
# Desde directorio monitoring/
docker-compose down -v
```

---

## ⚙️ Ejecución manual de k6 (sin scripts)

Si prefieres ejecutar k6 manualmente:

```bash
k6 run \
  --env TEST_ENV=dev \
  --env REPORT_DIR=performance/reports \
  --out experimental-prometheus-rw \
  performance/tests/smoke.test.js
```

**Nota:** Asegúrate de que Prometheus esté levantado (`http://localhost:9090/api/v1/status/config` debe responder).

---

## 🐳 Docker Compose: Services

### Prometheus

- **Image:** `prom/prometheus:latest`
- **Port:** `9090`
- **Volume:** `prometheus-storage:/prometheus`
- **Healthcheck:** Cada 10s
- **Restart:** unless-stopped

### Grafana

- **Image:** `grafana/grafana:latest`
- **Port:** `3000`
- **Volume:** `grafana-storage:/var/lib/grafana`
- **Depends on:** Prometheus (healthcheck)
- **Restart:** unless-stopped

**Network:** `k6-monitoring` (bridge)

---

## 🔗 Integración con scripts existentes

**IMPORTANTE:** Los scripts de k6 no fueron modificados.

La integración es **completamente desacoplada**:

- `performance/tests/*.test.js` → Sin cambios
- `performance/utils/*` → Sin cambios
- `performance/config/*` → Sin cambios
- `performance/flows/*` → Sin cambios

Solo se agregó:

- Opción de ejecución con `--out experimental-prometheus-rw`
- Scripts auxiliares en `monitoring/scripts/`
- Configuración de Prometheus en `monitoring/`

---

## 📝 Ejemplo: Flujo completo

```bash
# 1. Levantar monitoreo
bash monitoring/scripts/start-monitoring.sh

# 2. Esperar 10 segundos (servicios se estabilizan)
sleep 10

# 3. Ejecutar prueba de smoke
bash monitoring/scripts/run-k6-prometheus.sh smoke.test.js

# 4. Abrir Grafana
# http://localhost:3000

# 5. Ver resultados en tiempo real
# (Hacer click en "Dashboards" → Buscar "k6" o importar ID 2587)

# 6. Detener monitoreo
bash monitoring/scripts/stop-monitoring.sh
```

---

## 🐛 Troubleshooting

### Prometheus no responde

```bash
# Verificar que esté levantado
curl http://localhost:9090/api/v1/status/config

# Si falla, revisar logs
docker-compose logs prometheus

# Reiniciar
docker-compose restart prometheus
```

### Grafana no carga

```bash
# Verificar que esté levantado
curl http://localhost:3000/api/health

# Si falla, revisar logs
docker-compose logs grafana

# Reiniciar
docker-compose restart grafana
```

### k6 no envía métricas

1. Verificar que Prometheus esté corriendo:
   ```bash
   curl http://localhost:9090/api/v1/status/config
   ```

2. Verificar que k6 esté instalado:
   ```bash
   k6 version
   ```

3. Ver logs de k6 en stderr (buscar "prometheus" en salida)

---

## 📞 Recursos

- **k6 + Prometheus:** https://k6.io/docs/results-output/real-time/prometheus-remote-write/
- **Prometheus:** https://prometheus.io/docs/
- **Grafana:** https://grafana.com/docs/
- **Dashboard k6 oficial (ID 2587):** https://grafana.com/grafana/dashboards/2587/

---

**Versión:** 1.0  
**Última actualización:** 8 de abril de 2026  
**Mantenedor:** FoodTech QA Team
