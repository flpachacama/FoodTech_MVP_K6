export function loadJson(relativePath) {
  const resolvedPath = import.meta.resolve(relativePath);

  // k6 runtime does not expose URL globally, so normalize using plain strings.
  if (resolvedPath.startsWith('file://')) {
    let decodedPath = decodeURIComponent(resolvedPath.replace('file:///', '').replace('file://', ''));

    // file://C:/... can appear; normalize to C:/...
    if (/^[A-Za-z]:\//.test(decodedPath) === false && /^\/[A-Za-z]:\//.test(decodedPath)) {
      decodedPath = decodedPath.slice(1);
    }

    // file URLs may use forward slashes; k6 on Windows accepts both, keep as-is.
    if (!decodedPath) {
      throw new Error(`Unable to resolve data file path for: ${relativePath}`);
    }

    return JSON.parse(open(decodedPath));
  }

  return JSON.parse(open(resolvedPath));
}

export function pickByIteration(items, seed) {
  if (!items || items.length === 0) {
    return null;
  }
  return items[seed % items.length];
}

export function buildOrderPayload(basePayload, options) {
  const suffix = options && options.suffix ? options.suffix : `${__VU}-${__ITER}`;
  const climate = options && options.climate ? options.climate : basePayload.clima;

  return {
    ...basePayload,
    clima: climate,
    clienteId: basePayload.clienteId || Number(`9${(__VU % 9)}${(__ITER % 9)}`),
    clienteNombre: `${basePayload.clienteNombre}-${suffix}`,
    clienteTelefono: `3${String(100000000 + ((__VU * 1000 + __ITER) % 899999999))}`,
  };
}

export function pickClimateForStress() {
  const climates = ['SOLEADO', 'LLUVIA_SUAVE', 'LLUVIA_FUERTE'];
  return climates[(__VU + __ITER) % climates.length];
}
