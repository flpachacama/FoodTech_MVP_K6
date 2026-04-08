# 🚀 Guía rápida: k6 + Prometheus + Grafana

**Sistema operativo:** Windows  
**Duración:** 5-10 minutos

---

## ✅ Requisitos previos

1. **k6 instalado**
   ```powershell
   k6 version
   ```
   Si no está instalado:
   ```powershell
   choco install k6
   ```

2. **Docker Desktop corriendo**
   - Verifica que Docker esté activo (deberías ver el ícono en la bandeja)

3. **Backend (opcional pero recomendado)**
   - Si quieres probar contra tu backend: `docker-compose up -d` en raíz

---

## 🎯 Paso 1: Levantar Prometheus + Grafana

```powershell
cd "C:\Users\fredd\Desktop\Freddy Leonel\FoodTech_MVP_K6"
.\monitoring\scripts\start-monitoring.bat
```

**Espera a que aparezca:**
```
✅ Servicios levantados exitosamente
📊 Acceso a servicios:
🔍 Prometheus: http://localhost:9090
📈 Grafana:    http://localhost:3000
```

---

## 🎯 Paso 2: Ejecutar k6 con Prometheus

**Opción A: Con npm (más simple)**
```powershell
npm run perf:smoke:prom
npm run perf:load:prom
npm run perf:stress:prom
npm run perf:spike:prom
```

**Opción B: Con script directo**
```powershell
.\monitoring\scripts\run-k6-prometheus.bat smoke.test.js
.\monitoring\scripts\run-k6-prometheus.bat load.test.js
.\monitoring\scripts\run-k6-prometheus.bat stress.test.js
.\monitoring\scripts\run-k6-prometheus.bat spike.test.js
```

---

## 🎯 Paso 3: Visualizar en Grafana

1. **Abre** http://localhost:3000
2. **Inicia sesión:**
   - Usuario: `admin`
   - Contraseña: `admin`

3. **Importar dashboard oficial de k6:**
   - Ve a **Dashboards** → **Import**
   - ID: `2587`
   - Selecciona **Prometheus** como datasource
   - Click **Import**

4. **Ver métricas en tiempo real:**
   - Dashboard mostrará:
     - VUs activos
     - HTTP Requests/s
     - Request Duration (p95, p99)
     - Error Rate
     - Checks Pass/Fail

---

## 📊 URLs importantes

| Servicio | URL | Usuario | Pass |
|----------|-----|---------|------|
| Prometheus | http://localhost:9090 | - | - |
| Grafana | http://localhost:3000 | admin | admin |

---

## 🛑 Paso final: Detener

Cuando termines:

```powershell
.\monitoring\scripts\stop-monitoring.bat
```

O con npm:
```powershell
npm run monitoring:stop
```

---

## 📝 Notas importantes

✓ **Los scripts de k6 NO fueron modificados** → Funcionan igual que antes  
✓ **Prometheus recibe métricas en tiempo real** → Sin retrasos  
✓ **Grafana persiste datos** → Los dashboards se guardan  
✓ **Completamente desacoplado** → Puedes ejecutar k6 sin monitoreo

---

## 🐛 Si algo no funciona

### "Prometheus no disponible"
```powershell
# Verifica que esté corriendo
docker ps | findstr "prometheus"

# Reinicia
docker-compose -f monitoring\docker-compose.yml restart prometheus
```

### "Grafana no carga"
```powershell
# Verifica que esté corriendo
docker ps | findstr "grafana"

# Reinicia
docker-compose -f monitoring\docker-compose.yml restart grafana
```

### "k6 no envía métricas"
1. Verifica que Prometheus esté en http://localhost:9090
2. Verifica que k6 esté instalado: `k6 version`
3. Revisa logs de k6 en la terminal

---

## 💡 Próximas configuraciones (opcional)

- Crear dashboards personalizados en Grafana
- Configurar alertas en Prometheus
- Exportar dashboards como JSON para reutilizar
- Integrar con sistemas de notificación (Slack, email)

---

**¿Necesitas más ayuda?** Ver `monitoring/README.md` para documentación completa.
