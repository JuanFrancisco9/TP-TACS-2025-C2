import { useState } from 'react';
interface Persona {
    id: string;
    nombre: string;
    apellido: string;
}

interface Event {
    id: string;
    titulo: string;
    descripcion: string;
    fecha: string;
    horaInicio: string;
    duracion: number;
    ubicacion: {
        provincia: string;
        ciudad: string;
        direccion: string;
    };
    cupoMaximo: number;
    precio: { moneda: string; monto: number };
    organizador: Persona;
    estado: { tipoEstado: 'CONFIRMADO' | 'PENDENTE' | 'CANCELADO'; fechaCambio: string };
    categoria: { tipo: string };
    etiquetas: string[];
    inscriptos: Persona[];
    listaDeEspera: Persona[];
}
export default function PerfilOrganizador() {
    const [events, setEvents] = useState<Event[]>([
        {
            id: '1',
            titulo: 'Seminario de Mocks',
            descripcion: 'Introducción a Mocks',
            fecha: '2025-09-10',
            horaInicio: '19:00',
            duracion: 2.5,
            ubicacion: { provincia: 'Buenos Aires', ciudad: 'CABA', direccion: 'Av. Siempre Viva 123' },
            cupoMaximo: 30,
            precio: { moneda: 'ARS', monto: 1000 },
            organizador: { id: '1', nombre: 'Juan', apellido: 'Pérez' },
            estado: { tipoEstado: 'CONFIRMADO', fechaCambio: '2025-08-27' },
            categoria: { tipo: 'TECNOLOGIA' },
            etiquetas: ['mocks', 'testing', 'java'],
            inscriptos: [
                { id: 'u1', nombre: 'Pedro', apellido: 'Gómez' },
                { id: 'u2', nombre: 'María', apellido: 'López' }
            ],
            listaDeEspera: [
                { id: 'u3', nombre: 'Ana', apellido: 'Martínez' }
            ]
        }
    ]);

    const [user] = useState('Juan');
    const [editing, setEditing] = useState<Event | null>(null);
    const [verInscriptos, setVerInscriptos] = useState<Persona[] | null>(null);
    const [verListaEspera, setVerListaEspera] = useState<Persona[] | null>(null);

    const handleSave = (updated: Event) => {
        setEvents(prev => prev.map(ev => ev.id === updated.id ? updated : ev));
        setEditing(null);
    };

    const toggleEstado = (evento: Event) => {
        const nuevoEstado = evento.estado.tipoEstado === 'CONFIRMADO' ? 'CANCELADO' : 'CONFIRMADO';
        const actualizado: Event = {
            ...evento,
            estado: { tipoEstado: nuevoEstado as any, fechaCambio: new Date().toISOString() }
        };
        handleSave(actualizado);
    };

    return (
        <div>
            <h1>Bienvenido: {user}</h1>

            <h2>Eventos</h2>
            <table style={{ borderCollapse: 'collapse', width: '100%' }}>
                <thead>
                <tr>
                    <th>Título</th>
                    <th>Fecha</th>
                    <th>Ciudad</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                {events.map(e => (
                    <tr key={e.id}>
                        <td>{e.titulo}</td>
                        <td>{e.fecha}</td>
                        <td>{e.ubicacion.ciudad}</td>
                        <td>{e.estado.tipoEstado}</td>
                        <td>
                            <button onClick={() => setEditing(e)}>✏️</button>
                            <button onClick={() => setVerInscriptos(e.inscriptos)}>👁️</button>
                            <button onClick={() => setVerListaEspera(e.listaDeEspera)}>📋</button>
                            <button onClick={() => toggleEstado(e)}>
                                {e.estado.tipoEstado === 'CONFIRMADO' ? 'CLOSE' : 'OPEN'}
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {editing && (
                <EditEventModal event={editing} onClose={() => setEditing(null)} onSave={handleSave} />
            )}

            {verInscriptos && (
                <PersonListModal personas={verInscriptos} onClose={() => setVerInscriptos(null)} titulo="Inscriptos" />
            )}

            {verListaEspera && (
                <PersonListModal personas={verListaEspera} onClose={() => setVerListaEspera(null)} titulo="Lista de Espera" />
            )}
        </div>
    );
}

// --- Modal para editar evento ---
function EditEventModal({ event, onClose, onSave }: { event: Event; onClose: () => void; onSave: (e: Event) => void }) {
    const [form, setForm] = useState<Event>({...event});

    const handleChange = (field: keyof Event, value: any) => {
        setForm({ ...form, [field]: value });
    };

    return (
        <div style={modalOverlay}>
            <div style={modalContent}>
                <h2>Editar Evento</h2>
                <input value={form.titulo} onChange={(e) => handleChange('titulo', e.target.value)} placeholder="Título"/>
                <textarea value={form.descripcion} onChange={(e) => handleChange('descripcion', e.target.value)} placeholder="Descripción"/>
                <input type="date" value={form.fecha} onChange={(e) => handleChange('fecha', e.target.value)} />
                <input value={form.horaInicio} onChange={(e) => handleChange('horaInicio', e.target.value)} placeholder="Hora inicio"/>
                <input type="number" value={form.duracion} onChange={(e) => handleChange('duracion', parseFloat(e.target.value))} placeholder="Duración"/>
                <input value={form.ubicacion.provincia} onChange={(e) => setForm({...form, ubicacion: {...form.ubicacion, provincia: e.target.value}})} placeholder="Provincia"/>
                <input value={form.ubicacion.ciudad} onChange={(e) => setForm({...form, ubicacion: {...form.ubicacion, ciudad: e.target.value}})} placeholder="Ciudad"/>
                <input value={form.ubicacion.direccion} onChange={(e) => setForm({...form, ubicacion: {...form.ubicacion, direccion: e.target.value}})} placeholder="Dirección"/>
                <input type="number" value={form.cupoMaximo} onChange={(e) => handleChange('cupoMaximo', parseInt(e.target.value))} placeholder="Cupo máximo"/>
                <input value={form.precio.moneda} onChange={(e) => setForm({...form, precio: {...form.precio, moneda: e.target.value}})} placeholder="Moneda"/>
                <input type="number" value={form.precio.monto} onChange={(e) => setForm({...form, precio: {...form.precio, monto: parseFloat(e.target.value)}})} placeholder="Monto"/>
                <input value={form.categoria.tipo} onChange={(e) => setForm({...form, categoria: {tipo: e.target.value}})} placeholder="Categoría"/>
                <input value={form.etiquetas.join(', ')} onChange={(e) => setForm({...form, etiquetas: e.target.value.split(',').map(s=>s.trim())})} placeholder="Etiquetas separadas por coma"/>

                <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                    <button onClick={() => onSave(form)}>Guardar</button>
                    <button onClick={onClose}>Cancelar</button>
                </div>
            </div>
        </div>
    );
}

// --- Modal genérico para mostrar personas ---
function PersonListModal({ personas, onClose, titulo }: { personas: Persona[]; onClose: () => void; titulo: string }) {
    return (
        <div style={modalOverlay}>
            <div style={modalContent}>
                <h2>{titulo}</h2>
                {personas.length > 0 ? (
                    <ul>
                        {personas.map(p => (
                            <li key={p.id}>{p.nombre} {p.apellido}</li>
                        ))}
                    </ul>
                ) : (
                    <p>No hay personas</p>
                )}
                <button onClick={onClose}>Cerrar</button>
            </div>
        </div>
    );
}

// Estilos básicos de modal
const modalOverlay: React.CSSProperties = {
    position: 'fixed',
    inset: 0,
    background: 'rgba(0,0,0,0.4)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center'
};

const modalContent: React.CSSProperties = {
    background: 'white',
    padding: '2rem',
    borderRadius: '10px',
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
    minWidth: '400px'
};
