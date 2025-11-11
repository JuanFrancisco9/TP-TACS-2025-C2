export interface EventLike {
  fecha?: unknown;
  horaInicio?: string | null;
}

const MILLIS_IN_NANO = 1_000_000;

function fromDateComponents(
  year?: number,
  month?: number,
  day?: number,
  hour = 0,
  minute = 0,
  second = 0,
  nano = 0
): Date | null {
  if (
    typeof year !== 'number' ||
    typeof month !== 'number' ||
    typeof day !== 'number'
  ) {
    return null;
  }
  const millis = Number.isFinite(nano) ? Math.trunc(nano / MILLIS_IN_NANO) : 0;
  const date = new Date(year, month - 1, day, hour, minute, second, millis);
  return Number.isNaN(date.getTime()) ? null : date;
}

function parseDateInput(value: unknown): Date | null {
  if (!value) {
    return null;
  }

  if (value instanceof Date) {
    return Number.isNaN(value.getTime()) ? null : value;
  }

  if (typeof value === 'number' && Number.isFinite(value)) {
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? null : date;
  }

  if (typeof value === 'string') {
    const date = new Date(value);
    if (!Number.isNaN(date.getTime())) {
      return date;
    }
    const numeric = Number(value);
    if (Number.isFinite(numeric)) {
      const dateFromNumber = new Date(numeric);
      if (!Number.isNaN(dateFromNumber.getTime())) {
        return dateFromNumber;
      }
    }
    return null;
  }

  if (Array.isArray(value)) {
    const [year, month, day, hour, minute, second, nano] = value.map((item) => (
      typeof item === 'number' ? item : Number(item)
    ));
    return fromDateComponents(year, month, day, hour, minute, second, nano);
  }

  if (typeof value === 'object') {
    const record = value as Record<string, unknown>;

    if ('$date' in record) {
      return parseDateInput(record.$date);
    }

    if ('$numberLong' in record) {
      return parseDateInput(Number(record.$numberLong));
    }

    const year = typeof record.year === 'number' ? record.year : Number(record.year);
    const month = typeof record.month === 'number' ? record.month : Number(record.month);
    const day = typeof record.day === 'number' ? record.day : Number(record.day);
    const hour = typeof record.hour === 'number' ? record.hour : Number(record.hour ?? 0);
    const minute = typeof record.minute === 'number' ? record.minute : Number(record.minute ?? 0);
    const second = typeof record.second === 'number' ? record.second : Number(record.second ?? 0);
    const nano = typeof record.nano === 'number' ? record.nano : Number(record.nano ?? 0);

    const dateFromComponents = fromDateComponents(year, month, day, hour, minute, second, nano);
    if (dateFromComponents) {
      return dateFromComponents;
    }
  }

  return null;
}

/**
 * Devuelve un objeto Date con la fecha y hora de inicio del evento, o null si no se puede determinar.
 */
export function getEventStartDate(evento?: EventLike | null): Date | null {
  const baseDate = parseDateInput(evento?.fecha);
  if (!baseDate) {
    return null;
  }

  if (evento?.horaInicio) {
    const [horasRaw, minutosRaw, segundosRaw] = evento.horaInicio.split(':');
    const horas = Number(horasRaw);
    const minutos = Number(minutosRaw ?? 0);
    const segundos = Number(segundosRaw ?? 0);

    baseDate.setHours(
      Number.isNaN(horas) ? 0 : horas,
      Number.isNaN(minutos) ? 0 : minutos,
      Number.isNaN(segundos) ? 0 : segundos,
      0
    );
  }

  return baseDate;
}

/**
 * Indica si el evento ya ocurrió en base a la fecha/hora de inicio calculada.
 */
export function isEventInPast(evento?: EventLike | null, reference: Date | number = Date.now()): boolean {
  const startDate = getEventStartDate(evento);
  const referenceTime = reference instanceof Date ? reference.getTime() : reference;
  if (!startDate) {
    return false;
  }
  return startDate.getTime() < referenceTime;
}
