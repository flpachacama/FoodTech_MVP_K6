import { browseCatalogFlow, cancelOrderFlow, createFlowContext, createOrderJourney } from '../flows/business-flows.js';

export function runApiJourney(params) {
  const { config, user, payload, debug } = params;
  const ctx = createFlowContext(config, user, debug);

  const catalogTrace = { hu: 'HU10', tc: 'TC-031', scenarioType: 'journey' };
  const assignTrace = { hu: 'HU8-HU5', tc: 'TC-024-TC-013', scenarioType: 'journey' };
  const cancelTrace = { hu: 'HU9-HU6', tc: 'TC-029-TC-019', scenarioType: 'journey' };

  const catalog = browseCatalogFlow(ctx, catalogTrace);
  const created = createOrderJourney(ctx, payload, assignTrace, false);

  let canceled = { ok: true };
  if (config.enableCleanup && created.orderId) {
    canceled = cancelOrderFlow(ctx, created.orderId, created.repartidorId, cancelTrace);
  }

  return {
    ok: catalog.ok && created.ok && canceled.ok,
    orderId: created.orderId,
    repartidorId: created.repartidorId,
  };
}
